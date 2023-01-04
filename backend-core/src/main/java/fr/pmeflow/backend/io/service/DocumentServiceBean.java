/*******************************************************************************
 *    
 *                           APPLICATION Pmeflow
 *                          =======================
 *   
 *   @AUTHOR Pmeflow.legal
 *
 ******************************************************************************/
package fr.pmeflow.backend.io.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import fr.pmeflow.backend.abonnement.AbonnementService;
import fr.pmeflow.backend.facturation.model.Facture;
import fr.pmeflow.backend.io.DocumentService;
import fr.pmeflow.backend.io.model.StorageMultiPartBody;
import fr.pmeflow.backend.prestation.model.Prestation;
import fr.pmeflow.commons.PmeflowResources;
import fr.pmeflow.commons.authentification.BearerAuthenticator;
import fr.pmeflow.commons.model.enumeration.Langue;
import fr.pmeflow.iam.model.Client;
import fr.pmeflow.referentiel.parametre.ParametreService;
import fr.lixbox.common.exceptions.BusinessException;
import fr.lixbox.common.exceptions.ProcessusException;
import fr.lixbox.common.guid.GuidGenerator;
import fr.lixbox.common.stream.util.FileUtil;
import fr.lixbox.common.stream.util.MimeTypesUtil;
import fr.lixbox.common.util.CodeVersionUtil;
import fr.lixbox.common.util.DateUtil;
import fr.lixbox.common.util.ExceptionUtil;
import fr.lixbox.common.util.StringUtil;
import fr.lixbox.io.document.util.ReportUtil;
import fr.lixbox.io.document.xdocreport.template.formatter.FieldsMetadata;
import fr.lixbox.orm.redis.client.ExtendRedisClient;
import fr.lixbox.service.registry.cdi.LocalRegistryConfig;
import fr.lixbox.service.registry.model.health.Check;
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
@RolesAllowed({"READ_DOC", "WRITE_DOC", "GENERATE_DOC"})
public class DocumentServiceBean implements DocumentService
{
    // ----------- Attribut(s) -----------   
    private static final Log LOG = LogFactory.getLog(DocumentService.class);
    
    private static final long serialVersionUID = 202201261138L;
    private static final String PATH_SEPARATOR = "/";
    private static final String STORAGE_KEY_FIELD = "pmeflow:storage:key:";    
    private static final String MSG_ERROR_EXCEPUTI_02 = "MSG.ERROR.EXCEPUTI_02";
    private static final String KEY_SPEC = "AES";
    private static final String CIPHER_SPEC = "AES/GCM/NoPadding";
    private static final String DATA_EXT = ".data";
    private static final String CODE_APP = "pmeflow";

    @ConfigProperty(name = "quarkus.http.port") String port;
    
    @Inject @LocalRegistryConfig ExtendRedisClient redisClient;
    @Inject @LocalRegistryConfig AbonnementService abonnementService;
    @Inject @Named("parametreService_doc-generator") ParametreService parametreService;
        
    
    
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
    @PermitAll()
    public String getIdForDocument(@PathParam("langue") Langue langue) throws BusinessException
    {
        return GuidGenerator.getGUID(this);
    }



    @Override
    @PermitAll
    public Response getDocumentById(Langue langue, String id, String accessToken) throws BusinessException
    {
        Response response = null;
        
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { DocumentService.SERVICE_CODE, "id" },langue));   
        }
        if (StringUtil.isEmpty(accessToken))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { DocumentService.SERVICE_CODE, "accessToken" },langue));   
        }
        
        try 
        {
            String serviceUri = "http://localhost:"+port+"/backend/api/";
            ResteasyClientBuilder builder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
            ResteasyClient client = builder.build();
            client.register(new BearerAuthenticator(accessToken));
            ResteasyWebTarget target = client.target(UriBuilder.fromPath(serviceUri));
            DocumentService documentService = target.proxy(DocumentService.class);
            
            
            String secretKey = redisClient.get(STORAGE_KEY_FIELD+id);
            byte[] fileContent = documentService.getDocumentStorageById(langue, id, secretKey);
            String mimetype = FileUtil.getMIMEType(fileContent); 
            ResponseBuilder responseBuilder = Response.ok(fileContent);
            responseBuilder.header("content-type", mimetype);
            responseBuilder.header("Content-Disposition", "attachment; filename=\""+id+"."+MimeTypesUtil.getDefaultExt(mimetype)+"\"");
            response = responseBuilder.build();
        }
        catch(Exception e)
        {
            ExceptionUtil.traiterException(e, DocumentService.SERVICE_CODE, false);
            response = Response.status(Status.FORBIDDEN).build();
        }
        return response;
    }
    
    

    @Override
    @RolesAllowed("READ_DOC")
    public byte[] getDocumentStorageById(Langue langue, String id, String secretKey) throws BusinessException
    {
        //Controler les parametres
        if (StringUtil.isEmpty(id))
        {
            throw new BusinessException(PmeflowResources.getString(
                    MSG_ERROR_EXCEPUTI_02, 
                    new String[] { DocumentService.SERVICE_CODE, "id" },langue));   
        }
        
        byte[] result = new byte[0];
        try
        {
            String dataPath = parametreService.getParametreByCode(CODE_APP, "DOC_PATH").getValue();
            byte[] encrypted = FileUtil.readFileToByteArray(new File(dataPath+PATH_SEPARATOR+id+DATA_EXT));
            Cipher aesCipher = Cipher.getInstance(CIPHER_SPEC);
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8)), KEY_SPEC);
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            result = aesCipher.doFinal(encrypted);
        }
        catch(Exception e)
        {
            LOG.error(e);
        }
        return result;                
    }
    
    

    @Override
    @RolesAllowed("WRITE_DOC")
    public boolean mergeDocumentStorage(Langue langue, String id, StorageMultiPartBody form) throws BusinessException
    {
        boolean result=false;
        try
        {
            KeyGenerator keygenerator = KeyGenerator.getInstance(KEY_SPEC);
            keygenerator.init(256);
            SecretKey aesKey = keygenerator.generateKey();
            Cipher aesCipher;
            aesCipher = Cipher.getInstance(CIPHER_SPEC);
            
            String dataPath = parametreService.getParametreByCode(CODE_APP, "DOC_PATH").getValue();
            FileUtil.copyFile(form.file, new File(dataPath+PATH_SEPARATOR+id+DATA_EXT));            
            
            byte[] content = FileUtil.readFileToByteArray(new File(dataPath+PATH_SEPARATOR+id+DATA_EXT));
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] contentEncrypted = aesCipher.doFinal(content);
            FileUtil.writeByteArrayToFile(new File(dataPath+PATH_SEPARATOR+id+DATA_EXT), contentEncrypted);
            redisClient.put(STORAGE_KEY_FIELD+id, new String(Base64.getEncoder().encode(aesKey.getEncoded()), StandardCharsets.UTF_8));
            result=true;
        }
        catch(Exception e)
        {
            LOG.error(e);
        }
        return result;
    }



    @Override
    @RolesAllowed("GENERATE_DOC")
    public byte[] generateFacture(Langue langue, Facture facture) throws BusinessException
    {
        String facturePath = parametreService.getParametreByCode(CODE_APP, "FACT_PATH").getValue();
        byte[] result = new byte[0];
        try (InputStream in = new FileInputStream(facturePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();)
        {
            // generation du document
            ReportUtil reportUtil = new ReportUtil(in, facture.getNumero());
            Map<String, Object> context = new HashMap<>();
            context.put("transaction_date", DateUtil.format(facture.getDate(),"dd-MM-YYYY"));
            context.put("prestation", facture.getPrestation());
            context.put("client", facture.getClient());
            context.put("facture", facture);
            FieldsMetadata metadatas = reportUtil.getFieldsMetadata();
            metadatas.load("prestation", Prestation.class);
            metadatas.load("client", Client.class);
            metadatas.load("facture", Facture.class);
            reportUtil.generateReportDocxToPdf(out, context, metadatas);
            result = out.toByteArray();
        }
        catch (Exception e)
        {
            ExceptionUtil.traiterException(e, DocumentService.SERVICE_CODE, true);
        }
        return result;
    }
}