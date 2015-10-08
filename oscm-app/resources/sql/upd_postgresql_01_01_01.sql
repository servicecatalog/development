-- table creation

create table "configurationsetting" (
	"settingkey" VARCHAR(255) NOT NULL, 
	"settingvalue" TEXT NOT NULL
);

CREATE TABLE "instanceparameter" (
	"tkey" BIGINT NOT NULL, 
	"serviceinstance_tkey" BIGINT NOT NULL, 
	"parameterkey" VARCHAR(255) NOT NULL, 
	"parametervalue" TEXT NOT NULL
);

CREATE TABLE "serviceinstance" (
	"tkey" BIGINT NOT NULL, 
	"subscriptionid" VARCHAR(255) NOT NULL, 
	"organizationid" VARCHAR(255) NOT NULL, 
	"defaultlocale" VARCHAR(64) NOT NULL, 
	"besloginurl" VARCHAR(255), 
	"baseurl" VARCHAR(255) NOT NULL, 
	"organizationname" VARCHAR(255), 
	"requesttime" BIGINT NOT NULL, 
	"provisioningstatus" VARCHAR(255) NOT NULL, 
	"iaasinstanceid" VARCHAR(255) NOT NULL, 
	"serviceaccessinfo" VARCHAR(255), 
	"servicebaseurl" VARCHAR(255), 
	"serviceloginpath" VARCHAR(255),
	"locked" BOOLEAN NOT NULL DEFAULT FALSE,
	"runwithtimer" BOOLEAN NOT NULL DEFAULT TRUE	
);

CREATE TABLE "version" (
	"productmajorversion" INTEGER NOT NULL, 
	"productminorversion" INTEGER NOT NULL, 
	"schemaversion" INTEGER NOT NULL, 
	"migrationdate" TIMESTAMP
);

CREATE TABLE "hibernate_sequences" (
	"sequence_name" VARCHAR(255), 
	"sequence_next_hi_value" INTEGER
);

-- primary key definitions

ALTER TABLE "configurationsetting" ADD CONSTRAINT "configsetting_pk" PRIMARY KEY ("settingkey");

ALTER TABLE "instanceparameter" ADD CONSTRAINT "instanceparam_pk" PRIMARY KEY ("tkey");

ALTER TABLE "serviceinstance" ADD CONSTRAINT "serviceinst_pk" PRIMARY KEY ("tkey");

-- foreign key constraints

ALTER TABLE "instanceparameter" ADD CONSTRAINT "instanceparam_serviceinst_fk" FOREIGN KEY ("serviceinstance_tkey") REFERENCES "serviceinstance"("tkey");

-- unique constraint

ALTER TABLE "instanceparameter" ADD CONSTRAINT "instance_param_uc" UNIQUE ("serviceinstance_tkey", "parameterkey");