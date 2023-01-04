/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.iam.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.ProfilService;
import fr.pmeflow.iam.helper.KeycloakApiUtil;
import fr.pmeflow.iam.helper.KeycloakModelParser;
import fr.pmeflow.iam.model.Profil;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import io.quarkus.security.identity.SecurityIdentity;

/**
 * Ce service manage le profil utilisateur via keycloak.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_PROFIL", "WRITE_PROFIL"})
public class ProfilServiceBean implements ProfilService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 20211132044L;
    private static final Log LOG = LogFactory.getLog(ProfilService.class);
    
    private static final String SERVICE_KEYCLOAK_TEXT = "LE SERVICE KEYCLOAK ";

    @ConfigProperty(name="keycloak-url") String keycloakUri;
    @ConfigProperty(name="quarkus.oidc.client-id") String serviceName; 
    @ConfigProperty(name="quarkus.oidc.auth-server-url") String authServerUrl;
    
    @ConfigProperty(name="quarkus.oidc.client-id") String clientId;
    
    @Inject @LocalRegistryConfig RealmResource keycloakClient;    
    @Inject SecurityIdentity securityContext;
    @Inject JsonWebToken token;
    
    
    
    // ----------- Methode(s) -----------
    @Override
    @PermitAll
    public ServiceState checkHealth() 
    {
        return checkReady();
    }

    
    
    @Override
    @PermitAll
    public ServiceState checkReady()
    {
        LOG.debug("keycloak uri: "+keycloakUri);
        LOG.debug("Check Health started");
        ServiceState state = new ServiceState();
        
        //controle de keycloak
        try
        {
            keycloakClient.getClientSessionStats();
            state.setStatus(ServiceStatus.UP);
            state.getChecks().add(new Check(ServiceStatus.UP, SERVICE_KEYCLOAK_TEXT+keycloakUri+" EST DISPONIBLE"));
        }
        catch (Exception e)
        {
            state.setStatus(ServiceStatus.DOWN);
            state.getChecks().add(new Check(ServiceStatus.DOWN, SERVICE_KEYCLOAK_TEXT+keycloakUri+" N'EST PAS DISPONIBLE"));
        }
        
        LOG.debug("Check Health finished");
        if (state.getStatus().equals(ServiceStatus.DOWN))
        {
            throw new ProcessusException(state.toString());
        }
        return state;
    }
    
    
    
    @Override
    @PermitAll
    public ServiceState checkLive() 
    {
        return new ServiceState(ServiceStatus.UP);
    }



    /**
     * Cette methode renvoie la version courante du code. 
     */
    @Override
    @PermitAll
    public String getVersion()
    {   
        return CodeVersionUtil.getVersion(this.getClass());
    }



    @Override
    @RolesAllowed("READ_PROFIL")
    public Profil getProfil(Langue langue) throws BusinessException
    {
        Profil result = new Profil();
        if (token.getSubject()!=null)
        {
            UserResource userRes = keycloakClient.users().get(token.getSubject());
            result = KeycloakModelParser.parseProfil(userRes);
        }
        return result;
    }



    @Override
    @RolesAllowed("READ_PROFIL")
    public Map<String, Map<String, Map<String, Integer>>> getDisponibilite(Langue langue) throws BusinessException
    {
        Profil result = new Profil();
        if (token.getSubject()!=null)
        {
            UserResource userRes = keycloakClient.users().get(token.getSubject());
            result = KeycloakModelParser.parseProfil(userRes);
        }
        return result.getDisponibilite();
    }

    

    @Override
    @RolesAllowed("WRITE_PROFIL")
    public Profil mergeProfil(Langue langue, Profil profil) throws BusinessException
    {
        UserRepresentation user = KeycloakModelParser.toUserRepresentation(keycloakClient, profil);
        if (StringUtil.isNotEmpty(user.getId()))
        {
            keycloakClient.users().get(profil.getOid()).update(user);
        }
        else 
        {
            try (Response response = keycloakClient.users().create(user)) 
            {
                user.setId(KeycloakApiUtil.getCreatedId(response));
                profil.setOid(user.getId());
            }
        }   
        return profil; 
    }
    

    
    @Override
    @RolesAllowed("WRITE_PROFIL")
    public Boolean changePassword(@PathParam("langue") Langue langue, String password) throws BusinessException
    {
        Boolean result = false;
        
        try
        {
            UserResource userRes = keycloakClient.users().get(token.getSubject());
            UserRepresentation utilRep = userRes.toRepresentation();
            utilRep.setCredentials(new ArrayList<>());
            CredentialRepresentation credRep = new CredentialRepresentation();
            credRep.setTemporary(false);
            credRep.setType("password");
            credRep.setValue(password);        
            utilRep.getCredentials().add(credRep);
            userRes.update(utilRep);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, ProfilService.SERVICE_CODE, true);
        }
        return result; 
    }
    

    
    @Override
    @PermitAll
    public Boolean reinitialiserCompte(Langue langue, String email) throws BusinessException
    {
        Boolean result = false;
        
        try
        {
            List<UserRepresentation> users = keycloakClient.users().search(null, null, null, email, true, 0, 1, true, false);
            if (CollectionUtil.isNotEmpty(users))
            {
                UserRepresentation utilRep = users.get(0);
                utilRep.getRequiredActions().add("UPDATE_PASSWORD");
                UserResource userRes = keycloakClient.users().get(utilRep.getId());                
                userRes.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
                result = true;                
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, ProfilService.SERVICE_CODE, true);
        }
        return result; 
    }
}