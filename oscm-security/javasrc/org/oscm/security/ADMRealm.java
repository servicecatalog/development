/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: pock                                                     
 *                                                                              
 *  Creation Date: 14.01.2010                                             
 *                                                                              
 *******************************************************************************/

package org.oscm.security;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;

/**
 * Glassfish realm wrapper for supporting authentication on the base of the
 * local LDAP or an organization specific remote LDAP.
 * 
 */
public class ADMRealm extends AppservRealm {

    public static final String SUBST_SUBJECT_NAME = "%s";

    private static final String DEFAULT_CONTEXT_NAME = "bssRealm";

    private static final String AUTH_TYPE = "Federation";

    private ADMRealmImpl realmImpl;

    /**
     * This method is invoked during server startup when the realm is initially
     * loaded. The realm can do any initialization it needs in this method. If
     * the method returns without throwing an exception, J2EE Application Server
     * assumes the realm is ready to service authentication requests. If an
     * exception is thrown, the realm is disabled, check the server.log for
     * messages.
     * 
     * @param props
     *            contains the properties defined for this realm in domain.xml.
     * @throws BadRealmException
     * @throws NoSuchRealmException
     */
    @Override
    public void init(Properties props) throws BadRealmException,
            NoSuchRealmException {

        super.init(props);

        /*
         * Set the jaas context, otherwise server doesn't indentify the login
         * module. jaas-context is the property specified in domain.xml and is
         * the name corresponding to LoginModule config/login.conf
         */
        String jaasCtx = props.getProperty(AppservRealm.JAAS_CONTEXT_PARAM);
        if (jaasCtx == null) {
            jaasCtx = DEFAULT_CONTEXT_NAME;
        }
        this.setProperty(AppservRealm.JAAS_CONTEXT_PARAM, jaasCtx);

        try {
            realmImpl = new ADMRealmImpl(_logger, new InitialContext());
        } catch (NamingException e) {
            throw new BadRealmException(e);
        }
    }

    /**
     * Return a short description supported authentication by this realm.
     * 
     * @return Description of the kind of authentication that is directly
     *         supported by this realm.
     */
    @Override
    public String getAuthType() {
        return AUTH_TYPE;
    }

    /**
     * Returns names of all the groups in this particular realm.
     * 
     * @return enumeration of group names (strings)
     * 
     */
    @Override
    public Enumeration<String> getGroupNames() throws BadRealmException {
        return Collections.enumeration(Arrays
                .asList(ADMRealmImpl.GROUPLIST_ADMIN));
    }

    /**
     * Returns enumeration of groups that a particular user belongs to.
     * 
     * @return enumeration of groups that a particular user belongs to.
     * @throws NoSuchUserException
     *             if the userKey doesn't exist in the database
     */
    @Override
    public Enumeration<String> getGroupNames(String userKey)
            throws NoSuchUserException {
        try {
            return realmImpl.getGroupNames(userKey);
        } catch (SQLException e) {
            throw new NoSuchUserException(e.getMessage());
        } catch (NamingException e) {
            throw new NoSuchUserException(e.getMessage());
        }
    }

    /**
     * Checks the authentication of a user and returns the groups it belongs to.
     * 
     * @return groups that this particular user belongs to
     * @throws LoginException
     *             if the login failed.
     */
    public String[] authenticateUser(final String userKey, String password)
            throws LoginException {
        return realmImpl.authenticateUser(userKey, password);
    }

}
