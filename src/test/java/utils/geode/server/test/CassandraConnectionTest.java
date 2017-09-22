package utils.geode.server.test;

import java.util.Properties;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import utils.geode.server.cassandra.CassandraEventListener;

import utils.geode.server.test.domain.Order;

public class CassandraConnectionTest {

	public static void main(String[] args) {
		CassandraEventListener ev = new CassandraEventListener();
		ReflectionBasedAutoSerializer rbas = new ReflectionBasedAutoSerializer("utils.geode.server.test.domain.*");
		ClientCacheFactory cf = new ClientCacheFactory();
		ClientCache cache = cf.addPoolLocator("RCPLT001", 10000).setPdxReadSerialized(true).setPdxSerializer(rbas).create();
		Region ro = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("order");
		Order order = EventListenerTest.createOrder();
		ro.put(order.getCompoundKey(), order);
		PdxInstance pdx = (PdxInstance) ro.get(order.getCompoundKey());
		String json = JSONFormatter.toJSON((PdxInstance) pdx);
		Properties props = new Properties();
		props.put("propertyFile", "C:\\Temp\\gemfire\\cassandra.properties");
		ev.init(props);
		ev.processKeyImpl(order.getCompoundKey(), json);
		ev.close();
		cache.close();
	}

}
