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
import java.util.Map;
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
import com.stripe.model.PaymentIntent;

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
class TestPaymentService implements Serializable
{
    // ----------- Attribut(s) -----------
    private static final long serialVersionUID = 202209081514L;

    private static final String PAYMENT_BASE_PATH="/stripe/api/1.0/payment";
    private static final String SERVICE_URI="http://localhost:15000/stripe/api";
    private static RequestSpecification endpoint;
    
    @ConfigProperty(name="test.keycloakUri") String keycloakUri;
    @ConfigProperty(name="test.realmName") String realmName;
    @ConfigProperty(name="test.login") String login;
    @ConfigProperty(name="test.password") String password;
    @ConfigProperty(name="test.clientId") String clientId;
    @ConfigProperty(name="test.privateKey") String privateKey;
    
    @ConfigProperty(name="quarkus.http.port") int servicePort;

    private PaymentService securedPaymentService;
    private CustomerService securedCustomerService;
    
    

    // ----------- Methode(s) -----------
    @BeforeAll
    static void setupEndPoint() 
    {
        endpoint = given();
        endpoint.basePath(PAYMENT_BASE_PATH);
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
            securedPaymentService = target.proxy(PaymentService.class);
            
            target = client.target(UriBuilder.fromPath(SERVICE_URI));
            securedCustomerService = target.proxy(CustomerService.class);
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
    void test_preparePayment()
    {
        try
        {
            //create client
            Client client = new Utilisateur();
            client.setNom("DUPONT_preparePayment");
            client.setPrenom("JEAN_preparePayment");
            client.setTelephone("555-5561");
            client.setEmail("jean.dupont_preparePayment@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedCustomerService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //prepare paiement
            String paymentId = securedPaymentService.preparePayment(Langue.FR_FR, privateKey, 100.00, "test_preparePayment", client.getCustomerStripeId(), false).get("id");
            Assertions.assertTrue(StringUtil.isNotEmpty(paymentId));
            
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
            Assertions.assertTrue(paymentIntent!=null);
            Assertions.assertTrue(paymentIntent.getAmount()==10000 && paymentIntent.getDescription().equals("test_preparePayment"));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_pay_card_ko()
    {
        try
        {
            //create client
            Client client = new Utilisateur();
            client.setNom("DUPONT_pay_card");
            client.setPrenom("JEAN_pay_card");
            client.setTelephone("555-5562");
            client.setEmail("jean.dupont_pay_card@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedCustomerService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //prepare payment method
            String paymentMethodId = securedPaymentService.createCardPaymentMethod(Langue.FR_FR, privateKey, "4000000000009995", "9", "2023", "314");
            Assertions.assertTrue(StringUtil.isNotEmpty(paymentMethodId));

            //pay
            Map<String, String> result = securedPaymentService.pay(Langue.FR_FR, privateKey, 100.00, "test_pay_card_ko", client.getCustomerStripeId(), paymentMethodId);
            Assertions.assertTrue(StringUtil.isEmpty(result.get("id")));
            Assertions.assertTrue(result.get("fault_description").contains("Your card has insufficient funds."));
            Assertions.assertTrue("card_declined".equals(result.get("fault_code")));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_pay_card_ok()
    {
        try
        {
            //create client
            Client client = new Utilisateur();
            client.setNom("DUPONT_pay_card");
            client.setPrenom("JEAN_pay_card");
            client.setTelephone("555-5562");
            client.setEmail("jean.dupont_pay_card@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedCustomerService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //prepare payment method
            String paymentMethodId = securedPaymentService.createCardPaymentMethod(Langue.FR_FR, privateKey, "4242424242424242", "9", "2023", "314");
            Assertions.assertTrue(StringUtil.isNotEmpty(paymentMethodId));

            //pay
            Map<String, String> result = securedPaymentService.pay(Langue.FR_FR, privateKey, 100.00, "test_pay_card_ok", client.getCustomerStripeId(), paymentMethodId);
            Assertions.assertTrue(StringUtil.isEmpty(result.get("fault_code")));
            PaymentIntent paymentIntent = PaymentIntent.retrieve(result.get("id"));
            Assertions.assertTrue(paymentIntent!=null);
            Assertions.assertTrue(paymentIntent.getAmount()==10000);
            Assertions.assertTrue(paymentIntent.getDescription().equals("test_pay_card_ok"));
            Assertions.assertTrue(paymentIntent.getPaymentMethod().equals(paymentMethodId));
            Assertions.assertTrue(paymentIntent.getStatus().equals("succeeded"));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_pay_card_ok_but()
    {
        try
        {
            //create client
            Client client = new Utilisateur();
            client.setNom("DUPONT_pay_card");
            client.setPrenom("JEAN_pay_card");
            client.setTelephone("555-5562");
            client.setEmail("jean.dupont_pay_card@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedCustomerService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //prepare payment method
            String paymentMethodId = securedPaymentService.createCardPaymentMethod(Langue.FR_FR, privateKey, "4000002500003155", "9", "2023", "314");
            Assertions.assertTrue(StringUtil.isNotEmpty(paymentMethodId));

            //pay
            Map<String, String> result = securedPaymentService.pay(Langue.FR_FR, privateKey, 100.00, "test_pay_card_ok_but", client.getCustomerStripeId(), paymentMethodId);
            Assertions.assertTrue(StringUtil.isEmpty(result.get("fault_code")));
            PaymentIntent paymentIntent = PaymentIntent.retrieve(result.get("id"));
            Assertions.assertTrue(paymentIntent!=null);
            Assertions.assertTrue(paymentIntent.getAmount()==10000);
            Assertions.assertTrue(paymentIntent.getDescription().equals("test_pay_card_ok_but"));
            Assertions.assertTrue(paymentIntent.getPaymentMethod().equals(paymentMethodId));
            Assertions.assertTrue(paymentIntent.getStatus().equals("requires_action"));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }



    @Test
    void test_pay_sepa_ok()
    {
        try
        {
            //create client
            Client client = new Utilisateur();
            client.setNom("DUPONT_pay_sepa");
            client.setPrenom("JEAN_pay_sepa");
            client.setTelephone("555-5563");
            client.setEmail("jean.dupont_pay_sepa@reef.fr");
            client.setPriseContact(ModePriseContact.TEL);
            client = securedCustomerService.mergeClient(Langue.FR_FR, privateKey, client);
            Assertions.assertTrue(StringUtil.isNotEmpty(client.getCustomerStripeId()));
            
            //prepare payment method
            String paymentMethodId = securedPaymentService.createSepaPaymentMethod(Langue.FR_FR, privateKey, client, "FR1420041010050500013M02606");
            Assertions.assertTrue(StringUtil.isNotEmpty(paymentMethodId));

            //pay
            Map<String, String> result = securedPaymentService.pay(Langue.FR_FR, privateKey, 100.00, "test_pay_sepa_ok", client.getCustomerStripeId(), paymentMethodId);
            Assertions.assertTrue(StringUtil.isEmpty(result.get("fault_code")));
            PaymentIntent paymentIntent = PaymentIntent.retrieve(result.get("id"));
            Assertions.assertTrue(paymentIntent!=null);
            Assertions.assertTrue(paymentIntent.getAmount()==10000);
            Assertions.assertTrue(paymentIntent.getDescription().equals("test_pay_sepa_ok"));
            Assertions.assertTrue(paymentIntent.getPaymentMethod().equals(paymentMethodId));
            Assertions.assertTrue(paymentIntent.getStatus().equals("processing"));
            
            //verifier le success du paiement
            TimeUnit.SECONDS.sleep(15);
            paymentIntent = PaymentIntent.retrieve(result.get("id"));
            Assertions.assertTrue(paymentIntent.getStatus().equals("succeeded"));
            Customer.retrieve(client.getCustomerStripeId()).delete();
        }
        catch(Exception e)
        {
            System.err.println(e);
            Assertions.fail(e);
        }
    }
}