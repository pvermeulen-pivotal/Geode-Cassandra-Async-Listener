package utils.geode.server.test.domain;

import java.util.Date;
import java.util.List;

public class Order {

	private int orderNum;
	private long customerNum;
	private long lastUpdated;
	private long dateCreated;
	private String orderSubject;
	private String status;
	private List<String> orderLines;

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public long getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(long customerNum) {
		this.customerNum = customerNum;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getOrderSubject() {
		return orderSubject;
	}

	public void setOrderSubject(String orderSubject) {
		this.orderSubject = orderSubject;
	}

	public List<String> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<String> orderLines) {
		this.orderLines = orderLines;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompoundKey() {
		return String.valueOf(orderNum) + "::" + String.valueOf(lastUpdated);
	}
}
