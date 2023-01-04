/*******************************************************************************
 *    
 *                           APPLICATION PMEFLOW
 *                          =====================
 *                          
 *   Copyright (C) 2002 Lixtec-Ludovic TERRAL
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   @AUTHOR Lixtec-Ludovic TERRAL
 *
 ******************************************************************************/
package fr.pmeflow.stripe;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stripe.model.Customer;

import fr.lixbox.common.util.StringUtil;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ModePriseContact;
import fr.pmeflow.stripe.helper.ExtendKeycloak;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;


/**
 * 
 * @author ludovic.terral
 */
@QuarkusTest
class TestCustomerService implements Serializable
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = 202209081514L;
    private static final String BASE_PATH="/stripe/api/1.0/customer";
    private static final String SERVICE_URI="http://localhost:15000/stripe/api";
    private static RequestSpecification endpoint;
    
    @ConfigProperty(name="test.keycloakUri") String keycloakUri;
    @ConfigProperty(name="test.realmName") String realmName;
    @ConfigProperty(name="test.login") String login;
    @ConfigProperty(name="test.password") String password;
    @ConfigProperty(name="test.clientId") String clientId;
    @ConfigProperty(name="test.privateKey") String privateKey;
    
    @ConfigProperty(name="quarkus.http.port") int servicePort;

    private CustomerService securedService;
    
    

    // ----------- Methode(s) -----------
    @BeforeAll
    static void setupEndPoint() 
    {
        endpoint = given();
        endpoint.basePath(BASE_PATH);
    }
    
    
    
    @BeforeEach
    void initialisation() 
    {
        endpoint.port(servicePort);
        try 
        {
            String authToken = ExtendKeycloak.getInstance(keycloakUri, realmName, login, password, clientId).tokenManager().getAccessTokenString();

            ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
            ResteasyClient client = builder.build();
            client.register(new BearerAuthenticator(authToken));
            ResteasyWebTarget target = client.target(UriBuilder.fromPath(SERVICE_URI));
            securedService = target.proxy(CustomerService.class);
        }
        catch (Exception e)
        {
            Assertions.fail(e);
        }
    }



    @Test
    void test_checkHealth() {
        endpoint
          .when().get("/health")
          .then()
             .statusCode(200)
             .body(is("{\"status\":\"UP\",\"checks\":[]}"));
    }



    @Test
    void checkReady() {
        endpoint
          .when().get("/health/ready")
          .then()
             .statusCode(200)
             .body(is("{\"status\":\"UP\",\"checks\":[]}"));
    }



    @Test
    void test_checkLive() {
        endpoint
          .when().get("/health/live")
          .then()
             .statusCode(200)
             .body(is("{\"status\":\"UP\",\"checks\":[]}"));
    }



    @Test
    void test_getVersion() {
        endpoint
          .when().get("/version")
          .then()
             .statusCode(200);
    }



    @Test
    void test_getClientById()
    {
        try
        {
            Client client = new Utilisateur();
            client.setNom("DUPONT_getClientById");
            client.setPrenom("JEAN_getClientById");
            client.setTelephone("555-5551");
            client.setEmail("jean.dupont_getClientById@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));

            //verification du client memorise dans stripe
            client = securedService.getClientById(Langue.FR_FR, privateKey, client.getCustomerStripeId());
            Assertions.assertTrue("jean.dupont_getClientById@reef.fr".equals(client.getEmail()));
            Assertions.assertTrue("555-5551".equals(client.getTelephone()));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_mergeClient_new()
    {
        try
        {
            Client client = new Utilisateur();
            client.setNom("DUPONT_new");
            client.setPrenom("JEAN_new");
            client.setTelephone("555-5552");
            client.setEmail("jean.dupont_new@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));

            //verification du client memorise dans stripe
            client = securedService.getClientById(Langue.FR_FR, privateKey, client.getCustomerStripeId());
            Assertions.assertTrue("jean.dupont_new@reef.fr".equals(client.getEmail()));
            Assertions.assertTrue("555-5552".equals(client.getTelephone()));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_mergeClient_update()
    {
        try
        {
            //memorisation du client dans stripe
            Client client = new Utilisateur();
            client.setNom("DUPONT_update");
            client.setPrenom("JEAN_update");
            client.setTelephone("555-5571");
            client.setEmail("jean.dupont_update@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //verification du client memorise dans stripe
            client = securedService.getClientById(Langue.FR_FR, privateKey, client.getCustomerStripeId());
            Assertions.assertTrue("jean.dupont_update@reef.fr".equals(client.getEmail()));
            Assertions.assertTrue("555-5571".equals(client.getTelephone()));

            //update du client dans stripe
            client.setNom("DUPONT_update2");
            client.setPrenom("JEAN_update2");
            client.setTelephone("555-5554");
            client.setEmail("jean.dupont_update2@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //verification du client memorise dans stripe
            client = securedService.getClientById(Langue.FR_FR, privateKey, client.getCustomerStripeId());
            Assertions.assertTrue("jean.dupont_update2@reef.fr".equals(client.getEmail()));
            Assertions.assertTrue("555-5554".equals(client.getTelephone()));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_mergeClient_new_existentStripe()
    {
        try
        {
            //memorisation du client dans stripe
            Client client = new Utilisateur();
            client.setNom("DUPONT_new_existentStrpe");
            client.setPrenom("JEAN_new_existentStrpe");
            client.setTelephone("555-5573");
            client.setEmail("jean.dupont_new_existentStrpe@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));

            //verification du client memorise dans stripe
            client = securedService.getClientById(Langue.FR_FR, privateKey, client.getCustomerStripeId());
            Assertions.assertTrue("jean.dupont_new_existentStrpe@reef.fr".equals(client.getEmail()));
            Assertions.assertTrue("555-5573".equals(client.getTelephone()));
            TimeUnit.SECONDS.sleep(20);
            
            //update du client dans stripe
            String initialId = client.getCustomerStripeId();
            client.setCustomerStripeId(null);
            client.setNom("DUPONT_new_existentStripe");
            client.setPrenom("JEAN_new_existentStripee");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            Assertions.assertEquals(initialId, client.getCustomerStripeId());
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }
}