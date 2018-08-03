package com.idea.model.constant;

public enum CycleType {
	IMMEDIATELY(0), ONCE(1), SECOND(2), MINUTE(3), HOUR(4), DAY(5), WEEK(6), MONTH(7), YEAR(8);
	private int value;

	private CycleType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
