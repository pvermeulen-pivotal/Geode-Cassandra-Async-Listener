package utils.geode.server.cassandra;

import java.util.Date;

import org.apache.geode.pdx.WritablePdxInstance;

import com.datastax.driver.core.utils.UUIDs;

import utils.geode.server.cassandra.impl.CassandraEventListenerImpl;

// Extends CassandraEventListenerImpl class 
// Override CassandraEventListenerImpl three (3) methods 
// processKey() non-compound key
// processCompoundKey() compound key
// updateGemfireRegion() writeable pdx instance 
public class CassandraEventListener extends CassandraEventListenerImpl {

	public CassandraEventListener() {
		super();
	}

	@Override
	public Object[] processCompoundKey(String[] keyParts, Object value) {
		Object[] values = new Object[3];
		values[0] = Integer.parseInt(keyParts[0]);
		values[1] = UUIDs.startOf(Long.parseLong(keyParts[1]));
		values[2] = value;
		return values;
	}

	@Override
	public void updateGemfireRegion(WritablePdxInstance pdx) {
		pdx.setField("status", "COMPLETED");
		pdx.setField("lastUpdated", new Date().getTime());
	}

}
