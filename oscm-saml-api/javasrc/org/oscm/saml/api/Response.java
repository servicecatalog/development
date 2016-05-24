/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 3.4
 */
public class Response {

	public static final String RESPONSE = "Response";
	public static final String LOGOUT_REQUEST = "LogoutRequest";
	public static final String RESPONSE_ID = "ResponseID";
	public static final String ISSUE_INSTANT = "IssueInstant";
	public static final String IN_RESPONSE_TO = "InResponseTo";

	public static final String TAG_BEGIN_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\" \n"
			+ "    IssueInstant=\"[RESPONSE_INSTANT]\" \n"
			+ "    MajorVersion=\"1\" MinorVersion=\"1\" \n"
			+ "    ResponseID=\"[RESPONSE_ID]\"";
	public static final String OPT_IN_RESPONSE_TO = " \n    InResponseTo=\"[IN_RESPONSE_TO]\"";
	public static final String TAG_BEGIN_2 = ">\n";
	public static final String TAG_END = "</samlp:Response>";

	private static final String INSTANT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String INSTANT_TIMEZONE = "UTC";

	private String id;
	private Date issueInstant;
	private String inResponseTo;
	private Status status;
	private List<Assertion> assertions = new ArrayList<Assertion>();

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public Date getIssueInstant() {
		return issueInstant;
	}

	public void setIssueInstant(Date issueInstant) {
		this.issueInstant = issueInstant;
	}

	public List<Assertion> getAssertions() {
		return assertions;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public String getInResponseTo() {
		return inResponseTo;
	}

	public void setInResponseTo(String inResponseTo) {
		this.inResponseTo = inResponseTo;
	}

	public StringBuffer toXML() {
		validate();
		StringBuffer xml = new StringBuffer();
		String tag = TAG_BEGIN_1;
		tag = tag.replaceAll(Pattern.quote("[RESPONSE_ID]"), getID());
		tag = tag.replaceAll(Pattern.quote("[RESPONSE_INSTANT]"),
				formatIssueInstant(getIssueInstant()));
		xml.append(tag);
		if (getInResponseTo() != null && getInResponseTo().trim().length() > 0) {
			xml.append(OPT_IN_RESPONSE_TO.replaceAll(
					Pattern.quote("[IN_RESPONSE_TO]"), getInResponseTo()));
		}
		xml.append(TAG_BEGIN_2);
		xml.append(getStatus().toXML());
		for (Assertion assertion : getAssertions()) {
			xml.append(assertion.toXML());
		}
		xml.append(TAG_END);
		return xml;
	}

	public void validate() {
		if (getID() == null || getID().trim().length() <= 0) {
			throw new IllegalStateException("ResponseId is mandatory");
		}
		if (getIssueInstant() == null) {
			throw new IllegalStateException("IssueInstant is mandatory");
		}
		if (getStatus() == null) {
			throw new IllegalStateException("<Status> is mandatory");
		}
	}

	public static Response unmarshall(Node responseNode) throws ParseException {
		Response response = new Response();
		response.setID(XmlParser.parseAttribute(responseNode,
				Response.RESPONSE_ID));
		response.setIssueInstant(Response.parseIssueInstant(XmlParser
				.parseAttribute(responseNode, Response.ISSUE_INSTANT)));
		response.setInResponseTo(XmlParser.parseAttribute(responseNode,
				Response.IN_RESPONSE_TO));

		NodeList children = responseNode.getChildNodes();
		boolean hasStatus = false;
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (Status.STATUS.equals(child.getLocalName())) {
				if (hasStatus) {
					throw new IllegalStateException(
							"Duplicate <Status> occurence inside <Response>");
				}
				hasStatus = true;
				response.setStatus(Status.unmarshall(child));
			} else if (Assertion.ASSERTION.equals(child.getLocalName())) {
				response.getAssertions().add(Assertion.unmarshall(child));
			}
		}
		response.validate();
		return response;
	}

	public static Date parseIssueInstant(String issueInstant)
			throws ParseException {
		return createDateFormat().parse(issueInstant);
	}

	public static String formatIssueInstant(Date issueInstant) {
		if (issueInstant != null) {
			return createDateFormat().format(issueInstant);
		}
		return null;
	}

	private static DateFormat createDateFormat() {
		DateFormat format = new SimpleDateFormat(INSTANT_DATE_FORMAT);
		TimeZone timezone = TimeZone.getTimeZone(INSTANT_TIMEZONE);
		format.setTimeZone(timezone);
		return format;
	}

	public static String getPlaceHolder(String attribute) {
		return "[" + attribute + "]";
	}

}
