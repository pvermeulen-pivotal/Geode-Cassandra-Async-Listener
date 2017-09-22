package utils.geode.server.cassandra.impl;

public class CassandraProperties {

	private String listenerErrorRegion;
	private String listenerStatusRegion;
	private String listenerUpdateRegionName;
	private String cassandraClusterServersIPList;
	private String cassandraClusterServerPort;
	private String cassandraClusterUser;
	private String cassandraClusterPassword;
	private String cassandraInsertSql;
	private String cassandraSSLSignerName;
	private String cassandraClientKeystore;
	private String cassandraClientKeystorePass;
	private String gemfireCompoundKeyDelimiter;
	private boolean cassandraSSL;
	private boolean gemfireCompoundKey;
	private boolean listenerUpdateRegion;

	public String getListenerErrorRegion() {
		if (listenerErrorRegion != null)
			return listenerErrorRegion.trim();
		return listenerErrorRegion;
	}

	public void setListenerErrorRegion(String cassandraLoadErrorRegion) {
		this.listenerErrorRegion = cassandraLoadErrorRegion;
	}

	public String getListenerStatusRegion() {
		if (listenerStatusRegion != null)
			return listenerStatusRegion.trim();
		return listenerStatusRegion;
	}

	public void setListenerStatusRegion(String cassandraLoadStatusRegion) {
		this.listenerStatusRegion = cassandraLoadStatusRegion;
	}

	public String getCassandraClusterServersIPList() {
		if (cassandraClusterServersIPList != null)
			return cassandraClusterServersIPList.trim();
		return cassandraClusterServersIPList;
	}

	public void setCassandraClusterServersIPList(String cassandraClusterServersIPList) {
		this.cassandraClusterServersIPList = cassandraClusterServersIPList;
	}

	public String getCassandraClusterServerPort() {
		if (cassandraClusterServerPort != null)
			return cassandraClusterServerPort.trim();
		return cassandraClusterServerPort;
	}

	public void setCassandraClusterServerPort(String cassandraClusterServerPort) {
		this.cassandraClusterServerPort = cassandraClusterServerPort;
	}

	public String getCassandraClusterUser() {
		if (cassandraClusterUser != null)
			return cassandraClusterUser.trim();
		return cassandraClusterUser;
	}

	public void setCassandraClusterUser(String cassandraClusterUser) {
		this.cassandraClusterUser = cassandraClusterUser;
	}

	public String getCassandraClusterPassword() {
		if (cassandraClusterPassword != null)
			return cassandraClusterPassword.trim();
		return cassandraClusterPassword;
	}

	public void setCassandraClusterPassword(String cassandraClusterPassword) {
		this.cassandraClusterPassword = cassandraClusterPassword;
	}

	public String getCassandraInsertSql() {
		if (cassandraInsertSql != null)
			return cassandraInsertSql.trim();
		return cassandraInsertSql;
	}

	public void setCassandraInsertSql(String cassandraInsertSql) {
		this.cassandraInsertSql = cassandraInsertSql;
	}

	public boolean isCassandraSSL() {
		return cassandraSSL;
	}

	public void setCassandraSSL(boolean cassandraSSL) {
		this.cassandraSSL = cassandraSSL;
	}

	public boolean isGemfireCompoundKey() {
		return gemfireCompoundKey;
	}

	public void setGemfireCompoundKey(boolean cassandraCompoundKey) {
		this.gemfireCompoundKey = cassandraCompoundKey;
	}

	public String getListenerUpdateRegionName() {
		if (listenerUpdateRegionName != null)
			return listenerUpdateRegionName.trim();
		return listenerUpdateRegionName;
	}

	public void setListenerUpdateRegionName(String auditRegionName) {
		this.listenerUpdateRegionName = auditRegionName;
	}

	public boolean isListenerUpdateRegion() {
		return listenerUpdateRegion;
	}

	public void setListenerUpdateRegion(boolean updateAuditRegion) {
		this.listenerUpdateRegion = updateAuditRegion;
	}

	public String getCassandraSSLSignerName() {
		if (cassandraSSLSignerName != null)
			return cassandraSSLSignerName.trim();
		return cassandraSSLSignerName;
	}

	public void setCassandraSSLSignerName(String cassandraSSLIssurerName) {
		this.cassandraSSLSignerName = cassandraSSLIssurerName;
	}

	public String getCassandraClientKeystore() {
		if (cassandraClientKeystore != null)
			return cassandraClientKeystore.trim();
		return cassandraClientKeystore;
	}

	public void setCassandraClientKeystore(String cassandraClientKeystore) {
		this.cassandraClientKeystore = cassandraClientKeystore;
	}

	public String getCassandraClientKeystorePass() {
		if (cassandraClientKeystorePass != null)
			return cassandraClientKeystorePass.trim();
		return cassandraClientKeystorePass;
	}

	public void setCassandraClientKeystorePass(String cassandraClientKeystorePass) {
		this.cassandraClientKeystorePass = cassandraClientKeystorePass;
	}

	public String getGemfireCompoundKeyDelimiter() {
		if (gemfireCompoundKeyDelimiter != null)
			return gemfireCompoundKeyDelimiter.trim();
		return gemfireCompoundKeyDelimiter;
	}

	public void setGemfireCompoundKeyDelimiter(String gemfireCompoundKeyDelimiter) {
		this.gemfireCompoundKeyDelimiter = gemfireCompoundKeyDelimiter;
	}

	@Override
	public String toString() {
		return "CassandraProperties [listenerErrorRegion=" + listenerErrorRegion + ", listenerStatusRegion="
				+ listenerStatusRegion + ", listenerUpdateRegionName=" + listenerUpdateRegionName
				+ ", cassandraClusterServersIPList=" + cassandraClusterServersIPList + ", cassandraClusterServerPort="
				+ cassandraClusterServerPort + ", cassandraClusterUser=" + cassandraClusterUser
				+ ", cassandraClusterPassword=" + cassandraClusterPassword + ", cassandraInsertSql="
				+ cassandraInsertSql + ", cassandraSSLIssurerName=" + cassandraSSLSignerName
				+ ", cassandraClientKeystore=" + cassandraClientKeystore + ", cassandraClientKeystorePass="
				+ cassandraClientKeystorePass + ", gemfireCompoundKeyDelimiter=" + gemfireCompoundKeyDelimiter
				+ ", cassandraSSL=" + cassandraSSL + ", gemfireCompoundKey=" + gemfireCompoundKey
				+ ", listenerUpdateRegion=" + listenerUpdateRegion + "]";
	}
}
