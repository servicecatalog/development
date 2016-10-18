CREATE TABLE "customsetting"
(
  "settingkey" character varying(255) NOT NULL,
  "settingvalue" text NOT NULL,
  "organizationid" character varying(255) NOT NULL,
  CONSTRAINT "customsetting_pk" PRIMARY KEY ("settingkey", "organizationid")
);