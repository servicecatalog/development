-- sript to remove sensitive data from a productive database
-- please adapt to your needs

-- change email addresses of all users
update bssuser.platformuser set email = 'christoph.held@est.fujitsu.com';
update bssuser.platformuserhistory set email = 'christoph.held@est.fujitsu.com';

-- change email address of all organiztaions
update bssuser.organization set email = 'christoph.held@est.fujitsu.com' where email<>'null' and email<>'';
update bssuser.organizationhistory set email = 'christoph.held@est.fujitsu.com' where email<>'null' and email<>'';

-- update config settings
update bssuser.configurationsetting set env_value='christoph.held@est.fujitsu.com' where information_id = 'MAIL_RESPONSE_ADDRESS';
update bssuser.configurationsetting set env_value='http://estcheld:8180/oscm-portal' where information_id = 'BASE_URL';
update bssuser.configurationsetting set env_value='https://estcheld.intern.est.fujitsu.com:8181/oscm-portal' where information_id = 'BASE_URL_HTTPS';
update bssuser.configurationsetting set env_value='estdevmail1' where information_id = 'MAIL_SERVER';
update bssuser.configurationsetting set env_value='25' where information_id = 'MAIL_PORT';
update bssuser.configurationsetting set env_value='http://estcheld:8180/Report/ReportingServiceBean?wsdl' where information_id = 'REPORT_WSDLURL';
update bssuser.configurationsetting set env_value='http://estcheld:8180/Report/ReportingServiceBean' where information_id = 'REPORT_SOAP_ENDPOINT';
update bssuser.configurationsetting set env_value='estcheld' where information_id = 'SEARCH_INDEX_MASTER_HOST';
update bssuser.configurationsetting set env_value='8437' where information_id = 'SEARCH_INDEX_MASTER_PORT';

----------------------------------------------------------------------
-- now run update schema
----------------------------------------------------------------------

-----------------------------------------------------------------------
-- self register user=pwdtemplate pwd=admin123 and change all passwords
-----------------------------------------------------------------------

update bssuser.platformuser set passwordhash = (select passwordhash from bssuser.platformuser where userid='pwdtemplate')
update bssuser.platformuser set passwordsalt = (select passwordsalt from bssuser.platformuser where userid='pwdtemplate')
