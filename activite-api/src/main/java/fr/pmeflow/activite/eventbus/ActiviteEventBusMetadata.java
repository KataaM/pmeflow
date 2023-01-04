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
public class ActiviteEventBusMetadata {
	// ----------- Methode(s) -----------
	private ActiviteEventBusMetadata() {
		// private constructor for static class
	}

	// ----------- Attribut(s) -----------
	public static final String EXCHANGE = "ms.activite";
	public static final String QUEUE = "activite_queue";
	
//	public static final String EVENT_ACTIVITE_ADDED = "activite-added";
}
