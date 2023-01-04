/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io.model;

/**
 * Cette classe regroupe l'ensemble des métadatas du bus d'évènements lié aux emails
 * 
 * @author Ludovic.terral
 */
public class EmailEventBusMetadata
{
    // ----------- Methode(s) -----------
    private EmailEventBusMetadata()
    {
        //private constructor for static class
    }
    
    

    // ----------- Attribut(s) -----------
    public static final String EXCHANGE = "ms.email";

    public static final String QUEUE = "ms.email_queue";    
}
