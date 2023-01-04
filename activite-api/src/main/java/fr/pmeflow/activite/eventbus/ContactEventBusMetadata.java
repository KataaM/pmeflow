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
 * service Contact
 * 
 * @author Mattecoco
 */
public class ContactEventBusMetadata {
	// ----------- Methode(s) -----------
	private ContactEventBusMetadata() {
		// private constructor for static class
	}

	// ----------- Attribut(s) -----------
	public static final String EXCHANGE = "ms.contact";

	public static final String EVENT_CONTACT_ADDED = "contact-added";
	public static final String EVENT_CONTACT_UPDATED = "contact-updated";
	public static final String EVENT_CONTACT_DELETED = "contact-deleted";
	public static final String EVENT_CONTACT_ENTREPRISE_LINKED = "entreprise-linked";
}
