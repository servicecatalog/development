/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 16.07.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mocksts;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.STSAttributeProvider;

/**
 * This implementation of STSAttributeProvider is invoked by Metro when a SAML
 * assertion is generated. It adds the user name given for authentication to the
 * NameID and AttributeStatement elements of the assertion.
 * 
 * Must be referred to in META-INF/services.
 * 
 * @author roderus
 * 
 */
public class MockSTSAttributeProvider implements STSAttributeProvider {

    @Override
    public Map<QName, List<String>> getClaimedAttributes(Subject subject,
            String appliesTo, String tokenType, Claims claims) {
        String name = null;

        Set<Principal> principals = subject.getPrincipals();
        if (principals != null) {
            final Iterator<Principal> iterator = principals.iterator();
            while (iterator.hasNext()) {
                String cnName = principals.iterator().next().getName();
                int pos = cnName.indexOf("=");
                name = cnName.substring(pos + 1);
                break;
            }
        }

        Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

        List<String> nameIdAttrs = new ArrayList<String>();
        QName nameIdQName = new QName(STSAttributeProvider.NAME_IDENTIFIER);
        nameIdAttrs.add(name);
        attrs.put(nameIdQName, nameIdAttrs);

        nameIdAttrs = new ArrayList<String>();
        nameIdQName = new QName("dummy_id1");
        nameIdAttrs.add("test_dummy_attribute1");
        attrs.put(nameIdQName, nameIdAttrs);

        nameIdAttrs = new ArrayList<String>();
        nameIdQName = new QName("userid");
        nameIdAttrs.add(name);
        attrs.put(nameIdQName, nameIdAttrs);

        nameIdAttrs = new ArrayList<String>();
        nameIdQName = new QName("dummy_id2");
        nameIdAttrs.add("test_dummy_attribute2");
        attrs.put(nameIdQName, nameIdAttrs);

        // claims not considered here

        return attrs;
    }
}
