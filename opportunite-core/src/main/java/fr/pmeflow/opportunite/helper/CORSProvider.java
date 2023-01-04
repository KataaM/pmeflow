/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.opportunite.helper;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 * Ce provider ajoute une extension dans le service JAXRS
 * 
 * @author ludovic.terral
 */
@Provider
public class CORSProvider implements Feature {
	// ----------- Methode(s) -----------
	@Override
	public boolean configure(FeatureContext context) {
		CorsFilter filter = new CorsFilter();
		filter.getAllowedOrigins().add("*");
		filter.setAllowedMethods("GET, POST, OPTIONS, HEAD, DELETE");
		filter.setAllowedHeaders("access-control-allow-origin, authorization , accept, content-type, origin");
		context.register(filter);
		return true;
	}
}