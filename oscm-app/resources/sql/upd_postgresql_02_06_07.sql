UPDATE "instanceparameter" SET "parameterkey" = 'SUBNET' WHERE "parameterkey" = 'subnet';
UPDATE "instanceparameter" SET "parameterkey" = 'PUBLIC_IP' WHERE "parameterkey" = 'publicIp';
UPDATE "instanceparameter" SET "parameterkey" = 'DISK_SIZE' WHERE "parameterkey" = 'diskSize';
UPDATE "instanceparameter" SET "parameterkey" = 'INSTANCE_PLATFORM' WHERE "parameterkey" = 'instancePlatform';
UPDATE "instanceparameter" SET "parameterkey" = 'EAI_INSTANCE_PUBLIC_DNS' WHERE "parameterkey" = 'instancePublicDns';
UPDATE "instanceparameter" SET "parameterkey" = 'SNAPSHOT_ID' WHERE "parameterkey" = 'snapshotId';

UPDATE "instanceattribute" SET "attributekey" = 'SUBNET' WHERE "attributekey" = 'subnet';
UPDATE "instanceattribute" SET "attributekey" = 'PUBLIC_IP' WHERE "attributekey" = 'publicIp';
UPDATE "instanceattribute" SET "attributekey" = 'DISK_SIZE' WHERE "attributekey" = 'diskSize';
UPDATE "instanceattribute" SET "attributekey" = 'INSTANCE_PLATFORM' WHERE "attributekey" = 'instancePlatform';
UPDATE "instanceattribute" SET "attributekey" = 'EAI_INSTANCE_PUBLIC_DNS' WHERE "attributekey" = 'instancePublicDns';
UPDATE "instanceattribute" SET "attributekey" = 'SNAPSHOT_ID' WHERE "attributekey" = 'snapshotId';

UPDATE "customattribute" SET "attributekey" = 'SUBNET' WHERE "attributekey" = 'subnet';
UPDATE "customattribute" SET "attributekey" = 'PUBLIC_IP' WHERE "attributekey" = 'publicIp';
UPDATE "customattribute" SET "attributekey" = 'DISK_SIZE' WHERE "attributekey" = 'diskSize';
UPDATE "customattribute" SET "attributekey" = 'INSTANCE_PLATFORM' WHERE "attributekey" = 'instancePlatform';
UPDATE "customattribute" SET "attributekey" = 'EAI_INSTANCE_PUBLIC_DNS' WHERE "attributekey" = 'instancePublicDns';
UPDATE "customattribute" SET "attributekey" = 'SNAPSHOT_ID' WHERE "attributekey" = 'snapshotId';