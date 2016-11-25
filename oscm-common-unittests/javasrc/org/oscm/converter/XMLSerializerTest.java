/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.oscm.encrypter.AESEncrypter;
import org.oscm.internal.types.enumtypes.EventType;
import org.oscm.internal.types.enumtypes.OfferingType;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ParameterModificationType;
import org.oscm.internal.types.enumtypes.ParameterType;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.types.enumtypes.PaymentCollectionType;
import org.oscm.internal.types.enumtypes.PricingPeriod;
import org.oscm.internal.types.enumtypes.Salutation;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.SessionType;
import org.oscm.internal.types.enumtypes.SettingType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.TriggerProcessStatus;
import org.oscm.internal.types.enumtypes.TriggerType;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOUda;
import org.oscm.internal.vo.VOUdaDefinition;
import org.oscm.types.enumtypes.TriggerProcessParameterName;

@SuppressWarnings("unchecked")
public class XMLSerializerTest {
    @Test
    public void toXml() {
        // given
        Map<String, String> map = new HashMap<>();
        map.put("a", "a1");
        map.put("b", "b1");

        // when
        String xml = XMLSerializer.toXml(map);

        // then
        assertTrue(xml.contains("a"));
        assertTrue(xml.contains("a1"));
        assertTrue(xml.contains("b"));
        assertTrue(xml.contains("b1"));
    }

    @Test
    public void toObject() {
        // given
        Map<String, String> map = new HashMap<>();
        map.put("a", "a1");
        map.put("b", "b1");
        String xml = XMLSerializer.toXml(map);

        // when
        Map<String, String> map2 = (Map<String, String>) XMLSerializer
                .toObject(xml);

        // then
        assertEquals(map.size(), map2.size());
        assertTrue(map2.containsKey("a"));
        assertTrue(map2.containsKey("b"));
        assertEquals("a1", map2.get("a"));
        assertEquals("b1", map2.get("b"));
    }

    @Test
    public void testEncryption() throws Exception {

        String value = "test42";

        Class<?> enumArray[] = { EventType.class, OrganizationRoleType.class,
                ParameterType.class, ParameterValueType.class,
                PaymentCollectionType.class, PricingPeriod.class,
                ServiceAccessType.class, ServiceStatus.class, Salutation.class,
                SessionType.class, SettingType.class, SubscriptionStatus.class,
                TriggerType.class, TriggerProcessStatus.class,
                TriggerProcessParameterName.class, UserAccountStatus.class,
                UserRoleType.class, UdaConfigurationType.class,
                ParameterModificationType.class, OfferingType.class };

        AESEncrypter.generateKey();

        VOUdaDefinition udaDef1 = new VOUdaDefinition();
        udaDef1.setEncrypted(true);
        VOUda uda1 = new VOUda();
        uda1.setUdaDefinition(udaDef1);
        uda1.setUdaValue(value);

        VOUdaDefinition udaDef2 = new VOUdaDefinition();
        udaDef2.setEncrypted(false);
        VOUda uda2 = new VOUda();
        uda2.setUdaDefinition(udaDef2);
        uda2.setUdaValue(value);

        VOService service = new VOService();
        VOParameterDefinition paramDef1 = new VOParameterDefinition();
        paramDef1.setValueType(ParameterValueType.PWD);
        VOParameter param1 = new VOParameter();
        param1.setParameterDefinition(paramDef1);
        param1.setValue(value);
        List<VOParameter> listP = new ArrayList<>();
        listP.add(param1);
        service.setParameters(listP);
        service.setName("name");

        VOParameterDefinition paramDef2 = new VOParameterDefinition();
        paramDef2.setValueType(ParameterValueType.PWD);
        VOParameter param2 = new VOParameter();
        param2.setParameterDefinition(paramDef2);
        param2.setValue(value);

        List<VOUda> list1 = Arrays.asList(uda1);
        List<VOUda> list2 = Arrays.asList(uda2);
        List<VOParameter> list3 = Arrays.asList(param2);

        String xml1 = XMLSerializer.toXml(list1, enumArray);
        String xml2 = XMLSerializer.toXml(list2, enumArray);
        String xml3 = XMLSerializer.toXml(service, enumArray);
        String xml4 = XMLSerializer.toXml(list3, enumArray);

        assertTrue(xml1.contains(AESEncrypter.encrypt(value)));
        assertTrue(xml2.contains(value));
        assertTrue(xml3.contains(AESEncrypter.encrypt(value)));
        assertTrue(xml4.contains(AESEncrypter.encrypt(value)));

        VOUda result1 = ((List<VOUda>) XMLSerializer.toObject(xml1)).get(0);
        VOUda result2 = ((List<VOUda>) XMLSerializer.toObject(xml2)).get(0);
        VOService result3 = (VOService) XMLSerializer.toObject(xml3);
        VOParameter result4 = ((List<VOParameter>) XMLSerializer.toObject(xml4))
                .get(0);

        assertEquals(value, result1.getUdaValue());
        assertEquals(value, result2.getUdaValue());
        assertEquals(value, result3.getParameters().get(0).getValue());
        assertEquals(value, result4.getValue());

    }
}
