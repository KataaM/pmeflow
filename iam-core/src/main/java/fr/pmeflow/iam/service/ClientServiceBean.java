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
import fr.pmeflow.iam.ClientService;
import fr.pmeflow.iam.helper.ExtendKeycloak;
import fr.pmeflow.iam.helper.KeycloakApiUtil;
import fr.pmeflow.iam.helper.KeycloakModelParser;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Profil;
import fr.pmeflow.stripe.CustomerService;
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
 * Ce service manage les clients via keycloak.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@RolesAllowed({"READ_CLIENT", "WRITE_CLIENT"})
public class ClientServiceBean implements ClientService
{
    private static final String REALM_NAME = "master";
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 20211132044L;
    private static final Log LOG = LogFactory.getLog(ClientService.class);

    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String SERVICE_KEYCLOAK_TEXT = "LE SERVICE KEYCLOAK ";
    private static final String MSG_NOT_LOGIN = "IMPOSSIBLE DE VOUS AUTHENTIFIER, MERCI DE VERIFIER VOTRE SAISIE";

    @ConfigProperty(name="keycloak-url") String keycloakUri;
    @ConfigProperty(name="quarkus.oidc.client-id") String serviceName; 
    @ConfigProperty(name="quarkus.oidc.auth-server-url") String authServerUrl;
    
    @ConfigProperty(name="quarkus.oidc.client-id") String clientId;
    @ConfigProperty(name="register.user.login") String registerUserLogin;
    @ConfigProperty(name="register.user.password") String registerUserPassword;    
    @ConfigProperty(name="doc.generator.login") String docGeneratorLogin;
    @ConfigProperty(name="doc.generator.password") String docGeneratorPwd;
    
    
    @Inject @LocalRegistryConfig RealmResource keycloakClient;
    @Inject @LocalRegistryConfig CustomerService customerService;    
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
    @RolesAllowed("READ_CLIENT")
    public List<Client> getClients(Langue langue) throws BusinessException
    {
        List<Client> result = new ArrayList<>();
        if (token.getSubject()!=null)
        {
            result.addAll(KeycloakModelParser.parseClients(keycloakClient, keycloakClient.users()));
        }
        result = CollectionUtil.sortCollectionAsc("nom", result);
        return result;
    }

    

    @Override
    @RolesAllowed("WRITE_CLIENT")
    public Client mergeClient(Langue langue, Client client) throws BusinessException
    {
        //Controler les parametres
        if (client==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ClientService.SERVICE_CODE, "id" }, langue));   
        }
        
        ConteneurEvenement events = client.validate();
        if (!events.getEvenementTypeErreur().isEmpty())
        {
            throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {ClientService.SERVICE_CODE}, langue), events);
        }
                
        //merge customer on stripe
        try
        {
            client = customerService.mergeClient(langue, client);
        }
        catch(Exception e)
        {
            LOG.error(e,e);
        }
        
        //update customer on keycloak
        UserRepresentation user = KeycloakModelParser.toUserRepresentation(keycloakClient, client);        
        if (StringUtil.isNotEmpty(user.getId()))
        {
            keycloakClient.users().get(client.getOid()).update(user);
        }
        else 
        {
            user.setEnabled(true);
            try (Response response = keycloakClient.users().create(user)) 
            {
                RoleRepresentation profil = null;                
                ClientResource cliRes = keycloakClient.clients().get(PmeflowConstant.ARTMARKET_APP_CLIENT_ID);
                List<RoleRepresentation> clientRoles = cliRes.roles().list();
                for (RoleRepresentation roleRep : clientRoles)
                {
                    if ("CLIENT".equalsIgnoreCase(roleRep.getName()))
                    {
                        profil = roleRep;
                    }       
                }
                user.setId(KeycloakApiUtil.getCreatedId(response));
                keycloakClient.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).add(Arrays.asList(profil));
                client.setOid(user.getId());
            }
            catch (Exception e)
            {
                LOG.error(e,e);
            }
        }   
        try
        {
            UserResource userRes = keycloakClient.users().get(user.getId());                
            userRes.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
        }
        catch(Exception e)
        {
            LOG.error(e,e);
        }
        return client; 
    }



    @Override
    @PermitAll()
    public Client getClientByLoginPwd(Langue langue, String login, String pwd) 
        throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(login))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new Object[] { ClientService.SERVICE_CODE, "login" }, langue));   
        }
        if (StringUtil.isEmpty(pwd))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new Object[] { ClientService.SERVICE_CODE, "pwd" }, langue));   
        }
        
        
        Profil result = null;
        
        try
        {
            //by email
            List<UserRepresentation> users = keycloakClient.users().search(null,null,null,login,0,1);
            for (UserRepresentation user : users)
            {
                UserResource userRes = keycloakClient.users().get(user.getId());
                result = KeycloakModelParser.parseProfil(userRes);
            }
            
            //by login
            if (result == null)
            {
                users = keycloakClient.users().search(login,0,1);
                for (UserRepresentation user : users)
                {
                    UserResource userRes = keycloakClient.users().get(user.getId());
                    result = KeycloakModelParser.parseProfil(userRes);
                }
            }
            if (result!=null)
            {
                ExtendKeycloak.getInstance(keycloakUri, REALM_NAME, result.getLogin(), pwd, clientId).tokenManager().getAccessTokenString();
            }
        }
        catch (Exception e) 
        {
            result = null;
        }
        if (result == null)
        {
            throw new BusinessException(MSG_NOT_LOGIN);
        }
        return result;
    }



    @Override
    @RolesAllowed("READ_CLIENT")
    public Client getClientById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { ClientService.SERVICE_CODE, "id" }, langue));   
        }
        
        Client result = new Profil();
        if (StringUtil.isNotEmpty(id))
        {
            UserResource userRes = keycloakClient.users().get(id);
            result = KeycloakModelParser.parseClient(userRes.toRepresentation());
        }
        return result;
    }



    @Override
    @PermitAll
    public String getRegisterToken(Langue langue) throws BusinessException
    {
        return ExtendKeycloak.getInstance(keycloakUri, REALM_NAME, registerUserLogin, registerUserPassword, clientId).tokenManager().getAccessTokenString();
    }



    @Override
    @PermitAll
    public String getDocGeneratorToken(Langue langue) throws BusinessException
    {
        return ExtendKeycloak.getInstance(keycloakUri, REALM_NAME, docGeneratorLogin, docGeneratorPwd, clientId).tokenManager().getAccessTokenString();
    }
}