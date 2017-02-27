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

public class TestVOException extends BaseVO {

    private static final long serialVersionUID = 8050709752936485014L;

    private String name;

    public String getName() {
        throw new UnsupportedOperationException(name);
    }

    public void setName(String name) {
        this.name = name;
    }

}
