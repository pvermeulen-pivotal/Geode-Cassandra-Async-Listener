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
