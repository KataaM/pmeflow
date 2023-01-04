/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.iam;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Cette classe assure le démarrage de l'application JAX-RS
 * des Services IAM.
 * 
 * @author ludovic.terral
 */
@ApplicationPath("/iam/api")
public class IamServiceApp extends Application 
{
    // ----------- Attribut(s) -----------
    private Set<Object> singletons = new HashSet<>();

    
    
    // ----------- Methode(s) -----------
    @Override
    public Set<Object> getSingletons() 
    {
        return singletons;
    }
}