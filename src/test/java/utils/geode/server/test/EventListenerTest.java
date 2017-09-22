package utils.geode.server.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import utils.geode.server.test.domain.Order;

public class EventListenerTest {

	// Create order
	public static Order createOrder() {
		Order order = new Order();
		order.setOrderNum(17000);
		order.setCustomerNum(270000000);
		order.setDateCreated(new Date().getTime());
		order.setOrderSubject("Customer order " + String.valueOf(order.getOrderNum()));
		order.setLastUpdated(new Date().getTime());
		List<String> ol = new ArrayList<String>();
		ol.add(new String("Order Line 1 shirt $5.00"));
		ol.add(new String("Order Line 2 sox   $1.00"));
		order.setOrderLines(ol);
		order.setStatus("PENDING");
		return order;
	}

	// Test writing to two regions 
	// om region is non-compound key 
	// o region is compound key
	public static void main(String[] args) {
		ReflectionBasedAutoSerializer rbas = new ReflectionBasedAutoSerializer("utils.geode.server.test.domain.*");
		ClientCacheFactory cf = new ClientCacheFactory();
		ClientCache cache = cf.addPoolLocator("RCPLT001", 10000).setPdxReadSerialized(false).setPdxSerializer(rbas).create();
		Region om = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("order_management");
		Region o = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("order");
		Order order = createOrder();
		om.put(order.getOrderNum(), order);
		o.put(order.getCompoundKey(), order);
		cache.close();
	}

}
