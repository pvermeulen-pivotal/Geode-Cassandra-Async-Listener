package utils.geode.server.cassandra.impl;

import java.io.Serializable;
import java.util.Date;

public class Status implements Serializable {

	private static final long serialVersionUID = -566056572962371916L;

	private Date lastUpdate;
	private long numberCassandraUpdates;
	private long numberCassandraErrors;
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public long getNumberCassandraUpdates() {
		return numberCassandraUpdates;
	}
	
	public void setNumberCassandraUpdates(long numberCassandraUpdates) {
		this.numberCassandraUpdates = numberCassandraUpdates;
	}
	
	public long getNumberCassandraErrors() {
		return numberCassandraErrors;
	}
	
	public void setNumberCassandraErrors(long numberCassandraErrors) {
		this.numberCassandraErrors = numberCassandraErrors;
	}
	
}
