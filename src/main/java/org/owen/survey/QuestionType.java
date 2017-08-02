package org.owen.survey;

import java.util.HashMap;
import java.util.Map;

public enum QuestionType {
	ME(1), OPENTEXT(2), WE(3), ;

	// Reverse-lookup map for getting a question type from an question type ID
	private static final Map<Integer, QuestionType> lookup = new HashMap<Integer, QuestionType>();

	static {
		for (QuestionType d : QuestionType.values()) {
			lookup.put(d.getValue(), d);
		}
	}

	private int value;

	private QuestionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static QuestionType get(int value) {
		return lookup.get(value);
	}
}
