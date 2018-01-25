ALTER TABLE "subscription" ADD COLUMN "lastusedoperationid" VARCHAR(255) DEFAULT NULL;
ALTER TABLE "subscriptionhistory" ADD COLUMN "lastusedoperationid" VARCHAR(255) DEFAULT NULL;
