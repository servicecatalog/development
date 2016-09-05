CREATE TABLE "tenant" (
		"tkey" BIGINT NOT NULL,
		"version" INTEGER DEFAULT 0 NOT NULL,
		"tenantid" character varying(255) NOT NULL,
		"description" character varying(255),
		"idp" character varying(255)
	);
ALTER TABLE "tenant" ADD CONSTRAINT "tenant_pk" PRIMARY KEY ("tkey");

ALTER TABLE "marketplace" ADD COLUMN "tenant_tkey" BIGINT;

ALTER TABLE "marketplace" ADD CONSTRAINT "marketplace_tenant_fk" FOREIGN KEY ("tenant_tkey")
	REFERENCES "tenant" ("tkey");

ALTER TABLE "organization" ADD COLUMN "tenant_tkey" BIGINT;

ALTER TABLE "organization" ADD CONSTRAINT "organization_tenant_fk" FOREIGN KEY ("tenant_tkey")
  REFERENCES "tenant" ("tkey");

ALTER TABLE "platformuser" ADD COLUMN "tenant_tkey" BIGINT;

ALTER TABLE "platformuser" ADD CONSTRAINT "platformuser_tenant_fk" FOREIGN KEY ("tenant_tkey")
    REFERENCES "tenant" ("tkey");

ALTER TABLE "platformuser" ADD CONSTRAINT "pl_userid_tenantkey_uk" UNIQUE ("userid", "tenant_tkey");

CREATE TABLE "tenantsetting"
(
  "tkey" BIGINT NOT NULL,
  "version" INTEGER DEFAULT 0 NOT NULL ,
  "name" character varying(255) NOT NULL,
  "value" character varying(255),
  "tenant_tkey" bigint);

ALTER TABLE "tenantsetting" ADD CONSTRAINT "tenantsetting_pk" PRIMARY KEY ("tkey");

ALTER TABLE "tenantsetting" ADD CONSTRAINT "tenantsetting_tenant_fk" FOREIGN KEY ("tenant_tkey")
    REFERENCES "tenant" ("tkey");

INSERT INTO "hibernate_sequences" ("sequence_name", "sequence_next_hi_value") SELECT 'Tenant', COALESCE((MAX(tkey)
    /1000),0)+10 FROM "tenant";

INSERT INTO tenant(tkey, version, tenantid, description) VALUES (0, 1, 'default', 'platform tenant');
update organization set tenant_tkey=1;
update marketplace set tenant_tkey=1;
update platform_user set tenant_tkey=1;

insert into bssuser.tenantsetting select 1,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_IDP_URL';
insert into bssuser.tenantsetting select 2,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_ISSUER_ID';
insert into bssuser.tenantsetting select 3,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_IDP_AUTHENTICATION_REQUEST_HTTP_METHOD';
insert into bssuser.tenantsetting select 4,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_LOGOUT_URL';
insert into bssuser.tenantsetting select 5,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_SIGNING_KEY_ALIAS';
insert into bssuser.tenantsetting select 6,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_SIGNING_KEYSTORE_PASS';
insert into bssuser.tenantsetting select 7,1,information_id,env_value, 1 from bssuser.configurationsetting
where information_id='SSO_SIGNING_KEYSTORE';


INSERT INTO "hibernate_sequences" ("sequence_name", "sequence_next_hi_value") SELECT 'TenantSetting', COALESCE(
    (MAX(tkey)/1000),0)+10 FROM "tenantsetting";
