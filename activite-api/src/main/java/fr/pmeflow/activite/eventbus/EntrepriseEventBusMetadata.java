/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.activite.eventbus;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié au
 * service Entreprise
 * 
 * @author Mattecoco
 */
public class EntrepriseEventBusMetadata {
	// ----------- Methode(s) -----------
	private EntrepriseEventBusMetadata() {
		// private constructor for static class
	}

	// ----------- Attribut(s) -----------
	public static final String EXCHANGE = "ms.entreprise";

	public static final String EVENT_ENTREPRISE_ADDED = "entreprise-added";
	public static final String EVENT_ENTREPRISE_UPDATED = "entreprise-updated";
	public static final String EVENT_ENTREPRISE_DELETED = "entreprise-deleted";
	public static final String EVENT_ENTREPRISE_CONTACT_LINKED = "contact-linked";
}
