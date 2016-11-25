
/**
 * ProvisioningServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */
package org.oscm.example.server;

/**
 * ProvisioningServiceMessageReceiverInOut message receiver
 */

public class ProvisioningServiceMessageReceiverInOut
        extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {

    @Override
    public void invokeBusinessLogic(
            org.apache.axis2.context.MessageContext msgContext,
            org.apache.axis2.context.MessageContext newMsgContext)
            throws org.apache.axis2.AxisFault {

        try {

            // get the implementati on class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            ProvisioningServiceSkeleton skel = (ProvisioningServiceSkeleton) obj;
            // Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;
            // Find the axisOperation that has been set by the Dispatch phase.
            org.apache.axis2.description.AxisOperation op = msgContext
                    .getOperationContext().getAxisOperation();
            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                        "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            java.lang.String methodName;
            if ((op.getName() != null)
                    && ((methodName = org.apache.axis2.util.JavaUtils
                            .xmlNameToJavaIdentifier(
                                    op.getName().getLocalPart())) != null)) {

                if ("deleteUsers".equals(methodName)) {

                    org.oscm.xsd.DeleteUsersResponseE deleteUsersResponse57 = null;
                    org.oscm.xsd.DeleteUsersE wrappedParam = (org.oscm.xsd.DeleteUsersE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.DeleteUsersE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    deleteUsersResponse57 =

                            skel.deleteUsers(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            deleteUsersResponse57, false);
                } else

                if ("createUsers".equals(methodName)) {

                    org.oscm.xsd.CreateUsersResponseE createUsersResponse59 = null;
                    org.oscm.xsd.CreateUsersE wrappedParam = (org.oscm.xsd.CreateUsersE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.CreateUsersE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    createUsersResponse59 =

                            skel.createUsers(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            createUsersResponse59, false);
                } else

                if ("upgradeSubscription".equals(methodName)) {

                    org.oscm.xsd.UpgradeSubscriptionResponseE upgradeSubscriptionResponse61 = null;
                    org.oscm.xsd.UpgradeSubscriptionE wrappedParam = (org.oscm.xsd.UpgradeSubscriptionE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.UpgradeSubscriptionE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    upgradeSubscriptionResponse61 =

                            skel.upgradeSubscription(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            upgradeSubscriptionResponse61, false);
                } else

                if ("asyncCreateInstance".equals(methodName)) {

                    org.oscm.xsd.AsyncCreateInstanceResponseE asyncCreateInstanceResponse63 = null;
                    org.oscm.xsd.AsyncCreateInstanceE wrappedParam = (org.oscm.xsd.AsyncCreateInstanceE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.AsyncCreateInstanceE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    asyncCreateInstanceResponse63 =

                            skel.asyncCreateInstance(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            asyncCreateInstanceResponse63, false);
                } else

                if ("createInstance".equals(methodName)) {

                    org.oscm.xsd.CreateInstanceResponseE createInstanceResponse65 = null;
                    org.oscm.xsd.CreateInstanceE wrappedParam = (org.oscm.xsd.CreateInstanceE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.CreateInstanceE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    createInstanceResponse65 =

                            skel.createInstance(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            createInstanceResponse65, false);
                } else

                if ("sendPing".equals(methodName)) {

                    org.oscm.xsd.SendPingResponseE sendPingResponse67 = null;
                    org.oscm.xsd.SendPingE wrappedParam = (org.oscm.xsd.SendPingE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.SendPingE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    sendPingResponse67 =

                            skel.sendPing(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            sendPingResponse67, false);
                } else

                if ("deleteInstance".equals(methodName)) {

                    org.oscm.xsd.DeleteInstanceResponseE deleteInstanceResponse69 = null;
                    org.oscm.xsd.DeleteInstanceE wrappedParam = (org.oscm.xsd.DeleteInstanceE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.DeleteInstanceE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    deleteInstanceResponse69 =

                            skel.deleteInstance(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            deleteInstanceResponse69, false);
                } else

                if ("updateUsers".equals(methodName)) {

                    org.oscm.xsd.UpdateUsersResponseE updateUsersResponse71 = null;
                    org.oscm.xsd.UpdateUsersE wrappedParam = (org.oscm.xsd.UpdateUsersE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.UpdateUsersE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    updateUsersResponse71 =

                            skel.updateUsers(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            updateUsersResponse71, false);
                } else

                if ("modifySubscription".equals(methodName)) {

                    org.oscm.xsd.ModifySubscriptionResponseE modifySubscriptionResponse73 = null;
                    org.oscm.xsd.ModifySubscriptionE wrappedParam = (org.oscm.xsd.ModifySubscriptionE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.ModifySubscriptionE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    modifySubscriptionResponse73 =

                            skel.modifySubscription(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            modifySubscriptionResponse73, false);
                } else

                if ("asyncUpgradeSubscription".equals(methodName)) {

                    org.oscm.xsd.AsyncUpgradeSubscriptionResponseE asyncUpgradeSubscriptionResponse75 = null;
                    org.oscm.xsd.AsyncUpgradeSubscriptionE wrappedParam = (org.oscm.xsd.AsyncUpgradeSubscriptionE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.AsyncUpgradeSubscriptionE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    asyncUpgradeSubscriptionResponse75 =

                            skel.asyncUpgradeSubscription(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            asyncUpgradeSubscriptionResponse75, false);
                } else

                if ("asyncModifySubscription".equals(methodName)) {

                    org.oscm.xsd.AsyncModifySubscriptionResponseE asyncModifySubscriptionResponse77 = null;
                    org.oscm.xsd.AsyncModifySubscriptionE wrappedParam = (org.oscm.xsd.AsyncModifySubscriptionE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.AsyncModifySubscriptionE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    asyncModifySubscriptionResponse77 =

                            skel.asyncModifySubscription(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            asyncModifySubscriptionResponse77, false);
                } else

                if ("saveAttributes".equals(methodName)) {

                    org.oscm.xsd.SaveAttributesResponseE saveAttributesResponse79 = null;
                    org.oscm.xsd.SaveAttributesE wrappedParam = (org.oscm.xsd.SaveAttributesE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.SaveAttributesE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    saveAttributesResponse79 =

                            skel.saveAttributes(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            saveAttributesResponse79, false);
                } else

                if ("deactivateInstance".equals(methodName)) {

                    org.oscm.xsd.DeactivateInstanceResponseE deactivateInstanceResponse81 = null;
                    org.oscm.xsd.DeactivateInstanceE wrappedParam = (org.oscm.xsd.DeactivateInstanceE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.DeactivateInstanceE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    deactivateInstanceResponse81 =

                            skel.deactivateInstance(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            deactivateInstanceResponse81, false);
                } else

                if ("activateInstance".equals(methodName)) {

                    org.oscm.xsd.ActivateInstanceResponseE activateInstanceResponse83 = null;
                    org.oscm.xsd.ActivateInstanceE wrappedParam = (org.oscm.xsd.ActivateInstanceE) fromOM(
                            msgContext.getEnvelope().getBody()
                                    .getFirstElement(),
                            org.oscm.xsd.ActivateInstanceE.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    activateInstanceResponse83 =

                            skel.activateInstance(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            activateInstanceResponse83, false);

                } else {
                    throw new java.lang.RuntimeException("method not found");
                }

                newMsgContext.setEnvelope(envelope);
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    //
    private org.apache.axiom.om.OMElement toOM(org.oscm.xsd.DeleteUsersE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.DeleteUsersE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.DeleteUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.DeleteUsersResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(org.oscm.xsd.CreateUsersE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.CreateUsersE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.CreateUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.CreateUsersResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.UpgradeSubscriptionE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.UpgradeSubscriptionE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.UpgradeSubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.UpgradeSubscriptionResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncCreateInstanceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncCreateInstanceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncCreateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncCreateInstanceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.CreateInstanceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.CreateInstanceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.CreateInstanceResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.CreateInstanceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(org.oscm.xsd.SendPingE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.SendPingE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.SendPingResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.SendPingResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.DeleteInstanceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.DeleteInstanceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.DeleteInstanceResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.DeleteInstanceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(org.oscm.xsd.UpdateUsersE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.UpdateUsersE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.UpdateUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.UpdateUsersResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.ModifySubscriptionE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.ModifySubscriptionE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.ModifySubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.ModifySubscriptionResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncUpgradeSubscriptionE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncUpgradeSubscriptionE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncUpgradeSubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncUpgradeSubscriptionResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncModifySubscriptionE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncModifySubscriptionE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.AsyncModifySubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.AsyncModifySubscriptionResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.SaveAttributesE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.SaveAttributesE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.SaveAttributesResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.SaveAttributesResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.DeactivateInstanceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.DeactivateInstanceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.DeactivateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.DeactivateInstanceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.ActivateInstanceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(org.oscm.xsd.ActivateInstanceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(
            org.oscm.xsd.ActivateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(
                    org.oscm.xsd.ActivateInstanceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.DeleteUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.DeleteUsersResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.DeleteUsersResponseE wrapdeleteUsers() {
        org.oscm.xsd.DeleteUsersResponseE wrappedElement = new org.oscm.xsd.DeleteUsersResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.CreateUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.CreateUsersResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.CreateUsersResponseE wrapcreateUsers() {
        org.oscm.xsd.CreateUsersResponseE wrappedElement = new org.oscm.xsd.CreateUsersResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.UpgradeSubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.UpgradeSubscriptionResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.UpgradeSubscriptionResponseE wrapupgradeSubscription() {
        org.oscm.xsd.UpgradeSubscriptionResponseE wrappedElement = new org.oscm.xsd.UpgradeSubscriptionResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.AsyncCreateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.AsyncCreateInstanceResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.AsyncCreateInstanceResponseE wrapasyncCreateInstance() {
        org.oscm.xsd.AsyncCreateInstanceResponseE wrappedElement = new org.oscm.xsd.AsyncCreateInstanceResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.CreateInstanceResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.CreateInstanceResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.CreateInstanceResponseE wrapcreateInstance() {
        org.oscm.xsd.CreateInstanceResponseE wrappedElement = new org.oscm.xsd.CreateInstanceResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.SendPingResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.SendPingResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.SendPingResponseE wrapsendPing() {
        org.oscm.xsd.SendPingResponseE wrappedElement = new org.oscm.xsd.SendPingResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.DeleteInstanceResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.DeleteInstanceResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.DeleteInstanceResponseE wrapdeleteInstance() {
        org.oscm.xsd.DeleteInstanceResponseE wrappedElement = new org.oscm.xsd.DeleteInstanceResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.UpdateUsersResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.UpdateUsersResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.UpdateUsersResponseE wrapupdateUsers() {
        org.oscm.xsd.UpdateUsersResponseE wrappedElement = new org.oscm.xsd.UpdateUsersResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.ModifySubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.ModifySubscriptionResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.ModifySubscriptionResponseE wrapmodifySubscription() {
        org.oscm.xsd.ModifySubscriptionResponseE wrappedElement = new org.oscm.xsd.ModifySubscriptionResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.AsyncUpgradeSubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.AsyncUpgradeSubscriptionResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.AsyncUpgradeSubscriptionResponseE wrapasyncUpgradeSubscription() {
        org.oscm.xsd.AsyncUpgradeSubscriptionResponseE wrappedElement = new org.oscm.xsd.AsyncUpgradeSubscriptionResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.AsyncModifySubscriptionResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.AsyncModifySubscriptionResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.AsyncModifySubscriptionResponseE wrapasyncModifySubscription() {
        org.oscm.xsd.AsyncModifySubscriptionResponseE wrappedElement = new org.oscm.xsd.AsyncModifySubscriptionResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.SaveAttributesResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.SaveAttributesResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.SaveAttributesResponseE wrapsaveAttributes() {
        org.oscm.xsd.SaveAttributesResponseE wrappedElement = new org.oscm.xsd.SaveAttributesResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.DeactivateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody()
                    .addChild(param.getOMElement(
                            org.oscm.xsd.DeactivateInstanceResponseE.MY_QNAME,
                            factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.DeactivateInstanceResponseE wrapdeactivateInstance() {
        org.oscm.xsd.DeactivateInstanceResponseE wrappedElement = new org.oscm.xsd.DeactivateInstanceResponseE();
        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            org.oscm.xsd.ActivateInstanceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                    .getDefaultEnvelope();

            emptyEnvelope.getBody().addChild(param.getOMElement(
                    org.oscm.xsd.ActivateInstanceResponseE.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.oscm.xsd.ActivateInstanceResponseE wrapactivateInstance() {
        org.oscm.xsd.ActivateInstanceResponseE wrappedElement = new org.oscm.xsd.ActivateInstanceResponseE();
        return wrappedElement;
    }

    /**
     * get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
            java.lang.Class type, java.util.Map extraNamespaces)
            throws org.apache.axis2.AxisFault {

        try {

            if (org.oscm.xsd.DeleteUsersE.class.equals(type)) {

                return org.oscm.xsd.DeleteUsersE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.DeleteUsersResponseE.class.equals(type)) {

                return org.oscm.xsd.DeleteUsersResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.CreateUsersE.class.equals(type)) {

                return org.oscm.xsd.CreateUsersE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.CreateUsersResponseE.class.equals(type)) {

                return org.oscm.xsd.CreateUsersResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.UpgradeSubscriptionE.class.equals(type)) {

                return org.oscm.xsd.UpgradeSubscriptionE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.UpgradeSubscriptionResponseE.class.equals(type)) {

                return org.oscm.xsd.UpgradeSubscriptionResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncCreateInstanceE.class.equals(type)) {

                return org.oscm.xsd.AsyncCreateInstanceE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncCreateInstanceResponseE.class.equals(type)) {

                return org.oscm.xsd.AsyncCreateInstanceResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.CreateInstanceE.class.equals(type)) {

                return org.oscm.xsd.CreateInstanceE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.CreateInstanceResponseE.class.equals(type)) {

                return org.oscm.xsd.CreateInstanceResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.SendPingE.class.equals(type)) {

                return org.oscm.xsd.SendPingE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.SendPingResponseE.class.equals(type)) {

                return org.oscm.xsd.SendPingResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.DeleteInstanceE.class.equals(type)) {

                return org.oscm.xsd.DeleteInstanceE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.DeleteInstanceResponseE.class.equals(type)) {

                return org.oscm.xsd.DeleteInstanceResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.UpdateUsersE.class.equals(type)) {

                return org.oscm.xsd.UpdateUsersE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.UpdateUsersResponseE.class.equals(type)) {

                return org.oscm.xsd.UpdateUsersResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.ModifySubscriptionE.class.equals(type)) {

                return org.oscm.xsd.ModifySubscriptionE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.ModifySubscriptionResponseE.class.equals(type)) {

                return org.oscm.xsd.ModifySubscriptionResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncUpgradeSubscriptionE.class.equals(type)) {

                return org.oscm.xsd.AsyncUpgradeSubscriptionE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncUpgradeSubscriptionResponseE.class
                    .equals(type)) {

                return org.oscm.xsd.AsyncUpgradeSubscriptionResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncModifySubscriptionE.class.equals(type)) {

                return org.oscm.xsd.AsyncModifySubscriptionE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.AsyncModifySubscriptionResponseE.class
                    .equals(type)) {

                return org.oscm.xsd.AsyncModifySubscriptionResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.SaveAttributesE.class.equals(type)) {

                return org.oscm.xsd.SaveAttributesE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.SaveAttributesResponseE.class.equals(type)) {

                return org.oscm.xsd.SaveAttributesResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.DeactivateInstanceE.class.equals(type)) {

                return org.oscm.xsd.DeactivateInstanceE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.DeactivateInstanceResponseE.class.equals(type)) {

                return org.oscm.xsd.DeactivateInstanceResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.ActivateInstanceE.class.equals(type)) {

                return org.oscm.xsd.ActivateInstanceE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (org.oscm.xsd.ActivateInstanceResponseE.class.equals(type)) {

                return org.oscm.xsd.ActivateInstanceResponseE.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }

    /**
     * A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(
            org.apache.axiom.soap.SOAPEnvelope env) {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator
                    .next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

}// end of class
