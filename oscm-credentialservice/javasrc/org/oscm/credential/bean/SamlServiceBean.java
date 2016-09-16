/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.credential.bean;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.dataservice.local.DataService;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.saml.api.*;
import org.oscm.saml.api.AuthenticationStatement.AuthenticationMethod;
import org.oscm.saml.api.Status.FirstLevelStatusCode;
import org.oscm.saml.api.SubjectConfirmation.ConfirmationMethod;
import org.oscm.types.constants.Configuration;
import org.oscm.validation.Invariants;
import org.w3c.dom.Element;

@Stateless
@Remote(SamlService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SamlServiceBean implements SamlService {

    private static final String ISSUER = "org:oscm:idp";

    private static Random random = new Random();
    private static final char[] charMapping = { 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };

    @EJB(beanInterface = DataService.class)
    protected DataService dm;

    @EJB(beanInterface = ConfigurationServiceLocal.class)
    private ConfigurationServiceLocal configService;

    @Override
    public String createSamlResponse(String requestId) {
        Response response = new ResponseBuilder(
                getConfigurationLong(ConfigurationKey.IDP_ASSERTION_EXPIRATION),
                getConfigurationLong(ConfigurationKey.IDP_ASSERTION_VALIDITY_TOLERANCE))
                .createReponse(requestId, dm.getCurrentUser().getUserId());
        String responseString = response.toXML().toString();
        SamlKeyLoader keyLoader = new SamlKeyLoader(configService);
        responseString = signSamlResponse(responseString,
                keyLoader.getPrivateKey(), keyLoader.getPublicCertificate());
        responseString = SamlEncoder.encodeBase64(responseString);
        return responseString;
    }

    static String signSamlResponse(String samlResponse, PrivateKey privateKey,
            X509Certificate publicCertificate) {
        ResponseParser parser = new ResponseParser(samlResponse);
        SamlSigner signer = new SamlSigner(privateKey);
        signer.setPublicCertificate(publicCertificate);
        Element responseElement = parser.getResponseElement();
        for (Element assertionElement : parser.getAssertionElements()) {
            assertionElement.setIdAttribute("AssertionID", true);
            signer.signSamlElement(assertionElement, responseElement);
        }
        return ResponseParser.toString(responseElement);
    }

    static class ResponseBuilder {

        private Date issueInstant;
        private long assertionExpiration;
        private long assertionValidityTolerance;

        public ResponseBuilder(long assertionExpiration,
                long assertionValidityTolerance) {
            Invariants.assertGreaterThan(assertionExpiration, 0L);
            Invariants.assertGreaterThan(assertionValidityTolerance, 0L);
            this.assertionExpiration = assertionExpiration;
            this.assertionValidityTolerance = assertionValidityTolerance;
        }

        public Response createReponse(String requestId, String userId) {

            issueInstant = new Date();

            // <Response>
            Response response = new Response();
            response.setID(createID());
            response.setInResponseTo(requestId);
            response.setIssueInstant(issueInstant);
            response.setStatus(createStatus());
            response.getAssertions().add(createAssertion(userId));

            return response;
        }

        private Status createStatus() {
            // <Status>
            Status status = new Status();
            status.setStatusCode(FirstLevelStatusCode.SUCCESS);
            return status;
        }

        private Assertion createAssertion(String userId) {

            // <Conditions>
            Conditions conditions = new Conditions();

            // set
            long notBefore = issueInstant.getTime()
                    - assertionValidityTolerance;
            Calendar calendar = Calendar.getInstance(TimeZone
                    .getTimeZone(Response.INSTANT_TIMEZONE));
            calendar.setTimeInMillis(notBefore);
            conditions.setNotBefore(calendar.getTime());

            long notOnOrAfter = issueInstant.getTime() + assertionExpiration;
            calendar.setTimeInMillis(notOnOrAfter);
            conditions.setNotOnOrAfter(calendar.getTime());

            // <NameIdentifier>
            NameIdentifier nameId = new NameIdentifier();
            nameId.setNameIdentifier(userId);
            nameId.setFormat(NameIdentifier.Format.UNSPECIFIED);
            // nameId.setNameQualifier(ISSUER);

            // <subjectConfirmation>
            SubjectConfirmation subjectConfirmation = new SubjectConfirmation();
            subjectConfirmation.getConfirmationMethods().add(
                    ConfirmationMethod.BEARER);

            // <Subject>
            Subject subject = new Subject();
            subject.setNameIdentifier(nameId);
            subject.setSubjectConfirmation(subjectConfirmation);

            // <AuthenticationStatement>
            AuthenticationStatement authenticationStatement = new AuthenticationStatement();
            authenticationStatement.setSubject(subject);
            authenticationStatement
                    .setAuthenticationMethod(AuthenticationMethod.Password);
            // TODO: set the real authentication instant (user login time) when
            // it will be available
            authenticationStatement.setAuthenticationInstant(issueInstant);

            // <Assertion>
            Assertion assertion = new Assertion();
            assertion.setIssuer(ISSUER);
            assertion.setIssueInstant(issueInstant);
            assertion.setID(createID());
            assertion.setConditions(conditions);
            assertion.getAuthenticationStatements()
                    .add(authenticationStatement);

            return assertion;

        }

        /**
         * @return a string of random characters
         * @see SAML 1.1 Core 1.2.3
         */
        private String createID() {
            byte[] bytes = new byte[20]; // 160 bits
            random.nextBytes(bytes);

            char[] chars = new char[40];

            for (int i = 0; i < bytes.length; i++) {
                int left = (bytes[i] >> 4) & 0x0f;
                int right = bytes[i] & 0x0f;
                chars[i * 2] = charMapping[left];
                chars[i * 2 + 1] = charMapping[right];
            }

            return String.valueOf(chars);
        }
    }

    private long getConfigurationLong(ConfigurationKey configurationKey) {
        long expirationMillis = configService.getConfigurationSetting(
                configurationKey, Configuration.GLOBAL_CONTEXT).getLongValue();
        if (expirationMillis <= 0) {
            try {
                return Long.parseLong(configurationKey.getFallBackValue());
            } catch (NumberFormatException e) {
                throw new SaaSSystemException(
                        "Invalid number format in configuration setting "
                                + configurationKey.name(), e);
            }
        }
        return expirationMillis;
    }

}
