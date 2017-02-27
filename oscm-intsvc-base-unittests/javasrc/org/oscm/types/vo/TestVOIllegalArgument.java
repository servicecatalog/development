/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.types.vo;

import org.oscm.internal.vo.BaseVO;

/**
 * Test value object
 * 
 * @author pock
 */

public class TestVOIllegalArgument extends BaseVO {

    private static final long serialVersionUID = 8050709752936485014L;

    public String name2;

    private String name;

    public String getName(String name) {
        name2 = name;
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
