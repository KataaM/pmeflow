package fr.pmeflow.grc.model.enums;

public enum TypeFacturation {

	FORFAIT(0), UNITE(1), LOT(2), PERSONALISE(3);

	private int value;

	TypeFacturation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
