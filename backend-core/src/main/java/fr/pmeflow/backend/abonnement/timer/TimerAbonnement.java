/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.abonnement.timer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.backend.abonnement.model.AbonnementEventBusMetadata;
import fr.pmeflow.backend.abonnement.model.StatutAbonnement;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import io.quarkus.scheduler.Scheduled;

/**
 * 
 * Ce timer assure :
 * - la transmission des factures liees aux abonnements, 
 * - l'information de fin d'engagement envoyee aux clients
 * - la finalisation des resiliations d'abonnement
 * 
 * @author ludovic.terral
 */
@RequestScoped              
public class TimerAbonnement implements Serializable
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202203022148L;
    private static final String DD_MM_YYYY_PATTERN = "dd-MM-yyyy";
    private static final String SERVICE_CODE = "TIMABO";


    @Inject @LocalRegistryConfig Channel channel;
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    @Inject @LocalRegistryConfig AbonnementService abonnementService;
    
    
    
    // ----------- Methode(s) -----------
    @Scheduled(every="300s") 
    void clearNonVerifie() throws BusinessException 
    {
        try
        {
            Date debutMin = new Date();
            debutMin = DateUtil.addMinutes(debutMin, -10);            
            StringBuilder query = new StringBuilder(RedisSearchQueryHelper.toTextField("etat", StatutAbonnement.A_VERIFIER.name()));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toLowerEqualThanField("dateDebut", debutMin.getTime()));            
            List<Abonnement> abonnements = abonnementService.getAbonnementsByExpression(Langue.FR_FR, query.toString());
            for (Abonnement abonnement : abonnements)
            {
                redisClient.remove(Abonnement.class, abonnement.getOid());
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, SERVICE_CODE, false);
        }
    }
    
    
    
    @Scheduled(every="3600s")
    void facturerMensualite() throws BusinessException 
    {
        try
        {
            abonnementService.facturerMensualite(Langue.FR_FR, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, SERVICE_CODE, false);
        }
    }

    
    
    @Scheduled(cron="0 0 6 * * ?")
    void sendFinEngagement() throws BusinessException 
    {
        try
        {
            String query = RedisSearchQueryHelper.toTextField("etat", StatutAbonnement.ACTIF.name());
            List<Abonnement> abonnements = abonnementService.getAbonnementsByExpression(Langue.FR_FR, query);
            for (Abonnement abonnement : abonnements)
            {
                Date dateRef = abonnement.getDateDebut();
                dateRef = DateUtil.addMonths(dateRef, abonnement.getDureeEngagement()-1);
                if (DateUtil.format(dateRef, DD_MM_YYYY_PATTERN).equals(DateUtil.format(new Date(), DD_MM_YYYY_PATTERN)))
                {
                    channel.basicPublish(AbonnementEventBusMetadata.EXCHANGE, 
                            AbonnementEventBusMetadata.EVENT_FIN_ENGAGEMENT, null, abonnement.toString().getBytes(StandardCharsets.UTF_8));
                }       
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, SERVICE_CODE, false);
        }
    }
    
        

    @Scheduled(cron="0 0 23 * * ?")    
    void cancelSubscriptions() throws BusinessException 
    {
        try
        {
            String query = RedisSearchQueryHelper.toTextField("etat", StatutAbonnement.EN_COURS_DE_RESILIATION.name());
            List<Abonnement> abonnements = abonnementService.getAbonnementsByExpression(Langue.FR_FR, query);
            for (Abonnement abonnement : abonnements)
            {
                abonnementService.resilierAbonnement(Langue.FR_FR, abonnement.getOid(), "RESILIATION A LA DEMANDE DU CLIENT");
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, SERVICE_CODE, false);
        }
    }
}