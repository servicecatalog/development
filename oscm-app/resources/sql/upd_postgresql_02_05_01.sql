CREATE TABLE "operation" (
  "tkey" BIGINT NOT NULL, 
  "serviceinstance_tkey" BIGINT NOT NULL, 
  "operationid" VARCHAR(255) NOT NULL, 
  "parameters" TEXT NOT NULL
);

ALTER TABLE "operation" ADD CONSTRAINT "operation_pk" PRIMARY KEY ("tkey");
ALTER TABLE "operation" ADD CONSTRAINT "operation_serviceinst_fk" FOREIGN KEY ("serviceinstance_tkey") REFERENCES "serviceinstance"("tkey");