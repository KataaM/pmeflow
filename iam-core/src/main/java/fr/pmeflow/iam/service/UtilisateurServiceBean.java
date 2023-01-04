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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.helper.KeycloakApiUtil;
import fr.pmeflow.iam.helper.KeycloakModelParser;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import io.quarkus.security.identity.SecurityIdentity;

/**
 * Ce service manage les utilisateurs via keycloak.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@RolesAllowed({"READ_UTILISATEUR", "WRITE_UTILISATEUR"})
public class UtilisateurServiceBean implements UtilisateurService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 20211132044L;
    private static final Log LOG = LogFactory.getLog(UtilisateurService.class);

    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String SERVICE_KEYCLOAK_TEXT = "LE SERVICE KEYCLOAK ";

    @ConfigProperty(name="keycloak-url") String keycloakUri;
    @ConfigProperty(name="quarkus.oidc.client-id") String serviceName; 
    @ConfigProperty(name="quarkus.oidc.auth-server-url") String authServerUrl;
    
    @ConfigProperty(name="quarkus.oidc.client-id") String clientId;
    @ConfigProperty(name="register.user.login") String registerUserLogin;
    @ConfigProperty(name="register.user.password") String registerUserPassword;
    
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
    @RolesAllowed("READ_UTILISATEUR")
    public List<Utilisateur> getUtilisateurs(Langue langue) throws BusinessException
    {
        List<Utilisateur> result = new ArrayList<>();
        if (token.getSubject()!=null)
        {
            result.addAll(KeycloakModelParser.parseUtilisateurs(keycloakClient, keycloakClient.users()));
        }
        return result;
    }



    @Override
    @RolesAllowed("READ_UTILISATEUR")
    public List<Utilisateur> getJuristes(Langue langue) throws BusinessException
    {
        List<Utilisateur> result = new ArrayList<>();
        if (token.getSubject()!=null)
        {
            List<Utilisateur> tmp = new ArrayList<>();
            tmp.addAll(KeycloakModelParser.parseUtilisateurs(keycloakClient, keycloakClient.users()));
            for (Utilisateur util : tmp)
            {
                if (util.getProfils().contains(ProfilPmeflow.JURISTE))
                {
                    result.add(util);
                }
            }
            result = CollectionUtil.sortCollectionAsc("nom", result);
        }
        return result;
    }

    

    @Override
    @RolesAllowed("WRITE_UTILISATEUR")
    public Utilisateur mergeUtilisateur(Langue langue, Utilisateur utilisateur) throws BusinessException
    {
        //Controler les parametres
        if (utilisateur==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { UtilisateurService.SERVICE_CODE, "id" }, langue));   
        }
        
        ConteneurEvenement events = utilisateur.validate();
        if (!events.getEvenementTypeErreur().isEmpty())
        {
            throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {UtilisateurService.SERVICE_CODE}, langue), events);
        }
        UserRepresentation user = KeycloakModelParser.toUserRepresentation(keycloakClient, utilisateur);
        if (StringUtil.isNotEmpty(user.getId()))
        {
            keycloakClient.users().get(utilisateur.getOid()).update(user);
        }
        else 
        {
            user.setEnabled(true);
            try (Response response = keycloakClient.users().create(user)) 
            {
                user.setId(KeycloakApiUtil.getCreatedId(response));
                utilisateur.setOid(user.getId());
            }
            catch (Exception e)
            {
                LOG.error(e,e);
            }
        }   
        
        try
        {
            ClientResource cliRes = keycloakClient.clients().get(PmeflowConstant.ARTMARKET_APP_CLIENT_ID);
            List<RoleRepresentation> utilisateurRoles = cliRes.roles().list();
            keycloakClient.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).remove(keycloakClient.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).listAll());
            List<RoleRepresentation> newRoles = new ArrayList<>();
            for (RoleRepresentation roleRep : utilisateurRoles)
            {
                for (ProfilPmeflow profilUsr : utilisateur.getProfils())
                {
                    if (profilUsr.name().equalsIgnoreCase(roleRep.getName()))
                    {
                        newRoles.add(roleRep);
                    }
                }
            }
            keycloakClient.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).add(newRoles);
            UserResource userRes = keycloakClient.users().get(user.getId());                
            userRes.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
        }
        catch(Exception e)
        {
            LOG.error(e,e);
        }
        return utilisateur; 
    }



    @Override
    @RolesAllowed("READ_UTILISATEUR")
    public Utilisateur getUtilisateurById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { UtilisateurService.SERVICE_CODE, "id" }, langue));   
        }
        
        Utilisateur result = null;
        UserResource userRes = keycloakClient.users().get(id);
        result = KeycloakModelParser.parseUtilisateur(userRes);
        if (result==null || StringUtil.isEmpty(result.getOid()))
        {
            throw new BusinessException(
                    PmeflowResources.getString("MSG.ERROR.EXCEPUTI_01",
                            new String[] { UtilisateurService.SERVICE_CODE},
                            langue));
        }
        return result;
    }
}