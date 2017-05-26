CREATE TABLE "templatefile"
(
  "tkey" BIGINT NOT NULL, 
  "filename" character varying(255) NOT NULL,
  "content" OID NOT NULL,
  "lastchange" timestamp NOT NULL,
  "controllerid" character varying(255),
  CONSTRAINT "templatefile_pk" PRIMARY KEY ("tkey"),
  CONSTRAINT "templatefile_uc" UNIQUE ("filename", "controllerid")
);
