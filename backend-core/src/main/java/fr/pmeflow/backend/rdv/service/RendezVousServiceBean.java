/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.rdv.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.rabbitmq.client.Channel;

import fr.pmeflow.backend.dossier.DossierService;
import fr.pmeflow.backend.prestation.PrestationService;
import fr.pmeflow.backend.rdv.RendezVousService;
import fr.pmeflow.backend.rdv.model.Evenement;
import fr.pmeflow.backend.rdv.model.RendezVous;
import fr.pmeflow.backend.rdv.model.RendezVousEventBusMetadata;
import fr.pmeflow.commons.PmeflowConstant;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.model.enumeration.Etat;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.UtilisateurService;
import fr.pmeflow.iam.model.Utilisateur;
import fr.pmeflow.iam.model.enumeration.ProfilPmeflow;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.model.ConteneurEvenement;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.CollectionUtil;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.orm.redis.model.EQuery;
import fr.lixbox.orm.redis.query.RedisSearchQueryHelper;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Cette classe expose les fonctions de gestion des rendez-vous
 * 
 * @author ludovic.terral
 */

@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@RolesAllowed({"READ_RDV", "WRITE_RDV"})
public class RendezVousServiceBean implements RendezVousService
{
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202205262004L;
    private static final Log LOG = LogFactory.getLog(DossierService.class);
    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String FERMETURE_FIELD = "fermeture";
    private static final String OUVERTURE_FIELD = "ouverture";
    private static final String APRESMIDI_FIELD = "apresmidi";
    private static final String MATIN_FIELD = "matin";
    private static final String DD_MM_YYYY_FORMAT = "dd/MM/yyyy";
    private static final String DD_MM_YYYY_HH_MM_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String DATE_DEBUT_FIELD = "dateDebut";

    @Inject @LocalRegistryConfig Channel channel;    
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
   
    @Inject @Named("parametreService_doc-generator") ParametreService parametreService;
    @Inject @Named("utilisateurService_doc-generator") UtilisateurService utilisateurService;  
    
    @Inject JsonWebToken jwt;  
    
    
    
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
        try
        {
            redisClient.ping();
            state.getChecks().add(new Check(ServiceStatus.UP, PmeflowResources.getString("MSG.SERVICE.HEALTH", new Object[] {"redisClient"}, Langue.FR_FR)));   
        }
        catch(Exception e)
        {
            state.getChecks().add(new Check(ServiceStatus.DOWN, PmeflowResources.getString("MSG.SERVICE.NOT.HEALTH", new Object[]{"redisClient"}, Langue.FR_FR)));            
        }
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
    public List<RendezVous> getRendezVous(Langue langue) throws BusinessException
    {
        List<RendezVous> rdvs = new ArrayList<>();
        try
        {   
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            if (connected.getProfils().contains(ProfilPmeflow.ADMIN))
            {
                rdvs.addAll(redisClient.getTypedFromKeys(redisClient.getKeys(PmeflowConstant.PMEFLOW_CODE+":OBJECT:"+RendezVous.class.getName()+"*")));
            }
            else if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("client_id", jwt.getSubject()));
                rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            }
            else if (connected.getProfils().contains(ProfilPmeflow.JURISTE))
            {
                EQuery query = new EQuery(RedisSearchQueryHelper.toTextField("juriste_id", jwt.getSubject()));
                rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, false);
        }
        return rdvs;
    }

    
    
    @Override
    public List<RendezVous> getRendezVousForDossier(Langue langue, String dossierId) throws BusinessException
    {
        List<RendezVous> rdvs = new ArrayList<>();
        try
        {   
            String query = RedisSearchQueryHelper.toTextField("dossier_id", dossierId);
            rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            rdvs = CollectionUtil.sortCollectionAsc(DATE_DEBUT_FIELD, rdvs);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, false);
        }
        return rdvs;
    }



    @Override
    public List<RendezVous> getRendezVousByExpression(Langue langue, String query) throws BusinessException
    {
        List<RendezVous> rdvs = new ArrayList<>();
        try
        {   
            rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            rdvs = CollectionUtil.sortCollectionAsc(DATE_DEBUT_FIELD, rdvs);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, false);
        }
        return rdvs;
    }
    

    
    @Override
    public List<RendezVous> getRendezVousActifs(Langue langue) throws BusinessException
    {
        List<RendezVous> rdvs = new ArrayList<>();
        try
        {   
            Utilisateur connected = utilisateurService.getUtilisateurById(langue, jwt.getSubject());
            String query = RedisSearchQueryHelper.toTextField("etat", Etat.ACTIF.name());
            query+=" ";
            query+=RedisSearchQueryHelper.toGreaterEqualThanField(DATE_DEBUT_FIELD, Calendar.getInstance().getTimeInMillis());
            query+=" ";
            if (connected.getProfils().contains(ProfilPmeflow.ADMIN))
            {
                rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            }
            else if (connected.getProfils().contains(ProfilPmeflow.CLIENT))
            {
                query+=RedisSearchQueryHelper.toTextField("client_id", jwt.getSubject());
                rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            }
            else if (connected.getProfils().contains(ProfilPmeflow.JURISTE))
            {
                query+=RedisSearchQueryHelper.toTextField("juriste_id", jwt.getSubject());
                rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            }
            rdvs = CollectionUtil.sortCollectionAsc(DATE_DEBUT_FIELD, rdvs);
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, false);
        }
        return rdvs;
    }



    @Override
    public boolean annuler(Langue langue, String rdvId) throws BusinessException
    {
        boolean result = false;
        try
        {
            RendezVous rdv = getRendezVousById(langue, rdvId);
            rdv.setEtat(Etat.ANNULE);
            mergeRendezVous(langue, rdv);
            result = true;
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, true);
        }
        return result;
    }



    @Override
    @RolesAllowed({ "WRITE_PRESTATION" })
    public RendezVous mergeRendezVous(Langue langue, RendezVous rdv) throws BusinessException
    {
        try
        {
            Parametre param = parametreService.getParametreByCode("pmeflow-rdv", "rdv.duree");
            int duree = Integer.parseInt(param.getValue());
            if (rdv.getDateFin()==null)
            {
                rdv.setDateFin(rdv.getDateDebut());
                Calendar calFin = DateUtil.toCalendar(rdv.getDateDebut());
                calFin.add(Calendar.HOUR_OF_DAY, duree);
                rdv.setDateFin(calFin.getTime());
            }
            ConteneurEvenement events = rdv.validate();
            if (!events.getEvenementTypeErreur().isEmpty())
            {
                throw new BusinessException(PmeflowResources.getString("MSG.ERROR.EXCEPUTI_03",
                        new String[] { RendezVousService.SERVICE_CODE }, langue), events);
            }
            rdv = redisClient.merge(rdv);
            channel.basicPublish(RendezVousEventBusMetadata.EXCHANGE, RendezVousEventBusMetadata.EVENT_RDV_CONFIRM_TO_CLIENT, null, rdv.toString().getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(RendezVousEventBusMetadata.EXCHANGE, RendezVousEventBusMetadata.EVENT_RDV_INFORM_JURISTE, null, rdv.toString().getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, RendezVousService.SERVICE_CODE, true);
        }
        return rdv;
    }
    
    

    @Override
    public RendezVous getRendezVousById(Langue langue, String id) throws BusinessException
    {
        //Controler les parametres
        if (id==null)
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { PrestationService.SERVICE_CODE, "id" },langue));   
        }
        
        RendezVous result=null;
        try
        {
            result = redisClient.findById(RendezVous.class, id);
        }
        catch(BusinessException e)
        {
            LOG.error(e);
            throw e;
        }
        catch(Exception e)
        {
            LOG.error(e);
        }
        return result;
    }



    @PermitAll
    @Override
    public List<?>[] determinerRdvsPossiblesForJuriste(Langue langue, long start, String juristeId)
    {
        Calendar data = Calendar.getInstance();
        data.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        data.setTimeInMillis(start);
        List<?>[] result = new ArrayList[2];
        List<List<String[]>> jourRdvs = new ArrayList<>();
        List<List<String[]>> rdvRows = new ArrayList<>();
        List<String> rdvHeaders = new ArrayList<>();
        
        try 
        {
            Parametre param = parametreService.getParametreByCode("pmeflow-rdv", "rdv.duree");
            int duree = Integer.parseInt(param.getValue());
            Map<String, Map<String, Map<String, Integer>>> horaireOuverture = utilisateurService.getUtilisateurById(Langue.FR_FR, juristeId).getDisponibilite();
            
            for (int j=0; j<5; j++)
            {
                rdvHeaders.add(DateUtil.format(data, "dd/MM"));
                jourRdvs.add(determinerRdvPossible(data, duree, horaireOuverture));
                data.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            int nbreLignes = 0;
            for (int j=0; j<5; j++)
            {
                nbreLignes = jourRdvs.get(j).size()>nbreLignes?jourRdvs.get(j).size():nbreLignes;
            }
         
            for(int idxLigne=0; idxLigne<nbreLignes; idxLigne++)
            {
                List<String[]> lignes = new ArrayList<>();
                for (int j=0; j<5; j++)
                {
                    lignes.add(jourRdvs.get(j).size()>idxLigne?jourRdvs.get(j).get(idxLigne):new String[]{"",""});
                }
                rdvRows.add(lignes);
            }
            result[0] = rdvHeaders;
            result[1] = rdvRows;
        }
        catch (Exception e)
        {
            LOG.error(e);
        }
        return result;
    }
    
    
    
    @PermitAll
    public List<String[]> determinerRdvPossible(Calendar jour, int duree, Map<String, Map<String, Map<String, Integer>>> horaireOuverture)
    {
        long jourStart = DateUtil.parseCalendar((DateUtil.format(jour, DD_MM_YYYY_FORMAT) + " 00:00"), DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();
        long jourFin = DateUtil.parseCalendar((DateUtil.format(jour, DD_MM_YYYY_FORMAT) + " 23:59"), DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();
        
        //extraire les rendez-vous en cours
        List<Long[]> used = new ArrayList<>();
        try
        {   
            List<RendezVous> rdvs = new ArrayList<>();
            String query = RedisSearchQueryHelper.toBetweenField(DATE_DEBUT_FIELD, jourStart, jourFin);
            rdvs.addAll(redisClient.findByExpression(RendezVous.class, query));
            for (RendezVous rdv : rdvs)
            {
                used.add(new Long[] {rdv.getDateDebut().getTime(), rdv.getDateFin().getTime()});
            }
        }
        catch (Exception e)
        {
            LOG.info("Pas de rdv");
        }
        try
        {   
            List<Evenement> events = new ArrayList<>();
            String query = RedisSearchQueryHelper.toBetweenField("date_debut", jourStart, jourFin);
            events.addAll(redisClient.findByExpression(Evenement.class, query));
            for (Evenement event : events)
            {
                used.add(new Long[] {event.getDateDebut().getTime(), event.getDateFin().getTime()});
            }
        }
        catch (Exception e)
        {
            LOG.info("Pas d'event");
        }
        
        //extraire les events en cours
        
        
        //determiner les creneaux dispos en fonction duree creneau et type de jour        
        List<String[]> horaires = new ArrayList<>();
        try
        {
            int startAmHour = 0;
            int endAmHour = 0;
            int startPmHour = 0;
            int endPmHour = 0;
            Map<String, Integer> matin = null;
            Map<String, Integer> apresmidi = null;
            switch (jour.get(Calendar.DAY_OF_WEEK))
            {
                case Calendar.SUNDAY:
                    Map<String, Map<String, Integer>> horaire = horaireOuverture.get("dimanche");
                    if (horaire!=null)
                    {
                        matin = horaire.get(MATIN_FIELD);
                        apresmidi = horaire.get(APRESMIDI_FIELD);
                        if (matin != null && !matin.isEmpty())
                        {
                            startAmHour = matin.get(OUVERTURE_FIELD);
                            endAmHour = matin.get(FERMETURE_FIELD);
                        }
                        if (apresmidi!=null && !apresmidi.isEmpty())
                        {
                            startPmHour = apresmidi.get(OUVERTURE_FIELD);
                            endPmHour = apresmidi.get(FERMETURE_FIELD);
                        }
                    }
                    break;
                case Calendar.MONDAY:
                    horaire = horaireOuverture.get("lundi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                case Calendar.TUESDAY:
                    horaire = horaireOuverture.get("mardi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                case Calendar.WEDNESDAY:
                    horaire = horaireOuverture.get("mercredi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                case Calendar.THURSDAY:
                    horaire = horaireOuverture.get("jeudi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                case Calendar.FRIDAY:
                    horaire = horaireOuverture.get("vendredi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                case Calendar.SATURDAY:
                    horaire = horaireOuverture.get("samedi");
                    matin = horaire.get(MATIN_FIELD);
                    apresmidi = horaire.get(APRESMIDI_FIELD);
                    if (matin != null && !matin.isEmpty())
                    {
                        startAmHour = matin.get(OUVERTURE_FIELD);
                        endAmHour = matin.get(FERMETURE_FIELD);
                    }
                    if (apresmidi!=null && !apresmidi.isEmpty())
                    {
                        startPmHour = apresmidi.get(OUVERTURE_FIELD);
                        endPmHour = apresmidi.get(FERMETURE_FIELD);
                    }
                    break;
                default:
                    startAmHour = 0;
                    endAmHour = 0;
                    startPmHour = 0;
                    endPmHour = 0;
                    break;
            }
            
            for (int h=startAmHour; h<=(endAmHour-duree); h+=duree)
            {
                String hour = (h<10?"0"+h:h)+":00";
                int hEnd = h + 1;
                String hourEnd = (hEnd<10?"0"+hEnd:hEnd)+":00";
                Long horaireStart = DateUtil.parseCalendar(DateUtil.format(jour, DD_MM_YYYY_FORMAT)+" "+hour, DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();                
                Long horaireEnd = DateUtil.parseCalendar(DateUtil.format(jour, DD_MM_YYYY_FORMAT)+" "+hourEnd, DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();
                if (!isUsed(used, horaireStart, horaireEnd))
                {
                    horaires.add(new String[]{hour, horaireStart+""});
                }
            }
            for (int h=startPmHour; h<=(endPmHour-duree); h+=duree)
            {
                String hour = h+":00";
                int hEnd = h + 1;
                String hourEnd = hEnd+":00";
                Long horaireStart = DateUtil.parseCalendar(DateUtil.format(jour, DD_MM_YYYY_FORMAT)+" "+hour, DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();                
                Long horaireEnd = DateUtil.parseCalendar(DateUtil.format(jour, DD_MM_YYYY_FORMAT)+" "+hourEnd, DD_MM_YYYY_HH_MM_FORMAT).getTimeInMillis();
                if (!isUsed(used, horaireStart, horaireEnd))
                {
                    horaires.add(new String[]{hour, horaireStart+""});
                }
            }
        }
        catch (Exception e)
        {
            LOG.error(e);
        }
        return horaires;
    }
    
    
    
    private boolean isUsed(List<Long[]> used, long horaireStart, long horaireEnd) 
    {
        boolean result = false;        
        for (Long[] creneau : used)
        {
            if ((horaireStart>=creneau[0] && horaireStart<creneau[1])||
                (horaireEnd>creneau[0] && horaireEnd<creneau[1]))
            {
                result=true;
            }
        }        
        return result;
    }
}