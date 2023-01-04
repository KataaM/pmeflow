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
 * service Opportunite
 * 
 * @author Corentin
 */
public class OpportuniteEventBusMetadata {
	// ----------- Methode(s) -----------
	private OpportuniteEventBusMetadata() {
		// private constructor for static class
	}

	// ----------- Attribut(s) -----------
	public static final String EXCHANGE = "ms.opportunite";

	public static final String EVENT_OPPORTUNITE_ADDED = "opportunite-added";
	public static final String EVENT_OPPORTUNITE_UPDATED = "opportunite-updated";
	public static final String EVENT_OPPORTUNITE_DELETED = "opportunite-deleted";
	
}
