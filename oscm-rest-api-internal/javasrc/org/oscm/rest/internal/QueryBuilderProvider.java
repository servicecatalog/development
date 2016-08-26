package org.oscm.rest.internal;

import java.util.HashMap;
import java.util.Map;

import org.oscm.domobjects.Organization;
import org.oscm.rest.internal.builder.DefaultQueryBuilder;
import org.oscm.rest.internal.builder.OrganizationQueryBuilder;

public class QueryBuilderProvider {

    static final Map<Class<?>, QueryBuilder> BUILDERS = new HashMap<Class<?>, QueryBuilder>();
    static final DefaultQueryBuilder DEFAULT_BUILDER = new DefaultQueryBuilder();

    static {
        BUILDERS.put(Organization.class, new OrganizationQueryBuilder());
    }

    public static final QueryBuilder getFor(Class<?> clazz) {
        if (BUILDERS.containsKey(clazz)) {
            return BUILDERS.get(clazz);
        }
        return DEFAULT_BUILDER;
    }
}
