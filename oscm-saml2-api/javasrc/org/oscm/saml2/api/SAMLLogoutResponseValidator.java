/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                             
 *
 *  Creation Date: Jun 01, 2016
 *
 *******************************************************************************/

package org.oscm.saml2.api;

import java.util.Arrays;
import java.util.List;

import org.oscm.internal.types.exception.SAML2StatusCodeInvalidException;

/**
 * Class for validating the status code from a SAML LogoutResponse received from an IdP.
 *
 * @author mgrubski
 */
public class SAMLLogoutResponseValidator {

    SAMLResponseExtractor samlResponseExtractor;

    private final List<String> saml2ErrorStatusCodes = Arrays
            .asList(new String[] { "Requester", "Responder", "VersionMismatch",
                    "AuthnFailed", "InvalidAttrNameOrValue",
                    "InvalidNameIDPolicy", "NoAuthnContext", "NoAvailableIDP",
                    "NoPassive", "NoSupportedIDP", "ProxyCountExceeded",
                    "RequestDenied", "RequestUnsupported",
                    "RequestVersionDeprecated", "RequestVersionTooHigh",
                    "RequestVersionTooLow", "ResourceNotRecognized",
                    "TooManyResponses", "UnknownAttrProfile",
                    "UnknownPrincipal", "UnsupportedBinding" });

    public boolean responseStatusCodeSuccessful(String encodedSamlResponse)
            throws SAML2StatusCodeInvalidException {
        samlResponseExtractor = new SAMLResponseExtractor();
        final String statusCode = samlResponseExtractor.getSAMLLogoutResponseStatusCode(
                encodedSamlResponse);
        if (isSuccessful(statusCode)) {
            return true;
        } else if (errorInLogoutResponse(statusCode)) {
            return false;
        } else {
            throw new SAML2StatusCodeInvalidException();
        }
    }

    private boolean errorInLogoutResponse(String samlLogoutResponseStatus) {
        for (String status : saml2ErrorStatusCodes) {
            if (status.equalsIgnoreCase(samlLogoutResponseStatus)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuccessful(String statusCode) {
        if (statusCode.equalsIgnoreCase("Success")
                || statusCode.equalsIgnoreCase("PartialLogout")) {
            return true;
        }
        return false;
    }

}