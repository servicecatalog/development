/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 12.12.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects.similarity;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.index.FieldInvertState;
import org.junit.Test;

/**
 * @author barzu
 */
public class CustomSimilarityTest {

    @Test
    public void computeNorm() {
        // given
        FieldInvertState state = new FieldInvertState();
        state.setBoost(501F);

        // when
        float norm = new CustomSimilarity().computeNorm(null, state);

        // then
        assertEquals(Float.valueOf(501L), Float.valueOf(norm));
    }

    @Test(expected = NullPointerException.class)
    public void computeNorm_nullState() {
        new CustomSimilarity().computeNorm(null, null);
    }

    @Test
    public void tf() {
        // when
        float tf = new CustomSimilarity().tf(501F);

        // then
        assertEquals(Float.valueOf(1F), Float.valueOf(tf));
    }

    @Test
    public void idf() {
        // when
        float idf = new CustomSimilarity().idf(501, 502);

        // then
        assertEquals(Float.valueOf(1F), Float.valueOf(idf));
    }

}
