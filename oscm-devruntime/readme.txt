This project provides scripts to 
- prepare a datasource in the glassfish application server.

The scripts can be adapted to the local environment via environment variables. 
This means you have to set those environment variables in your run configuration before
you execute the script. Check the developer documentation for more details on this.

---------------------------------------------------------------------------------------------------

The script createGlassfishDatasource.bat is used to create a connection pool and a data source named
'SaaSDS' in your application server environment. It uses a embedded derby database, so no database
specific settings have to be provided.

The following environment variables need to be set:

GLASSFISH_HOME			points to the root folder of the glassfish installation to be used, 
							e.g. "C:\glassfish"
GF_ADMIN_DOMAIN_PORT	the port to access the admin console of the domain to be used, 
							e.g. 4848			

							