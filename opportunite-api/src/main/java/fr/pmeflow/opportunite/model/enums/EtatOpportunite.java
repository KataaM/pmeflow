package fr.pmeflow.opportunite.model.enums;

enum EtatOpportunite {
	NOUVEAU(0), CONTACT(1), RDV(2), DEVISENVOYE(3), ACCEPTE(4), REFUSE(5), SUPPRIME(6);
	
	private int value;


	EtatOpportunite(int value) {
		this.value = value;
		}

		public int getValue() {
			return value;
		}
}
