ALTER TABLE "subscription" ADD COLUMN "vmsnumber" INTEGER NOT NULL DEFAULT -1;
ALTER TABLE "subscriptionhistory" ADD COLUMN "vmsnumber" INTEGER NOT NULL DEFAULT -1;