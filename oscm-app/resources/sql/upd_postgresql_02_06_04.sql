CREATE TABLE "customsetting"
(
  "settingkey" character varying(255) NOT NULL,
  "settingvalue" text NOT NULL,
  "organizationid" character varying(255) NOT NULL,
  CONSTRAINT configsetting_pk PRIMARY KEY (settingkey, controllerid)
)