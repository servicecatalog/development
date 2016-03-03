/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.credential.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.oscm.credential.bean.SamlServiceBean.ResponseBuilder;
import org.oscm.saml.api.Assertion;
import org.oscm.saml.api.AuthenticationStatement;
import org.oscm.saml.api.NameIdentifier;
import org.oscm.saml.api.Response;
import org.oscm.saml.api.ResponseParser;
import org.oscm.saml.api.SubjectConfirmation;
import org.oscm.saml.api.SubjectConfirmation.ConfirmationMethod;

/**
 * Tests the SAML response generation, parsing and unmarshalling:
 * <ul>
 * <li>creates a <code>Response</code> instance using the same algorithm as
 * <code>SamlServiceBean</code></li>
 * <li>generates the string for the corresponding HTTP parameter - an exception
 * is thrown if the generated string does not comply with the SAML 1.1 Core
 * specification</li>
 * <li>parses the generated string using <code>ResponseParser</code> into a DOM
 * Element - an exception is thrown if parsing fails</li>
 * <li>creates another <code>Response</code> instance by unmarshalling the DOM
 * Element - an exception is thrown if the DOM Element does not comply with SAML
 * 1.1 Core specification</li>
 * <li>asserts that the two responses are equal (all attributes and sub-elements
 * are equal)</li>
 * </ul>
 * 
 * @author barzu
 */
public class SamlResponseTest {

    private static final String REQUEST_ID = "4040406c-1530-11e0-e869-0110283fdfd0";
    private static final String USER_ID = "admin";
    private static final long ASSERTION_EXPIRATION = 1800000L;
    private static final long ASSERTION_VALIDITY_TOLERANCE = 600000L;

    private Response createdResponse;
    private Response unmarshalledResponse;

    @Before
    public void init() throws Exception {
        ResponseBuilder builder = new ResponseBuilder(ASSERTION_EXPIRATION,
                ASSERTION_VALIDITY_TOLERANCE);
        createdResponse = builder.createReponse(REQUEST_ID, USER_ID);
        ResponseParser parser = new ResponseParser(createdResponse.toXML()
                .toString());
        unmarshalledResponse = Response.unmarshall(parser.getResponseElement());
    }

    @Test
    public void testResponseID() throws Exception {
        assertNotNull(createdResponse.getID());
        assertNotNull(unmarshalledResponse.getID());
        assertEquals(createdResponse.getID(), unmarshalledResponse.getID());
    }

    @Test
    public void testIssueInstant() throws Exception {
        assertNotNull(createdResponse.getIssueInstant());
        assertNotNull(unmarshalledResponse.getIssueInstant());
        assertEquals(createdResponse.getIssueInstant(),
                unmarshalledResponse.getIssueInstant());
        assertEquals(Response.formatIssueInstant(createdResponse
                .getIssueInstant()),
                Response.formatIssueInstant(unmarshalledResponse
                        .getIssueInstant()));
    }

    @Test
    public void testInResponseTo() throws Exception {
        assertEquals(REQUEST_ID, createdResponse.getInResponseTo());
        assertEquals(REQUEST_ID, unmarshalledResponse.getInResponseTo());
        assertEquals(createdResponse.getInResponseTo(),
                unmarshalledResponse.getInResponseTo());
    }

    @Test
    public void testStatus() throws Exception {
        // <StatusCode>
        assertNotNull(createdResponse.getStatus());
        assertNotNull(createdResponse.getStatus().getStatusCode());
        assertNotNull(unmarshalledResponse.getStatus());
        assertNotNull(unmarshalledResponse.getStatus().getStatusCode());
        assertEquals(createdResponse.getStatus().getStatusCode(),
                unmarshalledResponse.getStatus().getStatusCode());

        // second-level <StatusCode>
        if (createdResponse.getStatus().getSecondLevelStatusCode() == null) {
            assertNull(unmarshalledResponse.getStatus()
                    .getSecondLevelStatusCode());
        } else {
            assertNotNull(unmarshalledResponse.getStatus()
                    .getSecondLevelStatusCode());
            assertEquals(
                    createdResponse.getStatus().getSecondLevelStatusCode(),
                    unmarshalledResponse.getStatus().getSecondLevelStatusCode());
        }
    }

    @Test
    public void testAssertionsSize() throws Exception {
        assertEquals(createdResponse.getAssertions().size(),
                unmarshalledResponse.getAssertions().size());
    }

    @Test
    public void testAssertionDuplicates() throws Exception {
        for (Assertion assertion : createdResponse.getAssertions()) {
            getAssertion(assertion.getID(), createdResponse.getAssertions());
        }
        for (Assertion assertion : unmarshalledResponse.getAssertions()) {
            getAssertion(assertion.getID(),
                    unmarshalledResponse.getAssertions());
        }
    }

    @Test
    public void testAssertionID() throws Exception {
        new AssertionLooper() {

            @Override
            public void testAssertion(Assertion createdAssertion,
                    Assertion unmarshalledAssertion) {

                // check for duplicates
                getAssertion(createdAssertion.getID(),
                        createdResponse.getAssertions());
                getAssertion(createdAssertion.getID(),
                        unmarshalledResponse.getAssertions());

                // mandatory
                assertNotNull(createdAssertion.getID());
                assertNotNull(unmarshalledAssertion.getID());

            }
        }.run();
    }

    @Test
    public void testAssertionIssueInstant() throws Exception {
        new AssertionLooper() {

            @Override
            public void testAssertion(Assertion createdAssertion,
                    Assertion unmarshalledAssertion) {
                // mandatory
                assertNotNull(createdAssertion.getIssueInstant());
                assertNotNull(unmarshalledAssertion.getIssueInstant());

                assertEquals(createdAssertion.getIssueInstant(),
                        unmarshalledAssertion.getIssueInstant());
                assertEquals(Response.formatIssueInstant(createdAssertion
                        .getIssueInstant()),
                        Response.formatIssueInstant(unmarshalledAssertion
                                .getIssueInstant()));
            }
        }.run();
    }

    @Test
    public void testAssertionIssuer() throws Exception {
        new AssertionLooper() {

            @Override
            public void testAssertion(Assertion createdAssertion,
                    Assertion unmarshalledAssertion) {
                // mandatory
                assertNotNull(createdAssertion.getIssuer());
                assertNotNull(unmarshalledAssertion.getIssuer());

                assertEquals(createdAssertion.getIssuer(),
                        unmarshalledAssertion.getIssuer());
            }
        }.run();
    }

    @Test
    public void testConditions() throws Exception {
        new AssertionLooper() {

            @Override
            public void testAssertion(Assertion createdAssertion,
                    Assertion unmarshalledAssertion) {
                // optional
                if (createdAssertion.getConditions() == null) {
                    assertNull(unmarshalledAssertion.getConditions());
                } else {
                    assertNotNull(unmarshalledAssertion.getConditions());
                    // NotBefore
                    assertEquals(createdAssertion.getConditions()
                            .getNotBefore(), unmarshalledAssertion
                            .getConditions().getNotBefore());
                    assertEquals(Response.formatIssueInstant(createdAssertion
                            .getConditions().getNotBefore()),
                            Response.formatIssueInstant(unmarshalledAssertion
                                    .getConditions().getNotBefore()));
                    // NotOnOrAfter
                    assertEquals(createdAssertion.getConditions()
                            .getNotOnOrAfter(), unmarshalledAssertion
                            .getConditions().getNotOnOrAfter());
                    assertEquals(Response.formatIssueInstant(createdAssertion
                            .getConditions().getNotOnOrAfter()),
                            Response.formatIssueInstant(unmarshalledAssertion
                                    .getConditions().getNotOnOrAfter()));
                }
            }
        }.run();
    }

    @Test
    public void testAuthenticationStatementsSize() throws Exception {
        new AssertionLooper() {

            @Override
            public void testAssertion(Assertion createdAssertion,
                    Assertion unmarshalledAssertion) {
                assertFalse(
                        "At least one of the following elements is mandatory: <Statement>, <SubjectStatement>, <AuthenticationStatement>, <AuthorizationDecisionStatement>, <AttributeStatement>",
                        createdAssertion.getAuthenticationStatements()
                                .isEmpty());
                assertFalse(
                        "At least one of the following elements is mandatory: <Statement>, <SubjectStatement>, <AuthenticationStatement>, <AuthorizationDecisionStatement>, <AttributeStatement>",
                        unmarshalledAssertion.getAuthenticationStatements()
                                .isEmpty());
                assertEquals(createdAssertion.getAuthenticationStatements()
                        .size(), unmarshalledAssertion
                        .getAuthenticationStatements().size());
            }
        }.run();
    }

    @Test
    public void testAuthenticationInstant() throws Exception {
        new AuthenticationStatementLooper() {

            @Override
            public void testAuthenticationStatement(
                    AuthenticationStatement createdAuthnStatement,
                    AuthenticationStatement unmarshalledAuthnStatement) {
                // mandatory
                assertNotNull(createdAuthnStatement.getAuthenticationInstant());
                assertNotNull(unmarshalledAuthnStatement
                        .getAuthenticationInstant());

                assertEquals(createdAuthnStatement.getAuthenticationInstant(),
                        unmarshalledAuthnStatement.getAuthenticationInstant());
                assertEquals(Response.formatIssueInstant(createdAuthnStatement
                        .getAuthenticationInstant()),
                        Response.formatIssueInstant(unmarshalledAuthnStatement
                                .getAuthenticationInstant()));
            }
        }.run();
    }

    @Test
    public void testAuthenticationMethod() throws Exception {
        new AuthenticationStatementLooper() {

            @Override
            public void testAuthenticationStatement(
                    AuthenticationStatement createdAuthnStatement,
                    AuthenticationStatement unmarshalledAuthnStatement) {

                // mandatory
                assertNotNull(createdAuthnStatement.getAuthenticationMethod());
                assertNotNull(unmarshalledAuthnStatement
                        .getAuthenticationMethod());

                assertEquals(createdAuthnStatement.getAuthenticationMethod(),
                        unmarshalledAuthnStatement.getAuthenticationMethod());
            }
        }.run();
    }

    @Test
    public void testAuthenticationStatement_Subject() throws Exception {
        new AuthenticationStatementLooper() {

            @Override
            public void testAuthenticationStatement(
                    AuthenticationStatement createdAuthnStatement,
                    AuthenticationStatement unmarshalledAuthnStatement) {

                // mandatory
                assertNotNull(createdAuthnStatement.getSubject());
                assertNotNull(unmarshalledAuthnStatement.getSubject());

                assertTrue(
                        "At least one of the following elements is mandatory: <NameIdentifier>, <SubjectConfirmation>",
                        createdAuthnStatement.getSubject().getNameIdentifier() != null
                                || createdAuthnStatement.getSubject()
                                        .getSubjectConfirmation() != null);
                assertTrue(
                        "At least one of the following elements is mandatory: <NameIdentifier>, <SubjectConfirmation>",
                        unmarshalledAuthnStatement.getSubject()
                                .getNameIdentifier() != null
                                || unmarshalledAuthnStatement.getSubject()
                                        .getSubjectConfirmation() != null);
            }
        }.run();
    }

    @Test
    public void testNameIdentifier() throws Exception {
        new NameIdentifierLooper() {

            @Override
            public void testNameIdentifier(
                    NameIdentifier createdNameIdentifier,
                    NameIdentifier unmarshalledNameIdentifier) {

                // mandatory
                assertNotNull(createdNameIdentifier.getNameIdentifier());
                assertNotNull(unmarshalledNameIdentifier.getNameIdentifier());

                assertEquals(createdNameIdentifier.getNameIdentifier(),
                        unmarshalledNameIdentifier.getNameIdentifier());
            }
        }.run();
    }

    @Test
    public void testNameIdentifier_NameQualifier() throws Exception {
        new NameIdentifierLooper() {

            @Override
            public void testNameIdentifier(
                    NameIdentifier createdNameIdentifier,
                    NameIdentifier unmarshalledNameIdentifier) {

                // optional
                if (createdNameIdentifier.getNameQualifier() == null) {
                    assertNull(unmarshalledNameIdentifier.getNameQualifier());
                } else {
                    assertNotNull(unmarshalledNameIdentifier.getNameQualifier());
                    assertEquals(createdNameIdentifier.getNameQualifier(),
                            unmarshalledNameIdentifier.getNameQualifier());
                }
            }
        }.run();
    }

    @Test
    public void testNameIdentifier_Format() throws Exception {
        new NameIdentifierLooper() {

            @Override
            public void testNameIdentifier(
                    NameIdentifier createdNameIdentifier,
                    NameIdentifier unmarshalledNameIdentifier) {

                // optional
                if (createdNameIdentifier.getFormat() == null) {
                    assertNull(unmarshalledNameIdentifier.getFormat());
                } else {
                    assertNotNull(unmarshalledNameIdentifier.getFormat());
                    assertEquals(createdNameIdentifier.getFormat(),
                            unmarshalledNameIdentifier.getFormat());
                }
            }
        }.run();
    }

    @Test
    public void testSubject_SubjectConfirmation() throws Exception {
        new AuthenticationStatementLooper() {

            @Override
            public void testAuthenticationStatement(
                    AuthenticationStatement createdAuthnStatement,
                    AuthenticationStatement unmarshalledAuthnStatement) {

                if (createdAuthnStatement.getSubject().getSubjectConfirmation() == null) {
                    assertNull(unmarshalledAuthnStatement.getSubject()
                            .getSubjectConfirmation());
                } else {
                    assertNotNull(unmarshalledAuthnStatement.getSubject()
                            .getSubjectConfirmation());

                    // <ConfirmationMethod>
                    SubjectConfirmation createdSubjectConfirmation = createdAuthnStatement
                            .getSubject().getSubjectConfirmation();
                    SubjectConfirmation unmarshalledSubjectConfirmation = unmarshalledAuthnStatement
                            .getSubject().getSubjectConfirmation();

                    assertFalse(
                            "At least one <ConfirmationMethod> is mandatory",
                            createdSubjectConfirmation.getConfirmationMethods()
                                    .isEmpty());
                    assertFalse(
                            "At least one <ConfirmationMethod> is mandatory",
                            unmarshalledSubjectConfirmation
                                    .getConfirmationMethods().isEmpty());

                    assertTrue(createdSubjectConfirmation
                            .getConfirmationMethods().size() == unmarshalledSubjectConfirmation
                            .getConfirmationMethods().size());
                    for (int i = 0; i < createdSubjectConfirmation
                            .getConfirmationMethods().size(); i++) {
                        ConfirmationMethod createdMethod = createdSubjectConfirmation
                                .getConfirmationMethods().get(i);
                        ConfirmationMethod unmarshalledMethod = unmarshalledSubjectConfirmation
                                .getConfirmationMethods().get(i);

                        assertNotNull(createdMethod);
                        assertNotNull(unmarshalledMethod);
                        assertEquals(createdMethod, unmarshalledMethod);
                    }
                }
            }
        }.run();
    }

    private abstract class AssertionLooper {

        public void run() {
            for (Assertion createdAssertion : createdResponse.getAssertions()) {
                assertNotNull(createdAssertion.getID());
                Assertion unmarshalledAssertion = getAssertion(
                        createdAssertion.getID(),
                        unmarshalledResponse.getAssertions());
                assertNotNull(
                        "No unmarshalled Assertion found for AssertionID = "
                                + createdAssertion.getID(),
                        unmarshalledAssertion);
                testAssertion(createdAssertion, unmarshalledAssertion);
            }
        }

        public abstract void testAssertion(Assertion createdAssertion,
                Assertion unmarshalledAssertion);
    }

    private static Assertion getAssertion(String assertionID,
            List<Assertion> fromList) {
        Assertion foundAssertion = null;
        for (Assertion assertion : fromList) {
            assertNotNull(assertion.getID());
            if (assertion.getID().equals(assertionID)) {
                assertNull("Dupplicate assertion found for Assertion ID = "
                        + assertionID, foundAssertion);
                foundAssertion = assertion;
            }
        }
        return foundAssertion;
    }

    private abstract class AuthenticationStatementLooper extends
            AssertionLooper {

        @Override
        public void testAssertion(Assertion createdAssertion,
                Assertion unmarshalledAssertion) {

            for (int i = 0; i < createdAssertion.getAuthenticationStatements()
                    .size(); i++) {
                AuthenticationStatement createdAuthnStatement = createdAssertion
                        .getAuthenticationStatements().get(i);
                AuthenticationStatement unmarshalledAuthnStatement = unmarshalledAssertion
                        .getAuthenticationStatements().get(i);
                testAuthenticationStatement(createdAuthnStatement,
                        unmarshalledAuthnStatement);
            }
        }

        public abstract void testAuthenticationStatement(
                AuthenticationStatement createdAuthnStatement,
                AuthenticationStatement unmarshalledAuthnStatement);
    }

    private abstract class NameIdentifierLooper extends
            AuthenticationStatementLooper {

        @Override
        public void testAuthenticationStatement(
                AuthenticationStatement createdAuthnStatement,
                AuthenticationStatement unmarshalledAuthnStatement) {

            if (createdAuthnStatement.getSubject().getNameIdentifier() == null) {
                assertNull(unmarshalledAuthnStatement.getSubject()
                        .getNameIdentifier());
            } else {
                assertNotNull(unmarshalledAuthnStatement.getSubject()
                        .getNameIdentifier());
                testNameIdentifier(unmarshalledAuthnStatement.getSubject()
                        .getNameIdentifier(), unmarshalledAuthnStatement
                        .getSubject().getNameIdentifier());
            }

        }

        public abstract void testNameIdentifier(
                NameIdentifier createdNameIdentifier,
                NameIdentifier unmarshalledNameIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssertionExpiration_Negative() throws Exception {
        new ResponseBuilder(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssertionValidationTolerance_Negative() throws Exception {
        new ResponseBuilder(10, 0);
    }

}
