<template>
  <v-card elevation="3" outlined v-if="rendezVous!=null">
    <v-toolbar color="#982d43" dark>
        <v-toolbar-title>
          <v-icon>mdi-calendar</v-icon> {{rdvJourMois}}
          <v-icon class="ml-4">mdi-clock-time-eight-outline</v-icon> {{rdvHoraire}}
        </v-toolbar-title>  
    </v-toolbar>
    <v-card-text v-if="isJuriste==false || isAdmin==true">
      <v-list-item three-line>
        <v-list-item-avatar size="60">
          <v-img src="@/assets/avatars/assistance.jpg"/>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="text-h5 mb-1">
            {{rendezVous.juriste.nom}} {{rendezVous.juriste.prenom}}
          </v-list-item-title>
          <v-list-item-subtitle>{{rendezVous.juriste.specialite}}</v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </v-card-text>
    <v-divider class="mx-4" v-if="isJuriste==false || isAdmin==true"/>
    <v-card-text v-if="isJuriste==true ||isAdmin==true">
      <v-list-item three-line>
        <v-list-item-avatar size="60">
          <v-img src="@/assets/avatars/client.png"/>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="text-h5 mb-1">
            {{rendezVous.client.nom}} {{rendezVous.client.prenom}}
          </v-list-item-title>
          <v-list-item-subtitle>{{rendezVous.client.telephone}} / {{rendezVous.client.email}}</v-list-item-subtitle>
        </v-list-item-content>            
      </v-list-item>    
    </v-card-text>
    <v-card-text>
      <v-divider class="mx-4" v-if="isJuriste==true ||isAdmin==true"/>      
      <v-list-item three-line>
        <v-list-item-avatar size="60">
          <v-icon style="font-size: 60px;">mdi-calendar-text-outline</v-icon>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="text-h5 mb-1">{{rendezVous.motif}}</v-list-item-title>
          <v-list-item-subtitle>{{rendezVous.description!=""&&rendezVous.description!=null?rendezVous.description:'Aucune description'}}</v-list-item-subtitle>
        </v-list-item-content>            
      </v-list-item>
    </v-card-text>    
    <v-divider class="mx-4"></v-divider>
    <v-card-text>
      <v-list-item>
        <v-list-item-content>
          <v-btn color="red darken-1" rounded outlined @click="dialogAnnulation=true">{{$t("pmeflow.ui.rdv.button.annuler")}}</v-btn>
        </v-list-item-content>
      </v-list-item>
    </v-card-text>
    <v-divider class="mx-4"></v-divider>
    <v-card-text>
      <v-list-item>
        <v-list-item-content>
          <v-btn color="blue darken-1" rounded outlined @click="dialogLier=true" v-if="rendezVous.dossierId==null || rendezVous.dossierId==''">{{$t("pmeflow.ui.rdv.button.lier")}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined :href="dossierUri" v-if="rendezVous.dossierId!=null && rendezVous.dossierId!=''">{{$t("pmeflow.ui.rdv.button.consulter")}}</v-btn>  
        </v-list-item-content>
      </v-list-item>
    </v-card-text>
    <v-divider class="mx-4" v-if="rendezVous.dossierId==null || rendezVous.dossierId==''"/>
    <v-card-text v-if="rendezVous.dossierId==null || rendezVous.dossierId==''">
      <v-list-item three-line>
        <v-list-item-avatar size="60">
          <v-icon x-large>mdi-alert</v-icon>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="text-h5 mb-1">
            {{$t("pmeflow.ui.rdv.label.informations")}}
          </v-list-item-title>
          <v-list-item-subtitle>{{$t("pmeflow.ui.rdv.msg.preparerDoc")}}</v-list-item-subtitle>
        </v-list-item-content>            
      </v-list-item>
    </v-card-text>
    <v-divider class="mx-4" v-if="rendezVous.dossierId!=null && rendezVous.dossierId!=''"/>
    <v-card-text v-if="rendezVous.dossierId!=null && rendezVous.dossierId!=''">
      <v-list-item three-line>
        <v-list-item-avatar size="60">
          <v-icon x-large>mdi-file-document</v-icon>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="text-h5 mb-1">
            {{$t("pmeflow.ui.rdv.label.partage")}}
          </v-list-item-title>
          <v-list-item-subtitle>{{$t("pmeflow.ui.rdv.msg.partagerDoc")}}</v-list-item-subtitle>
        </v-list-item-content>            
      </v-list-item>
      <v-list-item>
        <v-list-item-content>
          <v-btn color="blue darken-1" rounded outlined @click="dialogJoindre=true">{{$t("pmeflow.ui.rdv.button.joindre")}}</v-btn>
        </v-list-item-content>
      </v-list-item>
    </v-card-text>
    <v-dialog v-model="dialogAnnulation" max-width="390">
      <v-card>
        <v-card-title class="text-h5">{{$t("pmeflow.ui.rdv.label.confirmation.annulation")}}</v-card-title>    
        <v-card-text>
          {{$t("pmeflow.ui.rdv.msg.confirmation.resiliation_msg")}}<br/><br/>
          {{$t("pmeflow.ui.rdv.msg.confirmation.resiliation")}}
        </v-card-text>    
        <v-card-actions>
          <v-spacer></v-spacer>    
          <v-btn color="blue darken-1" rounded outlined @click="dialogAnnulation=false">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="red darken-1" rounded outlined @click="annulerRdv">{{$t('pmeflow.ui.generic.button.entry.confirmer')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogJoindre" max-width="390">
      <documentComponent v-model="rendezVous.dossierId" :profil="profil" :new="dialogJoindre" v-on:close="dialogJoindre=false"/>
    </v-dialog>
    <v-dialog v-model="dialogLier" max-width="390">
      <dossier-selector v-model="rendezVous.dossierId" 
        :client="rendezVous.client" :juriste="rendezVous.juriste" 
        v-on:input="save" v-on:close="dialogLier=false" v-on:annuler="dialogLier=false"/>
    </v-dialog>
  </v-card>
</template>

<script>
import documentComponent from '@/components/ui/rdv/document-component.vue'
import dossierSelector from '@/components/ui/rdv/dossier-selector.vue'
import RendezVousService from "@/api/RendezVousService.js";

export default {
  components: {
    documentComponent,
    dossierSelector,
  },
  data: () => ({
    dialogAnnulation:false,
    dialogLier:false,
    dialogJoindre:false,
    rendezVous:null,
  }),
  props:{
    value:Object
  },
  computed: {
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
        let nDate = new Date(this.rendezVous.dateDebut);
        sDate = nDate.toLocaleDateString("fr-FR", options);
      }
      return sDate;
    },
    rdvHoraire(){
      let options = {hour: '2-digit', minute:'2-digit', hour12: false};
      let sDate = "";
      if (this.rendezVous!=null && this.rendezVous.dateDebut)
      {
        let nDate = new Date(this.rendezVous.dateDebut);
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
    profil(){
      let profil = this.rendezVous.client;
      if (this.isJuriste){
        profil = this.rendezVous.juriste;
      }
      return profil;
    },
  },
  mounted(){
    this.rendezVous = this.value;
  },
  watch: { 
    rendezVous: function(newVal) {
      this.rendezVous=newVal;
      if (this.rendezVous!=null && this.rendezVous.juriste==null){
        this.rendezVous.juriste={
          nom:"",
          prenom:"",
          specialite:""
        };
      }
      if (this.rendezVous!=null && this.rendezVous.client==null){
        this.rendezVous.client={
          nom:"",
          prenom:""
        }
      }
    }, 
  }, 
  created() {
  },
  methods: {
    getRendezVousService() {
      return new RendezVousService(
        this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    save(){
      return this.getRendezVousService().mergeRendezVous(this.rendezVous) 
        .then(response => {
          this.rendezVous = response.data;
        });
    },
    annulerRdv(){
      this.dialogAnnulation=false;
      this.rendezVous.etat='ANNULE';
      return this.save().then(setTimeout(()=>{this.$emit('remove', this.rendezVous.oid);}, 1000));      
    },
  }
};
</script>