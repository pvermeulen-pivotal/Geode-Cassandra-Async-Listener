package utils.geode.server.cassandra.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.pdx.*;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.RemoteEndpointAwareJdkSSLOptions;
import com.datastax.driver.core.SSLOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;

public abstract class CassandraEventListenerImpl implements Declarable, AsyncEventListener {
	private Log log = LogFactory.getLog(CassandraEventListenerImpl.class);
	private Cluster cluster = null;
	private Session session = null;
	private SSLOptions sslOptions = null;
	private CassandraProperties config = null;
	private Cache cache = null;
	private String listenerRegionName = null;
	private long numberCassandraUpdates = 0;
	private long numberCassandraErrors = 0;
	private boolean cassandraInitialized = false;
	private boolean listenerInitialized = false;

	public Cluster getCluster() {
		return cluster;
	}

	public Session getSession() {
		return session;
	}

	public CassandraProperties getConfig() {
		return config;
	}

	public Cache getCache() {
		return cache;
	}

	public Log getLog() {
		return log;
	}

	private void initializeListener(String regionName) {
		listenerRegionName = regionName;
		cache = CacheFactory.getAnyInstance();
		listenerInitialized = true;
	}

	public boolean processEvents(List<AsyncEvent> events) {
		Object value = null;
		log.debug("Processing events " + events.size());
		if (cassandraInitialized) {
			for (AsyncEvent event : events) {
				if (!listenerInitialized)
					initializeListener(event.getRegion().getName());
				boolean success = true;
				if (log.isDebugEnabled())
					log.debug("Procesing PDX async event key = " + event.getKey());
				try {
					value = formatObject(event);
					success = processKeyImpl(event.getKey(), value);
					if (success)
						numberCassandraUpdates++;
				} catch (NoHostAvailableException e) {
					log.error("Unable to add row to cassandra. No cassandra servers available: " + e.getMessage());
					cassandraInitialized = false;
					success = false;
				} catch (JSONFormatterException | QueryExecutionException | QueryValidationException
						| UnsupportedFeatureException e) {
					log.error("Unable to add row to cassandra. Exception: " + e.getMessage());
					success = false;
				}
				if (!success) {
					if (cassandraInitialized) {
						updateErrorRegion(event.getKey(), value);
					} else {
						close();
						cassandraInitialized = initializeCassandra();
						return false;
					}
				}
				if (config.isListenerUpdateRegion() && config.getListenerUpdateRegionName() != null
						&& config.getListenerUpdateRegionName().length() > 0) {
					value = JSONFormatter.fromJSON((String) value);
					updateGemfireRegionImpl(event.getKey(), value);
				}
			}
			updateRegionStatus();
			return true;
		} else {
			return false;
		}
	}

	private Object formatObject(AsyncEvent event) {
		if (event.getDeserializedValue() instanceof PdxInstance) {
			return JSONFormatter.toJSON((PdxInstance) event.getDeserializedValue());
		} else {
			return deserializeObject(event);
		}
	}

	private void updateGemfireRegionImpl(Object key, Object value) {
		if (config.getListenerUpdateRegionName() != null && config.getListenerUpdateRegionName().length() > 0) {
			log.debug("Writing event to Gemfire " + config.getListenerUpdateRegionName() + " region key = " + key);
			try {
				Region region = this.getCache().getRegion(config.getListenerUpdateRegionName());
				if (region != null) {
					if (value instanceof PdxInstance) {
						WritablePdxInstance wPdx = ((PdxInstance) value).createWriter();
						updateGemfireRegion(wPdx);
						region.put(key, wPdx);
					} else {
						log.error("Unable to write object to other region because object is not an instance of PDX");
					}
				} else {
					log.error("Unable to write object to other update region. No error region "
							+ this.getConfig().getListenerUpdateRegionName() + " exists");
				}
			} catch (Exception e) {
				log.error("Update GemFire Region " + config.getListenerUpdateRegionName() + " exception: "
						+ e.getMessage());
			}
		}
	}

	public boolean processKeyImpl(Object key, Object value) throws NoHostAvailableException, QueryExecutionException,
			QueryValidationException, UnsupportedFeatureException {
		if (config.isGemfireCompoundKey()) {
			if (key instanceof String) {
				String[] keyParts = ((String) key).split(this.getConfig().getGemfireCompoundKeyDelimiter());
				if (keyParts != null && keyParts.length > 1) {
					if (value != null) {
						Object[] values = processCompoundKey(keyParts, value);
						if (values != null && values[values.length - 1] != null) {
							session.execute(this.getConfig().getCassandraInsertSql(), values);
							return true;
						}
					} else {
						log.error("The object for " + key + " could not be deserialized");
					}
				} else {
					log.error("The event key is not GemFire compound key. Key: " + key);
				}
			} else {
				log.error("The event key must be a String type when a GemFire compound key is defined");
			}
		} else {
			if (value != null) {
				Object[] values = processKey(key, value);
				if (values != null && values[values.length - 1] != null) {
					session.execute(this.getConfig().getCassandraInsertSql(), values);
					return true;
				}
			} else {
				log.error("The object for " + key + " could not be deserialized");
			}
		}
		return false;
	}

	public Object[] processKey(Object key, Object value) {
		Object[] object = new Object[2];
		object[0] = key;
		object[1] = value;
		return object;
	}

	public Object[] processCompoundKey(String[] keyParts, Object value) {
		Object[] object = new Object[keyParts.length + 1];
		for (int i = 0; i < keyParts.length; i++) {
			object[i] = keyParts[i];
		}
		object[object.length - 1] = value;
		return object;
	}

	public void updateGemfireRegion(WritablePdxInstance pdx) {
		return;
	}

	private Object deserializeObject(AsyncEvent event) {
		byte[] serializedValue = event.getSerializedValue();
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(serializedValue);
			ObjectInputStream si = new ObjectInputStream(bi);
			return si.readObject();
		} catch (Exception e) {
			// do nothing
		}
		return event.getDeserializedValue();
	}

	private void updateRegionStatus() {
		if (config.getListenerStatusRegion() != null && config.getListenerStatusRegion().length() > 0) {
			Region statusRegion = cache.getRegion(config.getListenerStatusRegion());
			if (statusRegion != null) {
				Object obj = statusRegion.get(listenerRegionName);
				if (obj instanceof PdxInstance) {
					WritablePdxInstance wPdx = ((PdxInstance) obj).createWriter();
					wPdx.setField("lastUpdate", new Date());
					long updates = (Long) wPdx.getField("numberCassandraUpdates");
					long errors = (Long) wPdx.getField("numberCassandraErrors");
					wPdx.setField("numberCassandraUpdates", updates + numberCassandraUpdates);
					wPdx.setField("numberCassandraErrors", errors + numberCassandraErrors);
					statusRegion.put(listenerRegionName, wPdx);
				} else {
					Status status = (Status) statusRegion.get(listenerRegionName);
					if (status == null) {
						status = new Status();
						status.setLastUpdate(new Date());
						status.setNumberCassandraUpdates(numberCassandraUpdates);
						status.setNumberCassandraErrors(numberCassandraErrors);
					} else {
						status.setLastUpdate(new Date());
						status.setNumberCassandraUpdates(status.getNumberCassandraUpdates() + numberCassandraUpdates);
						status.setNumberCassandraErrors(status.getNumberCassandraErrors() + numberCassandraErrors);
					}
					statusRegion.put(listenerRegionName, status);
				}
				numberCassandraUpdates = 0;
				numberCassandraErrors = 0;
			}
		}
	}

	private void updateErrorRegion(Object key, Object value) {
		if (config.getListenerErrorRegion() != null && config.getListenerErrorRegion().length() > 0) {
			numberCassandraErrors++;
			log.info("Writing event to error region key = " + key);
			try {
				Region region = cache.getRegion(config.getListenerErrorRegion());
				if (region != null) {
					if (value instanceof PdxInstance) {
						region.put(listenerRegionName + ":" + key, JSONFormatter.toJSON((PdxInstance) value));
					} else {
						region.put(listenerRegionName + ":" + key, value);
					}
				} else {
					log.error("Unable to write error row to region. No error region " + config.getListenerErrorRegion()
							+ " exists");
				}
			} catch (Exception e) {
				log.error("Unable to write error row to " + config.getListenerErrorRegion() + " region. Exception: "
						+ e.getMessage());
			}
		}
	}

	private SSLOptions getOpenSSLContext() throws Exception {
		TrustCertificates.trustServerCertificates("TLS", config);
		SSLContext sslContext = SSLContext.getDefault();
		String[] opts = new String[] { "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA",
				"TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
				"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA" };
		return RemoteEndpointAwareJdkSSLOptions.builder().withSSLContext(sslContext).withCipherSuites(opts).build();
	}

	private boolean processProperties(Properties props) {
		boolean configOk = true;
		config = new CassandraProperties();
		config.setListenerErrorRegion(props.getProperty("listenerErrorRegion", null));
		config.setListenerStatusRegion(props.getProperty("listenerStatusRegion", null));
		config.setCassandraClusterServersIPList(props.getProperty("cassandraClusterServersIPList", null));
		config.setCassandraClusterServerPort(props.getProperty("cassandraClusterServerPort", null));
		config.setCassandraClusterUser(props.getProperty("cassandraClusterUser", null));
		config.setCassandraClusterPassword(props.getProperty("cassandraClusterPassword", null));
		config.setCassandraInsertSql(props.getProperty("cassandraInsertSql", null));
		config.setCassandraSSL(Boolean.parseBoolean(props.getProperty("cassandraSSL", "false").trim()));
		config.setCassandraSSLSignerName(props.getProperty("cassandraSSLSignerName"));
		config.setListenerUpdateRegion(Boolean.parseBoolean(props.getProperty("listenerUpdateRegion", "false").trim()));
		config.setListenerUpdateRegionName(props.getProperty("listenerUpdateRegionName", null));
		config.setCassandraClientKeystore(props.getProperty("cassandraClientKeystore", null));
		config.setCassandraClientKeystorePass(props.getProperty("cassandraClientKeystorePass", null));
		config.setGemfireCompoundKey(Boolean.parseBoolean(props.getProperty("gemfireCompoundKey", "false").trim()));
		config.setGemfireCompoundKeyDelimiter(props.getProperty("gemfireCompoundKeyDelimiter", null));
		if (config.getListenerErrorRegion() == null || config.getListenerErrorRegion().length() == 0)
			log.error("listenerErrorRegion property not defined");
		if (config.getListenerStatusRegion() == null || config.getListenerStatusRegion().length() == 0)
			log.error("listenerStatusRegion property not defined");
		if (config.getCassandraClusterUser() == null || config.getCassandraClusterUser().length() == 0
				|| config.getCassandraClusterPassword() == null || config.getCassandraClusterPassword().length() == 0) {
			log.error("cassandraClusterUser property and/or cassandraClusterPassword property not valid");
		}
		if (config.getCassandraClusterServerPort() == null || config.getCassandraClusterServerPort().length() == 0
				|| config.getCassandraClusterServersIPList() == null
				|| config.getCassandraClusterServersIPList().length() == 0 || config.getCassandraInsertSql() == null
				|| config.getCassandraInsertSql().length() == 0) {
			log.error("One or more cassandra properties are invalid: " + props.toString());
			configOk = false;
		}
		if (configOk)
			if (config.isCassandraSSL() && (config.getCassandraSSLSignerName() == null
					|| config.getCassandraSSLSignerName().length() == 0)) {
				log.error("cassandraSSL property enabled but cassandraSSLSignerName property not defined: "
						+ props.toString());
				configOk = false;
			}
		if (configOk)
			if (config.getCassandraClientKeystore() != null && config.getCassandraClientKeystore().length() > 0
					&& config.isCassandraSSL()) {
				if (config.getCassandraClientKeystorePass() == null
						|| config.getCassandraClientKeystorePass().length() == 0) {
					log.error(
							"cassandraClientKeystore property defined but no cassandraClientKeystorePass property defined");
					configOk = false;
				}
			} else if (config.getCassandraClientKeystore() != null && config.getCassandraClientKeystore().length() > 0
					&& !config.isCassandraSSL()) {
				log.error("cassandraClientKeystore property defined but cassandraSSL property is not enabled");
			}
		return configOk;
	}

	private void configureCluster(String[] servers, int port, AuthProvider authProvider, SSLOptions sslOptions) {
		if (authProvider != null && sslOptions != null) {
			cluster = Cluster.builder().addContactPoints(servers).withPort(port).withAuthProvider(authProvider)
					.withSSL(sslOptions).build();
		} else if (authProvider != null) {
			cluster = Cluster.builder().addContactPoints(servers).withPort(port).withAuthProvider(authProvider).build();
		} else if (sslOptions != null) {
			cluster = Cluster.builder().addContactPoints(servers).withPort(port).withSSL(sslOptions).build();
		} else {
			cluster = Cluster.builder().addContactPoints(servers).withPort(port).build();
		}
	}

	private boolean initializeCassandra() {
		boolean initialized = false;
		AuthProvider authProvider = null;
		String[] serverAddress = config.getCassandraClusterServersIPList().split(",");
		if (serverAddress != null && serverAddress.length > 0) {
			try {
				if (config.getCassandraClusterUser() != null && config.getCassandraClusterUser().length() > 0
						&& config.getCassandraClusterPassword() != null
						&& config.getCassandraClusterPassword().length() > 0) {
					authProvider = new PlainTextAuthProvider(config.getCassandraClusterUser(),
							config.getCassandraClusterPassword());
					if (log.isDebugEnabled())
						log.debug("Building cluster with AuthProvider");
				}
				if (config.isCassandraSSL() && sslOptions != null) {
					if (log.isDebugEnabled())
						log.debug("Building cluster with SSLOptions");
				} else {
					if (log.isDebugEnabled())
						log.debug("Building cluster without AuthProvider and SSLOptions");
				}
				configureCluster(serverAddress, Integer.parseInt(config.getCassandraClusterServerPort()), authProvider,
						sslOptions);
				session = cluster.connect();
				initialized = true;
			} catch (Exception ex) {
				log.error("Cassandra initialization error " + ex.getMessage());
			}
		} else {
			log.error("Cassandra initialization error: No servers defined");
		}
		return initialized;
	}

	public void init(Properties props) {
		log.info("Initializing Cassandra GatewayListener");
		Properties listenerProperties = null;
		if (props.containsKey("propertyFile")) {
			listenerProperties = new Properties();
			FileInputStream is;
			try {
				is = new FileInputStream(new File(props.getProperty("propertyFile")));
				listenerProperties.load(is);
				is.close();
			} catch (IOException e) {
				log.error(this.getClass().getName() + " unable to load properties file "
						+ props.getProperty("propertyFile"));
			}
		}

		if (!processProperties(listenerProperties))
			return;

		sslOptions = null;
		if (config.isCassandraSSL()) {
			try {
				sslOptions = getOpenSSLContext();
			} catch (Exception e) {
				log.error("Unable to create SSL Options: " + e.getMessage());
			}
		}
		cassandraInitialized = initializeCassandra();
	}

	public void close() {
		if (cassandraInitialized) {
			session.close();
			cluster.close();
		}
	}
}
