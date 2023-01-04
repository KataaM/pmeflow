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
package fr.pmeflow.stripe.helper;

import static org.keycloak.OAuth2Constants.PASSWORD;

import java.net.URI;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.keycloak.admin.client.ClientBuilderWrapper;
import org.keycloak.admin.client.Config;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.JacksonProvider;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.BearerAuthFilter;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.ServerInfoResource;
import org.keycloak.admin.client.token.TokenManager;

/**
 * Provides a Keycloak client. By default, this implementation uses a the
 * default RestEasy client builder settings. To customize the underling client,
 * use a {@link KeycloakBuilder} to create a Keycloak client.
 *
 * To read Responses, you can use {@link CreatedResponseUtil} for objects
 * created
 *
 * @author rodrigo.sasaki@icarros.com.br
 * @see KeycloakBuilder
 */
public class ExtendKeycloak implements AutoCloseable
{
    private final Config config;
    private final TokenManager tokenManager;
    private final String authToken;
    private final ResteasyWebTarget target;
    private final Client client;
    private boolean closed = false;

    
    
    ExtendKeycloak(String serverUrl, String realm, String username, String password,
            String clientId, String clientSecret, String grantType, Client resteasyClient,
            String authtoken)
    {
        config = new Config(serverUrl, realm, username, password, clientId, clientSecret);
        client = resteasyClient != null ? resteasyClient : newRestEasyClient(null, null, false);
        authToken = authtoken;
        tokenManager = authtoken == null ? new TokenManager(config, client) : null;
        target = (ResteasyWebTarget) client.target(config.getServerUrl());
        target.register(newAuthFilter());
    }



    private BearerAuthFilter newAuthFilter()
    {
        return authToken != null ? new BearerAuthFilter(authToken)
                : new BearerAuthFilter(tokenManager);
    }



    private static Client newRestEasyClient(ResteasyJackson2Provider customJacksonProvider,
            SSLContext sslContext, boolean disableTrustManager)
    {
        ClientBuilder clientBuilder = ClientBuilderWrapper.create(sslContext, disableTrustManager);
        if (customJacksonProvider != null)
        {
            clientBuilder.register(customJacksonProvider, 100);
        }
        else
        {
            clientBuilder.register(JacksonProvider.class, 100);
        }
        return clientBuilder.build();
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId, String clientSecret, SSLContext sslContext,
            ResteasyJackson2Provider customJacksonProvider, boolean disableTrustManager,
            String authToken)
    {
        return new ExtendKeycloak(serverUrl, realm, username, password, clientId, clientSecret,
                PASSWORD, newRestEasyClient(customJacksonProvider, sslContext, disableTrustManager),
                authToken);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId, String clientSecret)
    {
        return getInstance(serverUrl, realm, username, password, clientId, clientSecret, null, null,
                false, null);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId, String clientSecret, SSLContext sslContext)
    {
        return getInstance(serverUrl, realm, username, password, clientId, clientSecret, sslContext,
                null, false, null);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId, String clientSecret, SSLContext sslContext,
            ResteasyJackson2Provider customJacksonProvider)
    {
        return getInstance(serverUrl, realm, username, password, clientId, clientSecret, sslContext,
                customJacksonProvider, false, null);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId)
    {
        return getInstance(serverUrl, realm, username, password, clientId, null, null, null, false,
                null);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String username,
            String password, String clientId, SSLContext sslContext)
    {
        return getInstance(serverUrl, realm, username, password, clientId, null, sslContext, null,
                false, null);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String clientId,
            String authToken)
    {
        return getInstance(serverUrl, realm, null, null, clientId, null, null, null, false,
                authToken);
    }



    public static ExtendKeycloak getInstance(String serverUrl, String realm, String clientId,
            String authToken, SSLContext sllSslContext)
    {
        return getInstance(serverUrl, realm, null, null, clientId, null, sllSslContext, null, false,
                authToken);
    }



    public RealmsResource realms()
    {
        return target.proxy(RealmsResource.class);
    }



    public RealmResource realm(String realmName)
    {
        return realms().realm(realmName);
    }



    public ServerInfoResource serverInfo()
    {
        return target.proxy(ServerInfoResource.class);
    }



    public TokenManager tokenManager()
    {
        return tokenManager;
    }



    /**
     * Create a secure proxy based on an absolute URI. All set up with
     * appropriate token
     *
     * @param proxyClass
     * @param absoluteURI
     * @param <T>
     * @return
     */
    public <T> T proxy(Class<T> proxyClass, URI absoluteURI)
    {
        return ((ResteasyWebTarget) client.target(absoluteURI)).register(newAuthFilter())
                .proxy(proxyClass);
    }



    /**
     * Closes the underlying client. After calling this method, this
     * <code>Keycloak</code> instance cannot be reused.
     */
    @Override
    public void close()
    {
        closed = true;
        client.close();
    }



    /**
     * @return true if the underlying client is closed.
     */
    public boolean isClosed()
    {
        return closed;
    }



    public ResteasyWebTarget getTarget()
    {
        return target;
    }
}
