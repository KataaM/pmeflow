/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.facturation.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux facturations
 * 
 * @author Ludovic.terral
 */
public class FacturationEventBusMetadata
{
    // ----------- Methode(s) -----------
    private FacturationEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.facturation";
    
    public static final String QUEUE = "ms.facturation_queue";
    
    public static final String EVENT_FACTURE_READY = "facture-ready";
}
