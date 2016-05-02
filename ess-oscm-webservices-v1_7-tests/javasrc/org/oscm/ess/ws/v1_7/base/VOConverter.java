/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016
 *******************************************************************************/

package org.oscm.ess.ws.v1_7.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import com.fujitsu.bss.types.enumtypes.OperationParameterType;
import com.fujitsu.bss.types.enumtypes.TriggerProcessParameterType;

public class VOConverter {

    private static final String METHOD_CONVERT_TO_API = "convertToApi";
    private static final String METHOD_CONVERT_TO_INTERNAL = "convertToUp";
    private static final String VERSION_INTERNAL = "internal";
    private static final Log4jLogger LOGGER = LoggerFactory
            .getLogger(VOConverter.class);

    @SuppressWarnings("unchecked")
    public static <T, U> T reflectiveConvert(U objectToConvert) {

        String methodName = "";
        if (objectToConvert.getClass().getPackage().getName()
                .contains(VERSION_INTERNAL)) {
            methodName = METHOD_CONVERT_TO_API;
        } else {
            methodName = METHOD_CONVERT_TO_INTERNAL;
        }

        Method m;
        try {
            m = VOConverter.class.getDeclaredMethod(methodName,
                    objectToConvert.getClass());
            return (T) m.invoke(VOConverter.class, objectToConvert);
        } catch (Exception e) {
            org.oscm.internal.types.exception.SaaSSystemException exc = new org.oscm.internal.types.exception.SaaSSystemException(
                    e);
            LOGGER.logError(
                    Log4jLogger.SYSTEM_LOG,
                    exc,
                    org.oscm.types.enumtypes.LogMessageIdentifier.ERROR_VO_CONVERSION_1_6);
            throw exc;
        }
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOMarketplace convertToUp(
            com.fujitsu.bss.vo.VOMarketplace oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOMarketplace newVO = new org.oscm.internal.vo.VOMarketplace();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setCategoriesEnabled(oldVO.isCategoriesEnabled());
        newVO.setMarketplaceId(oldVO.getMarketplaceId());
        newVO.setName(oldVO.getName());
        newVO.setOpen(oldVO.isOpen());
        newVO.setOwningOrganizationId(oldVO.getOwningOrganizationId());
        newVO.setOwningOrganizationName(oldVO.getOwningOrganizationName());
        newVO.setReviewEnabled(oldVO.isReviewEnabled());
        newVO.setSocialBookmarkEnabled(oldVO.isSocialBookmarkEnabled());
        newVO.setTaggingEnabled(oldVO.isTaggingEnabled());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOMarketplace convertToApi(
            org.oscm.internal.vo.VOMarketplace oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOMarketplace newVO = new com.fujitsu.bss.vo.VOMarketplace();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setCategoriesEnabled(oldVO.isCategoriesEnabled());
        newVO.setMarketplaceId(oldVO.getMarketplaceId());
        newVO.setName(oldVO.getName());
        newVO.setOpen(oldVO.isOpen());
        newVO.setOwningOrganizationId(oldVO.getOwningOrganizationId());
        newVO.setOwningOrganizationName(oldVO.getOwningOrganizationName());
        newVO.setReviewEnabled(oldVO.isReviewEnabled());
        newVO.setSocialBookmarkEnabled(oldVO.isSocialBookmarkEnabled());
        newVO.setTaggingEnabled(oldVO.isTaggingEnabled());
        return newVO;
    }

    /**
     * Convert list of LdapProperties.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOCatalogEntry> convertToUpVOCatalogEntry(
            List<com.fujitsu.bss.vo.VOCatalogEntry> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOCatalogEntry> newVO = new ArrayList<org.oscm.internal.vo.VOCatalogEntry>();
        for (com.fujitsu.bss.vo.VOCatalogEntry tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of LdapProperties.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOCatalogEntry> convertToApiVOCatalogEntry(
            List<org.oscm.internal.vo.VOCatalogEntry> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOCatalogEntry> newVO = new ArrayList<com.fujitsu.bss.vo.VOCatalogEntry>();
        for (org.oscm.internal.vo.VOCatalogEntry tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.LdapProperties convertToUp(
            com.fujitsu.bss.vo.LdapProperties oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.LdapProperties newVO = new org.oscm.internal.vo.LdapProperties();
        newVO.setSettings(convertToUpSetting(oldVO.getSettings()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.LdapProperties convertToApi(
            org.oscm.internal.vo.LdapProperties oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.LdapProperties newVO = new com.fujitsu.bss.vo.LdapProperties();
        newVO.setSettings(convertToApiSetting(oldVO.getSettings()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceFeedback convertToApi(
            org.oscm.internal.vo.VOServiceFeedback oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceFeedback newVO = new com.fujitsu.bss.vo.VOServiceFeedback();
        newVO.setReviews(convertToApiVOServiceReview(oldVO.getReviews()));
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setAllowedToWriteReview(oldVO.isAllowedToWriteReview());
        newVO.setServiceKey(oldVO.getServiceKey());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceFeedback convertToUp(
            com.fujitsu.bss.vo.VOServiceFeedback oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceFeedback newVO = new org.oscm.internal.vo.VOServiceFeedback();
        newVO.setReviews(convertToUpVOServiceReview(oldVO.getReviews()));
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setAllowedToWriteReview(oldVO.isAllowedToWriteReview());
        newVO.setServiceKey(oldVO.getServiceKey());
        return newVO;
    }

    /**
     * Convert list of VOServiceReview.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOServiceReview> convertToApiVOServiceReview(
            List<org.oscm.internal.vo.VOServiceReview> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOServiceReview> newVO = new ArrayList<com.fujitsu.bss.vo.VOServiceReview>();
        for (org.oscm.internal.vo.VOServiceReview tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOServiceReview.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOServiceReview> convertToUpVOServiceReview(
            List<com.fujitsu.bss.vo.VOServiceReview> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOServiceReview> newVO = new ArrayList<org.oscm.internal.vo.VOServiceReview>();
        for (com.fujitsu.bss.vo.VOServiceReview tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOVatRate convertToUp(
            com.fujitsu.bss.vo.VOVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOVatRate newVO = new org.oscm.internal.vo.VOVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOVatRate convertToApi(
            org.oscm.internal.vo.VOVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOVatRate newVO = new com.fujitsu.bss.vo.VOVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        return newVO;
    }

    /**
     * Convert list of Setting.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static Set<org.oscm.internal.vo.Setting> convertToUpSetting(
            Set<com.fujitsu.bss.vo.Setting> oldVO) {
        if (oldVO == null) {
            return null;
        }
        Set<org.oscm.internal.vo.Setting> newVO = new HashSet<org.oscm.internal.vo.Setting>();
        for (com.fujitsu.bss.vo.Setting tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.Setting convertToUp(
            com.fujitsu.bss.vo.Setting oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.Setting newVO = new org.oscm.internal.vo.Setting();
        newVO.setKey(oldVO.getKey());
        newVO.setValue(oldVO.getValue());
        return newVO;
    }

    /**
     * Convert list of Setting.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static Set<com.fujitsu.bss.vo.Setting> convertToApiSetting(
            Set<org.oscm.internal.vo.Setting> oldVO) {
        if (oldVO == null) {
            return null;
        }
        Set<com.fujitsu.bss.vo.Setting> newVO = new HashSet<com.fujitsu.bss.vo.Setting>();
        for (org.oscm.internal.vo.Setting tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.Setting convertToApi(
            org.oscm.internal.vo.Setting oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.Setting newVO = new com.fujitsu.bss.vo.Setting();
        newVO.setKey(oldVO.getKey());
        newVO.setValue(oldVO.getValue());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPriceModelLocalization convertToUp(
            com.fujitsu.bss.vo.VOPriceModelLocalization oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPriceModelLocalization newVO = new org.oscm.internal.vo.VOPriceModelLocalization();
        newVO.setDescriptions(convertToUpVOLocalizedText(oldVO
                .getDescriptions()));
        newVO.setLicenses(convertToUpVOLocalizedText(oldVO.getLicenses()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPriceModelLocalization convertToApi(
            org.oscm.internal.vo.VOPriceModelLocalization oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPriceModelLocalization newVO = new com.fujitsu.bss.vo.VOPriceModelLocalization();
        newVO.setDescriptions(convertToVOLocalizedText(oldVO.getDescriptions()));
        newVO.setLicenses(convertToVOLocalizedText(oldVO.getLicenses()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUda convertToUp(
            com.fujitsu.bss.vo.VOUda oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUda newVO = new org.oscm.internal.vo.VOUda();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUdaDefinition(convertToUp(oldVO.getUdaDefinition()));
        newVO.setUdaValue(oldVO.getUdaValue());
        newVO.setTargetObjectKey(oldVO.getTargetObjectKey());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUda convertToApi(
            org.oscm.internal.vo.VOUda oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUda newVO = new com.fujitsu.bss.vo.VOUda();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUdaDefinition(convertToApi(oldVO.getUdaDefinition()));
        newVO.setUdaValue(oldVO.getUdaValue());
        newVO.setTargetObjectKey(oldVO.getTargetObjectKey());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOSubscription convertToUp(
            com.fujitsu.bss.vo.VOSubscription oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOSubscription newVO = new org.oscm.internal.vo.VOSubscription();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToUpVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOSubscription convertToApi(
            org.oscm.internal.vo.VOSubscription oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOSubscription newVO = new com.fujitsu.bss.vo.VOSubscription();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToApiVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPricedRole convertToUp(
            com.fujitsu.bss.vo.VOPricedRole oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPricedRole newVO = new org.oscm.internal.vo.VOPricedRole();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setRole(convertToUp(oldVO.getRole()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPricedRole convertToApi(
            org.oscm.internal.vo.VOPricedRole oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPricedRole newVO = new com.fujitsu.bss.vo.VOPricedRole();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setRole(convertToApi(oldVO.getRole()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOTechnicalServiceOperation convertToUp(
            com.fujitsu.bss.vo.VOTechnicalServiceOperation oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOTechnicalServiceOperation newVO = new org.oscm.internal.vo.VOTechnicalServiceOperation();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOperationId(oldVO.getOperationId());
        newVO.setOperationName(oldVO.getOperationName());
        newVO.setOperationDescription(oldVO.getOperationDescription());
        if (oldVO.getOperationParameters() != null) {
            for (com.fujitsu.bss.vo.VOServiceOperationParameter tmp : oldVO
                    .getOperationParameters()) {
                newVO.getOperationParameters().add(convertToUp(tmp));
            }
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceOperationParameter convertToUp(
            com.fujitsu.bss.vo.VOServiceOperationParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceOperationParameter newVO = new org.oscm.internal.vo.VOServiceOperationParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setParameterName(oldVO.getParameterName());
        newVO.setParameterValue(oldVO.getParameterValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                org.oscm.internal.types.enumtypes.OperationParameterType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceOperationParameterValues convertToUp(
            com.fujitsu.bss.vo.VOServiceOperationParameterValues oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceOperationParameterValues newVO = new org.oscm.internal.vo.VOServiceOperationParameterValues();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setParameterName(oldVO.getParameterName());
        newVO.setParameterValue(oldVO.getParameterValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                org.oscm.internal.types.enumtypes.OperationParameterType.class));
        newVO.setValues(oldVO.getValues());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOTechnicalServiceOperation convertToApi(
            org.oscm.internal.vo.VOTechnicalServiceOperation oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOTechnicalServiceOperation newVO = new com.fujitsu.bss.vo.VOTechnicalServiceOperation();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOperationId(oldVO.getOperationId());
        newVO.setOperationName(oldVO.getOperationName());
        newVO.setOperationDescription(oldVO.getOperationDescription());
        if (oldVO.getOperationParameters() != null) {
            for (org.oscm.internal.vo.VOServiceOperationParameter tmp : oldVO
                    .getOperationParameters()) {
                newVO.getOperationParameters().add(convertToApi(tmp));
            }
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceOperationParameter convertToApi(
            org.oscm.internal.vo.VOServiceOperationParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceOperationParameter newVO = new com.fujitsu.bss.vo.VOServiceOperationParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setParameterName(oldVO.getParameterName());
        newVO.setParameterValue(oldVO.getParameterValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                OperationParameterType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceOperationParameterValues convertToApi(
            org.oscm.internal.vo.VOServiceOperationParameterValues oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceOperationParameterValues newVO = new com.fujitsu.bss.vo.VOServiceOperationParameterValues();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setParameterName(oldVO.getParameterName());
        newVO.setParameterValue(oldVO.getParameterValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                OperationParameterType.class));
        newVO.setValues(oldVO.getValues());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOGatheredEvent convertToUp(
            com.fujitsu.bss.vo.VOGatheredEvent oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOGatheredEvent newVO = new org.oscm.internal.vo.VOGatheredEvent();
        newVO.setOccurrenceTime(oldVO.getOccurrenceTime());
        newVO.setActor(oldVO.getActor());
        newVO.setEventId(oldVO.getEventId());
        newVO.setMultiplier(oldVO.getMultiplier());
        newVO.setUniqueId(oldVO.getUniqueId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOGatheredEvent convertToApi(
            org.oscm.internal.vo.VOGatheredEvent oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOGatheredEvent newVO = new com.fujitsu.bss.vo.VOGatheredEvent();
        newVO.setOccurrenceTime(oldVO.getOccurrenceTime());
        newVO.setActor(oldVO.getActor());
        newVO.setEventId(oldVO.getEventId());
        newVO.setMultiplier(oldVO.getMultiplier());
        newVO.setUniqueId(oldVO.getUniqueId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOReport convertToUp(
            com.fujitsu.bss.vo.VOReport oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOReport newVO = new org.oscm.internal.vo.VOReport();
        newVO.setReportName(oldVO.getReportName());
        newVO.setLocalizedReportName(oldVO.getLocalizedReportName());
        newVO.setReportURLTemplate(oldVO.getReportUrlTemplate());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOReport convertToApi(
            org.oscm.internal.vo.VOReport oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOReport newVO = new com.fujitsu.bss.vo.VOReport();
        newVO.setReportName(oldVO.getReportName());
        newVO.setLocalizedReportName(oldVO.getLocalizedReportName());
        newVO.setReportURLTemplate(oldVO.getReportUrlTemplate());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOParameterDefinition convertToUp(
            com.fujitsu.bss.vo.VOParameterDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOParameterDefinition newVO = new org.oscm.internal.vo.VOParameterDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterOptions(convertToUpVOParameterOption(oldVO
                .getParameterOptions()));
        newVO.setDefaultValue(oldVO.getDefaultValue());
        newVO.setMinValue(oldVO.getMinValue());
        newVO.setMaxValue(oldVO.getMaxValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setConfigurable(oldVO.isConfigurable());
        newVO.setParameterType(EnumConverter.convert(oldVO.getParameterType(),
                org.oscm.internal.types.enumtypes.ParameterType.class));
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setValueType(EnumConverter.convert(oldVO.getValueType(),
                org.oscm.internal.types.enumtypes.ParameterValueType.class));
        newVO.setModificationType(EnumConverter.convert(
                oldVO.getModificationType(),
                org.oscm.internal.types.enumtypes.ParameterModificationType.class));
        newVO.setDescription(oldVO.getDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOParameterDefinition convertToApi(
            org.oscm.internal.vo.VOParameterDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOParameterDefinition newVO = new com.fujitsu.bss.vo.VOParameterDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterOptions(convertToApiVOParameterOption(oldVO
                .getParameterOptions()));
        newVO.setDefaultValue(oldVO.getDefaultValue());
        newVO.setMinValue(oldVO.getMinValue());
        newVO.setMaxValue(oldVO.getMaxValue());
        newVO.setMandatory(oldVO.isMandatory());
        newVO.setConfigurable(oldVO.isConfigurable());
        newVO.setParameterType(EnumConverter.convert(oldVO.getParameterType(),
                com.fujitsu.bss.types.enumtypes.ParameterType.class));
        newVO.setParameterId(oldVO.getParameterId());
        newVO.setValueType(EnumConverter.convert(oldVO.getValueType(),
                com.fujitsu.bss.types.enumtypes.ParameterValueType.class));
        newVO.setModificationType(EnumConverter.convert(
                oldVO.getModificationType(),
                com.fujitsu.bss.types.enumtypes.ParameterModificationType.class));
        newVO.setDescription(oldVO.getDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOTriggerProcess convertToUp(
            com.fujitsu.bss.vo.VOTriggerProcess oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOTriggerProcess newVO = new org.oscm.internal.vo.VOTriggerProcess();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setReason(oldVO.getReason());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.TriggerProcessStatus.class));
        newVO.setTriggerDefinition(convertToUp(oldVO.getTriggerDefinition()));
        newVO.setUser(convertToUp(oldVO.getUser()));
        newVO.setTargetNames(oldVO.getTargetNames());
        newVO.setParameter(oldVO.getParameter());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOTriggerProcess convertToApi(
            org.oscm.internal.vo.VOTriggerProcess oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOTriggerProcess newVO = new com.fujitsu.bss.vo.VOTriggerProcess();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setReason(oldVO.getReason());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.TriggerProcessStatus.class));
        newVO.setTriggerDefinition(convertToApi(oldVO.getTriggerDefinition()));
        newVO.setUser(convertToApi(oldVO.getUser()));
        newVO.setTargetNames(oldVO.getTargetNames());
        newVO.setParameter(oldVO.getParameter());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOParameterOption convertToUp(
            com.fujitsu.bss.vo.VOParameterOption oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOParameterOption newVO = new org.oscm.internal.vo.VOParameterOption();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOptionId(oldVO.getOptionId());
        newVO.setOptionDescription(oldVO.getOptionDescription());
        newVO.setParamDefId(oldVO.getParamDefId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOParameterOption convertToApi(
            org.oscm.internal.vo.VOParameterOption oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOParameterOption newVO = new com.fujitsu.bss.vo.VOParameterOption();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOptionId(oldVO.getOptionId());
        newVO.setOptionDescription(oldVO.getOptionDescription());
        newVO.setParamDefId(oldVO.getParamDefId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOOrganization convertToUp(
            com.fujitsu.bss.vo.VOOrganization oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOOrganization newVO = new org.oscm.internal.vo.VOOrganization();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setAddress(oldVO.getAddress());
        newVO.setEmail(oldVO.getEmail());
        newVO.setLocale(oldVO.getLocale());
        newVO.setName(oldVO.getName());
        newVO.setPhone(oldVO.getPhone());
        newVO.setUrl(oldVO.getUrl());
        newVO.setDescription(oldVO.getDescription());
        newVO.setDiscount(convertToUp(oldVO.getDiscount()));
        newVO.setDistinguishedName(oldVO.getDistinguishedName());
        newVO.setDomicileCountry(oldVO.getDomicileCountry());
        newVO.setNameSpace(oldVO.getNameSpace());
        newVO.setImageDefined(oldVO.isImageDefined());
        newVO.setSupportEmail(oldVO.getSupportEmail());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOOrganization convertToApi(
            org.oscm.internal.vo.VOOrganization oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOOrganization newVO = new com.fujitsu.bss.vo.VOOrganization();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setAddress(oldVO.getAddress());
        newVO.setEmail(oldVO.getEmail());
        newVO.setLocale(oldVO.getLocale());
        newVO.setName(oldVO.getName());
        newVO.setPhone(oldVO.getPhone());
        newVO.setUrl(oldVO.getUrl());
        newVO.setDescription(oldVO.getDescription());
        newVO.setDiscount(convertToApi(oldVO.getDiscount()));
        newVO.setDistinguishedName(oldVO.getDistinguishedName());
        newVO.setDomicileCountry(oldVO.getDomicileCountry());
        newVO.setNameSpace(oldVO.getNameSpace());
        newVO.setImageDefined(oldVO.isImageDefined());
        newVO.setSupportEmail(oldVO.getSupportEmail());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUserDetails convertToUp(
            com.fujitsu.bss.vo.VOUserDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUserDetails newVO = new org.oscm.internal.vo.VOUserDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEMail(oldVO.getEMail());
        newVO.setFirstName(oldVO.getFirstName());
        newVO.setAdditionalName(oldVO.getAdditionalName());
        newVO.setLastName(oldVO.getLastName());
        newVO.setAddress(oldVO.getAddress());
        newVO.setPhone(oldVO.getPhone());
        newVO.setLocale(oldVO.getLocale());
        newVO.setSalutation(EnumConverter.convert(oldVO.getSalutation(),
                org.oscm.internal.types.enumtypes.Salutation.class));
        newVO.setRealmUserId(oldVO.getRealmUserId());
        newVO.setRemoteLdapActive(oldVO.isRemoteLdapActive());
        newVO.setRemoteLdapAttributes(EnumConverter.convertList(
                oldVO.getRemoteLdapAttributes(),
                org.oscm.internal.types.enumtypes.SettingType.class));
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setUserId(oldVO.getUserId());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.UserAccountStatus.class));
        newVO.setOrganizationRoles(EnumConverter.convertSet(
                oldVO.getOrganizationRoles(),
                org.oscm.internal.types.enumtypes.OrganizationRoleType.class));
        newVO.setUserRoles(EnumConverter.convertSet(oldVO.getUserRoles(),
                org.oscm.internal.types.enumtypes.UserRoleType.class));

        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUserDetails convertToApi(
            org.oscm.internal.vo.VOUserDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUserDetails newVO = new com.fujitsu.bss.vo.VOUserDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEMail(oldVO.getEMail());
        newVO.setFirstName(oldVO.getFirstName());
        newVO.setAdditionalName(oldVO.getAdditionalName());
        newVO.setLastName(oldVO.getLastName());
        newVO.setAddress(oldVO.getAddress());
        newVO.setPhone(oldVO.getPhone());
        newVO.setLocale(oldVO.getLocale());
        newVO.setSalutation(EnumConverter.convert(oldVO.getSalutation(),
                com.fujitsu.bss.types.enumtypes.Salutation.class));
        newVO.setRealmUserId(oldVO.getRealmUserId());
        newVO.setRemoteLdapActive(oldVO.isRemoteLdapActive());
        newVO.setRemoteLdapAttributes(EnumConverter.convertList(
                oldVO.getRemoteLdapAttributes(),
                com.fujitsu.bss.types.enumtypes.SettingType.class));
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setUserId(oldVO.getUserId());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.UserAccountStatus.class));
        newVO.setOrganizationRoles(EnumConverter.convertSet(
                oldVO.getOrganizationRoles(),
                com.fujitsu.bss.types.enumtypes.OrganizationRoleType.class));
        newVO.setUserRoles(EnumConverter.convertSet(oldVO.getUserRoles(),
                com.fujitsu.bss.types.enumtypes.UserRoleType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOTechnicalService convertToUp(
            com.fujitsu.bss.vo.VOTechnicalService oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOTechnicalService newVO = new org.oscm.internal.vo.VOTechnicalService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEventDefinitions(convertToUpVOEventDefinition(oldVO
                .getEventDefinitions()));
        newVO.setTechnicalServiceId(oldVO.getTechnicalServiceId());
        newVO.setTechnicalServiceBuildId(oldVO.getTechnicalServiceBuildId());
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setTechnicalServiceDescription(oldVO
                .getTechnicalServiceDescription());
        newVO.setBaseUrl(oldVO.getBaseUrl());
        newVO.setProvisioningUrl(oldVO.getProvisioningUrl());
        newVO.setLoginPath(oldVO.getLoginPath());
        newVO.setProvisioningVersion(oldVO.getProvisioningVersion());
        newVO.setParameterDefinitions(convertToUpVOParameterDefinition(oldVO
                .getParameterDefinitions()));
        newVO.setRoleDefinitions(convertToUpVORoleDefinition(oldVO
                .getRoleDefinitions()));
        newVO.setTags(oldVO.getTags());
        newVO.setLicense(oldVO.getLicense());
        newVO.setAccessInfo(oldVO.getAccessInfo());
        newVO.setTechnicalServiceOperations(convertToUpVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOTechnicalService convertToApi(
            org.oscm.internal.vo.VOTechnicalService oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOTechnicalService newVO = new com.fujitsu.bss.vo.VOTechnicalService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEventDefinitions(convertToApiVOEventDefinition(oldVO
                .getEventDefinitions()));
        newVO.setTechnicalServiceId(oldVO.getTechnicalServiceId());
        newVO.setTechnicalServiceBuildId(oldVO.getTechnicalServiceBuildId());
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setTechnicalServiceDescription(oldVO
                .getTechnicalServiceDescription());
        newVO.setBaseUrl(oldVO.getBaseUrl());
        newVO.setProvisioningUrl(oldVO.getProvisioningUrl());
        newVO.setLoginPath(oldVO.getLoginPath());
        newVO.setProvisioningVersion(oldVO.getProvisioningVersion());
        newVO.setParameterDefinitions(convertToApiVOParameterDefinition(oldVO
                .getParameterDefinitions()));
        newVO.setRoleDefinitions(convertToApiVORoleDefinition(oldVO
                .getRoleDefinitions()));
        newVO.setTags(oldVO.getTags());
        newVO.setLicense(oldVO.getLicense());
        newVO.setAccessInfo(oldVO.getAccessInfo());
        newVO.setTechnicalServiceOperations(convertToApiVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUserSubscription convertToUp(
            com.fujitsu.bss.vo.VOUserSubscription oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUserSubscription newVO = new org.oscm.internal.vo.VOUserSubscription();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToUpVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setLicense(convertToUp(oldVO.getLicense()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUserSubscription convertToApi(
            org.oscm.internal.vo.VOUserSubscription oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUserSubscription newVO = new com.fujitsu.bss.vo.VOUserSubscription();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToApiVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setLicense(convertToApi(oldVO.getLicense()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceDetails convertToUp(
            com.fujitsu.bss.vo.VOServiceDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceDetails newVO = new org.oscm.internal.vo.VOServiceDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setTechnicalService(convertToUp(oldVO.getTechnicalService()));
        newVO.setImageDefined(oldVO.isImageDefined());
        newVO.setParameters(convertToUpVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                org.oscm.internal.types.enumtypes.OfferingType.class));
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceDetails convertToApi(
            org.oscm.internal.vo.VOServiceDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceDetails newVO = new com.fujitsu.bss.vo.VOServiceDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setTechnicalService(convertToApi(oldVO.getTechnicalService()));
        newVO.setImageDefined(oldVO.isImageDefined());
        newVO.setParameters(convertToApiVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                com.fujitsu.bss.types.enumtypes.OfferingType.class));
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceEntry convertToApi(
            org.oscm.internal.vo.VOServiceEntry oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceEntry newVO = new com.fujitsu.bss.vo.VOServiceEntry();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToApiVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                com.fujitsu.bss.types.enumtypes.OfferingType.class));
        newVO.setSubscriptionLimitReached(oldVO.isSubscriptionLimitReached());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceEntry convertToUp(
            com.fujitsu.bss.vo.VOServiceEntry oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceEntry newVO = new org.oscm.internal.vo.VOServiceEntry();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToUpVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                org.oscm.internal.types.enumtypes.OfferingType.class));
        newVO.setSubscriptionLimitReached(oldVO.isSubscriptionLimitReached());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPricedParameter convertToUp(
            com.fujitsu.bss.vo.VOPricedParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPricedParameter newVO = new org.oscm.internal.vo.VOPricedParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setVoParameterDef(convertToUp(oldVO.getVoParameterDef()));
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setPricePerSubscription(oldVO.getPricePerSubscription());
        newVO.setParameterKey(oldVO.getParameterKey());
        newVO.setPricedOptions(convertToUpVOPricedOption(oldVO
                .getPricedOptions()));
        newVO.setRoleSpecificUserPrices(convertToUpVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        newVO.setSteppedPrices(convertToUpVOSteppedPrice(oldVO
                .getSteppedPrices()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPricedParameter convertToApi(
            org.oscm.internal.vo.VOPricedParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPricedParameter newVO = new com.fujitsu.bss.vo.VOPricedParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setVoParameterDef(convertToApi(oldVO.getVoParameterDef()));
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setPricePerSubscription(oldVO.getPricePerSubscription());
        newVO.setParameterKey(oldVO.getParameterKey());
        newVO.setPricedOptions(convertToApiVOPricedOption(oldVO
                .getPricedOptions()));
        newVO.setRoleSpecificUserPrices(convertToApiVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        newVO.setSteppedPrices(convertToApiVOSteppedPrice(oldVO
                .getSteppedPrices()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUdaDefinition convertToUp(
            com.fujitsu.bss.vo.VOUdaDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUdaDefinition newVO = new org.oscm.internal.vo.VOUdaDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUdaId(oldVO.getUdaId());
        newVO.setTargetType(oldVO.getTargetType());
        newVO.setDefaultValue(oldVO.getDefaultValue());
        newVO.setConfigurationType(EnumConverter.convert(
                oldVO.getConfigurationType(),
                org.oscm.internal.types.enumtypes.UdaConfigurationType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUdaDefinition convertToApi(
            org.oscm.internal.vo.VOUdaDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUdaDefinition newVO = new com.fujitsu.bss.vo.VOUdaDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUdaId(oldVO.getUdaId());
        newVO.setTargetType(oldVO.getTargetType());
        newVO.setDefaultValue(oldVO.getDefaultValue());
        newVO.setConfigurationType(EnumConverter.convert(
                oldVO.getConfigurationType(),
                com.fujitsu.bss.types.enumtypes.UdaConfigurationType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOSubscriptionDetails convertToUp(
            com.fujitsu.bss.vo.VOSubscriptionDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOSubscriptionDetails newVO = new org.oscm.internal.vo.VOSubscriptionDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToUpVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setUsageLicenses(convertToUpVOUsageLicense(oldVO
                .getUsageLicenses()));
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setSubscribedService(convertToUp(oldVO.getSubscribedService()));
        newVO.setBillingContact(convertToUp(oldVO.getBillingContact()));
        newVO.setPaymentInfo(convertToUp(oldVO.getPaymentInfo()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOSubscriptionDetails convertToApi(
            org.oscm.internal.vo.VOSubscriptionDetails oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOSubscriptionDetails newVO = new com.fujitsu.bss.vo.VOSubscriptionDetails();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setServiceKey(oldVO.getServiceKey());
        newVO.setActivationDate(oldVO.getActivationDate());
        newVO.setCreationDate(oldVO.getCreationDate());
        newVO.setDeactivationDate(oldVO.getDeactivationDate());
        newVO.setServiceAccessInfo(oldVO.getServiceAccessInfo());
        newVO.setServiceAccessType(EnumConverter.convert(
                oldVO.getServiceAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setServiceBaseURL(oldVO.getServiceBaseURL());
        newVO.setServiceLoginPath(oldVO.getServiceLoginPath());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.SubscriptionStatus.class));
        newVO.setServiceInstanceId(oldVO.getServiceInstanceId());
        newVO.setTimeoutMailSent(oldVO.isTimeoutMailSent());
        newVO.setPurchaseOrderNumber(oldVO.getPurchaseOrderNumber());
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setProvisioningProgress(oldVO.getProvisioningProgress());
        newVO.setNumberOfAssignedUsers(oldVO.getNumberOfAssignedUsers());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setTechnicalServiceOperations(convertToApiVOTechnicalServiceOperation(oldVO
                .getTechnicalServiceOperations()));
        newVO.setUsageLicenses(convertToVOUsageLicense(oldVO.getUsageLicenses()));
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setSubscribedService(convertToApi(oldVO.getSubscribedService()));
        newVO.setBillingContact(convertToApi(oldVO.getBillingContact()));
        newVO.setPaymentInfo(convertToApi(oldVO.getPaymentInfo()));
        newVO.setOwnerId(oldVO.getOwnerId());
        newVO.setUnitKey(oldVO.getUnitKey());
        newVO.setUnitName(oldVO.getUnitName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPricedOption convertToUp(
            com.fujitsu.bss.vo.VOPricedOption oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPricedOption newVO = new org.oscm.internal.vo.VOPricedOption();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setPricePerSubscription(oldVO.getPricePerSubscription());
        newVO.setParameterOptionKey(oldVO.getParameterOptionKey());
        newVO.setOptionId(oldVO.getOptionId());
        newVO.setRoleSpecificUserPrices(convertToUpVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPricedOption convertToApi(
            org.oscm.internal.vo.VOPricedOption oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPricedOption newVO = new com.fujitsu.bss.vo.VOPricedOption();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setPricePerUser(oldVO.getPricePerUser());
        newVO.setPricePerSubscription(oldVO.getPricePerSubscription());
        newVO.setParameterOptionKey(oldVO.getParameterOptionKey());
        newVO.setOptionId(oldVO.getOptionId());
        newVO.setRoleSpecificUserPrices(convertToApiVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOBillingContact convertToUp(
            com.fujitsu.bss.vo.VOBillingContact oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOBillingContact newVO = new org.oscm.internal.vo.VOBillingContact();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEmail(oldVO.getEmail());
        newVO.setCompanyName(oldVO.getCompanyName());
        newVO.setAddress(oldVO.getAddress());
        newVO.setOrgAddressUsed(oldVO.isOrgAddressUsed());
        newVO.setId(oldVO.getId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VODiscount convertToUp(
            com.fujitsu.bss.vo.VODiscount oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VODiscount newVO = new org.oscm.internal.vo.VODiscount();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEndTime(oldVO.getEndTime());
        newVO.setStartTime(oldVO.getStartTime());
        newVO.setValue(oldVO.getValue());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOBillingContact convertToApi(
            org.oscm.internal.vo.VOBillingContact oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOBillingContact newVO = new com.fujitsu.bss.vo.VOBillingContact();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEmail(oldVO.getEmail());
        newVO.setCompanyName(oldVO.getCompanyName());
        newVO.setAddress(oldVO.getAddress());
        newVO.setOrgAddressUsed(oldVO.isOrgAddressUsed());
        newVO.setId(oldVO.getId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VODiscount convertToApi(
            org.oscm.internal.vo.VODiscount oldVO) {

        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VODiscount newVO = new com.fujitsu.bss.vo.VODiscount();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEndTime(oldVO.getEndTime());
        newVO.setStartTime(oldVO.getStartTime());
        newVO.setValue(oldVO.getValue());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPaymentType convertToUp(
            com.fujitsu.bss.vo.VOPaymentType oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPaymentType newVO = new org.oscm.internal.vo.VOPaymentType();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setName(oldVO.getName());
        newVO.setPaymentTypeId(oldVO.getPaymentTypeId());
        newVO.setCollectionType(EnumConverter.convert(
                oldVO.getCollectionType(),
                org.oscm.internal.types.enumtypes.PaymentCollectionType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPaymentType convertToApi(
            org.oscm.internal.vo.VOPaymentType oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPaymentType newVO = new com.fujitsu.bss.vo.VOPaymentType();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setName(oldVO.getName());
        newVO.setPaymentTypeId(oldVO.getPaymentTypeId());
        newVO.setCollectionType(EnumConverter.convert(
                oldVO.getCollectionType(),
                com.fujitsu.bss.types.enumtypes.PaymentCollectionType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPaymentInfo convertToApi(
            org.oscm.internal.vo.VOPaymentInfo oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPaymentInfo newVO = new com.fujitsu.bss.vo.VOPaymentInfo();
        newVO.setAccountNumber(oldVO.getAccountNumber());
        newVO.setId(oldVO.getId());
        newVO.setKey(oldVO.getKey());
        newVO.setPaymentType(convertToApi(oldVO.getPaymentType()));
        newVO.setProviderName(oldVO.getProviderName());
        newVO.setVersion(oldVO.getVersion());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPaymentInfo convertToUp(
            com.fujitsu.bss.vo.VOPaymentInfo oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPaymentInfo newVO = new org.oscm.internal.vo.VOPaymentInfo();
        newVO.setAccountNumber(oldVO.getAccountNumber());
        newVO.setId(oldVO.getId());
        newVO.setKey(oldVO.getKey());
        newVO.setPaymentType(convertToUp(oldVO.getPaymentType()));
        newVO.setProviderName(oldVO.getProviderName());
        newVO.setVersion(oldVO.getVersion());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOSteppedPrice convertToUp(
            com.fujitsu.bss.vo.VOSteppedPrice oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOSteppedPrice newVO = new org.oscm.internal.vo.VOSteppedPrice();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setLimit(oldVO.getLimit());
        newVO.setPrice(oldVO.getPrice());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOSteppedPrice convertToApi(
            org.oscm.internal.vo.VOSteppedPrice oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOSteppedPrice newVO = new com.fujitsu.bss.vo.VOSteppedPrice();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setLimit(oldVO.getLimit());
        newVO.setPrice(oldVO.getPrice());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOInstanceInfo convertToUp(
            com.fujitsu.bss.vo.VOInstanceInfo oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOInstanceInfo newVO = new org.oscm.internal.vo.VOInstanceInfo();
        newVO.setInstanceId(oldVO.getInstanceId());
        newVO.setAccessInfo(oldVO.getAccessInfo());
        newVO.setBaseUrl(oldVO.getBaseUrl());
        newVO.setLoginPath(oldVO.getLoginPath());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOInstanceInfo convertToApi(
            org.oscm.internal.vo.VOInstanceInfo oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOInstanceInfo newVO = new com.fujitsu.bss.vo.VOInstanceInfo();
        newVO.setInstanceId(oldVO.getInstanceId());
        newVO.setAccessInfo(oldVO.getAccessInfo());
        newVO.setBaseUrl(oldVO.getBaseUrl());
        newVO.setLoginPath(oldVO.getLoginPath());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPricedEvent convertToUp(
            com.fujitsu.bss.vo.VOPricedEvent oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPricedEvent newVO = new org.oscm.internal.vo.VOPricedEvent();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setSteppedPrices(convertToUpVOSteppedPrice(oldVO
                .getSteppedPrices()));
        newVO.setEventDefinition(convertToUp(oldVO.getEventDefinition()));
        newVO.setEventPrice(oldVO.getEventPrice());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPricedEvent convertToApi(
            org.oscm.internal.vo.VOPricedEvent oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPricedEvent newVO = new com.fujitsu.bss.vo.VOPricedEvent();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setSteppedPrices(convertToApiVOSteppedPrice(oldVO
                .getSteppedPrices()));
        newVO.setEventDefinition(convertToApi(oldVO.getEventDefinition()));
        newVO.setEventPrice(oldVO.getEventPrice());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOTriggerDefinition convertToUp(
            com.fujitsu.bss.vo.VOTriggerDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOTriggerDefinition newVO = new org.oscm.internal.vo.VOTriggerDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                org.oscm.internal.types.enumtypes.TriggerType.class));
        newVO.setTargetType(EnumConverter.convert(oldVO.getTargetType(),
                org.oscm.internal.types.enumtypes.TriggerTargetType.class));
        newVO.setTarget(oldVO.getTarget());
        newVO.setSuspendProcess(oldVO.isSuspendProcess());
        newVO.setName(oldVO.getName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOTriggerDefinition convertToApi(
            org.oscm.internal.vo.VOTriggerDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOTriggerDefinition newVO = new com.fujitsu.bss.vo.VOTriggerDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                com.fujitsu.bss.types.enumtypes.TriggerType.class));
        newVO.setTargetType(EnumConverter.convert(oldVO.getTargetType(),
                com.fujitsu.bss.types.enumtypes.TriggerTargetType.class));
        newVO.setTarget(oldVO.getTarget());
        newVO.setSuspendProcess(oldVO.isSuspendProcess());
        newVO.setName(oldVO.getName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOImageResource convertToUp(
            com.fujitsu.bss.vo.VOImageResource oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOImageResource newVO = new org.oscm.internal.vo.VOImageResource();
        newVO.setBuffer(oldVO.getBuffer());
        newVO.setContentType(oldVO.getContentType());
        newVO.setImageType(EnumConverter.convert(oldVO.getImageType(),
                org.oscm.internal.types.enumtypes.ImageType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOImageResource convertToApi(
            org.oscm.internal.vo.VOImageResource oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOImageResource newVO = new com.fujitsu.bss.vo.VOImageResource();
        newVO.setBuffer(oldVO.getBuffer());
        newVO.setContentType(oldVO.getContentType());
        newVO.setImageType(EnumConverter.convert(oldVO.getImageType(),
                com.fujitsu.bss.types.enumtypes.ImageType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceLocalization convertToUp(
            com.fujitsu.bss.vo.VOServiceLocalization oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceLocalization newVO = new org.oscm.internal.vo.VOServiceLocalization();
        newVO.setNames(convertToUpVOLocalizedText(oldVO.getNames()));
        newVO.setDescriptions(convertToUpVOLocalizedText(oldVO
                .getDescriptions()));
        newVO.setShortDescriptions(convertToUpVOLocalizedText(oldVO
                .getShortDescriptions()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceLocalization convertToApi(
            org.oscm.internal.vo.VOServiceLocalization oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceLocalization newVO = new com.fujitsu.bss.vo.VOServiceLocalization();
        newVO.setNames(convertToVOLocalizedText(oldVO.getNames()));
        newVO.setDescriptions(convertToVOLocalizedText(oldVO.getDescriptions()));
        newVO.setShortDescriptions(convertToVOLocalizedText(oldVO
                .getShortDescriptions()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceListResult convertToApi(
            org.oscm.internal.vo.VOServiceListResult oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceListResult newVO = new com.fujitsu.bss.vo.VOServiceListResult();
        newVO.setResultSize(oldVO.getResultSize());
        for (org.oscm.internal.vo.VOService s : oldVO.getServices()) {
            newVO.getServices().add(convertToApi(s));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceListResult convertToUp(
            com.fujitsu.bss.vo.VOServiceListResult oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceListResult newVO = new org.oscm.internal.vo.VOServiceListResult();
        newVO.setResultSize(oldVO.getResultSize());
        for (com.fujitsu.bss.vo.VOService s : oldVO.getServices()) {
            newVO.getServices().add(convertToUp(s));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.ListCriteria convertToUp(
            com.fujitsu.bss.vo.ListCriteria oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.ListCriteria newVO = new org.oscm.internal.vo.ListCriteria();
        newVO.setOffset(oldVO.getOffset());
        newVO.setLimit(oldVO.getLimit());
        newVO.setFilter(oldVO.getFilter());
        newVO.setSorting(EnumConverter.convert(oldVO.getSorting(),
                org.oscm.internal.types.enumtypes.Sorting.class));
        newVO.setCategoryId(oldVO.getCategoryId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.ListCriteria convertToApi(
            org.oscm.internal.vo.ListCriteria oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.ListCriteria newVO = new com.fujitsu.bss.vo.ListCriteria();
        newVO.setOffset(oldVO.getOffset());
        newVO.setLimit(oldVO.getLimit());
        newVO.setFilter(oldVO.getFilter());
        newVO.setSorting(EnumConverter.convert(oldVO.getSorting(),
                com.fujitsu.bss.types.enumtypes.Sorting.class));
        newVO.setCategoryId(oldVO.getCategoryId());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOOrganizationPaymentConfiguration convertToUp(
            com.fujitsu.bss.vo.VOOrganizationPaymentConfiguration oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOOrganizationPaymentConfiguration newVO = new org.oscm.internal.vo.VOOrganizationPaymentConfiguration();
        newVO.setOrganization(convertToUp(oldVO.getOrganization()));
        newVO.setEnabledPaymentTypes(convertToUpVOPaymentType(oldVO
                .getEnabledPaymentTypes()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOOrganizationPaymentConfiguration convertToApi(
            org.oscm.internal.vo.VOOrganizationPaymentConfiguration oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOOrganizationPaymentConfiguration newVO = new com.fujitsu.bss.vo.VOOrganizationPaymentConfiguration();
        newVO.setOrganization(convertToApi(oldVO.getOrganization()));
        newVO.setEnabledPaymentTypes(convertToApiVOPaymentType(oldVO
                .getEnabledPaymentTypes()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VORoleDefinition convertToUp(
            com.fujitsu.bss.vo.VORoleDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VORoleDefinition newVO = new org.oscm.internal.vo.VORoleDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRoleId(oldVO.getRoleId());
        newVO.setName(oldVO.getName());
        newVO.setDescription(oldVO.getDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VORoleDefinition convertToApi(
            org.oscm.internal.vo.VORoleDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VORoleDefinition newVO = new com.fujitsu.bss.vo.VORoleDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRoleId(oldVO.getRoleId());
        newVO.setName(oldVO.getName());
        newVO.setDescription(oldVO.getDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOService convertToUp(
            com.fujitsu.bss.vo.VOService oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOService newVO = new org.oscm.internal.vo.VOService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToUpVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                org.oscm.internal.types.enumtypes.OfferingType.class));
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceReview convertToUp(
            com.fujitsu.bss.vo.VOServiceReview oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceReview newVO = new org.oscm.internal.vo.VOServiceReview();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setTitle(oldVO.getTitle());
        newVO.setComment(oldVO.getComment());
        newVO.setRating(oldVO.getRating());
        newVO.setModificationDate(oldVO.getModificationDate());
        newVO.setProductKey(oldVO.getProductKey());
        newVO.setUserId(oldVO.getUserId());
        newVO.setUserName(oldVO.getUserName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceReview convertToApi(
            org.oscm.internal.vo.VOServiceReview oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceReview newVO = new com.fujitsu.bss.vo.VOServiceReview();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setTitle(oldVO.getTitle());
        newVO.setComment(oldVO.getComment());
        newVO.setRating(oldVO.getRating());
        newVO.setModificationDate(oldVO.getModificationDate());
        newVO.setProductKey(oldVO.getProductKey());
        newVO.setUserId(oldVO.getUserId());
        newVO.setUserName(oldVO.getUserName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOService convertToApi(
            org.oscm.internal.vo.VOService oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOService newVO = new com.fujitsu.bss.vo.VOService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToApiVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                com.fujitsu.bss.types.enumtypes.OfferingType.class));
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOEventDefinition convertToUp(
            com.fujitsu.bss.vo.VOEventDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOEventDefinition newVO = new org.oscm.internal.vo.VOEventDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEventType(EnumConverter.convert(oldVO.getEventType(),
                org.oscm.internal.types.enumtypes.EventType.class));
        newVO.setEventId(oldVO.getEventId());
        newVO.setEventDescription(oldVO.getEventDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOEventDefinition convertToApi(
            org.oscm.internal.vo.VOEventDefinition oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOEventDefinition newVO = new com.fujitsu.bss.vo.VOEventDefinition();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setEventType(EnumConverter.convert(oldVO.getEventType(),
                com.fujitsu.bss.types.enumtypes.EventType.class));
        newVO.setEventId(oldVO.getEventId());
        newVO.setEventDescription(oldVO.getEventDescription());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOParameter convertToUp(
            com.fujitsu.bss.vo.VOParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOParameter newVO = new org.oscm.internal.vo.VOParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterDefinition(convertToUp(oldVO.getParameterDefinition()));
        newVO.setValue(oldVO.getValue());
        newVO.setConfigurable(oldVO.isConfigurable());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOParameter convertToApi(
            org.oscm.internal.vo.VOParameter oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOParameter newVO = new com.fujitsu.bss.vo.VOParameter();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameterDefinition(convertToApi(oldVO
                .getParameterDefinition()));
        newVO.setValue(oldVO.getValue());
        newVO.setConfigurable(oldVO.isConfigurable());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOPriceModel convertToUp(
            com.fujitsu.bss.vo.VOPriceModel oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOPriceModel newVO = new org.oscm.internal.vo.VOPriceModel();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setDescription(oldVO.getDescription());
        newVO.setConsideredEvents(convertToUpVOPricedEvent(oldVO
                .getConsideredEvents()));
        newVO.setSelectedParameters(convertToUpVOPricedParameter(oldVO
                .getSelectedParameters()));
        newVO.setPeriod(EnumConverter.convert(oldVO.getPeriod(),
                org.oscm.internal.types.enumtypes.PricingPeriod.class));
        newVO.setPricePerPeriod(oldVO.getPricePerPeriod());
        newVO.setPricePerUserAssignment(oldVO.getPricePerUserAssignment());
        newVO.setCurrencyISOCode(oldVO.getCurrencyISOCode());
        newVO.setOneTimeFee(oldVO.getOneTimeFee());
        newVO.setRoleSpecificUserPrices(convertToUpVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        newVO.setSteppedPrices(convertToUpVOSteppedPrice(oldVO
                .getSteppedPrices()));
        newVO.setLicense(oldVO.getLicense());
        newVO.setFreePeriod(oldVO.getFreePeriod());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                org.oscm.internal.types.enumtypes.PriceModelType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOPriceModel convertToApi(
            org.oscm.internal.vo.VOPriceModel oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOPriceModel newVO = new com.fujitsu.bss.vo.VOPriceModel();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setDescription(oldVO.getDescription());
        newVO.setConsideredEvents(convertToApiVOPricedEvent(oldVO
                .getConsideredEvents()));
        newVO.setSelectedParameters(convertToApiVOPricedParameter(oldVO
                .getSelectedParameters()));
        newVO.setPeriod(EnumConverter.convert(oldVO.getPeriod(),
                com.fujitsu.bss.types.enumtypes.PricingPeriod.class));
        newVO.setPricePerPeriod(oldVO.getPricePerPeriod());
        newVO.setPricePerUserAssignment(oldVO.getPricePerUserAssignment());
        newVO.setCurrencyISOCode(oldVO.getCurrencyISOCode());
        newVO.setOneTimeFee(oldVO.getOneTimeFee());
        newVO.setRoleSpecificUserPrices(convertToApiVOPricedRole(oldVO
                .getRoleSpecificUserPrices()));
        newVO.setSteppedPrices(convertToApiVOSteppedPrice(oldVO
                .getSteppedPrices()));
        newVO.setLicense(oldVO.getLicense());
        newVO.setFreePeriod(oldVO.getFreePeriod());
        newVO.setType(EnumConverter.convert(oldVO.getType(),
                com.fujitsu.bss.types.enumtypes.PriceModelType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUsageLicense convertToUp(
            com.fujitsu.bss.vo.VOUsageLicense oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUsageLicense newVO = new org.oscm.internal.vo.VOUsageLicense();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUser(convertToUp(oldVO.getUser()));
        newVO.setApplicationUserId(oldVO.getApplicationUserId());
        newVO.setRoleDefinition(convertToUp(oldVO.getRoleDefinition()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUsageLicense convertToApi(
            org.oscm.internal.vo.VOUsageLicense oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUsageLicense newVO = new com.fujitsu.bss.vo.VOUsageLicense();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setUser(convertToApi(oldVO.getUser()));
        newVO.setApplicationUserId(oldVO.getApplicationUserId());
        newVO.setRoleDefinition(convertToApi(oldVO.getRoleDefinition()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOLocalizedText convertToUp(
            com.fujitsu.bss.vo.VOLocalizedText oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOLocalizedText newVO = new org.oscm.internal.vo.VOLocalizedText();
        newVO.setLocale(oldVO.getLocale());
        newVO.setText(oldVO.getText());
        newVO.setVersion(oldVO.getVersion());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOLocalizedText convertToApi(
            org.oscm.internal.vo.VOLocalizedText oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOLocalizedText newVO = new com.fujitsu.bss.vo.VOLocalizedText();
        newVO.setLocale(oldVO.getLocale());
        newVO.setText(oldVO.getText());
        newVO.setVersion(oldVO.getVersion());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOUser convertToUp(
            com.fujitsu.bss.vo.VOUser oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOUser newVO = new org.oscm.internal.vo.VOUser();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setUserId(oldVO.getUserId());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.UserAccountStatus.class));
        newVO.setOrganizationRoles(EnumConverter.convertSet(
                oldVO.getOrganizationRoles(),
                org.oscm.internal.types.enumtypes.OrganizationRoleType.class));
        newVO.setUserRoles(EnumConverter.convertSet(oldVO.getUserRoles(),
                org.oscm.internal.types.enumtypes.UserRoleType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOUser convertToApi(
            org.oscm.internal.vo.VOUser oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOUser newVO = new com.fujitsu.bss.vo.VOUser();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setUserId(oldVO.getUserId());
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.UserAccountStatus.class));
        newVO.setOrganizationRoles(EnumConverter.convertSet(
                oldVO.getOrganizationRoles(),
                com.fujitsu.bss.types.enumtypes.OrganizationRoleType.class));
        newVO.setUserRoles(EnumConverter.convertSet(oldVO.getUserRoles(),
                com.fujitsu.bss.types.enumtypes.UserRoleType.class));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOCustomerService convertToUp(
            com.fujitsu.bss.vo.VOCustomerService oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOCustomerService newVO = new org.oscm.internal.vo.VOCustomerService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToUpVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                org.oscm.internal.types.enumtypes.OfferingType.class));
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setOrganizationKey(oldVO.getOrganizationKey());
        newVO.setOrganizationName(oldVO.getOrganizationName());
        newVO.setName(oldVO.getName());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOCustomerService convertToApi(
            org.oscm.internal.vo.VOCustomerService oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOCustomerService newVO = new com.fujitsu.bss.vo.VOCustomerService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToApiVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                com.fujitsu.bss.types.enumtypes.OfferingType.class));
        newVO.setOrganizationId(oldVO.getOrganizationId());
        newVO.setOrganizationKey(oldVO.getOrganizationKey());
        newVO.setOrganizationName(oldVO.getOrganizationName());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOSubscriptionIdAndOrganizations convertToUp(
            com.fujitsu.bss.vo.VOSubscriptionIdAndOrganizations oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOSubscriptionIdAndOrganizations newVO = new org.oscm.internal.vo.VOSubscriptionIdAndOrganizations();
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setOrganizations(convertToUpVOOrganization(oldVO
                .getOrganizations()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOSubscriptionIdAndOrganizations convertToApi(
            org.oscm.internal.vo.VOSubscriptionIdAndOrganizations oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOSubscriptionIdAndOrganizations newVO = new com.fujitsu.bss.vo.VOSubscriptionIdAndOrganizations();
        newVO.setSubscriptionId(oldVO.getSubscriptionId());
        newVO.setOrganizations(convertToApiVOOrganization(oldVO
                .getOrganizations()));
        return newVO;
    }

    /**
     * Convert list of VOPricedRole.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOPricedRole> convertToUpVOPricedRole(
            List<com.fujitsu.bss.vo.VOPricedRole> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOPricedRole> newVO = new ArrayList<org.oscm.internal.vo.VOPricedRole>();
        for (com.fujitsu.bss.vo.VOPricedRole tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedRole.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOPricedRole> convertToApiVOPricedRole(
            List<org.oscm.internal.vo.VOPricedRole> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOPricedRole> newVO = new ArrayList<com.fujitsu.bss.vo.VOPricedRole>();
        for (org.oscm.internal.vo.VOPricedRole tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOTechnicalServiceOperation.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOTechnicalServiceOperation> convertToUpVOTechnicalServiceOperation(
            List<com.fujitsu.bss.vo.VOTechnicalServiceOperation> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOTechnicalServiceOperation> newVO = new ArrayList<org.oscm.internal.vo.VOTechnicalServiceOperation>();
        for (com.fujitsu.bss.vo.VOTechnicalServiceOperation tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOTechnicalServiceOperation.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOTechnicalServiceOperation> convertToApiVOTechnicalServiceOperation(
            List<org.oscm.internal.vo.VOTechnicalServiceOperation> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOTechnicalServiceOperation> newVO = new ArrayList<com.fujitsu.bss.vo.VOTechnicalServiceOperation>();
        for (org.oscm.internal.vo.VOTechnicalServiceOperation tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameterDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOParameterDefinition> convertToUpVOParameterDefinition(
            List<com.fujitsu.bss.vo.VOParameterDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOParameterDefinition> newVO = new ArrayList<org.oscm.internal.vo.VOParameterDefinition>();
        for (com.fujitsu.bss.vo.VOParameterDefinition tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameterDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOParameterDefinition> convertToApiVOParameterDefinition(
            List<org.oscm.internal.vo.VOParameterDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOParameterDefinition> newVO = new ArrayList<com.fujitsu.bss.vo.VOParameterDefinition>();
        for (org.oscm.internal.vo.VOParameterDefinition tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOOrganization.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOOrganization> convertToUpVOOrganization(
            List<com.fujitsu.bss.vo.VOOrganization> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOOrganization> newVO = new ArrayList<org.oscm.internal.vo.VOOrganization>();
        for (com.fujitsu.bss.vo.VOOrganization tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOOrganization.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOOrganization> convertToApiVOOrganization(
            List<org.oscm.internal.vo.VOOrganization> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOOrganization> newVO = new ArrayList<com.fujitsu.bss.vo.VOOrganization>();
        for (org.oscm.internal.vo.VOOrganization tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameterOption.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOParameterOption> convertToUpVOParameterOption(
            List<com.fujitsu.bss.vo.VOParameterOption> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOParameterOption> newVO = new ArrayList<org.oscm.internal.vo.VOParameterOption>();
        for (com.fujitsu.bss.vo.VOParameterOption tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameterOption.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOParameterOption> convertToApiVOParameterOption(
            List<org.oscm.internal.vo.VOParameterOption> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOParameterOption> newVO = new ArrayList<com.fujitsu.bss.vo.VOParameterOption>();
        for (org.oscm.internal.vo.VOParameterOption tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedParameter.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOPricedParameter> convertToUpVOPricedParameter(
            List<com.fujitsu.bss.vo.VOPricedParameter> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOPricedParameter> newVO = new ArrayList<org.oscm.internal.vo.VOPricedParameter>();
        for (com.fujitsu.bss.vo.VOPricedParameter tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedParameter.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOPricedParameter> convertToApiVOPricedParameter(
            List<org.oscm.internal.vo.VOPricedParameter> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOPricedParameter> newVO = new ArrayList<com.fujitsu.bss.vo.VOPricedParameter>();
        for (org.oscm.internal.vo.VOPricedParameter tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedOption.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOPricedOption> convertToUpVOPricedOption(
            List<com.fujitsu.bss.vo.VOPricedOption> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOPricedOption> newVO = new ArrayList<org.oscm.internal.vo.VOPricedOption>();
        for (com.fujitsu.bss.vo.VOPricedOption tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedOption.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOPricedOption> convertToApiVOPricedOption(
            List<org.oscm.internal.vo.VOPricedOption> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOPricedOption> newVO = new ArrayList<com.fujitsu.bss.vo.VOPricedOption>();
        for (org.oscm.internal.vo.VOPricedOption tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPaymentType.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static Set<org.oscm.internal.vo.VOPaymentType> convertToUpVOPaymentType(
            Set<com.fujitsu.bss.vo.VOPaymentType> oldVO) {
        if (oldVO == null) {
            return null;
        }
        Set<org.oscm.internal.vo.VOPaymentType> newVO = new HashSet<org.oscm.internal.vo.VOPaymentType>();
        for (com.fujitsu.bss.vo.VOPaymentType tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPaymentType.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static Set<com.fujitsu.bss.vo.VOPaymentType> convertToApiVOPaymentType(
            Set<org.oscm.internal.vo.VOPaymentType> oldVO) {
        if (oldVO == null) {
            return null;
        }
        Set<com.fujitsu.bss.vo.VOPaymentType> newVO = new HashSet<com.fujitsu.bss.vo.VOPaymentType>();
        for (org.oscm.internal.vo.VOPaymentType tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOSteppedPrice.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOSteppedPrice> convertToUpVOSteppedPrice(
            List<com.fujitsu.bss.vo.VOSteppedPrice> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOSteppedPrice> newVO = new ArrayList<org.oscm.internal.vo.VOSteppedPrice>();
        for (com.fujitsu.bss.vo.VOSteppedPrice tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOSteppedPrice.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOSteppedPrice> convertToApiVOSteppedPrice(
            List<org.oscm.internal.vo.VOSteppedPrice> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOSteppedPrice> newVO = new ArrayList<com.fujitsu.bss.vo.VOSteppedPrice>();
        for (org.oscm.internal.vo.VOSteppedPrice tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedEvent.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOPricedEvent> convertToUpVOPricedEvent(
            List<com.fujitsu.bss.vo.VOPricedEvent> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOPricedEvent> newVO = new ArrayList<org.oscm.internal.vo.VOPricedEvent>();
        for (com.fujitsu.bss.vo.VOPricedEvent tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOPricedEvent.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOPricedEvent> convertToApiVOPricedEvent(
            List<org.oscm.internal.vo.VOPricedEvent> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOPricedEvent> newVO = new ArrayList<com.fujitsu.bss.vo.VOPricedEvent>();
        for (org.oscm.internal.vo.VOPricedEvent tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VORoleDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VORoleDefinition> convertToUpVORoleDefinition(
            List<com.fujitsu.bss.vo.VORoleDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VORoleDefinition> newVO = new ArrayList<org.oscm.internal.vo.VORoleDefinition>();
        for (com.fujitsu.bss.vo.VORoleDefinition tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VORoleDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VORoleDefinition> convertToApiVORoleDefinition(
            List<org.oscm.internal.vo.VORoleDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VORoleDefinition> newVO = new ArrayList<com.fujitsu.bss.vo.VORoleDefinition>();
        for (org.oscm.internal.vo.VORoleDefinition tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOEventDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOEventDefinition> convertToUpVOEventDefinition(
            List<com.fujitsu.bss.vo.VOEventDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOEventDefinition> newVO = new ArrayList<org.oscm.internal.vo.VOEventDefinition>();
        for (com.fujitsu.bss.vo.VOEventDefinition tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOEventDefinition.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOEventDefinition> convertToApiVOEventDefinition(
            List<org.oscm.internal.vo.VOEventDefinition> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOEventDefinition> newVO = new ArrayList<com.fujitsu.bss.vo.VOEventDefinition>();
        for (org.oscm.internal.vo.VOEventDefinition tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameter.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOParameter> convertToUpVOParameter(
            List<com.fujitsu.bss.vo.VOParameter> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOParameter> newVO = new ArrayList<org.oscm.internal.vo.VOParameter>();
        for (com.fujitsu.bss.vo.VOParameter tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOParameter.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOParameter> convertToApiVOParameter(
            List<org.oscm.internal.vo.VOParameter> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOParameter> newVO = new ArrayList<com.fujitsu.bss.vo.VOParameter>();
        for (org.oscm.internal.vo.VOParameter tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOUsageLicense.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOUsageLicense> convertToUpVOUsageLicense(
            List<com.fujitsu.bss.vo.VOUsageLicense> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOUsageLicense> newVO = new ArrayList<org.oscm.internal.vo.VOUsageLicense>();
        for (com.fujitsu.bss.vo.VOUsageLicense tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOUsageLicense.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOUsageLicense> convertToVOUsageLicense(
            List<org.oscm.internal.vo.VOUsageLicense> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOUsageLicense> newVO = new ArrayList<com.fujitsu.bss.vo.VOUsageLicense>();
        for (org.oscm.internal.vo.VOUsageLicense tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOLocalizedText.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOLocalizedText> convertToUpVOLocalizedText(
            List<com.fujitsu.bss.vo.VOLocalizedText> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOLocalizedText> newVO = new ArrayList<org.oscm.internal.vo.VOLocalizedText>();
        for (com.fujitsu.bss.vo.VOLocalizedText tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert list of VOLocalizedText.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOLocalizedText> convertToVOLocalizedText(
            List<org.oscm.internal.vo.VOLocalizedText> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOLocalizedText> newVO = new ArrayList<com.fujitsu.bss.vo.VOLocalizedText>();
        for (org.oscm.internal.vo.VOLocalizedText tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOCatalogEntry convertToUp(
            com.fujitsu.bss.vo.VOCatalogEntry oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOCatalogEntry newVO = new org.oscm.internal.vo.VOCatalogEntry();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setMarketplace(convertToUp(oldVO.getMarketplace()));
        newVO.setAnonymousVisible(oldVO.isAnonymousVisible());
        newVO.setCategories(convertToUpVOCategory(oldVO.getCategories()));
        newVO.setVisibleInCatalog(oldVO.isVisibleInCatalog());
        newVO.setService(convertToUp(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert list of VOCategory.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<org.oscm.internal.vo.VOCategory> convertToUpVOCategory(
            List<com.fujitsu.bss.vo.VOCategory> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<org.oscm.internal.vo.VOCategory> newVO = new ArrayList<org.oscm.internal.vo.VOCategory>();
        for (com.fujitsu.bss.vo.VOCategory tmp : oldVO) {
            newVO.add(convertToUp(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOCatalogEntry convertToApi(
            org.oscm.internal.vo.VOCatalogEntry oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOCatalogEntry newVO = new com.fujitsu.bss.vo.VOCatalogEntry();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setMarketplace(convertToApi(oldVO.getMarketplace()));
        newVO.setAnonymousVisible(oldVO.isAnonymousVisible());
        newVO.setCategories(convertToApiVOCategory(oldVO.getCategories()));
        newVO.setVisibleInCatalog(oldVO.isVisibleInCatalog());
        newVO.setService(convertToApi(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert list of VOCategory.
     * 
     * @param oldVO
     *            List of VO to convert.
     * @return Converted list of VO.
     */
    public static List<com.fujitsu.bss.vo.VOCategory> convertToApiVOCategory(
            List<org.oscm.internal.vo.VOCategory> oldVO) {
        if (oldVO == null) {
            return null;
        }
        List<com.fujitsu.bss.vo.VOCategory> newVO = new ArrayList<com.fujitsu.bss.vo.VOCategory>();
        for (org.oscm.internal.vo.VOCategory tmp : oldVO) {
            newVO.add(convertToApi(tmp));
        }
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOCategory convertToUp(
            com.fujitsu.bss.vo.VOCategory oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOCategory newVO = new org.oscm.internal.vo.VOCategory();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setCategoryId(oldVO.getCategoryId());
        newVO.setMarketplaceId(oldVO.getMarketplaceId());
        newVO.setName(oldVO.getName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOCategory convertToApi(
            org.oscm.internal.vo.VOCategory oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOCategory newVO = new com.fujitsu.bss.vo.VOCategory();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setCategoryId(oldVO.getCategoryId());
        newVO.setMarketplaceId(oldVO.getMarketplaceId());
        newVO.setName(oldVO.getName());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOTag convertToUp(
            com.fujitsu.bss.vo.VOTag oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOTag newVO = new org.oscm.internal.vo.VOTag();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setLocale(oldVO.getLocale());
        newVO.setValue(oldVO.getValue());
        newVO.setNumberReferences(oldVO.getNumberReferences());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOTag convertToApi(
            org.oscm.internal.vo.VOTag oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOTag newVO = new com.fujitsu.bss.vo.VOTag();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setLocale(oldVO.getLocale());
        newVO.setValue(oldVO.getValue());
        newVO.setNumberReferences(oldVO.getNumberReferences());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOCompatibleService convertToUp(
            com.fujitsu.bss.vo.VOCompatibleService oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOCompatibleService newVO = new org.oscm.internal.vo.VOCompatibleService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToUpVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToUp(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                org.oscm.internal.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                org.oscm.internal.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                org.oscm.internal.types.enumtypes.OfferingType.class));
        newVO.setCompatible(oldVO.isCompatible());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOCompatibleService convertToApi(
            org.oscm.internal.vo.VOCompatibleService oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOCompatibleService newVO = new com.fujitsu.bss.vo.VOCompatibleService();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setParameters(convertToApiVOParameter(oldVO.getParameters()));
        newVO.setDescription(oldVO.getDescription());
        newVO.setName(oldVO.getName());
        newVO.setServiceId(oldVO.getServiceId());
        newVO.setTechnicalId(oldVO.getTechnicalId());
        newVO.setFeatureURL(oldVO.getFeatureURL());
        newVO.setBaseURL(oldVO.getBaseURL());
        newVO.setPriceModel(convertToApi(oldVO.getPriceModel()));
        newVO.setStatus(EnumConverter.convert(oldVO.getStatus(),
                com.fujitsu.bss.types.enumtypes.ServiceStatus.class));
        newVO.setAccessType(EnumConverter.convert(oldVO.getAccessType(),
                com.fujitsu.bss.types.enumtypes.ServiceAccessType.class));
        newVO.setSellerId(oldVO.getSellerId());
        newVO.setSellerName(oldVO.getSellerName());
        newVO.setSellerKey(oldVO.getSellerKey());
        newVO.setTags(oldVO.getTags());
        newVO.setShortDescription(oldVO.getShortDescription());
        newVO.setAverageRating(oldVO.getAverageRating());
        newVO.setNumberOfReviews(oldVO.getNumberOfReviews());
        newVO.setOfferingType(EnumConverter.convert(oldVO.getOfferingType(),
                com.fujitsu.bss.types.enumtypes.OfferingType.class));
        newVO.setCompatible(oldVO.isCompatible());
        newVO.setConfiguratorUrl(oldVO.getConfiguratorUrl());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOCountryVatRate convertToUp(
            com.fujitsu.bss.vo.VOCountryVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOCountryVatRate newVO = new org.oscm.internal.vo.VOCountryVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        newVO.setCountry(oldVO.getCountry());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOCountryVatRate convertToApi(
            org.oscm.internal.vo.VOCountryVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOCountryVatRate newVO = new com.fujitsu.bss.vo.VOCountryVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        newVO.setCountry(oldVO.getCountry());
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOOrganizationVatRate convertToUp(
            com.fujitsu.bss.vo.VOOrganizationVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOOrganizationVatRate newVO = new org.oscm.internal.vo.VOOrganizationVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        newVO.setOrganization(convertToUp(oldVO.getOrganization()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOOrganizationVatRate convertToApi(
            org.oscm.internal.vo.VOOrganizationVatRate oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOOrganizationVatRate newVO = new com.fujitsu.bss.vo.VOOrganizationVatRate();
        newVO.setKey(oldVO.getKey());
        newVO.setVersion(oldVO.getVersion());
        newVO.setRate(oldVO.getRate());
        newVO.setOrganization(convertToApi(oldVO.getOrganization()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServiceActivation convertToUp(
            com.fujitsu.bss.vo.VOServiceActivation oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServiceActivation newVO = new org.oscm.internal.vo.VOServiceActivation();
        newVO.setActive(oldVO.isActive());
        newVO.setCatalogEntries(convertToUpVOCatalogEntry(oldVO
                .getCatalogEntries()));
        newVO.setService(convertToUp(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServiceActivation convertToApi(
            org.oscm.internal.vo.VOServiceActivation oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServiceActivation newVO = new com.fujitsu.bss.vo.VOServiceActivation();
        newVO.setActive(oldVO.isActive());
        newVO.setCatalogEntries(convertToApiVOCatalogEntry(oldVO
                .getCatalogEntries()));
        newVO.setService(convertToApi(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static org.oscm.internal.vo.VOServicePaymentConfiguration convertToUp(
            com.fujitsu.bss.vo.VOServicePaymentConfiguration oldVO) {
        if (oldVO == null) {
            return null;
        }
        org.oscm.internal.vo.VOServicePaymentConfiguration newVO = new org.oscm.internal.vo.VOServicePaymentConfiguration();
        newVO.setEnabledPaymentTypes(convertToUpVOPaymentType(oldVO
                .getEnabledPaymentTypes()));
        newVO.setService(convertToUp(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     */
    public static com.fujitsu.bss.vo.VOServicePaymentConfiguration convertToApi(
            org.oscm.internal.vo.VOServicePaymentConfiguration oldVO) {
        if (oldVO == null) {
            return null;
        }
        com.fujitsu.bss.vo.VOServicePaymentConfiguration newVO = new com.fujitsu.bss.vo.VOServicePaymentConfiguration();
        newVO.setEnabledPaymentTypes(convertToApiVOPaymentType(oldVO
                .getEnabledPaymentTypes()));
        newVO.setService(convertToApi(oldVO.getService()));
        return newVO;
    }

    /**
     * Convert source version VO to target version VO.
     * 
     * @param oldVO
     *            VO to convert.
     * @return VO of target version.
     * @throws OperationNotPermittedException
     */

}
