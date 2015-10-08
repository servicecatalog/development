/*
 * Copyright 2000,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.j2ep;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.sf.j2ep.model.Server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper for the normal HttpServletResponse, based on the content-type
 * either the normal output stream of a wrapped stream will be returned. The
 * wrapped stream can handle rewrite of links found in the source.
 * 
 * This class also handles rewriting of the headers Location and Set-Cookie.
 * 
 * @author Anders Nyman
 */
public final class UrlRewritingResponseWrapper extends
		HttpServletResponseWrapper {

	/**
	 * Stream we are using for the response.
	 */
	private UrlRewritingOutputStream outStream;

	/**
	 * Writer we are using for the response.
	 */
	private PrintWriter outWriter;

	/**
	 * Writer that writes to the underlying stream.
	 */
	private PrintWriter originalWriter;

	/**
	 * Server used for this page
	 */
	private Server server;

	/**
	 * The location for this server, used when we rewrite absolute URIs
	 */
	private String ownHostName;

	/**
	 * The contextPath, needed when we rewrite links.
	 */
	private String contextPath;

	/**
	 * The servers.
	 */
	private ServerChain serverChain;

	/**
	 * Regex to find absolute links.
	 */
	// pock: '-' added to the pattern as it is a valid character for a domain
	// name
	private static Pattern linkPattern = Pattern.compile(
			"\\b([^/]+://)([^/]+)([\\w/-]*)", Pattern.CASE_INSENSITIVE
					| Pattern.CANON_EQ);

	/**
	 * Regex to find the path in Set-Cookie headers.
	 */
	private static Pattern pathAndDomainPattern = Pattern.compile(
			"\\b(path=|domain=)([^;\\s]+);?", Pattern.CASE_INSENSITIVE
					| Pattern.CANON_EQ);

	/**
	 * Logging element supplied by commons-logging.
	 */
	private static Log log;

	/**
	 * Basic constructor.
	 * 
	 * pock: For BES we use a new server for every request. We can't determine
	 * this server from the serverChain --> use the server of the request as
	 * parameter (Bug 5487)
	 * 
	 * @param response
	 *            The response we are wrapping
	 * @param server
	 *            The server that was matched
	 * @param ownHostName
	 *            String we are rewriting servers to
	 * @throws IOException
	 *             When there is a problem with the streams
	 */
	public UrlRewritingResponseWrapper(HttpServletResponse response,
			Server server, String ownHostName, String contextPath,
			ServerChain serverChain) throws IOException {
		super(response);
		this.server = server;
		this.ownHostName = ownHostName;
		this.contextPath = contextPath;
		this.serverChain = serverChain;

		log = LogFactory.getLog(UrlRewritingResponseWrapper.class);
		outStream = new UrlRewritingOutputStream(response.getOutputStream(),
				contextPath, response
						.getCharacterEncoding());
		outWriter = new PrintWriter(outStream);
		originalWriter = new PrintWriter(response.getOutputStream());
	}

	/**
	 * Checks if we have to rewrite the header and if so will rewrite it.
	 * 
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String,
	 *      java.lang.String)
	 */
	public void addHeader(String name, String originalValue) {
		String value;
		if (name.equalsIgnoreCase("location")) {
			value = rewriteLocation(originalValue);
		} else if (name.equalsIgnoreCase("set-cookie")) {
			value = rewriteSetCookie(originalValue);
		} else {
			value = originalValue;
		}
		super.addHeader(name, value);
	}

	/**
	 * Checks if we have to rewrite the header and if so will rewrite it.
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String,
	 *      java.lang.String)
	 */
	public void setHeader(String name, String originalValue) {
		String value;
		if (name.equalsIgnoreCase("location")) {
			value = rewriteLocation(originalValue);
		} else if (name.equalsIgnoreCase("set-cookie")) {
			value = rewriteSetCookie(originalValue);
		} else {
			value = originalValue;
		}
		super.setHeader(name, value);
	}

	/**
	 * Rewrites the location header. Will first locate any links in the header
	 * and then rewrite them.
	 * 
	 * @param value
	 *            The header value we are to rewrite
	 * @return A rewritten header
	 */
	private String rewriteLocation(String value) {
		StringBuffer header = new StringBuffer();

		Matcher matcher = linkPattern.matcher(value);
		while (matcher.find()) {

			String link = matcher.group(3).replaceAll("\\$", "\\\\$");
			if (link.length() == 0) {
				link = "/";
			}
			String location = matcher.group(2) + link;

			// pock: we first consider the current server (this helps to
			// identify the right server even if multiple server would match the
			// location
			Server matchingServer = null;
			String fullPath = server.getDomainName() + server.getPath() + "/";
			if (location.startsWith(fullPath)) {
				matchingServer = server;
			}
			if (matchingServer == null) {
				matchingServer = serverChain.getServerMapped(location);
			}
			// pock: don't rewrite the link if it already starts with the
			// current context path
			if (matchingServer != null && !link.startsWith(contextPath)) {
				link = link.substring(matchingServer.getPath().length());
				link = matchingServer.getRule().revert(link);
				matcher.appendReplacement(header, "$1" + ownHostName
						+ contextPath + link);
			}
		}
		matcher.appendTail(header);
		log.debug("Location header rewritten " + value + " >> "
				+ header.toString());
		return header.toString();
	}

	/**
	 * Rewrites the header Set-Cookie so that path and domain is correct.
	 * 
	 * @param value
	 *            The original header
	 * @return The rewritten header
	 */
	private String rewriteSetCookie(String value) {
		StringBuffer header = new StringBuffer();

		Matcher matcher = pathAndDomainPattern.matcher(value);
		while (matcher.find()) {
			if (matcher.group(1).equalsIgnoreCase("path=")) {
				String path = server.getRule().revert(matcher.group(2));
				matcher.appendReplacement(header, "$1" + contextPath + path
						+ ";");
			} else {
				matcher.appendReplacement(header, "");
			}

		}
		matcher.appendTail(header);
		log.debug("Set-Cookie header rewritten \"" + value + "\" >> "
				+ header.toString());
		return header.toString();
	}

	/**
	 * Based on the value in the content-type header we either return the
	 * default stream or our own stream that can rewrite links.
	 * 
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		if (getContentType() != null && shouldRewrite(getContentType())) {
			return outStream;
		} else {
			return super.getOutputStream();
		}
	}

	/**
	 * Based on the value in the content-type header we either return the
	 * default writer or our own writer. Our own writer will write to the stream
	 * that can rewrite links.
	 * 
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		if (getContentType() != null && shouldRewrite(getContentType())) {
			return outWriter;
		} else {
			return originalWriter;
		}
	}

	/**
	 * Rewrites the output stream to change any links. Also closes all the
	 * streams and writers. We need the user to flush and close the streams
	 * himself as usual but we can't be sure that the writers created are used
	 * by the client and therefor we close them here.
	 * 
	 * @throws IOException
	 *             Is thrown when there is a problem with the streams
	 */
	public void processStream() throws IOException {
		if (getContentType() != null && shouldRewrite(getContentType())) {
			outStream.rewrite(server);
		}
		super.getOutputStream().flush();
		super.getOutputStream().close();
		outStream.close();
		originalWriter.close();
		outWriter.close();
	}

	/**
	 * Checks the contentType to evaluate if we should do link rewriting for
	 * this content.
	 * 
	 * @param contentType
	 *            The Content-Type header
	 * @return true if we need to rewrite links, false otherwise
	 */
	private boolean shouldRewrite(String contentType) {
		String lowerCased = contentType.toLowerCase();
		return (lowerCased.indexOf("html") > -1
				|| lowerCased.indexOf("css") > -1 || lowerCased
				.indexOf("javascript") > -1);
	}
}
