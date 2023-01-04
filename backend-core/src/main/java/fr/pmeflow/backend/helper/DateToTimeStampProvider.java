/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.helper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Ce provider assure la conversion des dates en timestamp pour les echanges json. 
 * 
 * @author Ludovic.terral
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DateToTimeStampProvider implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper objectMapper;

    
    
    public DateToTimeStampProvider()
    {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }
    
    

    @Override
    public ObjectMapper getContext(Class<?> objectType)
    {
        return objectMapper;
    }
}