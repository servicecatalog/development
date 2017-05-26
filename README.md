[![Build Status](https://travis-ci.org/servicecatalog/development.svg?branch=master)](https://travis-ci.org/servicecatalog/development)

<p align="center"><h1><img height="52" src="https://avatars0.githubusercontent.com/u/14330878" alt="Open Service Catalog Manager"/>&nbsp;Open Service Catalog Manager</h1></p> 

Open Service Catalog Manager (OSCM) is an open source application with enterprise quality level. It supports bright spectrum of use cases, from SaaS Marketplace to Enterprise IaaS Store. It offers ready-to-use service provisioning adapters for IaaS providers like Amazon AWS and OpenStack, but is open for integrating other platforms.

The Service Providers can define their services with flexible price plans and publish them to the OSCM Marketplaces. The Service Provider can decide on using the OSCM Billing Engine for the service usage cost calculation or integrate external one. The End-Users can subscribe and use the services.

OSCM supports configurable authentication. It can use own user store, but also existing LDAP systems. OSCM can be participant in an identity federation using SAML2 profiles for SSO (Single-Sign On) and integrated with any SAML2-enabled authentication server (e.g OpenAM, ADFS)

More details on [OSCM homepage](http://openservicecatalogmanager.org/).

## Contributions
All contributions are welcome - Open Service Catalog Manager uses the Apache 2.0 license and requires the contributor to agree with the [OSCM Individual CLA (ICLA)](https://github.com/servicecatalog/development/blob/master/ICLA.txt). If the contributor submits patches on behalf of a company, then additionally the [OSCM Corporate CLA (CCLA)](https://github.com/servicecatalog/development/blob/master/CCLA.txt) must be agreed. Even if the contributor is included in such CCLA, she/he is still required to agree with the ICLA. To submit the CLAs please:
* download the [ICLA.txt](https://github.com/servicecatalog/development/blob/master/ICLA.txt) and if needed the [CCLA.txt](https://github.com/servicecatalog/development/blob/master/CCLA.txt)
* fill in the required information and sign them
* scan them as pdf files and email them to secretary-oscm@ml.css.fujitsu.com. We will reply to you as soon as possible.

## Releases
Newest releases can be found [here](https://github.com/servicecatalog/development/releases).

## Getting started and building from sources
Please follow the guide from top to bottom, this is the easiest way to avoid errors later on.

#### Prerequisites
* Installed [JDK 8u121](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html#jdk-8u121-oth-JPR)
or higher.

* [PostgreSQL 9.3](http://www.enterprisedb.com/products-services-training/pgdownload) database installer.
* [GlassFish 4.1.2](http://download.oracle.com/glassfish/4.1.2/release/index.html) server installer.
* [Eclipse ECJ 4.2.1](http://mvnrepository.com/artifact/org.eclipse.jdt.core.compiler/ecj/4.2.1) library.
* [Apache Ivy 2.4.0](http://www.apache.org/dist/ant/ivy/2.4.0/) library.

#### Setting up a workspace
1. Download the latest sources for [this](https://github.com/servicecatalog/development) and [documentation](https://github.com/servicecatalog/documentation) repositories. Set the directory name of documentation as "document".
2. Import the project into your IDE. You should adjust some of the preferences:
  * Set the compiler level to the installed version of Java 1.7.
  * Set UTF-8 file encoding and Unix line endings.
3. Import and configure the code formatting rules and code templates.
  * Download the files from the [codestyle folder](https://github.com/servicecatalog/development/tree/master/oscm-devruntime/javares/codestyle).
  * Import them into your Eclipse IDE ([Help](https://github.com/servicecatalog/development/tree/master/oscm-devruntime/javares/codestyle/README.md))
  * Configure the formatting for non-java files ([Rules and Help](https://github.com/servicecatalog/development/tree/master/oscm-devruntime/javares/codestyle/README.md))

#### Setting up the database
1. Install the database using a path without any whitespaces for installation directory. During installation a system-startup service and a database specific user should be created.
2. Update `<postgres-root-dir>/data/postgresql.conf` properties:

| Property  | Value |  Comment  |
| ------------- | ------------- | ------------- |
| `max_prepared_transactions`  | `50`  |  Sets the maximum number of transactions that can be in the "prepared" state simultaneously.  |
| `max_connections`  | `210`  |  Determines the maximum number of concurrent connections to the database server.  |
| `listen_addresses`  |  `'*'`  |  Specifies the TCP/IP address(es) on which the server is to listen for connections from client applications.  |

3. Update `<postgres-root-dir>/data/pg_hba.conf` properties:

```
host all all 127.0.0.1/32 trust
host all all 0.0.0.0/0 trust
host all all <host-ipv6>/128 trust
```

4. Confirm all changes and restart PostgreSQL service to apply changes.

#### Setting up the mail server
1. Download and install any mail server.
2. Create any domain and at least one user account in it.

#### Setting up server
1. Install the GlassFish server following instructions.
2. Check if the Java location is valid in the following configuration files:
```
<glassfish-root-dir>/glassfish/config/asenv.bat
<glassfish-root-dir>/glassfish/config/asenv.con
<glassfish-root-dir>/mq/etc/imqenv.conf
```

#### Building the application
1. Update the properties in `/oscm-devruntime/javares/local/`. You can also look for examples in this directory:

| Property  | Note |
| ------------- | ------------- |
| `/oscm-devruntime/javares/local/<hostname>/db.properties` | Database connection details.  |
| `/oscm-devruntime/javares/local/<hostname>/db-app.properties`  | Database connection details.  |
| `/oscm-devruntime/javares/local/<hostname>/integration.properties`  |  Mostly server settings like ports etc.  |
| `/oscm-devruntime/javares/local/<hostname>/configsettings.properties`  |  Mostly server settings like ports etc. |
| `/oscm-devruntime/javares/local/property-templates`  |  Folder containing templates files.  |

2. Add the Eclipse ECJ and Apache Ivy libraries to Ant runtime in your IDE.
3. Add the following arguments to JVM: `-Dhttp.proxyHost=<proxy-host> -Dhttp.proxyPort=8080`.
4. Add the following scripts to Ant view in your IDE:

| Script  | Note |
| ------------- | ------------- |
| `/oscm-devruntime/javares/devscripts/build-dev-Database.xml` | Handles database operations, e.g. initialization, schema update etc.  |
| `/oscm-devruntime/javares/devscripts/build-dev-GlassFish.xml`  | Handles server tasks like starting or stopping domains.  |
| `/oscm-devruntime/javares/devscripts/build-dev-PackageDeploy.xml`  |  Used to build application source code and to deploy its artifacts. Add "proxyuser" and "proxypassword" properties to setproxy tag if your proxy needs authentication. |
| `/oscm-portal-webtests/run_in_eclipse.xml`  |  Executes UI tests. |
| `/oscm-integrationtests-setup/resources/build.xml`  |  Used to create all neccessary resources for integration environment.  |
5. Build the source code of the application using `All.BUILD` target from `/oscm-devruntime/javares/devscripts/build-dev-PackageDeploy.xml`. The result will be located in `/oscm-build/result`. If you encounter an out of memory error, increase the VM heap size by using the -Xmx argument in the configuration of your java runtime for executing ANT.
6. Create the database and server resources using `STANDALONE.setup` target from `/oscm-integrationtests-setup/build.xml`. It will also deploy all artifacts to the appropriate domains.

#### Deploying the application

After the environment is set, developers can use the Ant targets to build/redeploy only specific modules. For example, to redeploy the portal `Portal.BUILD` and then `Portal.REDEPLOY` should be run one after another.


#### Deploying eclipse-birt-runtime

OSCM uses [eclipse-birt-runtime](http://www.eclipse.org/birt/) to generate reports. After OSCM is deployed, it is time for you to download and deploy eclipse-birt-runtime. You can find it under the [link](http://download.eclipse.org/birt/downloads/). Birt.war is the application you should be interested in. We advise you to get the latest version. When you have deployed the application, you may upload all the reports that we have designed for you. The reports are delivered with every [release](https://github.com/servicecatalog/development/releases) in oscm-reports.zip. Just unpack the content to a folder on glassfish where the application has been deployed (usually: ${glassfishHome}\glassfish\domains\\{domain}\applications\\{ecilpse_runtime_folder}).

