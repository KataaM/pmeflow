/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.pmeflow.backend.abonnement.model.Abonnement;
import fr.pmeflow.backend.abonnement.model.AbonnementEventBusMetadata;
import fr.pmeflow.backend.dossier.model.Dossier;
import fr.pmeflow.backend.dossier.model.DossierEventBusMetadata;
import fr.pmeflow.backend.facturation.model.FacturationEventBusMetadata;
import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.io.DocumentService;
import fr.pmeflow.backend.io.EmailService;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.backend.prestation.model.PrestationEventBusMetadata;
import fr.pmeflow.backend.rdv.model.RendezVous;
import fr.pmeflow.backend.rdv.model.RendezVousEventBusMetadata;
import fr.pmeflow.commons.model.BusEvent;
import fr.pmeflow.commons.model.EventBusMetadata;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.pmeflow.referentiel.parametre.model.Parametre;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.helper.StringTokenizer;
import fr.lixbox.common.stream.util.FileUtil;
import fr.lixbox.common.stream.util.MimeTypesUtil;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.json.JsonUtil;
import fr.lixbox.service.registry.model.health.ServiceState;
import fr.lixbox.service.registry.model.health.ServiceStatus;

/**
 * Ce service implémente le référentiel des documents.
 * 
 * @author ludovic.terral
 */
@RequestScoped
@Produces({"application/json"})
@Consumes({"application/json"})
@PermitAll
public class EmailServiceBean implements EmailService
{
    private static final String RDV_DATE_FIELD = "#rdv.date#";
    private static final String DD_MM_YYY_HH_MM_FORMAT = "dd/MM/yyy HH:mm";
    private static final String DOSSIER_LIBELLE_FIELD = "#dossier.libelle#";
    private static final String DOSSIER_ID_FIELD = "#dossier.id#";
    private static final String PRESTATION_LIBELLE_FIELD = "#prestation.libelle#";
    private static final String CLIENT_NOM_FIELD = "#client.nom#";
    private static final String CLIENT_PRENOM_FIELD = "#client.prenom#";
    private static final String APPLICATION_URI_FIELD = "#application.uri.base#";
    private static final String ARTMARKET_MAIL_CODE = "pmeflow-mail";
    // ----------- Attribut(s) -----------   
    private static final long serialVersionUID = 202203022234L;
    private static final Log LOG = LogFactory.getLog(EmailService.class);
        
    @Inject @Named("parametreService_doc-generator") ParametreService parametreService;
    @Inject @Named("documentService_doc-generator") DocumentService documentService;
    
    @ConfigProperty(name = "smtp.host") String smtpHost;
    @ConfigProperty(name = "smtp.port") String smtpPort;
    @ConfigProperty(name = "smtp.login") String smtpLogin;
    @ConfigProperty(name = "smtp.pwd") String smtpPwd;
    @ConfigProperty(name = "smtp.mail.contact") String smtpMailContact;
    @ConfigProperty(name = "smtp.mail.noreply") String smtpMailNoreply;
    @ConfigProperty(name = "application.uri.base") String applicationBaseUri;

    
    
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
    public boolean routeBusEvent(Langue langue, BusEvent event) throws BusinessException
    {
        boolean result = true;
        try 
        {
            switch(event.getName())
            {
                case AbonnementEventBusMetadata.EVENT_FIN_ENGAGEMENT:                    
                    sendEmailAbonnementFinEngagement(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Abonnement>(){}));
                    break;
                case DossierEventBusMetadata.EVENT_DOSSIER_CREATED:
                    
                    sendEmailDossierCreated(langue, 
                            JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Dossier>(){}), 
                            Boolean.parseBoolean(event.getProperties().get(EventBusMetadata.MAJ_BY_CLIENT)), 
                            new StringTokenizer(event.getProperties().get(EventBusMetadata.INFORM_MAIL_ADDRESS),";").getTokens().toArray(new String[0]));
                    break;
                case DossierEventBusMetadata.EVENT_DOSSIER_UPDATED:
                    sendEmailDossierUpdated(langue, 
                            JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Dossier>(){}),
                            Boolean.parseBoolean(event.getProperties().get(EventBusMetadata.MAJ_BY_CLIENT)), 
                            new StringTokenizer(event.getProperties().get(EventBusMetadata.INFORM_MAIL_ADDRESS),";").getTokens().toArray(new String[0]));
                    break;
                case DossierEventBusMetadata.EVENT_DOSSIER_TO_BE_COMPLETED:
                    sendEmailDossierToBeCompleted(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Dossier>(){}));
                    break;                    
                case FacturationEventBusMetadata.EVENT_FACTURE_READY:
                    sendEmailFactureReady(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Facture>(){}));
                    break;      
                case PrestationEventBusMetadata.EVENT_PRESTATION_TO_PAY:
                    sendEmailPrestationARegler(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<Prestation>(){}));
                    break;      
                case RendezVousEventBusMetadata.EVENT_RDV_CONFIRM_TO_CLIENT:
                    sendEmailRendezVousConfirmClient(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<RendezVous>(){}));
                    break;   
                case RendezVousEventBusMetadata.EVENT_RDV_INFORM_JURISTE:
                    sendEmailRendezVousInformJuriste(langue, JsonUtil.transformJsonToObject(event.getBody(), new TypeReference<RendezVous>(){}));
                    break;  
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            LOG.error(e);
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, true);            
        }
        return result;
    }



    @Override
    public boolean sendEmailPrestationARegler(Langue langue, Prestation prestation) throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.prestation.aregler.fr.title");
            String subject = pSubject.getValue();
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.prestation.aregler.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, prestation.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, prestation.getClient().getNom());    
            body = body.replaceAll(PRESTATION_LIBELLE_FIELD, prestation.getLibelle());            
            sendEmail(prestation.getClient().getEmail(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    public boolean sendEmailFactureReady(Langue langue, Facture facture) throws BusinessException
    {
        boolean result = false;
        try 
        {
            byte[] document = documentService.generateFacture(langue, facture);
            
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.facture.fr.title");
            String subject = pSubject.getValue();
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.facture.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, facture.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, facture.getClient().getNom());
            body = body.replaceAll("#facture.numero#", facture.getNumero());
            body = body.replaceAll("#facture.date#", DateUtil.format(facture.getDate(),"dd/MM/yyyy"));
            body = body.replaceAll("#facture.totalTTC#", facture.getTotalTTC()+"");            
            body = body.replaceAll(PRESTATION_LIBELLE_FIELD, facture.getPrestation().getLibelle());            
            sendEmail(facture.getClient().getEmail(), "", subject, body, document);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    public boolean sendEmailDossierCreated(Langue langue, Dossier dossier, boolean majClient, String[] emails) throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.created.fr.title");
            String subject = pSubject.getValue();
            subject = subject.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            subject = subject.replaceAll(DOSSIER_LIBELLE_FIELD, dossier.getLibelle());
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.created.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            if (!majClient)
            {
                body = body.replaceAll(CLIENT_PRENOM_FIELD, dossier.getClient().getPrenom());
                body = body.replaceAll(CLIENT_NOM_FIELD, dossier.getClient().getNom());
            }
            else 
            {
                body = body.replaceAll(CLIENT_PRENOM_FIELD, dossier.getJuriste().getPrenom());
                body = body.replaceAll(CLIENT_NOM_FIELD, dossier.getJuriste().getNom());
            }
            body = body.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            body = body.replaceAll(DOSSIER_LIBELLE_FIELD, dossier.getLibelle());
            StringBuilder sMail = new StringBuilder();
            for (String email : emails)
            {
                sMail.append(email+" ");   
            }
            sendEmail(sMail.toString(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    public boolean sendEmailDossierToBeCompleted(Langue langue, Dossier dossier)
            throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.tocomplete.fr.title");
            String subject = pSubject.getValue();
            subject = subject.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.tocomplete.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, dossier.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, dossier.getClient().getNom());
            body = body.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            sendEmail(dossier.getClient().getEmail(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }



    @Override
    public boolean sendEmailDossierUpdated(Langue langue, Dossier dossier, boolean majClient, String[] emails) throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.updated.fr.title");
            String subject = pSubject.getValue();
            subject = subject.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            subject = subject.replaceAll(DOSSIER_LIBELLE_FIELD, dossier.getLibelle());
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.dossier.updated.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            if (!majClient)
            {
                body = body.replaceAll(CLIENT_PRENOM_FIELD, dossier.getClient().getPrenom());
                body = body.replaceAll(CLIENT_NOM_FIELD, dossier.getClient().getNom());
            }
            else 
            {
                body = body.replaceAll(CLIENT_PRENOM_FIELD, dossier.getJuriste().getPrenom());
                body = body.replaceAll(CLIENT_NOM_FIELD, dossier.getJuriste().getNom());
            }
            body = body.replaceAll(DOSSIER_ID_FIELD, dossier.getOid());
            body = body.replaceAll(DOSSIER_LIBELLE_FIELD, dossier.getLibelle());
            StringBuilder sMail = new StringBuilder();
            for (String email : emails)
            {
                sMail.append(email+" ");   
            }
            sendEmail(sMail.toString(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }
    


    @Override
    public boolean sendEmailAbonnementFinEngagement(Langue langue, Abonnement abonnement)
            throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.abo.fin.engagement.fr.title");
            String subject = pSubject.getValue();
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.abo.fin.engagement.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, abonnement.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, abonnement.getClient().getNom());
            body = body.replaceAll("#abonnement.libelle#", abonnement.getLibelle());            
            sendEmail(abonnement.getClient().getEmail(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }



    
    @Override
    public boolean sendEmailRendezVousConfirmClient(Langue langue, RendezVous rdv) throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.rdv.confirm-client.fr.title");
            String subject = pSubject.getValue();
            subject = subject.replaceAll(RDV_DATE_FIELD, DateUtil.format(rdv.getDateDebut(),DD_MM_YYY_HH_MM_FORMAT));
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.rdv.confirm-client.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, rdv.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, rdv.getClient().getNom());    
            body = body.replaceAll(RDV_DATE_FIELD, DateUtil.format(rdv.getDateDebut(),DD_MM_YYY_HH_MM_FORMAT));
            body = body.replaceAll("#juriste.prenom#", rdv.getJuriste().getPrenom());
            body = body.replaceAll("#juriste.nom#", rdv.getJuriste().getNom());  
            body = body.replaceAll("#juriste.specialite#", rdv.getJuriste().getSpecialite());  
            sendEmail(rdv.getClient().getEmail(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }
    

    
    @Override
    public boolean sendEmailRendezVousInformJuriste(Langue langue, RendezVous rdv) throws BusinessException
    {
        boolean result = false;
        try 
        {
            //envoie du mail
            Parametre pSubject = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.rdv.inform-juriste.fr.title");
            String subject = pSubject.getValue();
            subject = subject.replaceAll(RDV_DATE_FIELD, DateUtil.format(rdv.getDateDebut(),DD_MM_YYY_HH_MM_FORMAT));
            Parametre pBody = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.rdv.inform-juriste.fr.body");
            String body = pBody.getValue();
            body = body.replaceAll(APPLICATION_URI_FIELD, applicationBaseUri);
            body = body.replaceAll(CLIENT_PRENOM_FIELD, rdv.getClient().getPrenom());
            body = body.replaceAll(CLIENT_NOM_FIELD, rdv.getClient().getNom());    
            body = body.replaceAll(RDV_DATE_FIELD, DateUtil.format(rdv.getDateDebut(),DD_MM_YYY_HH_MM_FORMAT));
            body = body.replaceAll("#rdv.motif#", rdv.getMotif());
            body = body.replaceAll("#juriste.prenom#", rdv.getJuriste().getPrenom());
            body = body.replaceAll("#juriste.nom#", rdv.getJuriste().getNom());  
            body = body.replaceAll("#juriste.specialite#", rdv.getJuriste().getSpecialite());  
            sendEmail(rdv.getJuriste().getEmail(), "", subject, body);
            result = true;
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, false);
        }
        return result;
    }


    
    private void sendEmail(String toEmail, String bccEmail, String subject, String body, Object... objects) 
        throws BusinessException
    {
        try
        {
            Parametre pBcc = parametreService.getParametreByCode(ARTMARKET_MAIL_CODE,"mail.bcc");
            
            Properties props = new Properties();
            props.put("mail.transport.protocol", "SMTP");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "false");
            Authenticator auth = new Authenticator() 
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() 
                {
                    return new PasswordAuthentication(smtpLogin, smtpPwd);
                }
            };
            Session session = Session.getInstance(props, auth);
            
            
            // preparation 
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(smtpMailContact));
            msg.setReplyTo(InternetAddress.parse(smtpMailNoreply, false));
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            if (StringUtil.isEmpty(bccEmail))
            {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccEmail, false));
            }
            msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(pBcc.getValue(), false));
            msg.setSubject(subject, "UTF-8");
            Multipart multipart = new MimeMultipart();
                        
            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
                        
            List<String> paths = new ArrayList<>();
            if (objects != null && objects.length>0)
            {
                int i=0;
                String filename;
                for (Object object : objects)
                {
                    if (object instanceof byte[])
                    {
                        String mimeType = "application/pdf";
                        if (!"application/unknown".equals(FileUtil.getMIMEType((byte[]) object)))
                        {
                            mimeType = FileUtil.getMIMEType((byte[]) object);
                        }
                        filename = "join_"+i+"."+MimeTypesUtil.lookupExt(mimeType);
                        messageBodyPart = new MimeBodyPart(); 
                        ByteArrayDataSource bds = new ByteArrayDataSource(new ByteArrayInputStream((byte[]) object), mimeType); 
                        messageBodyPart.setDataHandler(new DataHandler(bds)); 
                        messageBodyPart.setFileName(filename);
                    }
                    else if (object instanceof File)
                    {
                        messageBodyPart = new MimeBodyPart(new FileInputStream((File)object)); 
                        filename = ((File)object).getName();
                        FileDataSource source = new FileDataSource(filename);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename);
                    }
                    multipart.addBodyPart(messageBodyPart);
                }
            }            
            msg.setContent(multipart);
            Transport.send(msg);
            for (String path : paths)
            {
                Files.delete(Paths.get(path));
            }               
            LOG.info("Msg \""+subject+"\" sended to "+toEmail);
        }
        catch (Exception e) 
        {
          LOG.error(e);
          ExceptionUtil.traiterException(e, EmailService.SERVICE_CODE, true);
        }
    }
}