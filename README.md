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

# gfsh Commands

### gfsh Basic Commands
* debug - Enable or disable debugging output in gfsh
* echo - Echo the given text, which may include system and user variables
* exit - Exit the gfsh shell. You can also use quit to exit the shell
* help - If the argument is a gfsh command, displays syntax and usage information for the command. If there are no arguments, displays a list of all available commands
* hint - Display information on topics and a list of commands associated with a topic
* history - Show or save the command history
* run - Execute a set of GFSH commands
* sh - Executes operating system (OS) commands
* set variable - Set variables in the GFSH environment
* sleep - Delay gfsh command execution
* version - Display product version information

### gfsh Configuration Commands
* alter runtime - Alters configuration properties for a specific member or members while the member or members are running.
* change loglevel - Changes the logging level on specified servers
* configure pdx - This command alters cluster-wide PDX configuration settings for all caches
* describe config - Display the configuration of a member
* export config - Export configurations, data, logs and stacktraces
* export cluster-configuration - Exports a shared configuration zip file that contains cache.xml files,  gemfire.properties files and jar files needed to configure and operate a GemFire distributed system
* status cluster-config-service - Reports on the status of the cluster configuration server
* import cluster-configuration - Import a shared configuration

### gfsh Data Commands
* export data - Export user data from a region to a file
* get - Display an entry in a region
* import - Import user data from a file to a region
* locate entry - Locate a region entry on a member
* put - Add or update a region entry
* query - Run queries against GemFire regions
* remove - Remove an entry from a region

### gfsh Deployment Commands
* deploy - Deploy JAR-packaged applications to a member or members
* list deployed - Display a list of JARs that were deployed to members using the deploy command
* undeploy - Undeploy the JAR files that were deployed on members or groups using deploy command

### gfsh Disk Commands
* alter disk-store - Modify an existing GemFire resource
* backup disk-store - Back up persistent data from all members to the specified directory
* compact disk-store - Compact online disk-stores
* compact offline-disk-store - Compact an offline disk store
* create disk-store - Defines a pool of one or more disk stores, which can be used by regions and client subscription queues
* describe disk-store - Display information about a member’s disk store
* describe offline-disk-store - Display information about an offline member’s disk store
* destroy disk-store - Deletes a disk store and all files on disk used by the disk store. Data for closed regions that previously used this disk store is lost
* list disk-stores - List all available disk stores in a GemFire cluster
* revoke missing-disk-store - Instruct the member(s) of a distributed system to stop waiting for a disk store to be available
* show missing-disk-stores - Display a summary of the disk stores that are currently missing from a distributed system
* upgrade offline-disk-store - Upgrade offline disk-stores used in Pivotal GemFire 6.5 or 6.6 installations to a format that is compatible with Pivotal GemFire or Pivotal GemFire 7.0 or higher
* validate offline-disk-store - Validate offline disk stores

### gfsh Durable CQ and Client Commands
* list durable-cqs - List durable client CQs associated with the specified durable client id
* close durable-cq - Closes the durable CQ registered by the durable client and drain events held for the durable CQ from the subscription queue
* close durable-client - Attempts to close the durable client. The client must be disconnected
* show subscription-queue-size - Shows the number of events in the subscription queue. If a CQ name is provided, it counts the number of eventsin the subscription queue for the specified CQ

### gfsh Function Execution Commands
* destroy function - Destroy or unregister a function. The default is for the function to be unregistered from all members
* execute function - Execute the function with the specified ID. By default, executes on all members
* list functions - Display a list of registered functions. The default is to display functions for all members

### gfsh Gateway (WAN) Commands
* create async-event-queue - Creates an asynchronous event queue
* create gateway-receiver - Creates a gateway receiver on one or more members
* create gateway-sender - Creates a gateway sender on one or more members
* list async-event-queues - Display a list of async event queues for all members
* list gateways - Displays the gateway senders and receivers for a member or members
* load-balance-gateway-sender - Causes the specified gateway sender to close its current connections and reconnect to remote gateway receivers in a more balanced fashion
* pause gateway-sender - Pause a gateway sender
* resume gateway-sender - Resume any gateway senders that you have paused
* start gateway-receiver - Start the gateway receiver on a given member or group of members
* start gateway-sender - Start the gateway sender on a member or members
* status gateway-receiver - Display the status of the specified gateway receiver
* status gateway-sender - Display the status of the specified gateway sender
* stop gateway-receiver - Stop the gateway receiver on a member or members
* stop gateway-sender - Stop a gateway sender with a given id on a specified member or members of a specified member group

### gfsh GemFireAsyncEventQueue Commands
* create async-event-queue - Creates an asynchronous event queue
* list async-event-queues - Display a list of async event queues for all members

### gfsh Monitoring Commands
* describe client - Displays details about a specified client
* describe member - Display details of a member with given name/id
* export logs - Export/dump logs to a given directory
* export stack-traces - Export the stack trace for a member or members
* gc - Force garbage collection on a member or members
* list clients - Displays a list of connected clients
* list members - Display all or a subset of members
* netstat - Report network information and statistics via the “netstat” operating system command
* show deadlocks - Display deadlocks, logs, metrics and missing disk-stores
* show log - Display the log for a member
* show metrics - Display or export metrics for the entire distributed system, a member or a region
* shutdown - Shut down all members that have a cache
* start jconsole - Start the JDK JConsole monitoring application in a separate process. JConsole automatically connects to a running JMX Manager node if one is available
* start jvisualvm - Start the JDK’s Java VisualVM monitoring application in a separate process
* start pulse - Launch the GemFire Pulse monitoring dashboard tool in the user’s default system browser

### gfsh Index Commands
* clear defined indexes - Clears all the defined indexes
* create defined indexes - Creates all the defined indexes
* create index - Create an index that can be used when executing queries
* define index - Define an index that can be used when executing queries. Then you can create multiple indexes all at once
* destroy index - Destroy or remove the specified index
* list indexes - Display the list of indexes created for all members

### gfsh JMX Connection Commands
* connect - Connect to a jmx-manager either directly or via a locator
* describe connection - Display connection information details
* disconnect - Close any active connection(s)

### gfsh Locator Commands
* start locator - Start a locator. The command creates a subdirectory and log file named after the locator. If the locator detects that no other JMX Manager exists, then the locator will automatically start an embedded JMX Manager and connect the current gfsh session to the JMX Manager
* status locator - Displays the status of the specified locator
* stop locator - Stop a locator

### gfsh PDX Commands
* configure pdx - Configure Portable Data eXchange for all the cache(s) in the cluster
* pdx rename - Renames PDX types in an offline disk store

### gfsh Region Commands
* alter region - Alters the configuration of a region
* create region - Create and configure a region
* describe region - Display the attributes and key information of a region
* destroy region - Destroy or remove a region
* list regions - Display regions of a member or members. If no parameter is specified, all regions in the GemFire distributed system are listed
* rebalance - Rebalance partitioned regions

### gfsh Server Commands
* start server - Start a GemFire cache server process
* status server - Display the status of the specified GemFire cache server
* stop server - Stop a GemFire cache server
