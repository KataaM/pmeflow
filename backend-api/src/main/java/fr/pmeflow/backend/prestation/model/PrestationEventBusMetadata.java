/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.prestation.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux prestations
 * 
 * @author Ludovic.terral
 */
public class PrestationEventBusMetadata
{
    // ----------- Methode(s) -----------
    private PrestationEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.prestation";
    
    public static final String EVENT_PRESTATION_TO_PAY = "prestation-to-pay";
    public static final String EVENT_PRESTATION_PAYED = "prestation-payed";
}
