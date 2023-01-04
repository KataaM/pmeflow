/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io.model;

import java.io.File;
import java.io.Serializable;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cette classe est l'entite representant un storage de document.
 * 
 * @author ludovic.terral
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class StorageMultiPartBody implements Serializable
{
    // ----------- Attribut -----------    
	private static final long serialVersionUID = 202201172101L;


    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public File file;

    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
}