ALTER TABLE "operation" ADD COLUMN "userid" VARCHAR(255) NOT NULL;
ALTER TABLE "operation" ADD COLUMN "transactionid" VARCHAR(255);
ALTER TABLE "operation" ALTER COLUMN "parameters" DROP NOT NULL;
