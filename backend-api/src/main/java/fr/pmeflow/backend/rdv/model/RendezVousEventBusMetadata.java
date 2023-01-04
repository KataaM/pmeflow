/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux 
 * rendez-vous
 * 
 * @author Ludovic.terral
 */
public class RendezVousEventBusMetadata
{
    // ----------- Methode(s) -----------
    private RendezVousEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.rendezVous";
    
    public static final String EVENT_RDV_CONFIRM_TO_CLIENT = "rdv-confirm-client";
    public static final String EVENT_RDV_INFORM_JURISTE = "rdv-inform-juriste";
}
