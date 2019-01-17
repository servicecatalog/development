/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2019                                           
 *                                                                                                                                 
 *  Creation Date: 16.01.2019                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.identityservice.bean;

import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.RoleAssignment;

/**
 * @author goebel
 *
 */
class UserChecksum {

    static long of(PlatformUser pUser) {

        // TODO avoid with factory
        if (pUser == null) {
            return of("null");
        }
        StringBuffer sb = new StringBuffer();
        sb.append(pUser.getUserId() + ",");
        sb.append(pUser.getEmail() + ",");
        if (pUser.getSalutation() != null)
            sb.append(pUser.getSalutation().name() + ",");
        sb.append(pUser.getFirstName() + ",");
        sb.append(pUser.getLastName() + ",");
        sb.append(pUser.getRealmUserId() + ",");
        sb.append(pUser.getLocale() + ",");
        sb.append(pUser.getAddress() + ",");
        sb.append(pUser.getPhone() + ",");
        sb.append(pUser.getTenantId() + ",");

        Iterator<RoleAssignment> roleIterator = pUser.getAssignedRoles()
                .iterator();
        while (roleIterator.hasNext()) {
            RoleAssignment roleAssignment = roleIterator.next();
            String r = roleAssignment.getRole().getRoleName().toString();
            sb.append(r + ",");
        }
        String user = sb.toString();
        return of(user);
    }

    static long of(String input) {
        byte bytes[] = input.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
