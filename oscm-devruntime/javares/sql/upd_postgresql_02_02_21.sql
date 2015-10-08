ALTER TABLE "billingresult" ADD COLUMN "netamount" numeric(19,2);

run:MigrationBillingResultNetAmount;

ALTER TABLE "billingresult" ALTER COLUMN "netamount" SET NOT NULL;
