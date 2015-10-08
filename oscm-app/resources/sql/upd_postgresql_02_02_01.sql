ALTER TABLE "serviceinstance" RENAME COLUMN "iaasinstanceid" TO "instanceid";
ALTER TABLE "serviceinstance" ADD COLUMN "controllerid" VARCHAR(255) NOT NULL DEFAULT 'ess.vmware';
ALTER TABLE "serviceinstance" ADD COLUMN "instanceprovisioning" BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE "serviceinstance" ADD COLUMN "controllerready" BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE "serviceinstance" ADD CONSTRAINT "subscription_id_uc" UNIQUE ("organizationid", "subscriptionid");
ALTER TABLE "serviceinstance" ADD CONSTRAINT "instance_id_uc" UNIQUE("instanceid", "controllerid");

ALTER TABLE "configurationsetting" ADD COLUMN "controllerid" VARCHAR(255) NOT NULL DEFAULT 'PROXY';
DELETE FROM "configurationsetting" WHERE settingkey='IAAS_PROXY_PLATFORM_CONTROLLER_IMPLEMENTATION';

UPDATE "configurationsetting" SET settingkey='APP_BASE_URL' WHERE settingkey='IAAS_PROXY_BASE_URL';
UPDATE "configurationsetting" SET settingkey='APP_TIMER_INTERVAL' WHERE settingkey='IAAS_PROXY_TIMER_INTERVAL';
UPDATE "configurationsetting" SET settingkey='APP_PROVISIONING_ON_INSTANCE' WHERE settingkey='IAAS_PROXY_SERVICE_PROVISIONING';
UPDATE "configurationsetting" SET settingkey='APP_MAIL_RESOURCE' WHERE settingkey='IAAS_PROXY_MAIL_RESOURCE';
UPDATE "configurationsetting" SET settingkey='BSS_WEBSERVICE_URL' WHERE settingkey='BES_WEBSERVICE_URL';

ALTER TABLE "configurationsetting" DROP CONSTRAINT configsetting_pk;
ALTER TABLE "configurationsetting" ADD CONSTRAINT configsetting_pk PRIMARY KEY(settingkey, controllerid);

UPDATE "configurationsetting" SET controllerid='ess.vmware' WHERE substring(settingkey from 1 for 7) ='VMWAPI_' OR settingkey='APP_PROVISIONING_ON_INSTANCE';
