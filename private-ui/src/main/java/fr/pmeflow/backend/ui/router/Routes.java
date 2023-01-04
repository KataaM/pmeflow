/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.ui.router;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.stream.util.StreamStringUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

/**
 * Ce roueur assure le dispatch des différentes vues.
 * 
 * @author ludovic.terral
 */
@RequestScoped 
public class Routes implements Serializable
{
    // ----------- Attributs -----------    
    private static final long serialVersionUID = 202201281909L;
    private static final Log LOG = LogFactory.getLog(Routes.class);
    
    @ConfigProperty(name="backend.path") String backendPath;
    @ConfigProperty(name="referentiel.path") String referentielPath;
    @ConfigProperty(name="iam.path") String iamPath;
    @ConfigProperty(name="sso.api.uri") String ssoApiUri;
    
    @Inject @LocalRegistryConfig ParametreService parametreService;

    

    // ----------- Methodes -----------
    @Route(path = "/configuration")
    void routeToConfiguration(RoutingContext rc) throws BusinessException
    {
        String tva="20";
        String stripePublicKey="";
        String dureeRdv="60";
        List<Map<String,String>> eventTypes=null;
        try
        {
            Parametre parametre = parametreService.getParametreByCode("pmeflow", "stripe-public-key");
            stripePublicKey=parametre.getValue();

            parametre = parametreService.getParametreByCode("pmeflow-rdv", "rdv.duree");
            dureeRdv=parametre.getValue();

            parametre = parametreService.getParametreByCode("pmeflow-rdv", "event.types");
            eventTypes=JsonUtil.transformJsonToObject(parametre.getValue(),new TypeReference<List<Map<String,String>>>() {});

            parametre = parametreService.getParametreByCode("pmeflow", "VAT_RATE");
            tva=parametre.getValue();
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, "PRIVUI", false);
        }
        
        String response = "";
        Map<String,Object> config = new HashMap<>();        
        Map<String,Object> services = new HashMap<>();
        services.put("abonnementServiceUri", backendPath+"/backend/api/1.0/abonnement");
        services.put("clientServiceUri", iamPath+"/iam/api/1.0/client");
        services.put("documentServiceUri", backendPath+"/backend/api/1.0/document");
        services.put("dossierServiceUri", backendPath+"/backend/api/1.0/dossier");
        services.put("evenementServiceUri", backendPath+"/backend/api/1.0/evenement");
        services.put("facturationServiceUri", backendPath+"/backend/api/1.0/facturation");
        services.put("keycloakServiceUri", ssoApiUri);
        services.put("parametreServiceUri", referentielPath+"/referentiel/api/1.0/parametre");
        services.put("prestationServiceUri", backendPath+"/backend/api/1.0/prestation");        
        services.put("profilServiceUri", iamPath+"/iam/api/1.0/profil");
        services.put("refAbonnementServiceUri", referentielPath+"/referentiel/api/1.0/abonnement");
        services.put("refFormulaireServiceUri", referentielPath+"/referentiel/api/1.0/formulaire");
        services.put("refPrestationServiceUri", referentielPath+"/referentiel/api/1.0/prestation");
        services.put("rendezVousServiceUri", backendPath+"/backend/api/1.0/rendez-vous");
        services.put("utilisateurServiceUri", iamPath+"/iam/api/1.0/utilisateur");
        config.put("services", services);
        
        Map<String,Object> parametres = new HashMap<>();
        parametres.put("tva", tva);
        parametres.put("stripePublicKey", stripePublicKey);
        parametres.put("dureeRdv", dureeRdv);
        parametres.put("eventTypes", eventTypes);        
        config.put("parametres", parametres);
        
        response = JsonUtil.transformObjectToJson(config,false);        
        rc.response()
            .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .end(response, asr->{
                if(asr.succeeded()) 
                {
                    LOG.debug("success....");
                } 
                else 
                {
                    LOG.error("Something went wrong " + asr.cause());
                }
            });        
    }
    
    

    @Route(path = "/private")
    @Route(path = "/private/abonnements")
    @Route(path = "/private/clients")
    @Route(path = "/private/coaching_rdv")
    @Route(path = "/private/configuration")
    @Route(path = "/private/dashboard")
    @Route(path = "/private/dossiers")
    @Route(path = "/private/mon-planning")
    @Route(path = "/private/mon-profil")
    @Route(path = "/private/prestation")
    @Route(path = "/private/ref_abonnement")
    @Route(path = "/private/ref_formulaire")
    @Route(path = "/private/ref_prestation")
    @Route(path = "/private/utilisateurs")
    void routeToRegistry(RoutingContext rc)
    {
        String response = StreamStringUtil.read(Routes.class.getResourceAsStream("/META-INF/resources/index.html"));
        rc.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .end(response, asr->{
                if(asr.succeeded()) 
                {
                    LOG.debug("success....");
                } 
                else 
                {
                    LOG.error("Something went wrong " + asr.cause());
                }
            });        
    }
}
