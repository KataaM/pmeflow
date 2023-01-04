/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.referentiel.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Schema;

import fr.lixbox.common.util.StringUtil;

/**
 * Ce filtre supprime les données de l'api qui ne doivent pas être diffusées
 * 
 * @author ludovic.terral
 */
public class CommonOASFilter implements OASFilter
{
    // ----------- Methode(s) -----------
    @Override
    public void filterOpenAPI(OpenAPI openAPI) 
    {
        // registry client
        Map<String, PathItem> pathItems = new HashMap<>(openAPI.getPaths().getPathItems());
        
        for (Entry<String, PathItem> entry : pathItems.entrySet())
        {
            if (!
                (StringUtil.contains(entry.getKey(), "/1.0/abonnement")||
                 StringUtil.contains(entry.getKey(), "/1.0/parametre") ||
                 StringUtil.contains(entry.getKey(), "/1.0/prestation")))
            {
                openAPI.getPaths().removePathItem(entry.getKey());
            }
        }
        
        //components
        Map<String, Schema> temp = new HashMap<>(openAPI.getComponents().getSchemas());
        
        
        
        
        List<String> typesForRemoved = Arrays.asList("Formulaire", "Step", "Question","TypeReponse");
        for (String type : typesForRemoved)
        {
            temp.remove(type);
        }
        openAPI.getComponents().setSchemas(temp);
    }
}
