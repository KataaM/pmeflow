package fr.pmeflow.activite.model.enums;

public enum TypeDomaine {

	OPPORTUNITE(0), PROJET(1), ENTREPRISE(2), CONTACT(3), SUIVITEMPS(4);

	private int value;

	TypeDomaine(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
