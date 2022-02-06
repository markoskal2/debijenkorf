package com.bijenkorf.assignment.model;

public enum Type {
	JPG("jpg"),
	PNG("png");

	private final String name;

	Type(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
