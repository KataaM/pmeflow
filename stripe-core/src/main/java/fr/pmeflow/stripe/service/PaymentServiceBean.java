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

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentConfirmParams.MandateData;
import com.stripe.param.PaymentIntentConfirmParams.MandateData.CustomerAcceptance.Type;
import com.stripe.param.PaymentIntentCreateParams;

import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.stripe.CustomerService;
import fr.pmeflow.stripe.PaymentService;
import fr.pmeflow.stripe.StripeConstant;

/**
 * Ce service assure la gestion des paiements dans Stripe
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"CREATE_PAYMENT", "PAY"})
public class PaymentServiceBean implements PaymentService
{
    // ----------- Attribut(s) ----------- 
    private static final long serialVersionUID = 202208121930L;
    private static final Log LOG = LogFactory.getLog(PaymentService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String CLIENT_PARAM = "client";
    private static final String PAYMENT_METHOD_ID_PARAM = "paymentMethodId";
    private static final String CUSTOMER_ID_PARAM = "customerId";
    private static final String DESCRIPTION_PARAM = "description";
    private static final String PRIVATE_KEY_PARAM = "privateKey";
    private static final String LANGUE_PARAM = "langue";

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
    @RolesAllowed({"CREATE_PAYMENT"})
    public Map<String, String> preparePayment(Langue langue, String privateKey, double amount, String description, String customerId, boolean savePaymentMethod)
        throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, LANGUE_PARAM }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, PRIVATE_KEY_PARAM }, langue));
        }
        if (StringUtil.isEmpty(description))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, DESCRIPTION_PARAM }, langue));
        }
        if (StringUtil.isEmpty(customerId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, CUSTOMER_ID_PARAM }, langue));
        }
        
        Map<String, String> result=new HashMap<>();
        try
        {
            Stripe.apiKey = privateKey;
            Map<String, Object> params = new HashMap<>();
            params.put(StripeConstant.AMOUNT_FIELD, (int)(amount*100d));
            params.put(StripeConstant.CURRENCY_FIELD, "eur");
            params.put(StripeConstant.DESCRIPTION_FIELD, description);
            params.put(StripeConstant.PAYMENT_METHOD_TYPES_FIELD, Arrays.asList(StripeConstant.CARD_FIELD, StripeConstant.SEPA_DEBIT_FIELD));
            params.put(StripeConstant.CUSTOMER_FIELD, customerId);
            if (savePaymentMethod) {
                params.put(StripeConstant.SETUP_FUTURE_USAGE_FIELD, "off_session");
            }
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            result.put(StripeConstant.ID_FIELD, paymentIntent.getId());
            result.put(StripeConstant.CLIENT_SECRET_FIELD, paymentIntent.getClientSecret());
        }
        catch(Exception e) 
        {
            result.put(StripeConstant.FAULT_DESCRIPTION_FIELD, e.getLocalizedMessage());
            ExceptionUtil.traiterException(e, PaymentService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    @RolesAllowed({"PAY"})
    public Map<String, String> pay(Langue langue, String privateKey, double amount, String description,
            String customerId, String paymentMethodId) throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, LANGUE_PARAM }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, PRIVATE_KEY_PARAM }, langue));
        }
        if (StringUtil.isEmpty(description))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, DESCRIPTION_PARAM }, langue));
        }
        if (StringUtil.isEmpty(customerId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, CUSTOMER_ID_PARAM }, langue));
        }
        if (StringUtil.isEmpty(paymentMethodId))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, PAYMENT_METHOD_ID_PARAM }, langue));
        }
        
        Map<String, String> result=new HashMap<>();
        try
        {
            Stripe.apiKey = privateKey;
            PaymentIntentCreateParams cParams = PaymentIntentCreateParams.builder()
                    .setAmount((long)(amount*100d))
                    .setCurrency("eur")
                    .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                    .setDescription(description)
                    .addPaymentMethodType(StripeConstant.CARD_FIELD)
                    .addPaymentMethodType(StripeConstant.SEPA_DEBIT_FIELD)
                    .setCustomer(customerId)
                    .putMetadata(StripeConstant.INTEGRATION_CHECK_FIELD, "sepa_debit_accept_a_payment")
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(cParams);
            MandateData mandat = MandateData.builder().setCustomerAcceptance(
                    MandateData.CustomerAcceptance.builder().setType(Type.OFFLINE).setAcceptedAt(Calendar.getInstance().getTimeInMillis()).build()).build();
            paymentIntent.confirm(
                    PaymentIntentConfirmParams.builder()
                        .setMandateData(mandat)
                        .setPaymentMethod(paymentMethodId).build());
            result.put(StripeConstant.ID_FIELD, paymentIntent.getId());
            result.put(StripeConstant.CLIENT_SECRET_FIELD, paymentIntent.getClientSecret());
        }
        catch(CardException ce) 
        {
            result.put(StripeConstant.FAULT_CODE_FIELD, ce.getCode());
            result.put(StripeConstant.FAULT_DESCRIPTION_FIELD, ce.getLocalizedMessage());
            ExceptionUtil.traiterException(ce, PaymentService.SERVICE_CODE, false);
        }
        catch(Exception e) 
        {
            result.put(StripeConstant.FAULT_DESCRIPTION_FIELD, e.getLocalizedMessage());
            ExceptionUtil.traiterException(e, PaymentService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    @RolesAllowed({"PAY"})
    public String createCardPaymentMethod(Langue langue, String privateKey, String number, String month, String year, String cvc) throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, LANGUE_PARAM }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, PRIVATE_KEY_PARAM }, langue));
        }
        if (StringUtil.isEmpty(number))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, StripeConstant.CARD_NUMBER_FIELD }, langue));
        }
        if (StringUtil.isEmpty(month))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, StripeConstant.CARD_EXP_MONTH_FIELD }, langue));
        }
        if (StringUtil.isEmpty(year))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, StripeConstant.CARD_EXP_YEAR_FIELD }, langue));
        }
        if (StringUtil.isEmpty(cvc))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, StripeConstant.CARD_CVC_FIELD }, langue));
        }
        
        String result="";
        try
        {
            Stripe.apiKey = privateKey;
            Map<String, Object> card = new HashMap<>();
            card.put(StripeConstant.CARD_NUMBER_FIELD, number);
            card.put(StripeConstant.CARD_EXP_MONTH_FIELD, month);
            card.put(StripeConstant.CARD_EXP_YEAR_FIELD, year);
            card.put(StripeConstant.CARD_CVC_FIELD, cvc);
            Map<String, Object> params = new HashMap<>();
            params.put(StripeConstant.TYPE_FIELD, StripeConstant.CARD_FIELD);
            params.put(StripeConstant.CARD_FIELD, card);
            PaymentMethod paymentMethod = PaymentMethod.create(params);
            result = paymentMethod.getId();
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PaymentService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    public String createSepaPaymentMethod(Langue langue, String privateKey, Client client, String iban) throws BusinessException
    {
        //Controler les parametres
        if (langue==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, LANGUE_PARAM }, langue));   
        }
        if (StringUtil.isEmpty(privateKey))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, PRIVATE_KEY_PARAM }, langue));
        }
        if (client==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, CLIENT_PARAM }, langue));
        }
        if (StringUtil.isEmpty(iban))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { CustomerService.SERVICE_CODE, StripeConstant.IBAN_FIELD }, langue));
        }
        
        String result="";
        try
        {
            Stripe.apiKey = privateKey;
            Map<String, Object> sepa = new HashMap<>();
            sepa.put(StripeConstant.IBAN_FIELD, iban);
            Map<String, Object> params = new HashMap<>();
            params.put(StripeConstant.TYPE_FIELD, StripeConstant.SEPA_DEBIT_FIELD);
            params.put(StripeConstant.SEPA_DEBIT_FIELD, sepa);
            Map<String, Object> billingDetails = new HashMap<>();
            billingDetails.put(StripeConstant.NAME_FIELD, client.getPrenom()+" "+client.getNom());
            billingDetails.put(StripeConstant.EMAIL_FIELD, client.getEmail());
            params.put(StripeConstant.BILLING_DETAILS_FIELD, billingDetails);
            PaymentMethod paymentMethod = PaymentMethod.create(params);
            result = paymentMethod.getId();
        }
        catch(Exception e) 
        {
            ExceptionUtil.traiterException(e, PaymentService.SERVICE_CODE, false);
        }
        return result;
    }
}