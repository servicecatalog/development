ALTER TABLE "serviceinstance" DROP COLUMN "baseurl";
ALTER TABLE "serviceinstance" ALTER COLUMN "serviceaccessinfo" TYPE VARCHAR(4096);
