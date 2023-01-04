/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.abonnement.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux abonnements
 * 
 * @author Ludovic.terral
 */
public class AbonnementEventBusMetadata
{
    // ----------- Methode(s) -----------
    private AbonnementEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.abonnement";
    
    public static final String EVENT_FIN_ENGAGEMENT = "abonnement-fin-engagement";
}
