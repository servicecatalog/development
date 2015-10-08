/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 01.12.2011                                                      
 *                                                                              
 *******************************************************************************/

package net.sf.j2ep;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import net.sf.j2ep.model.Rule;
import net.sf.j2ep.model.Server;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class UrlRewritingOutputStreamTest {

    private UrlRewritingOutputStream urw;

    private String contextPath = "contextPath/";
    private String encoding = "UTF-8";
    private Server server;
    private ServletOutputStream originalStream;
    private StringBuffer modifiedContent = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        urw = new UrlRewritingOutputStream(originalStream, contextPath,
                encoding);
    }

    @Test
    public void rewrite_NoLinks() throws Exception {
        initEnvironment("</html>", "");
        urw.rewrite(server);
        assertStreamContent("</html>");
    }

    @Test
    public void rewrite_RelativeLinkNoBaseDirNoSegment() throws Exception {
        initEnvironment("<html><a href=\"index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink2NoBaseDirNoSegment() throws Exception {
        initEnvironment(
                "<html><a href=\"./index.html\">description</a></html>", "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"./index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink3NoBaseDirNoSegment() throws Exception {
        initEnvironment("<html><a href=\"/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"contextPath/reverted/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLinkNoBaseDirOneSegment() throws Exception {
        initEnvironment(
                "<html><a href=\"subdir/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"subdir/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink2NoBaseDirOneSegment() throws Exception {
        initEnvironment(
                "<html><a href=\"./subdir/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"./subdir/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink3NoBaseDirOneSegment() throws Exception {
        initEnvironment(
                "<html><a href=\"/subdir/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"contextPath/reverted/subdir/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLinkNoBaseDirFourSegments() throws Exception {
        initEnvironment(
                "<html><a href=\"subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink2NoBaseDirFourSegments() throws Exception {
        initEnvironment(
                "<html><a href=\"./subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"./subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink3NoBaseDirFourSegments() throws Exception {
        initEnvironment(
                "<html><a href=\"/subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"contextPath/reverted/subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_RelativeLink3NoBaseDirMatrixParams() throws Exception {
        initEnvironment(
                "<html><a href=\"/subdir1;param1=value1/index.html\">description</a></html>",
                "");
        urw.rewrite(server);
        assertStreamContent("<html><a href=\"contextPath/reverted/subdir1;param1=value1/index.html\">description</a></html>");
    }

    @Test
    public void rewrite_AbsoluteLinkNoBaseDirNoSegment() throws Exception {
        String url = "<html><a href=\"http://www.google.de/index.html\">description</a></html>";
        initEnvironment(url, "");
        urw.rewrite(server);
        assertStreamContent(url);
    }

    @Test
    public void rewrite_AbsoluteLinkNoBaseDirOneSegment() throws Exception {
        String url = "<html><a href=\"http://www.google.de/subdir/index.html\">description</a></html>";
        initEnvironment(url, "");
        urw.rewrite(server);
        assertStreamContent(url);
    }

    @Test
    public void rewrite_AbsoluteLinkNoBaseDirFourSegments() throws Exception {
        String url = "<html><a href=\"http://www.google.de/subdir1/subdir2/subdir3/subdir4/index.html\">description</a></html>";
        initEnvironment(url, "");
        urw.rewrite(server);
        assertStreamContent(url);
    }

    @Test
    public void rewrite_AbsoluteLink3NoBaseDirMatrixParams() throws Exception {
        String url = "<html><a href=\"http://www.google.de/subdir1;param1=value1/index.html\">description</a></html>";
        initEnvironment(url, "");
        urw.rewrite(server);
        assertStreamContent(url);
    }

    private void initEnvironment(String streamContent, final String baseDir)
            throws IOException {
        originalStream = mock(ServletOutputStream.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                modifiedContent.append(new String((byte[]) invocation
                        .getArguments()[0], encoding));
                return null;
            }
        }).when(originalStream).write(Matchers.any(byte[].class));

        server = mock(Server.class);
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return baseDir;
            }
        }).when(server).getPath();

        final Rule rule = mock(Rule.class);
        doAnswer(new Answer<Rule>() {
            public Rule answer(InvocationOnMock invocation) throws Throwable {
                return rule;
            }
        }).when(server).getRule();
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "reverted" + invocation.getArguments()[0];
            }
        }).when(rule).revert(Matchers.anyString());

        urw = new UrlRewritingOutputStream(originalStream, contextPath,
                encoding);
        urw.write(streamContent.getBytes(encoding));
    }

    private void assertStreamContent(String expectedContent) {
        assertEquals(expectedContent, modifiedContent.toString());
    }

}
