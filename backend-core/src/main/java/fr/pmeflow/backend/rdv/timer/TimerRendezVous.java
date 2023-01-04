/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv.timer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.abonnement.model.StatutAbonnement;
import fr.pmeflow.backend.rdv.RendezVousService;
import fr.pmeflow.backend.rdv.model.RendezVous;
import fr.pmeflow.backend.rdv.model.RendezVousEventBusMetadata;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import io.quarkus.scheduler.Scheduled;

/**
 * 
 * Ce timer assure :
 * - la transmission du rappel de rendez-vous
 * 
 * @author ludovic.terral
 */
@RequestScoped              
public class TimerRendezVous implements Serializable
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202203022148L;
    private static final String SERVICE_CODE = "TIMRDV";

    @Inject @LocalRegistryConfig Channel channel;    
    @Inject @Named("RendezVousService_doc-generator") RendezVousService rendezVousService;
    
    
    
    // ----------- Methode(s) -----------
    @Scheduled(cron="0 30 11 * * ?")
    void sendRappelRdvH24() throws BusinessException 
    {
        try
        {
            Calendar start =  DateUtil.parseCalendar(DateUtil.format(Calendar.getInstance(),"dd/MM/yyyy")+" 00:00:00","dd/MM/yyyy HH:mm:ss");
            start = DateUtil.addDelayToCalendar(start, (24*60*60*1000));
            Calendar end =  DateUtil.parseCalendar(DateUtil.format(Calendar.getInstance(),"dd/MM/yyyy")+" 23:59:59","dd/MM/yyyy HH:mm:ss");
            end = DateUtil.addDelayToCalendar(end, (24*60*60*1000));
            StringBuilder query = new StringBuilder(RedisSearchQueryHelper.toTextField("etat", StatutAbonnement.ACTIF.name()));
            query.append(" ");
            query.append(RedisSearchQueryHelper.toGreaterEqualThanField("dateDebut", start.getTimeInMillis()));
            query.append(RedisSearchQueryHelper.toLowerEqualThanField("dateDebut", end.getTimeInMillis()));
            List<RendezVous> rendezVous = rendezVousService.getRendezVousByExpression(Langue.FR_FR, query.toString());
            for (RendezVous rdv : rendezVous)
            {
                channel.basicPublish(RendezVousEventBusMetadata.EXCHANGE, RendezVousEventBusMetadata.EVENT_RDV_CONFIRM_TO_CLIENT, null, rdv.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, SERVICE_CODE, false);
        }
    }
}