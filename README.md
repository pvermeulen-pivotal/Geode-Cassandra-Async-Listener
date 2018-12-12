# Geode-Cassandra-Async-Listener

The purpose of the Geode-Cassandra-Async-Listener is to write GemFire objects to a Cassandra database table. 

The listener supports only PDX objects and converts a PDX object to JSON document that is written to a Cassandra table. 

### Extendibility
The CassandraEventListener class extends the CassandraEventListenerImpl abstract class and allows three (3) methods to be overridden:
1.	processKey(Object key, String json) 
2.	processCompoundKey(String[] keyParts, String json)
3.	updateGemfireRegion(WriteablePdxInstance)

### SSL
The SSL implementation for Cassandra supports both server and client/server implementations. 
For the server SSL implementation, the listener uses the certificate signer distinguished name to validate the server certificate and requires no client trust store to be defined. 
The client SSL implementation requires the client key store and password to be defined in the property file. 
The client certificate public key must be installed in the Cassandra trust store.

### Async Queue Listener Properties

**listenerErrorRegion**: The name of a GemFire region where objects that cannot be written to Cassandra are saved. 
*If the property is null no errors will be written.*

**listenerStatusRegion**: The name of a GemFire region where the listener writes the detail of the number of successful objects and unsuccessful objects written to Cassandra. 
*If the property is null no status will be written.*

**cassandraClusterServersIPList**: A list of Cassandra IP address for the cluster separated by a comma.

**cassandraClusterServerPort**: The Cassandra session port number.

**cassandraInsertSql**: The Cassandra SQL statement used to insert rows. The SQL statement contains the keyspace.tablename as part of the SQL statement.
*Example: INSERT INTO test.user (id, value) VALUES (?,?).*

**cassandraClusterUser**: The user id to connect to the Cassandra cluster.

**cassandraClusterPassword**: The password to connect to the Cassandra cluster.
*The listener does not support password encryption.*

**cassandraSSL**: A Boolean value indicating if the connection to the Cassandra cluster uses SSL

**cassandraSSLSignerName**: The distinguished name of the certificate signer which is used to validate the server certificate
*Example: CN=rootCa,OU=Organizational_Unit,O=Organization_Name,C=US*

**gemfireCompoundKey**: A Boolean value indicating the region key is a compound key.

**gemfireCompoundKeyDelimiter**: The delimiter used to denote the value that separates the compound key parts. 

**gemfireUpdateRegion**: A Boolean value indicating if the listener will also write the object to a GemFire region after being enriched/changed/updated.

**gemfireUpdateRegionName**: The GemFire region name where the enriched/changed/updated object will be written.

# PCF - PCC Configure and Setup Commands

### Create Service Commands
* cf create-service p-cloudcache small my-cloudcache -c '{"num_servers": 5}'
* cf create-service p-cloudcache small my-cloudcache -c '{"tls": true}'
* cf create-service p-cloudcache small my-cloudcache -c '{"new_size_percentage": 50}'
* cf create-service p-cloudcache small my-cloudcache -c '{"distributed_system_id" : 1 }'
* cf create-service p-cloudcache small my-cloudcache -c '{"num_servers": 5, "tls": true,  "new_size_percentage": 50, "distributed_system_id" : 1 }'

### Update Service Commands
* cf update-service my-cloudcache -c '{"num_servers": true}'
* cf update-service my-cloudcache -c '{"tls": true}'
* cf update-service my-cloudcache -c '{"new_size_percentage": 50}'
* cf update-service my-cloudcache -c '{" distributed_system_id" : 1 }'

### Restart Service Commands
* cf update-service my- cloudcache -c '{"restart": true}'

## PCC Geode/GemFire Overview and Commands

### GFSH System Variables

System Variable	Description
* SYS_CLASSPATH	- CLASSPATH of the gfsh JVM.  read only
* SYS_GEMFIRE_DIR	- Product directory where GemFire has been installed. read only
* SYS_HOST_NAME	- Host from which gfsh is started. read only
* SYS_JAVA_VERSION - Java version used. read only
* SYS_OS - OS name. read only
* SYS_OS_LINE_SEPARATOR	- Line separator (\ or ^) variable that you can use when writing gfsh &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;scripts. read only
* SYS_USER - User name. read only
* SYS_USER_HOME - User’s home directory. read only

### GFSH Environment Variables

Environment Variable	Description
* APP_FETCH_SIZE - Fetch size to be used while querying. Values: 0 - 2147483647. Default value is 100
* APP_LAST_EXIT_STATUS - Last command exit status. Similar to $? (Unix) and %errorlevel% (Windows). Values: 0 (successful), 1 (error). read only
* APP_LOGGING_ENABLED	- Whether gfsh logging is enabled. Default: false. read only. You can enable gfsh logging by setting the gfsh.log-level Java system property to a supported Java log level
* APP_LOG_FILE - Path and name of current gfsh log file. read only
* APP_NAME - Name of the application – gfsh. read only
* APP_PWD - working directory where gfsh was launched. read only
* APP_QUERY_RESULTS_DISPLAY_MODE - Toggle the display mode for returning query results. Values: table or catalog. Default value is table
* APP_QUIET_EXECUTION - Whether the execution should be in quiet mode. Values (case insensitive): true, false. Default value is false
* APP_RESULT_VIEWER - Set this variable to external to enable viewing of the output using the UNIX less command. Unix only

### Display GFSH System and Environment Variables

echo --string=${SYS_USER}

### Set GFSH Environment Variables

set variable --name="APP_FETCH_SIZE" --value="200"
set variable --name="APP_QUERY_RESULTS_DISPLAY_MODE" --value="catalog"
set variable --name="APP_RESULT_VIEWER " --value="less" 

### Creating and Running gfsh Command Scripts

Gfsh offers several ways to script run commands.

**Running gfsh Scripts**

You can create and run scripts that contain gfsh commands that you wish to execute. To execute the script, use the gfsh run command. 

For example: gfsh run --file=myFile.gfsh --continue-on-error --quiet

When you run a gfsh script, interactive parameters are ignored. You can also set the script to run in quiet mode to prevent output and instruct the script to skip any errors it encounters.

Your command history file can be helpful when you write a gfsh script. A history of commands that have been executed successfully is logged in the .gfsh.history file in the home directory of the user running gfsh. You can also export a history file with the history --file=your_file_name command.

When a user runs start server or start locator from gfsh without specifying the member name, gfsh will automatically pick a random member name. This is useful for automation.

**Running gfsh Commands on the OS Command Line**

You can run some gfsh commands directly from your operating system’s prompt by preceding the command with gfsh . This can be useful for Unix shell or Windows batch scripting. 

For example: gfsh start locator --name=locator1 --port=10334

**Running Multiple gfsh Commands on the OS Command Line**

To run multiple commands directly on the command line, use the -e option followed by the gfsh command within quote marks. 

For example: gfsh -e "connect --use-http --url https://cloudcache-1.example.com/gemfire/v1" -e "rebalance" 

**gfsh Commands with Parameter to Test Existence**

The parameter "--if-not-exists" is provided on a gfsh command, allows for a script when running a gfsh create command that supports this parameter does not fail in the event the object exists.

The current gfsh commands that support the "--if-not-exists"  parameter are:
*	create gateway receiver
* create jndi-binding
* create region
* put

### gfsh Command Restrictions 
Developers may invoke all gfsh commands. Given credentials with sufficient permissions, those gfsh command will be executed. However, not all gfsh commands are supported. An invocation of an unsupported command may lead to incorrect results. Those results range from ineffective results to inconsistent region entries. 

Do not use these listed gfsh commands; each has an explanation why it must not be used.
These gfsh start commands will bring up members contrary to the configured plan. Their configuration will be wrong, and their existence is likely to contribute to data loss. Since they are not part of the configured plan, any upgrade will not include them, and if they were to stop or crash, the BOSH Director will not restart them.
*	gfsh start locator
* gfsh start server

These cluster stop commands will temporarily stop the member or cluster. However, the BOSH Director will notice that members are not running and restart them. So, these commands will be ineffective:
* gfsh stop locator
* gfsh stop server
* gfsh shutdown

These Lucene-related commands are not supported:
* gfsh create lucene index
* gfsh describe lucene index
* gfsh destroy lucene index
* gfsh list lucene indexes
* gfsh search lucene

These JNDI binding-related commands are not supported:
* gfsh create jndi-binding
* gfsh describe jndi-binding
* gfsh destroy jndi-binding
* gfsh list jndi-binding

This configure command will instill configuration contrary to the already-configured plan. Since it is not part of the configured plan, any upgrade will not include it. Therefore, do not use:
* gfsh configure pdx

The create of a gateway receiver will never be appropriate for any situation. The PCC cluster will already have gateway receivers, and there is no situation in which the cluster can benefit from creating more. Therefore, do not use:
* gfsh create gateway receiver

Do Not Export from a GemFire Cluster to a PCC Cluster
While the expectation is that configuration and data can be exported from a GemFire cluster and then imported into a PCC cluster, this does not work. Using export and import commands will not have the desired effect of migration from one cluster to another. The import of cluster configuration requires a state that cannot be provided by a PCC cluster. The PCC cluster will already have its configuration, and upon restart or upgrade, that same configuration will be used. Given that the configuration cannot be imported, data import is problematic. Therefore, do not use:
* gfsh import cluster-configuration
* gfsh import data



