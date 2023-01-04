<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view" v-if="this.dossier!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.dossier.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-tabs vertical>
          <v-tab>{{$t("pmeflow.ui.dossier.tab.resume")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.dossier.tab.communication")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.dossier.tab.documents")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.dossier.tab.prestations")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.dossier.tab.rdv")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.dossier.tab.resume")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-select return-object v-model="dossier.client" :items="clients" item-text="nom" item-value=""  menu-props="auto" 
                        :label="$t('pmeflow.ui.dossier.label.choisirClient')" hide-details prepend-icon="mdi-book-open" single-line 
                         v-if="dossier.client==null">
                      </v-select>
                      <v-card elevation="2" style="background-color:#d3d3d3" v-if="dossier.client!=null">
                        <v-card-title>Client</v-card-title>
                        <v-card-text>
                          {{dossier.client.nom}} {{dossier.client.prenom}}<br/>
                          {{dossier.client.adresse}}<br/>
                          {{dossier.client.codePostal}} {{dossier.client.ville}}<br/><br/>
                          {{dossier.client.email}}<br/>
                          {{dossier.client.telephone}}
                        </v-card-text>
                      </v-card>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-select return-object v-model="dossier.juriste" :items="juristes" item-text="nom" item-value=""  menu-props="auto" 
                        :label="$t('pmeflow.ui.dossier.label.choisirJuriste')" hide-details prepend-icon="mdi-book-open" single-line 
                         v-if="dossier.juriste==null && isJuriste==true">
                      </v-select>
                      <v-card elevation="2" style="background-color:#d3d3d3" v-if="dossier.juriste!=null">
                        <v-card-title>Juriste</v-card-title>
                        <v-card-text>
                          {{dossier.juriste.nom}} {{dossier.juriste.prenom}}<br/>
                          {{dossier.juriste.adresse}}<br/>
                          {{dossier.juriste.codePostal}} {{dossier.juriste.ville}}<br/><br/>
                          {{dossier.juriste.email}}<br/>
                          {{dossier.juriste.telephone}}
                        </v-card-text>
                      </v-card>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="dossier.libelle" :label="$t('pmeflow.ui.generic.label.libelle')" class="required" :rules="formValidation.required"/>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-select v-model="dossier.etat" :items="etats" :label="$t('pmeflow.ui.generic.label.etat')"  class="required" :rules="formValidation.required"/>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-textarea v-model="dossier.description" :label="$t('pmeflow.ui.generic.label.description')" class="required" rows="2" :rules="formValidation.required"/>
                    </v-col>
                  </v-row>                  
                </v-container>
              </v-card-text>
            </v-card>
            <suivisComponent v-model="dossier.suivis" :editable="isJuriste==true" v-on:input="save"/>
          </v-tab-item>
          <v-tab-item>  
            <v-card flat>              
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.dossier.tab.communication")}}</span>
              </v-card-title>
              <v-card-text>
                <chat v-model="dossier.echanges" :isClient="!isJuriste" :profil="profil" v-on:input="save"/>
              </v-card-text>
            </v-card>
          </v-tab-item>
          <v-tab-item>      
            <documents v-model="dossier.documents" :profil="profil" v-on:input="save"/>      
          </v-tab-item>
          <v-tab-item>      
            <prestations v-model="dossier" :profil="profil" v-on:input="save"/>
          </v-tab-item>
          <v-tab-item>      
            <rendez-vous v-model="dossier" :profil="profil" v-on:input="save"/>      
          </v-tab-item>
        </v-tabs>  
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>                            
        <v-btn color="red darken-1" rounded outlined @click="fermer">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
        <v-btn color="primary" rounded outlined @click="save" v-if="isEditable==true">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card class="card-view" v-if="this.dossier==null">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.dossier.title.multiple")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
          <v-col cols="12" md="5"  sm="12" style="text-align:right" v-if="mayCreateDossier">
            <v-btn color="primary" rounded outlined class="mr-2" @click="ajouter">{{$t("pmeflow.ui.dossier.button.ajouter")}}</v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="dossiers"
            :sort-by="['dateCreation']"
            :sort-desc="[true]"
            multi-sort
            @click:row="editerDossier($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.dateCreation`]="{ item }">
              {{new Date(item.dateCreation).toLocaleString("fr-FR").substr(0, 16)}}
            </template>
            <template v-slot:[`item.etat`]="{ item }">
              <span v-if="item.etat=='CLOS'" style="color:red">clos</span>
              <span v-if="item.etat=='PRIS_EN_CHARGE'" style="color:orange">pris en charge</span>
              <span v-if="item.etat=='NOUVEAU'" style="color:green">nouveau</span>
            </template>
            <template v-slot:[`item.prix`]="{ item }">
              {{item.prix}} €
            </template>
        </v-data-table>
      </v-card-text>      
    </v-card>      
  </v-col>
</template>

<script>
import ClientService from "@/api/ClientService.js";
import chat from "@/components/ui/chat-component.vue";
import documents from '@/components/ui/dossier/documents-component.vue';
import DossierService from "@/api/DossierService.js";
import { eventBus } from "@/plugins/event-bus.js";
import prestations from '@/components/ui/dossier/prestations-component.vue';
import ProfilService from "@/api/ProfilService.js";
import rendezVous from '@/components/ui/dossier/rendez-vous-component.vue';
import suivisComponent from '@/components/ui/dossier/suivis-component.vue';
import UtilisateurService from "@/api/UtilisateurService.js";


export default {
  components: {
    chat,
    documents,
    prestations,
    rendezVous,
    suivisComponent
  },
  data: () => ({
    dialogClient:true,
    search:"",
    defaultDossier: {
      oid: "",
      libelle: "",
      description: "",
      client : null,
      juriste: null,
      documents : [],
      echanges : [],
      suivis : [],
      prestations : [],
      etat: "NOUVEAU"
    },
    dossier:null,
    headers: [
      { text: "Date Création", value: "dateCreation", width:"10rem" },
      { text: "Libellé", value: "libelle", width:"15rem" },
      { text: "Description", value: "description", width:"15rem"},
      { text: "Client", value: "client.nom", width:"7rem" },
      { text: "Juriste", value: "juriste.nom", width:"7rem" },
      { text: "Etat", value: "etat", width:"8rem" }
    ],
    etats:['NOUVEAU', 'PRIS_EN_CHARGE', 'CLOS'],
    dossiers: [],
  }),
  created() {
    this.initialize();
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
    mayCreateDossier:{
      get() {
        return this.getDossierService().verifDossierCreate().then(response=>response.data);
      },
      default: false
    },
  },
  computed:{
    isJuriste(){
      let result = false;
      let roles = sessionStorage.getItem("oidc-roles");
      let authorize = ['ADMIN', 'JURISTE'];
      authorize.forEach(role => {
        result |= roles.includes(role);
      });
      return result;
    },
    isEditable(){
      let result = this.isJuriste;
      result |= (this.dossier.etat==="nouveau");
      return result;
    },
    profil(){
      let profil = this.dossier.client;
      if (this.isJuriste){
        profil = this.dossier.juriste;
      }
      return profil;
    },
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },    
    dialogSize(){
      let width = `${window.innerWidth*0.8}px`;
      let height = `${window.innerHeight*0.8}px`;
      return {
        "width":width,
        "height":height
      };
    }   
  },
  methods: {
    getClientService() {
      return new ClientService(
        this.$store.state.configuration.services.clientServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getDossierService() {
      return new DossierService(
        this.$store.state.configuration.services.dossierServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },    getProfilService() {
      return new ProfilService(
        this.$store.state.configuration.services.profilServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getUtilisateurService() {
      return new UtilisateurService(
        this.$store.state.configuration.services.utilisateurServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getDossiers();
    },
    getProfil() {
      this.getProfilService().getProfil().then(profil => {
      if (!this.isJuriste){
        this.dossier.client=profil;
      }});
    },
    ajouter(){
      this.dossier = Object.assign({}, this.defaultDossier);
      this.getProfil();
      eventBus.$emit("errors-loaded", []);
    },
    fermer(){
      this.dossier=null;
      eventBus.$emit("errors-loaded", []);
    },
    getDossiers() {
      if (this.$route.params.id!=null)
      {
        this.getDossierService().getDossierById(this.$route.params.id).then(response => {
          this.dossier = response.data;
          eventBus.$emit("errors-loaded", []);
        });
      }
      else
      {
        this.getDossierService().getDossiers().then(response => {
          this.dossiers = response.data;
          eventBus.$emit("errors-loaded", []);
        });
      }
    },
    editerDossier(item) {
      this.editedIndex = this.dossiers.indexOf(item);
      this.dossier = Object.assign({}, item);
      eventBus.$emit("errors-loaded", []);
    },
    save() {
      this.getDossierService().mergeDossier(this.dossier)
        .then(response => {
          this.dossier = response.data;
          if (this.editedIndex > -1) {
            Object.assign(this.dossiers[this.editedIndex], this.dossier);
          } else {
            this.dossiers.push(this.dossier);
          }
          eventBus.$emit("errors-loaded", []);
        })
        .catch(e => {
          let errors=[];
          if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
          {
            let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
            for (let index = 0; index < oEvents.length; index++) {
              errors.push({
                "type" : oEvents[index][1].niveau.toLowerCase(),
                "msg" :  oEvents[index][1].libelle
              });
            }
          }
          else
          {
            errors.push({
              type:"error",
              msg:e.message
            });
          }
          eventBus.$emit("errors-loaded", errors);
        });
    }
  }
};
</script>