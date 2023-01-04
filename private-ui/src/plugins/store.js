import Vue from "vue";
import Vuex from "vuex";
import KeycloakService from "@/api/KeycloakService.js";
Vue.use(Vuex);

const DEFAULT_CONFIG = {
  "services":{
    "siteUri": process.env.VUE_APP_PUBLIC_URI,
    "abonnementServiceUri": process.env.VUE_APP_ABONNEMENT_SERVICE_URI,
    "clientServiceUri": process.env.VUE_APP_CLIENT_SERVICE_URI,
    "documentServiceUri": process.env.VUE_APP_DOCUMENT_SERVICE_URI,
    "dossierServiceUri": process.env.VUE_APP_DOSSIER_SERVICE_URI,
    "evenementServiceUri": process.env.VUE_APP_EVENEMENT_SERVICE_URI,
    "facturationServiceUri": process.env.VUE_APP_FACTURATION_SERVICE_URI,
    "keycloakServiceUri": process.env.VUE_APP_OIDC_SERVER,
    "parametreServiceUri": process.env.VUE_APP_PARAMETRE_SERVICE_URI,
    "prestationServiceUri": process.env.VUE_APP_PRESTATION_SERVICE_URI,
    "profilServiceUri": process.env.VUE_APP_PROFIL_SERVICE_URI,
    "refAbonnementServiceUri": process.env.VUE_APP_REF_ABONNEMENT_SERVICE_URI,
    "refFormulaireServiceUri": process.env.VUE_APP_REF_FORMULAIRE_SERVICE_URI,
    "refPrestationServiceUri": process.env.VUE_APP_REF_PRESTATION_SERVICE_URI,
    "rendezVousServiceUri": process.env.VUE_APP_RENDEZ_VOUS_SERVICE_URI,
    "utilisateurServiceUri": process.env.VUE_APP_UTILISATEUR_SERVICE_URI
  },
  "parametres":{
    "stripePublicKey":"pk_test_51LVYgTBgYaLiNQYa9E60WcERWEUXM0ycpmSS0UBb86d5kY4yccqPYKTKJ1AdamHPxqsGLuNQVGC0vMbKngmLgoVR00MqXUoQzL",
    "tva":"20",
    "dureeRdv":60,
    "eventTypes":[
      {type:"indefini",color:"blue"},
      {type:"personnel",color:"green"},
      {type:"coaching",color:"#ff7151"},
      {type:"conge",color:"purple"},
      {type:"reunion",color:"blue"},
      {type:"voyage",color:"orange"},
      {type:"conference",color:"RosyBrown"},
      {type:"anniversaire",color:"GreenYellow"}
    ]
  }
};

export default new Vuex.Store({
  state: {
    keycloakService: null,
    configuration: DEFAULT_CONFIG,
  },
  mutations: {
    LOAD_CONFIGURATION(state) {
      try{
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", process.env.VUE_APP_CONFIGURATION_URI, false );
        xmlHttp.send(null);
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
          state.configuration = JSON.parse(xmlHttp.responseText);
        }
      }
      catch(e){
        console.log(JSON.stringify(e));
      }
      state.keycloakService=new KeycloakService(state.configuration.services.keycloakServiceUri);
    },
  },
});