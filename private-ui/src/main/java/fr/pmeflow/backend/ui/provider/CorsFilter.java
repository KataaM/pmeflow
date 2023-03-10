/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.ui.provider;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cette classe assure le traitement des headers lié à CORS.
 * 
 * @author ludovic.terral
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = {"/*","/private/*","/configuration"}, asyncSupported = true)
public class CorsFilter implements Filter
{
    // ----------- Attributs -----------    
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";



    // ----------- Methodes -----------
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) 
        throws IOException, ServletException 
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "access-control-allow-origin, authorization , accept, content-type, origin");
        response.setHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET,POST,OPTIONS,HEAD,DELETE");
        if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) 
        {
            chain.doFilter(servletRequest, servletResponse);
        }
        else 
        {
            response.flushBuffer();
        }
    }
}