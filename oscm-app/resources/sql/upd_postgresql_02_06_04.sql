CREATE TABLE "customattribute"
(
  "attributekey" character varying(255) NOT NULL,
  "attributevalue" text,
  "organizationid" character varying(255) NOT NULL,
  "encrypted" BOOLEAN NOT NULL,
  "controllerid" character varying(255),
  CONSTRAINT "customattribute_pk" PRIMARY KEY ("attributekey", "organizationid")
);

CREATE TABLE "instanceattribute"
(
  "tkey" bigint NOT NULL,
  "serviceinstance_tkey" bigint NOT NULL,
  "attributekey" character varying(255) NOT NULL,
  "attributevalue" text,
  "encrypted" BOOLEAN NOT NULL,
  "controllerid" character varying(255),
  CONSTRAINT "instanceattr_pk" PRIMARY KEY ("tkey"),
  CONSTRAINT "instanceattr_serviceinst_fk" FOREIGN KEY ("serviceinstance_tkey")
      REFERENCES "serviceinstance" ("tkey") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "instance_attr_uc" UNIQUE ("serviceinstance_tkey", "attributekey")
);

ALTER TABLE "serviceinstance" add "referenceid" character varying(255);
ALTER TABLE "instanceparameter" ADD COLUMN "encrypted" BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE "instanceparameter" SET "encrypted" = TRUE WHERE "parameterkey" like '%_PWD';