/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                  
 *  Creation Date: 20.07.15 17:30
 *
 *******************************************************************************/

package org.oscm.converter.api;

import java.util.ArrayList;
import java.util.List;

import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.UserGroup;
import org.oscm.paginator.Pagination;
import org.oscm.converter.strategy.ConversionFactory;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.converter.strategy.api.ToExtPaginationStrategy;
import org.oscm.converter.strategy.api.ToExtUnitStrategy;
import org.oscm.converter.strategy.api.ToExtUserStrategy;
import org.oscm.converter.strategy.domain.ToCommonPaginationStrategy;
import org.oscm.converter.strategy.domain.ToDomUnitStrategy;
import org.oscm.converter.strategy.domain.ToDomUserStrategy;
import org.oscm.vo.VOOrganizationalUnit;
import org.oscm.vo.VOUser;

/**
 * Generic converter used for object conversion between types in order to add
 * new object converter. Register it using
 * {@link ConversionFactory#register(Class, Class, ConversionStrategy)} and add
 * new conversion strategy class that implements {@link ConversionStrategy}
 * interface.
 */
public class Converter {

    /**
     * Register here new object converter with
     * {@link ConversionFactory#register(Class, Class, ConversionStrategy)}
     */
    static {
        ConversionFactory.register(UserGroup.class, VOOrganizationalUnit.class,
                new ToExtUnitStrategy());
        ConversionFactory.register(VOOrganizationalUnit.class, UserGroup.class,
                new ToDomUnitStrategy());

        ConversionFactory.register(PlatformUser.class, VOUser.class,
                new ToExtUserStrategy());
        ConversionFactory.register(VOUser.class, PlatformUser.class,
                new ToDomUserStrategy());

        ConversionFactory.register(Pagination.class,
                org.oscm.paginator.Pagination.class,
                new ToExtPaginationStrategy());
        ConversionFactory.register(org.oscm.paginator.Pagination.class,
                Pagination.class, new ToCommonPaginationStrategy());
    }

    private Converter() {
    }

    /**
     * If supported conversion strategy is found converts object from one type
     * to another.
     * 
     * @param object
     *            Object to convert
     * @param from
     *            - Object class
     * @param to
     *            - Target Object class
     * @param <From>
     * @param <To>
     * @return - Converted object to target class
     */
    public static <From, To> To convert(From object, Class<From> from,
            Class<To> to) {
        ConversionStrategy<From, To> converter = ConversionFactory
                .getConverter(from, to);
        return converter.convert(object);
    }

    /**
     * Allows conversion of objects in list from list of one type to another
     * {@link #convert(Object, Class, Class)}
     * 
     * @param list
     * @param from
     * @param to
     * @param <From>
     * @param <To>
     * @return
     */
    public static <From, To> List<To> convertList(List<From> list,
            Class<From> from, Class<To> to) {
        ConversionStrategy<From, To> converter = ConversionFactory
                .getConverter(from, to);

        List<To> result = new ArrayList<>();
        for (From e : list) {
            result.add(converter.convert(e));
        }

        return result;
    }
}
