package fr.pmeflow.activite.model.enums;

public enum ProfilCollaborateur {

	COMMERCIAL(0), RH(1), COLLABORATEUR(2), DIRECTEUR(3);

	private int value;

	ProfilCollaborateur(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
