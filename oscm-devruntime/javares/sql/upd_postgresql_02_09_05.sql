ALTER TABLE technicalproduct ADD COLUMN customtabname character varying(40) DEFAULT NULL;
ALTER TABLE technicalproduct ADD COLUMN customtaburl character varying(255) DEFAULT NULL;
ALTER TABLE technicalproducthistory ADD COLUMN customtabname character varying(40) DEFAULT NULL;
ALTER TABLE technicalproducthistory ADD COLUMN customtaburl character varying(255) DEFAULT NULL;
