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

/**
 * Cette classe regroupe toutes les constantes utilisées pour l'accès à l'API STRIPE.
 * 
 * @author Ludovic.TERRAL
 */
public class StripeConstant
{
    public static final String CUSTOMER_FIELD = "customer";
    public static final String PAYMENT_METHOD_TYPES_FIELD = "payment_method_types";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String AMOUNT_FIELD = "amount";
    public static final String CURRENCY_FIELD = "currency";
    public static final String CARD_FIELD = "card";
    public static final String CARD_CVC_FIELD = "cvc";
    public static final String CARD_EXP_YEAR_FIELD = "exp_year";
    public static final String CARD_EXP_MONTH_FIELD = "exp_month";
    public static final String CARD_NUMBER_FIELD = "number";
    public static final String SEPA_DEBIT_FIELD = "sepa_debit";
    public static final String TYPE_FIELD = "type";
    public static final String IBAN_FIELD = "iban";
    public static final String NAME_FIELD = "name";
    public static final String EMAIL_FIELD = "email";
    public static final String BILLING_DETAILS_FIELD = "billing_details";
    public static final String FAULT_CODE_FIELD = "fault_code";
    public static final String INTEGRATION_CHECK_FIELD = "integration_check";
    public static final String FAULT_DESCRIPTION_FIELD = "fault_description";
    public static final String CLIENT_SECRET_FIELD = "clientSecret";
    public static final String ID_FIELD = "id";
    public static final String SETUP_FUTURE_USAGE_FIELD = "setup_future_usage";
    
    
    private StripeConstant() {
        // private constructeur
    }
}
