package org.owen.hr;

public class Edge {

	private int fromEmployeeId;
	private int toEmployeeId;
	private int relationshipTypeId;
	private String relationshipName;
	private double weight;

	public int getFromEmployeeId() {
		return fromEmployeeId;
	}

	public void setFromEmployeeId(int fromEmployeeId) {
		this.fromEmployeeId = fromEmployeeId;
	}

	public int getToEmployeeId() {
		return toEmployeeId;
	}

	public void setToEmployeeId(int toEmployeeId) {
		this.toEmployeeId = toEmployeeId;
	}

	public int getRelationshipTypeId() {
		return relationshipTypeId;
	}

	public void setRelationshipTypeId(int relationshipTypeId) {
		this.relationshipTypeId = relationshipTypeId;
	}

	public String getRelationshipName() {
		return relationshipName;
	}

	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
