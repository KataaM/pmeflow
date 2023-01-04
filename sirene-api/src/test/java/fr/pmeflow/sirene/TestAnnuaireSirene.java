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
package fr.pmeflow.sirene;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.lixbox.io.json.JsonUtil;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.sirene.model.SireneResponse;

/**
 * 
 * @author ludovic.terral
 */
class TestAnnuaireSirene
{
    private static final String DEFAULT_API_URL = "https://api.insee.fr/entreprises";
    private static final String DEFAUlT_SIREN_PP = "811100387";
    private static final String DEFAULT_SIRET_PP = "81110038700018";
    
    private static final String DEFAUlT_SIREN_PM = "385009576";
    private static final String DEFAULT_SIRET_PM = "38500957600039";
    
    private static final String authToken = "90c708ee-03e7-373f-9b7a-cb3d10df6b11";
    
    private static AnnuaireSireneService service;
    
    
    
    
    @BeforeAll
    static void initialisation() 
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};

        // Install the all-trusting trust manager
        try 
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
            builder.sslContext(sc);
            ResteasyClient client = builder.build();
            client.register(new BearerAuthenticator(authToken));
            ResteasyWebTarget target = client.target(UriBuilder.fromPath(DEFAULT_API_URL));
            service = target.proxy(AnnuaireSireneService.class);
        }
        catch (Exception e)
        {
            Assertions.fail(e);
        }
    }

    @Test
    void testGetSiren_SIREN_PERSONNE_PHYSIQUE()
    {
        try
        {
            SireneResponse response = service.getOrganisationBySiren(DEFAUlT_SIREN_PP);
            Assertions.assertTrue(response!=null && 
                    response.getUniteLegale().getPeriodesUniteLegale().get(0).getActivitePrincipaleUniteLegale().equals("96.09Z"));
        }
        catch(Exception e)
        {
            Assertions.fail(e);
        }
    }



    @Test
    void testGetSiren_SIREN_PERSONNE_MORALE()
    {
        try
        {
            SireneResponse response = service.getOrganisationBySiren(DEFAUlT_SIREN_PM);
            Assertions.assertTrue(response!=null && response.getUniteLegale().getPeriodesUniteLegale().get(0).getActivitePrincipaleUniteLegale().equals("56.21Z"));
        }
        catch(Exception e)
        {
            Assertions.fail(e);
        }
    }



    @Test
    void testGetSiren_SIRET_PERSONNE_PHYSIQUE()
    {
        try
        {
            SireneResponse response = service.getOrganisationBySiret(DEFAULT_SIRET_PP);
            Assertions.assertTrue(JsonUtil.transformObjectToJson(response, false).length()==4458);
            Assertions.assertTrue(response!=null && response.getEtablissement().getSiret().equals(DEFAULT_SIRET_PP));
        }
        catch(Exception e)
        {
            Assertions.fail(e);
        }
    }



    @Test
    void testGetSiren_SIRET_PERSONNE_MORALE()
    {
        try
        {
            SireneResponse response = service.getOrganisationBySiret(DEFAULT_SIRET_PM);
            Assertions.assertTrue(response!=null && response.getEtablissement().getSiret().equals(DEFAULT_SIRET_PM));
        }
        catch(Exception e)
        {
            Assertions.fail(e);
        }
    }



    @Test
    void testGetSiren_SIRET_PERSONNE_MORALE_JSON()
    {
        try
        {
            SireneResponse response = service.getOrganisationBySiret(DEFAULT_SIRET_PM);
            Assertions.assertTrue(JsonUtil.transformObjectToJson(response, false).length()==2498);
            Assertions.assertTrue(response!=null && response.getEtablissement().getSiret().equals(DEFAULT_SIRET_PM));
        }
        catch(Exception e)
        {
            Assertions.fail(e);
        }
    }
}