CREATE TABLE "customattribute"
(
  "attributekey" character varying(255) NOT NULL,
  "attributevalue" text,
  "organizationid" character varying(255) NOT NULL,
  CONSTRAINT "customattribute_pk" PRIMARY KEY ("attributekey", "organizationid")
);

CREATE TABLE "instanceattribute"
(
  "tkey" bigint NOT NULL,
  "serviceinstance_tkey" bigint NOT NULL,
  "attributekey" character varying(255) NOT NULL,
  "attributevalue" text,
  CONSTRAINT "instanceattr_pk" PRIMARY KEY ("tkey"),
  CONSTRAINT "instanceattr_serviceinst_fk" FOREIGN KEY ("serviceinstance_tkey")
      REFERENCES "serviceinstance" ("tkey") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "instance_attr_uc" UNIQUE ("serviceinstance_tkey", "attributekey")
);

ALTER TABLE "serviceinstance" add "referenceid" character varying(255);