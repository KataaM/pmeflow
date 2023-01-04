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
package fr.pmeflow.commons;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import fr.pmeflow.commons.model.enumeration.Langue;

/**
 * Cette classe externalise les strings des classes.
 * 
 * @author ludovic.terral
 */
public class PmeflowResources
{
    // ----------- Attribut -----------
    private static final String BUNDLE_NAME = "fr.pmeflow.commons.PmeflowResources";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    private static final ResourceBundle RESOURCE_BUNDLE_EN = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("en","US"));



    // ----------- Methode -----------
    private PmeflowResources()
    {
        //private constructor for static class
    }



    /**
     * Cette methode renvoie le texte associe au resource bundle.
     * 
     * @param key de texte.
     * 
     * @return message le texte.
     */
    public static String getString(final String key, final Langue langue)
    {
        String result = null;
        try
        {
            switch (langue)
            {
                case US_EN:
                    result = RESOURCE_BUNDLE_EN.getString(key);
                    break;
                default:
                    result = RESOURCE_BUNDLE.getString(key);
            }
        }
        catch (final MissingResourceException e)
        {
            result = '!' + key + '!';
        }
        return result;
    }



    /**
     * Cette methode renvoie le texte associe au resource bundle en utilisant des masques de
     * formatage.
     * 
     * @param key de texte.
     * @param parameters de format
     * 
     * @return message le texte.
     */
    public static String getString(final String key, final Object[] parameters, final Langue langue)
    {
        String baseMsg;
        try
        {
            switch (langue)
            {
                case US_EN:
                    baseMsg = RESOURCE_BUNDLE_EN.getString(key);
                    break;
                default:
                    baseMsg = RESOURCE_BUNDLE.getString(key);
            }
        }
        catch (final MissingResourceException e)
        {
            baseMsg = '!' + key + '!';
        }
        return MessageFormat.format(baseMsg, parameters);
    }
}
