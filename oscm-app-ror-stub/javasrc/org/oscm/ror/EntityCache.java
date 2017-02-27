/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 03.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.oscm.ror.api.CreateLPlatform;

/**
 * @author kulle
 * 
 */
@Singleton
public class EntityCache {

    private Map<Type, Set<Object>> cache = new HashMap<Type, Set<Object>>();

    public <T> void put(Class<T> type, Object object) {
        if (cache.containsKey(type)) {
            cache.get(type).add(object);
        } else {
            Set<Object> entities = new HashSet<Object>();
            entities.add(object);
            cache.put(type, entities);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> findEntities(Class<T> type) {
        return (Set<T>) cache.get(type);
    }

    public CreateLPlatform findLplatform(String lplatformId) {
        Set<CreateLPlatform> lplatforms = findEntities(CreateLPlatform.class);
        for (CreateLPlatform lplatform : lplatforms) {
            if (lplatform.getLplatformId().equals(lplatformId)) {
                return lplatform;
            }
        }
        return null;
    }
}
