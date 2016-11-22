/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.11.16 10:01
 *
 ******************************************************************************/

package org.oscm.app.converter;

import java.util.List;

import org.oscm.app.v1_0.data.LocalizedText;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AbortException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.app.v1_0.exceptions.ControllerLookupException;
import org.oscm.app.v1_0.exceptions.InstanceExistsException;
import org.oscm.app.v1_0.exceptions.InstanceNotAliveException;
import org.oscm.app.v1_0.exceptions.ObjectNotFoundException;
import org.oscm.app.v1_0.exceptions.SuspendException;

public class APPInterfaceExceptionConverter {
    private APPInterfaceDataConverter dataConverter = new APPInterfaceDataConverter();

    public org.oscm.app.v2_0.exceptions.AbortException convertToNew(
            AbortException ex) {
        if (ex == null) {
            return null;
        }
        List<LocalizedText> providerMessages = ex.getProviderMessages();
        List<org.oscm.app.v2_0.data.LocalizedText> newProviderMsgs = dataConverter
                .convertToNewLocalizedTexts(providerMessages);
        org.oscm.app.v2_0.exceptions.AbortException newEx = new org.oscm.app.v2_0.exceptions.AbortException(
                retrieveNewLocalizedMessages(ex), newProviderMsgs);
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.APPlatformException convertToNew(
            APPlatformException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.APPlatformException newEx = new org.oscm.app.v2_0.exceptions.APPlatformException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.AuthenticationException convertToNew(
            AuthenticationException ex) {
        if (ex == null) {
            return null;
        }
        String oldMessage = ex.getMessage();
        org.oscm.app.v2_0.exceptions.AuthenticationException newEx = new org.oscm.app.v2_0.exceptions.AuthenticationException(
                oldMessage, ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.ConfigurationException convertToNew(
            ConfigurationException ex) {
        if (ex == null) {
            return null;
        }
        String oldMessage = ex.getMessage();
        String oldAffectedKey = ex.getAffectedKey();
        org.oscm.app.v2_0.exceptions.ConfigurationException newEx = new org.oscm.app.v2_0.exceptions.ConfigurationException(
                oldMessage, oldAffectedKey);
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.ControllerLookupException convertToNew(
            ControllerLookupException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.ControllerLookupException newEx = new org.oscm.app.v2_0.exceptions.ControllerLookupException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.InstanceExistsException convertToNew(
            InstanceExistsException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.InstanceExistsException newEx = new org.oscm.app.v2_0.exceptions.InstanceExistsException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.InstanceNotAliveException convertToNew(
            InstanceNotAliveException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.InstanceNotAliveException newEx = new org.oscm.app.v2_0.exceptions.InstanceNotAliveException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.ObjectNotFoundException convertToNew(
            ObjectNotFoundException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.ObjectNotFoundException newEx = new org.oscm.app.v2_0.exceptions.ObjectNotFoundException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public org.oscm.app.v2_0.exceptions.SuspendException convertToNew(
            SuspendException ex) {
        if (ex == null) {
            return null;
        }
        org.oscm.app.v2_0.exceptions.SuspendException newEx = new org.oscm.app.v2_0.exceptions.SuspendException(
                retrieveNewLocalizedMessages(ex), ex.getCause());
        decorateNewException(ex, newEx);
        return newEx;
    }

    public AbortException convertToOld(
            org.oscm.app.v2_0.exceptions.AbortException ex) {
        if (ex == null) {
            return null;
        }
        List<org.oscm.app.v2_0.data.LocalizedText> providerMessages = ex
                .getProviderMessages();
        List<org.oscm.app.v2_0.data.LocalizedText> localizedMessages = ex
                .getLocalizedMessages();
        List<LocalizedText> newLocalizedTexts = dataConverter
                .convertToOldLocalizedTexts(localizedMessages);
        List<LocalizedText> newProviderMessages = dataConverter
                .convertToOldLocalizedTexts(providerMessages);
        AbortException oldEx = new AbortException(newLocalizedTexts,
                newProviderMessages);
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public APPlatformException convertToOld(
            org.oscm.app.v2_0.exceptions.APPlatformException ex) {
        if (ex == null) {
            return null;
        }
        List<org.oscm.app.v2_0.data.LocalizedText> newMessages = ex
                .getLocalizedMessages();
        List<LocalizedText> oldMessages = dataConverter
                .convertToOldLocalizedTexts(newMessages);
        APPlatformException oldEx = new APPlatformException(oldMessages,
                ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public AuthenticationException convertToOld(
            org.oscm.app.v2_0.exceptions.AuthenticationException ex) {
        if (ex == null) {
            return null;
        }
        String newMessage = ex.getMessage();
        AuthenticationException oldEx = new AuthenticationException(newMessage,
                ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public ConfigurationException convertToOld(
            org.oscm.app.v2_0.exceptions.ConfigurationException ex) {
        if (ex == null) {
            return null;
        }
        String newMessage = ex.getMessage();
        String newAffectedKey = ex.getAffectedKey();
        ConfigurationException oldEx = new ConfigurationException(newMessage,
                newAffectedKey);
        return oldEx;
    }

    public ControllerLookupException convertToOld(
            org.oscm.app.v2_0.exceptions.ControllerLookupException ex) {
        if (ex == null) {
            return null;
        }
        List<LocalizedText> oldMessages = retrieveOldLocalizedMessages(ex);
        ControllerLookupException oldEx = new ControllerLookupException(
                oldMessages, ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public InstanceExistsException convertToOld(
            org.oscm.app.v2_0.exceptions.InstanceExistsException ex) {
        if (ex == null) {
            return null;
        }
        InstanceExistsException oldEx = new InstanceExistsException(
                retrieveOldLocalizedMessages(ex), ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public InstanceNotAliveException convertToOld(
            org.oscm.app.v2_0.exceptions.InstanceNotAliveException ex) {
        if (ex == null) {
            return null;
        }
        InstanceNotAliveException oldEx = new InstanceNotAliveException(
                retrieveOldLocalizedMessages(ex), ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public ObjectNotFoundException convertToOld(
            org.oscm.app.v2_0.exceptions.ObjectNotFoundException ex) {
        if (ex == null) {
            return null;
        }
        ObjectNotFoundException oldEx = new ObjectNotFoundException(
                retrieveOldLocalizedMessages(ex), ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    public SuspendException convertToOld(
            org.oscm.app.v2_0.exceptions.SuspendException ex) {
        if (ex == null) {
            return null;
        }
        SuspendException oldEx = new SuspendException(
                retrieveOldLocalizedMessages(ex), ex.getCause());
        decorateOldException(ex, oldEx);
        return oldEx;
    }

    private void decorateNewException(APPlatformException oldEx,
            org.oscm.app.v2_0.exceptions.APPlatformException newEx) {
        newEx.setChangedParameters(dataConverter.convertToNew(oldEx
                .getChangedParameters()));
        if (oldEx.getStackTrace() != null) {
            newEx.setStackTrace(oldEx.getStackTrace());
        }
    }

    private void decorateOldException(
            org.oscm.app.v2_0.exceptions.APPlatformException ex,
            APPlatformException oldEx) {
        oldEx.setChangedParameters(dataConverter.convertToOld(ex
                .getChangedParameters()));
        if (ex.getStackTrace() != null) {
            oldEx.setStackTrace(ex.getStackTrace());
        }
    }

    private List<org.oscm.app.v2_0.data.LocalizedText> retrieveNewLocalizedMessages(
            APPlatformException ex) {
        List<LocalizedText> oldMessages = ex.getLocalizedMessages();
        return dataConverter.convertToNewLocalizedTexts(oldMessages);
    }

    private List<LocalizedText> retrieveOldLocalizedMessages(
            org.oscm.app.v2_0.exceptions.APPlatformException ex) {
        List<org.oscm.app.v2_0.data.LocalizedText> newMessages = ex
                .getLocalizedMessages();
        return dataConverter.convertToOldLocalizedTexts(newMessages);
    }
}
