/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.iam.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Profil;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ModePriseContact;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;

/**
 * Cette classe sert à parser le modele keycloak vers le modele iam et vice versa
 * 
 * @author ludovic.terral
 *
 */
public class KeycloakModelParser
{   
    // ----------- Attribut(s) ----------- 
    private static final List<String> DAYS = Arrays.asList("lundi","mardi","mercredi","jeudi","vendredi","samedi","dimanche");
    private static final String DISPONIBILITE_FIELD = "disponibilite-";
    private static final String STRIPE_CUSTOMER_ID_FIELD = "stripeCustomerId";
    private static final String SPECIALITE_FIELD = "specialite";
    private static final String PRISE_CONTACT_FIELD = "priseContact";
    private static final String TELEPHONE_FIELD = "telephone";
    private static final String VILLE_FIELD = "ville";
    private static final String CODE_POSTAL_FIELD = "codePostal";
    private static final String ADRESSE_FIELD = "adresse";
    
    
    
    // ----------- Methode(s) -----------
    private KeycloakModelParser() 
    {
        //util class
    }
    
    
    
    public static Profil parseProfil(UserResource userRes)
    {
        UserRepresentation userRepresentation = userRes.toRepresentation();
        Profil profil = new Profil();
        profil.setLogin(userRepresentation.getUsername());
        profil.setNom(userRepresentation.getLastName());
        profil.setOid(userRepresentation.getId());
        profil.setPrenom(userRepresentation.getFirstName());
        profil.setEmail(userRepresentation.getEmail());
        
        if (userRepresentation.getAttributes()!=null)
        {
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(ADRESSE_FIELD)))
            {
                profil.setAdresse(userRepresentation.getAttributes().get(ADRESSE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD)))
            {
                profil.setCodePostal(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(VILLE_FIELD)))
            {
                profil.setVille(userRepresentation.getAttributes().get(VILLE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(TELEPHONE_FIELD)))
            {
                profil.setTelephone(userRepresentation.getAttributes().get(TELEPHONE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)))
            {
                profil.setPriseContact(ModePriseContact.valueOf(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(SPECIALITE_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(SPECIALITE_FIELD).get(0)))
            {
                profil.setSpecialite(userRepresentation.getAttributes().get(SPECIALITE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD).get(0)))
            {
                profil.setCustomerStripeId(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD).get(0));
            }
            
            for (String day : DAYS)
            {
                if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day).get(0)))
                {
                    profil.getDisponibilite().put(day, JsonUtil.transformJsonToObject(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day).get(0), new TypeReference<Map<String, Map<String, Integer>>>(){}));
                }
                else
                {
                    profil.getDisponibilite().put(day, new LinkedHashMap<>());
                }
            }
        }
        return profil;
    }



    public static UserRepresentation toUserRepresentation(RealmResource keycloakClient, Profil profil)
    {
        UserRepresentation usrRep = StringUtil.isEmpty(profil.getOid())?new UserRepresentation():keycloakClient.users().get(profil.getOid()).toRepresentation();
        usrRep.setUsername(profil.getLogin());   
        usrRep.setEmail(profil.getEmail());
        usrRep.setFirstName(profil.getPrenom());
        usrRep.setLastName(profil.getNom());
        if (usrRep.getAttributes()==null)
        {
            usrRep.setAttributes(new HashMap<>());
        }
        usrRep.getAttributes().put(ADRESSE_FIELD, Arrays.asList(profil.getAdresse()));
        usrRep.getAttributes().put(CODE_POSTAL_FIELD, Arrays.asList(profil.getCodePostal()));
        usrRep.getAttributes().put(VILLE_FIELD, Arrays.asList(profil.getVille()));
        usrRep.getAttributes().put(TELEPHONE_FIELD, Arrays.asList(profil.getTelephone()));
        usrRep.getAttributes().put(PRISE_CONTACT_FIELD, Arrays.asList(profil.getPriseContact()!=null?profil.getPriseContact().toString():""));
        usrRep.getAttributes().put(SPECIALITE_FIELD, Arrays.asList(profil.getSpecialite()!=null?profil.getSpecialite().toString():""));
        for (String day : DAYS)
        {
            usrRep.getAttributes().put(DISPONIBILITE_FIELD+day, Arrays.asList(profil.getDisponibilite()!=null?JsonUtil.transformObjectToJson(profil.getDisponibilite().get(day),false):""));
        }
        return usrRep;
    }



    public static List<Client> parseClients(RealmResource keycloakClient, UsersResource users)
    {   
        List<Client> clients = new ArrayList<>();
        for (UserRepresentation user : users.list())
        {
            boolean isClient = false;
            List<RoleRepresentation> roles = keycloakClient.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).listAll();
            for (RoleRepresentation role : roles)
            {
                if ("CLIENT".equalsIgnoreCase(role.getName())) {
                    isClient=true;
                }
            }
            if (isClient)
            {
                clients.add(parseClient(user));
            }
        }
        return clients;
    }



    public static UserRepresentation toUserRepresentation(RealmResource keycloakClient, Client client)
    {
        String login = StringUtil.removeAccent(client.getPrenom()+"."+client.getNom()).toLowerCase();
        UserRepresentation usrRep = StringUtil.isEmpty(client.getOid())?new UserRepresentation():keycloakClient.users().get(client.getOid()).toRepresentation();
        usrRep.setUsername(login);   
        usrRep.setEmail(client.getEmail());
        usrRep.setFirstName(client.getPrenom());
        usrRep.setLastName(client.getNom());
        if (usrRep.getAttributes()==null)
        {
            usrRep.setAttributes(new HashMap<>());
        }
        usrRep.getAttributes().put(ADRESSE_FIELD, Arrays.asList(client.getAdresse()));
        usrRep.getAttributes().put(CODE_POSTAL_FIELD, Arrays.asList(client.getCodePostal()));
        usrRep.getAttributes().put(VILLE_FIELD, Arrays.asList(client.getVille()));
        usrRep.getAttributes().put(TELEPHONE_FIELD, Arrays.asList(client.getTelephone()));
        usrRep.getAttributes().put(PRISE_CONTACT_FIELD, Arrays.asList(client.getPriseContact()!=null?client.getPriseContact().toString():""));
        usrRep.getAttributes().put(STRIPE_CUSTOMER_ID_FIELD, Arrays.asList(client.getCustomerStripeId()));
        return usrRep;
    }



    public static Client parseClient(UserRepresentation userRepresentation)
    {
        Client client = new Client();
        client.setNom(userRepresentation.getLastName());
        client.setOid(userRepresentation.getId());
        client.setPrenom(userRepresentation.getFirstName());
        client.setEmail(userRepresentation.getEmail());
        
        if (userRepresentation.getAttributes()!=null)
        {
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(ADRESSE_FIELD)))
            {
                client.setAdresse(userRepresentation.getAttributes().get(ADRESSE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD)))
            {
                client.setCodePostal(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(VILLE_FIELD)))
            {
                client.setVille(userRepresentation.getAttributes().get(VILLE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(TELEPHONE_FIELD)))
            {
                client.setTelephone(userRepresentation.getAttributes().get(TELEPHONE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)))
            {
                client.setPriseContact(ModePriseContact.valueOf(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD).get(0)))
            {
                client.setCustomerStripeId(userRepresentation.getAttributes().get(STRIPE_CUSTOMER_ID_FIELD).get(0));
            }
        }
        return client;
    }



    public static Collection<Utilisateur> parseUtilisateurs(
            RealmResource keycloakUtilisateur, UsersResource users)
    {        
        List<Utilisateur> utilisateurs = new ArrayList<>();
        for (UserRepresentation user : users.list())
        {
            boolean isUser = false;
            List<RoleRepresentation> roles = keycloakUtilisateur.users().get(user.getId()).roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).listAll();
            for (RoleRepresentation role : roles)
            {
                if (ProfilPmeflow.JURISTE.name().equalsIgnoreCase(role.getName()) ||
                    ProfilPmeflow.ADMIN_FONCTIONNEL.name().equalsIgnoreCase(role.getName()) ||
                    ProfilPmeflow.ADMIN.name().equalsIgnoreCase(role.getName())) 
                {
                    isUser=true;
                }
            }
            if (isUser)
            {
                utilisateurs.add(parseUtilisateur(users.get(user.getId())));
            }
        }
        return utilisateurs;
    }



    public static UserRepresentation toUserRepresentation(RealmResource keycloakClient,
            Utilisateur utilisateur)
    {
        String login = StringUtil.removeAccent(utilisateur.getPrenom()+"."+utilisateur.getNom()).toLowerCase();
        UserRepresentation usrRep = StringUtil.isEmpty(utilisateur.getOid())?new UserRepresentation():keycloakClient.users().get(utilisateur.getOid()).toRepresentation();
        usrRep.setUsername(login);   
        usrRep.setEmail(utilisateur.getEmail());
        usrRep.setFirstName(utilisateur.getPrenom());
        usrRep.setLastName(utilisateur.getNom());
        if (usrRep.getAttributes()==null)
        {
            usrRep.setAttributes(new HashMap<>());
        }
        usrRep.getAttributes().put(ADRESSE_FIELD, Arrays.asList(utilisateur.getAdresse()));
        usrRep.getAttributes().put(CODE_POSTAL_FIELD, Arrays.asList(utilisateur.getCodePostal()));
        usrRep.getAttributes().put(VILLE_FIELD, Arrays.asList(utilisateur.getVille()));
        usrRep.getAttributes().put(TELEPHONE_FIELD, Arrays.asList(utilisateur.getTelephone()));
        usrRep.getAttributes().put(PRISE_CONTACT_FIELD, Arrays.asList(utilisateur.getPriseContact()!=null?utilisateur.getPriseContact().toString():""));
        usrRep.setClientRoles(new HashMap<>());
        usrRep.getClientRoles().put(PmeflowConstant.ARTMARKET_APP_CLIENT_ID, utilisateur.getProfils().stream().map(ProfilPmeflow::name).collect(Collectors.toList()));

        for (String day : DAYS)
        {
            usrRep.getAttributes().put(DISPONIBILITE_FIELD+day, Arrays.asList(utilisateur.getDisponibilite()!=null?JsonUtil.transformObjectToJson(utilisateur.getDisponibilite().get(day),false):""));
        }
        return usrRep;
    }



    public static Utilisateur parseUtilisateur(UserResource userResource)
    {
        UserRepresentation userRepresentation = userResource.toRepresentation();
        List<RoleRepresentation> roles = userResource.roles().clientLevel(PmeflowConstant.ARTMARKET_APP_CLIENT_ID).listAll();        
        userRepresentation.setClientRoles(new HashMap<>());
        userRepresentation.getClientRoles().put(PmeflowConstant.ARTMARKET_APP_CLIENT_ID, roles.stream().map(RoleRepresentation::getName).collect(Collectors.toList()));
        
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(userRepresentation.getLastName());
        utilisateur.setOid(userRepresentation.getId());
        utilisateur.setPrenom(userRepresentation.getFirstName());
        utilisateur.setEmail(userRepresentation.getEmail());
                
        if (userRepresentation.getAttributes()!=null)
        {
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(ADRESSE_FIELD)))
            {
                utilisateur.setAdresse(userRepresentation.getAttributes().get(ADRESSE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD)))
            {
                utilisateur.setCodePostal(userRepresentation.getAttributes().get(CODE_POSTAL_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(VILLE_FIELD)))
            {
                utilisateur.setVille(userRepresentation.getAttributes().get(VILLE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(TELEPHONE_FIELD)))
            {
                utilisateur.setTelephone(userRepresentation.getAttributes().get(TELEPHONE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)))
            {
                utilisateur.setPriseContact(ModePriseContact.valueOf(userRepresentation.getAttributes().get(PRISE_CONTACT_FIELD).get(0)));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(SPECIALITE_FIELD)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(SPECIALITE_FIELD).get(0)))
            {
                utilisateur.setSpecialite(userRepresentation.getAttributes().get(SPECIALITE_FIELD).get(0));
            }
            if (CollectionUtil.isNotEmpty(userRepresentation.getClientRoles().get(PmeflowConstant.ARTMARKET_APP_CLIENT_ID)))
            {
                for (RoleRepresentation rpProfil : roles)
                {
                    utilisateur.getProfils().add(ProfilPmeflow.valueOf(rpProfil.getName()));
                }
            }            
            for (String day : DAYS)
            {
                if (CollectionUtil.isNotEmpty(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day)) && StringUtil.isNotEmpty(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day).get(0)))
                {
                    utilisateur.getDisponibilite().put(day, JsonUtil.transformJsonToObject(userRepresentation.getAttributes().get(DISPONIBILITE_FIELD+day).get(0), new TypeReference<Map<String, Map<String, Integer>>>(){}));
                }
                else
                {
                    utilisateur.getDisponibilite().put(day, new LinkedHashMap<>());
                }
            }
        }
        return utilisateur;
    }
}
