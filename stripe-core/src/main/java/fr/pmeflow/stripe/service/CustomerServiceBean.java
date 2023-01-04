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
package fr.pmeflow.stripe.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.CustomerUpdateParams;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.stripe.CustomerService;

/**
 * Ce service manage les clients via stripe.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@RolesAllowed({"READ_CLIENT", "WRITE_CLIENT"})
public class CustomerServiceBean implements CustomerService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202209081416L;
    private static final Log LOG = LogFactory.getLog(CustomerService.class);

    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
        
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
        LOG.debug("Check Health started");
        ServiceState state = new ServiceState();
        state.setStatus(ServiceStatus.UP);
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
    public Client getClientById(Langue langue, String privateKey, String customerId) throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "langue" }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "privateKey" }, langue));
        }
        if (StringUtil.isEmpty(customerId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "customerId" }, langue));
        }
     
        Client client=null;
        try
        {
            Stripe.apiKey = privateKey;
            Customer customer = Customer.retrieve(customerId);
            client = new Utilisateur();
            client.setCustomerStripeId(customerId);
            client.setEmail(customer.getEmail());
            client.setTelephone(customer.getPhone());
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, CustomerService.SERVICE_CODE, false);
        }
        return client; 
    }
    
    

    @Override
    @RolesAllowed("WRITE_CLIENT")
    public Client mergeClient(Langue langue, String privateKey, Client client) throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "langue" }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "privateKey" }, langue));
        }
        if (client==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, "client" }, langue));   
        }
        
        ConteneurEvenement events = client.validate();
        if (!events.getEvenementTypeErreur().isEmpty())
        {
            throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03", new String[] {CustomerService.SERVICE_CODE}, langue), events);
        }
                
        try
        {
            Stripe.apiKey = privateKey;
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("name",client.getNom()+" "+client.getPrenom());
            customerParams.put("phone",client.getTelephone());
            customerParams.put("email", client.getEmail());
            
            CustomerSearchParams params = CustomerSearchParams.builder()
                                              .setQuery("phone:'"+client.getTelephone()+"' OR email:'"+client.getEmail()+"'")
                                              .build();
            CustomerSearchResult result = Customer.search(params);
            LOG.info("phone:'"+client.getTelephone()+"' OR email:'"+client.getEmail()+"' result: "+result.getData().size());
            if (!result.getData().isEmpty()) {
                LOG.info("setCustomerStripeId:'"+result.getData().get(0).getId()+"'");
                client.setCustomerStripeId(result.getData().get(0).getId());
            }
            
            if (StringUtil.isEmpty(client.getCustomerStripeId()))
            { 
                Customer customer = Customer.create(customerParams);
                client.setCustomerStripeId(customer.getId());
            }
            else 
            {
                Customer customer = Customer.retrieve(client.getCustomerStripeId());
                customer.update(CustomerUpdateParams.builder().putAllExtraParam(customerParams).build());
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, CustomerService.SERVICE_CODE, false);
        }
        return client; 
    }
}