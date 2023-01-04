<template>
  <div>
    <v-stepper v-model="currentStep" vertical>
      <v-stepper-step :complete="currentStep>1" step="1">
        {{$t("pmeflow.ui.rdv.label.votreExpert")}}
        <small v-if="currentStep==1">{{$t("pmeflow.ui.rdv.msg.quelExpert")}}</small>
        <v-list-item three-line v-if="rendezVous.juriste!=null && currentStep>1">
          <v-list-item-avatar size="60">
            <v-img src="@/assets/avatars/client.png"/>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title class="text-h5 mb-1">{{rendezVous.juriste.nom}} {{rendezVous.juriste.prenom}}</v-list-item-title>
            <v-list-item-subtitle>{{rendezVous.juriste.specialite}}</v-list-item-subtitle>
          </v-list-item-content>            
        </v-list-item>
      </v-stepper-step>  
      <v-stepper-content step="1">
        <v-card>
          <v-card-text>
            <div style="display:inline-flex" v-for="juriste in juristes" v-bind:key="juriste.oid">
              <input :class="juriste.oid" style="margin-top: 40px;" type="radio" :value="juriste" v-model="rendezVous.juriste"/>
              <v-list-item three-line @click="selectRadio(juriste.oid)">
                <v-list-item-avatar size="60">
                  <v-img src="@/assets/avatars/client.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">{{juriste.nom}} {{juriste.prenom}}</v-list-item-title>
                  <v-list-item-subtitle>{{juriste.specialite}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" rounded outlined v-if="rendezVous.juriste!=null" @click="(!isJuriste && !isAdmin)?currentStep=3:currentStep=2">{{$t('pmeflow.ui.generic.button.entry.suivant')}}</v-btn>
          </v-card-actions>
        </v-card>
      </v-stepper-content>

      <v-stepper-step :complete="currentStep>2" step="2" v-if="isJuriste || isAdmin">
        {{$t("pmeflow.ui.rdv.label.votreClient")}}
        <small v-if="currentStep==2">{{$t("pmeflow.ui.rdv.msg.quelClient")}}</small>
        <v-list-item three-line v-if="rendezVous.client!=null && currentStep>2">
          <v-list-item-avatar size="60">
            <v-img src="@/assets/avatars/client.png"/>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title class="text-h5 mb-1">{{rendezVous.client.nom}} {{rendezVous.client.prenom}}</v-list-item-title>
          </v-list-item-content>            
        </v-list-item>
      </v-stepper-step>  
      <v-stepper-content step="2" v-if="isJuriste || isAdmin">
        <v-card>
          <v-card-text>
            <div style="display:inline-flex" v-for="client in clients" v-bind:key="client.oid">
              <input :class="client.oid" style="margin-top: 20px;" type="radio" :value="client" v-model="rendezVous.client"/>
              <v-list-item @click="selectRadio(client.oid)">
                <v-list-item-avatar size="30">
                  <v-img src="@/assets/avatars/client.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">{{client.nom}} {{client.prenom}}</v-list-item-title>
                </v-list-item-content>            
              </v-list-item>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" rounded outlined v-if="rendezVous.client!=null" @click="currentStep=3">{{$t('pmeflow.ui.generic.button.entry.suivant')}}</v-btn>
            </v-card-actions>
        </v-card>
      </v-stepper-content>
      
      <v-stepper-step :complete="currentStep>2" step="2" v-if="!isJuriste && !isAdmin">
        {{$t("pmeflow.ui.rdv.label.vous")}}
        <v-list-item three-line v-if="rendezVous.client!=null && currentStep>2">
          <v-list-item-avatar size="60">
            <v-img src="@/assets/avatars/client.png"/>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title class="text-h5 mb-1">{{rendezVous.client.nom}} {{rendezVous.client.prenom}}</v-list-item-title>
          </v-list-item-content>            
        </v-list-item>
      </v-stepper-step>        

      <v-stepper-step :complete="currentStep>3" step="3" v-if="(currentStep<=3)||(currentStep>3 && dossier!=null)">
        {{$t("pmeflow.ui.rdv.label.dossierAssocie")}}
        <small v-if="currentStep==3">{{$t("pmeflow.ui.rdv.msg.selectionDossier")}}</small>
        <v-list-item three-line v-if="dossier!=null && currentStep>3">
          <v-list-item-avatar size="60">
            <v-icon style="font-size: 60px;">mdi-archive</v-icon>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-title class="text-h5 mb-1">{{dossier.libelle}}</v-list-item-title>
            <v-list-item-subtitle>{{dossier.description}}</v-list-item-subtitle>
          </v-list-item-content>            
        </v-list-item>
      </v-stepper-step>
      <v-stepper-content step="3">
        <dossier-selector v-model="rendezVous.dossierId" noTitle="true"
          v-bind:dossier.sync="dossier"
          :client="rendezVous.client" :juriste="rendezVous.juriste" 
          v-on:input="currentStep=4" v-on:annuler="close" v-on:close="currentStep=4"/>
      </v-stepper-content>
      
      <v-stepper-step :complete="currentStep>4" step="4">
        {{$t("pmeflow.ui.rdv.label.choixRdv")}}
        <small v-if="currentStep==4">{{$t("pmeflow.ui.rdv.msg.quelHoraire")}}</small>
        <v-list-item three-line v-if="rendezVous.dateDebut!=null && currentStep>4">
          <v-list-item-content>
            <v-list-item-title class="text-h5 mb-1">
              <v-icon>mdi-calendar</v-icon> {{rdvJourMois}}
              <v-icon class="ml-4">mdi-clock-time-eight-outline</v-icon> {{rdvHoraire}}
            </v-list-item-title>
          </v-list-item-content>            
        </v-list-item>

      </v-stepper-step>  
      <v-stepper-content step="4" v-if="rendezVous.juriste!=null">
        <horraire-selector v-model="rendezVous.dateDebut" v-on:input="currentStep=5" :juriste="rendezVous.juriste"/>
        <v-btn color="primary" rounded outlined @click="currentStep=5" v-if="rendezVous.dateDebut!=null">{{$t('pmeflow.ui.generic.button.entry.suivant')}}</v-btn>
      </v-stepper-content>
      
      <v-stepper-step :complete="currentStep>5" step="5">
        {{$t("pmeflow.ui.rdv.label.validation")}}
        <small>{{$t("pmeflow.ui.rdv.msg.verifInformation")}}</small>
      </v-stepper-step>  
      <v-stepper-content step="5">
        <v-card>
          <v-card-text>
            <v-row>
              <v-col md="12" cols="12">
                  <v-col md="12" cols="12">
                    <v-text-field v-model="rendezVous.motif" :label="$t('pmeflow.ui.generic.label.libelle')" class="required" :rules="formValidation.required"/>
                  </v-col>
                  <v-col md="12" cols="12">
                    <v-textarea v-model="rendezVous.description" :label="$t('pmeflow.ui.generic.label.description')" rows="2"/>
                  </v-col>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions v-if="rendezVous.motif!=null && rendezVous.motif.length>0">
            <v-btn color="primary" rounded outlined @click="save">{{$t('pmeflow.ui.rdv.button.prendreRdv')}}</v-btn>
          </v-card-actions>
        </v-card>
      </v-stepper-content>
    </v-stepper>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>
  </div>
</template>

<script>
import ClientService from "@/api/ClientService.js";
import dossierSelector from "@/components/ui/rdv/dossier-selector.vue";
import horraireSelector from "@/components/ui/rdv/horraire-selector.vue";
import ProfilService from "@/api/ProfilService.js";
import RendezVousService from "@/api/RendezVousService.js";
import UtilisateurService from "@/api/UtilisateurService.js";

export default {
  components: {
    dossierSelector,
    horraireSelector,
  },
  data: () => ({
    currentStep:1,
    msg:{
      type:"info",
      text:"",
      presence:false
    },   
    defaultRendezVous:{
      juriste:null,
      client:null,
      dossierId:null,
      dateDebut:null,
      motif:null,
      description:null,
    },
    rendezVous:{
      juriste:null,
      client:null,
      dossierId:null,
      dateDebut:null,
      motif:null,
      description:null,
    },
    dossier:null,
    profil:null
  }),
  props:{
    value:Object,
    clear:Boolean
  },
  watch:{
    clear:function(newValue){
      if (newValue==false){
        this.clearDatas();
      }
    }
  },
  asyncComputed: {
    clients: {
      get() {
        return this.getClientService().getClients().then(response=>response.data);
      },
      default: []
    },
    juristes: {
      get() {
        return this.getUtilisateurService().getJuristes().then(response=>response.data);
      },
      default: []
    },
  },
  computed: {    
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
    dossierUri(){
      let uri = "";
      if (this.rendezVous!=null)
      {
        uri="/private/dossiers/"+this.rendezVous.dossierId;
      }
      return uri;
    },
    rdvJourMois(){
      let options = { weekday: 'long', month: 'long', day: 'numeric' };
      let sDate = "";
      if (this.rendezVous!=null && this.rendezVous.dateDebut)
      {
        let nDate = new Date(this.rendezVous.dateDebut*1);
        sDate = nDate.toLocaleDateString("fr-FR", options);
      }
      return sDate;
    },
    rdvHoraire(){
      let options = {hour: '2-digit', minute:'2-digit', hour12: false};
      let sDate = "";
      if (this.rendezVous!=null && this.rendezVous.dateDebut)
      {
        let nDate = new Date(this.rendezVous.dateDebut*1);
        sDate = nDate.toLocaleTimeString("fr-FR", options);
      }
      return sDate;
    },
    isJuriste(){
      let result = false;
      let roles = sessionStorage.getItem("oidc-roles");
      let authorize = ['JURISTE'];
      authorize.forEach(role => {
        result |= roles.includes(role);
      });
      return result;
    },
    isAdmin(){
      let result = false;
      let roles = sessionStorage.getItem("oidc-roles");
      let authorize = ['ADMIN'];
      authorize.forEach(role => {
        result |= roles.includes(role);
      });
      return result;
    },
  },
  mounted(){
    if (this.isJuriste && !this.isAdmin){
      this.currentStep=2;
    }
    this.getProfil().then(()=>{
      if (this.isJuriste && !this.isAdmin){
      this.rendezVous.juriste = this.profil;
    }
    if (!this.isJuriste && !this.isAdmin){
      this.rendezVous.client = this.profil;
    }
    });    
  },
  methods: {    
    getClientService() {
      return new ClientService(
        this.$store.state.configuration.services.clientServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getProfilService() {
      return new ProfilService(
        this.$store.state.configuration.services.profilServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getRendezVousService() {
      return new RendezVousService(
        "https://dev.pmeflow.client.lixtec.fr/backend/api/1.0/rendez-vous",
        //this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getUtilisateurService() {
      return new UtilisateurService(
        this.$store.state.configuration.services.utilisateurServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },    
    getProfil() {
      return this.getProfilService().getProfil().then(profil => {
        this.profil = profil;
      });
    },
    clearDatas(){
      if (this.isJuriste && !this.isAdmin){
        this.currentStep=2;
      }
      else{
        this.currentStep=1;
      }
      this.dossier=null;
      Object.assign(this.rendezVous, this.defaultRendezVous);
    },
    close(){
      this.clearDatas();
      this.$emit('close');
    },
    save(){
      this.getRendezVousService().mergeRendezVous(this.rendezVous) 
        .then(response => {
          this.$emit("input", response.data);
          this.$emit("close");
        })
        .catch(e => {
          this.msg({
            type:"error",
            text:e.message,
            presence:true
          });   
          this.$emit("close");       
        });
    },
    selectRadio(id){
      document.getElementsByClassName(id)[0].click();
    }
  }
};
</script>
<style>
  .v-stepper__step--complete .mdi-check {
    background-color: rgb(41, 193, 41) !important;
    border-color: rgb(41, 193, 41) !important;
  }
  .v-stepper__step--complete .v-icon.mdi-check {
    font-size: 24px !important;
  }
</style>