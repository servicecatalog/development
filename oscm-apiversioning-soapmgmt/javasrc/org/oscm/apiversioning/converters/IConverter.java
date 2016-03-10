/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jan 22, 2015                                                     
 *                                                                              
 *******************************************************************************/

package org.oscm.apiversioning.converters;

import javax.xml.soap.SOAPException;

/**
 * @author qiu
 * 
 */
public interface IConverter {

    public void exec(ConverterContext context, ConverterChain chain)
            throws SOAPException;

}
