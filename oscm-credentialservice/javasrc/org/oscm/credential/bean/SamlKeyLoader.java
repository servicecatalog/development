/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.credential.bean;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.saml.tools.Saml20KeyLoader;

/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: Apr 20, 2011                                                      
 *                                                                              
 *******************************************************************************/

/**
 * Locates and loads the private key and the public certificate for signing SAMl
 * responses.
 * 
 * @author barzu
 */
class SamlKeyLoader extends Saml20KeyLoader {


    public SamlKeyLoader(ConfigurationServiceLocal configService) {
        super(configService);
    }

    @Override
    protected ConfigurationKey getPrivateKeyFilePath() {
        return ConfigurationKey.IDP_PRIVATE_KEY_FILE_PATH;
    }

    @Override
    protected ConfigurationKey getPublicCertificateFilePath() {
        return ConfigurationKey.IDP_PUBLIC_CERTIFICATE_FILE_PATH;
    }

}
