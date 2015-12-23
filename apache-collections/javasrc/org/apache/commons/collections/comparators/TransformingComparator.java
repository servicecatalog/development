/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015
 *
 *  Creation Date: 23.11.15 12:15
 *
 ******************************************************************************/
package org.apache.commons.collections.comparators;

import java.util.Comparator;

import org.apache.commons.collections.Transformer;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * {@link Comparator#compare(Object,Object) compare} method.
 * 
 * @since Commons Collections 2.0 (?)
 * @version $Revision: 646777 $ $Date: 2008-04-10 13:33:15 +0100 (Thu, 10 Apr 2008) $
 * 
 * @see org.apache.commons.collections.Transformer
 * @see ComparableComparator
 */
public class TransformingComparator implements Comparator {
    
    /** The decorated comparator. */
    protected Comparator decorated;
    /** The transformer being used. */    
    protected Transformer transformer;

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the given Transformer and a 
     * {@link ComparableComparator ComparableComparator}.
     * 
     * @param transformer what will transform the arguments to <code>compare</code>
     */
    public TransformingComparator(Transformer transformer) {
        this(transformer, new ComparableComparator());
    }

    /**
     * Constructs an instance with the given Transformer and Comparator.
     * 
     * @param transformer  what will transform the arguments to <code>compare</code>
     * @param decorated  the decorated Comparator
     */
    public TransformingComparator(Transformer transformer, Comparator decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the result of comparing the values from the transform operation.
     * 
     * @param obj1  the first object to transform then compare
     * @param obj2  the second object to transform then compare
     * @return negative if obj1 is less, positive if greater, zero if equal
     */
    public int compare(Object obj1, Object obj2) {
        Object value1 = this.transformer.transform(obj1);
        Object value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }

}

