/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                  
 *  Creation Date: 22.07.15 13:39
 *
 *******************************************************************************/

package org.oscm.converter.strategy.domain;

import org.oscm.domobjects.PlatformUser;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.vo.VOUser;

public class ToDomUserStrategy implements
        ConversionStrategy<VOUser, PlatformUser> {

    @Override
    public PlatformUser convert(VOUser voUser) {
        if (voUser == null) {
            return null;
        }

        PlatformUser user = new PlatformUser();

        user.setKey(voUser.getKey());
        user.setUserId(voUser.getUserId());

        return user;
    }
}
