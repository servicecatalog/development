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

public class TestVO extends BaseVO {

    private static final long serialVersionUID = 8050709752936485014L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
