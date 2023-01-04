/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.dossier.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux dossiers
 * 
 * @author Ludovic.terral
 */
public class DossierEventBusMetadata
{
    // ----------- Methode(s) -----------
    private DossierEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.dossier";
    
    public static final String EVENT_DOSSIER_CREATED = "dossier-created";
    public static final String EVENT_DOSSIER_UPDATED = "dossier-updated";
    public static final String EVENT_DOSSIER_TO_BE_COMPLETED = "dossier-to-be-completed";    
}
