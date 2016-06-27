/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 24.06.16 11:17
 *
 ******************************************************************************/

package org.oscm.saml2;

import static org.oscm.internal.types.enumtypes.ConfigurationKey.*;
import static org.oscm.types.constants.Configuration.GLOBAL_CONTEXT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.types.exception.UnsupportedOperationException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;

/**
 * Authored by dawidch
 */
@Stateless(mappedName = "saml2Bean", name = "saml2Bean")
@Remote(SamlService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SamlServiceBean implements SamlService {

    private static final String UTF_8 = "UTF-8";
    private static final String FORMAT = "http://schemas.xmlsoap.org/claims/UPN";
    private static final Log4jLogger LOGGER = LoggerFactory
            .getLogger(SamlServiceBean.class);

    @EJB
    private ConfigurationService configurationService;

    /**
     * Currently unsupported - used for creating the login response when
     * application acts as the identity provider.
     * 
     * @param requestId
     * 
     * @return
     */
    @Override
    public String createSamlResponse(String requestId) {
        throw new UnsupportedOperationException(
                "Not supported, as OSCM does not act as Idp anymore.");
    }

    /**
     * Creates SAML LogoutRequest as per SAML 2.0 protocol specification.
     * 
     * @param idpSessionIndex
     *            - session index of the identity provider (receiver of the
     *            request)
     * @param nameID
     *            - unique identifier of the user registered with the identity
     *            provider.
     * 
     * @return
     * @throws SaaSApplicationException
     */
    @Override
    public String generateLogoutRequest(String idpSessionIndex, String nameID)
            throws SaaSApplicationException {
        try {
            String logoutURL = getLogoutURL();
            String keystorePath = getKeystorePath();
            String issuer = getIssuer();
            String keyAlias = getKeyAlias();
            LOGGER.logDebug("Im trying to generate SAML logout request with the following properties: "
                    + " logoutURL: "
                    + logoutURL
                    + " keystorePath: "
                    + keystorePath
                    + " keyAlias: "
                    + keyAlias
                    + " issuer: "
                    + issuer);
            return getRequest(logoutURL, nameID, FORMAT, idpSessionIndex,
                    keystorePath, issuer, keyAlias, getKeystorePass());
        } catch (XMLStreamException | IOException | GeneralSecurityException e) {
            throw new SaaSApplicationException(
                    "Exception during SAML logout URL generation.", e);
        }
    }

    public String getKeystorePass() {
        return configurationService.getVOConfigurationSetting(
                SSO_SIGNING_KEYSTORE_PASS, GLOBAL_CONTEXT).getValue();
    }

    public String getKeyAlias() {
        return configurationService.getVOConfigurationSetting(
                SSO_SIGNING_KEY_ALIAS, GLOBAL_CONTEXT).getValue();
    }

    public String getIssuer() {
        return configurationService.getVOConfigurationSetting(SSO_ISSUER_ID,
                GLOBAL_CONTEXT).getValue();
    }

    public String getKeystorePath() {
        return configurationService.getVOConfigurationSetting(
                SSO_SIGNING_KEYSTORE, GLOBAL_CONTEXT).getValue();
    }

    public String getLogoutURL() {
        return configurationService.getVOConfigurationSetting(SSO_LOGOUT_URL,
                GLOBAL_CONTEXT).getValue();
    }

    private String getRequest(String logoutUrl, String nameID, String format,
            String sessionIndex, String keyPath, String issuer,
            String keyAlias, String keystorePass) throws XMLStreamException,
            IOException, GeneralSecurityException, SaaSApplicationException {

        String issueInstant = getIssueDate();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

        writer.writeStartElement("saml2p", "LogoutRequest",
                "urn:oasis:names:tc:SAML:2.0:protocol");
        writer.writeNamespace("saml2p", "urn:oasis:names:tc:SAML:2.0:protocol");
        writer.writeAttribute("ID", "_" + UUID.randomUUID().toString());
        writer.writeAttribute("Version", "2.0");
        writer.writeAttribute("Destination", logoutUrl);
        writer.writeAttribute("IssueInstant", issueInstant + "Z");

        writer.writeStartElement("saml2", "Issuer",
                "urn:oasis:names:tc:SAML:2.0:assertion");
        writer.writeNamespace("saml2", "urn:oasis:names:tc:SAML:2.0:assertion");
        writer.writeCharacters(issuer);
        writer.writeEndElement();

        writer.writeStartElement("saml", "NameID",
                "urn:oasis:names:tc:SAML:2.0:assertion");
        writer.writeNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
        writer.writeAttribute("Format", format);
        writer.writeCharacters(nameID);
        writer.writeEndElement();

        writer.writeStartElement("saml2p", "SessionIndex",
                "urn:oasis:names:tc:SAML:2.0:protocol");
        writer.writeCharacters(sessionIndex);
        writer.writeEndElement();

        writer.writeEndElement();
        writer.flush();

        LOGGER.logDebug("The unsigned SAML envelope is: "
                + new String(baos.toByteArray(), UTF_8));

        // Compress the bytes
        ByteArrayOutputStream deflatedBytes = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        DeflaterOutputStream deflaterStream = new DeflaterOutputStream(
                deflatedBytes, deflater);
        deflaterStream.write(baos.toByteArray());
        deflaterStream.finish();

        // Base64 Encode the bytes
        byte[] encoded = Base64
                .encodeBase64Chunked(deflatedBytes.toByteArray());

        // URL Encode the bytes
        String encodedRequest = URLEncoder.encode(
                new String(encoded, Charset.forName(UTF_8)), UTF_8);
        String finalSignatureValue = "";

        // If a keyPath was provided, sign it!
        if (StringUtils.isNotEmpty(keyPath)) {
            String encodedSigAlg = URLEncoder.encode(
                    "http://www.w3.org/2000/09/xmldsig#rsa-sha1", UTF_8);

            Signature signature = Signature.getInstance("SHA1withRSA");

            String strSignature = "SAMLRequest=" + getRidOfCRLF(encodedRequest)
                    + "&SigAlg=" + encodedSigAlg;

            signature.initSign(SamlKeyLoader.loadPrivateKeyFromStore(keyPath,
                    keystorePass, keyAlias));
            signature.update(strSignature.getBytes(UTF_8));

            String encodedSignature = URLEncoder.encode(
                    Base64.encodeBase64String(signature.sign()), UTF_8);

            finalSignatureValue = "&SigAlg=" + encodedSigAlg + "&Signature="
                    + encodedSignature;
        }
        String appender = "?";

        if (logoutUrl.indexOf("?") >= 0) {
            appender = "&";
        }

        String fullLogoutURL = logoutUrl + appender + "SAMLRequest="
                + getRidOfCRLF(encodedRequest) + finalSignatureValue;

        LOGGER.logDebug("The logoutURL generated is: " + fullLogoutURL);
        return fullLogoutURL;
    }

    private String getIssueDate() {
        SimpleDateFormat simpleDf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        return simpleDf.format(new Date());
    }

    private String getRidOfCRLF(String input) {
        String lf = "%0D";
        String cr = "%0A";
        String now = lf;

        int index = input.indexOf(now);
        StringBuffer r = new StringBuffer();

        while (index != -1) {
            r.append(input.substring(0, index));
            input = input.substring(index + 3, input.length());

            if (now.equals(lf)) {
                now = cr;
            } else {
                now = lf;
            }

            index = input.indexOf(now);
        }
        return r.toString();
    }
}
