CREATE TABLE "templatefile"
(
  "tkey" BIGINT NOT NULL, 
  "attributevalue" text,
  "filename" character varying(255) NOT NULL,
  "content" TEXT NOT NULL,
  "controllerid" character varying(255),
  CONSTRAINT "templatefile_pk" PRIMARY KEY ("tkey")
  CONSTRAINT "instance_attr_uc" UNIQUE ("filename", "controllerid")
);