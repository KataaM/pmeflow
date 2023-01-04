<template>
  <div>
    <errors :items="errors"/>
    <v-card>
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.dossier.tab.rdv")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
          <v-spacer></v-spacer>
          <v-btn color="primary" rounded outlined class="mb-2" @click="dialog=true;" v-if="isJuriste==true">{{$t("pmeflow.ui.rdv.button.ajouter")}}</v-btn>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-dialog v-model="dialog" width="50vw">
          <rendezVousStepper :clear="dialog" v-on:input="getRendezVousForDossier" v-on:close="dialog=false"/>
        </v-dialog>
        <v-data-table
          :headers="headers"
          :search="search"
          :items="rdvs"
          :sort-by="['dateDebut']"
          :sort-desc="[true]"
          multi-sort>
          <template v-slot:[`item.dateDebut`]="{ item }">
            <span v-if="item.dateDebut!==null">
            {{new Date(item.dateDebut).toLocaleString("fr-FR").substr(0, 16).replace(",","")}}
            </span>
          </template>
          <template v-slot:[`item.juriste`]="{ item }">
            {{item.juriste.nom}} {{item.juriste.prenom}}
          </template>
          <template v-slot:[`item.etat`]="{ item }">
              <span v-if="item.etat=='ANNULE'" style="color:red">{{item.etat}}</span>
              <span v-if="item.etat=='ACTIF'" style="color:green">{{item.etat}}</span>
          </template>
        </v-data-table>
      </v-card-text>      
    </v-card>
  </div>
</template>

<script>
import errors from '@/components/ui/errors-render.vue';
import RendezVousService from "@/api/RendezVousService.js";
import rendezVousStepper from '@/components/ui/rdv/rendez-vous-stepper.vue';

export default {
  components: {
    errors,
    rendezVousStepper,
  },  
  props:{
    value: Object,
    profil: Object
  },
  data: () => ({
    dialog:false,
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    errors: [],
    rdvs:[],
    headers : [
        { text: "Date", value: "dateDebut", width:"10rem" },
        { text: "Motif", value: "motif", width:"20rem" },
        { text: "Description", value: "description", width:"20rem" },
        { text: "Juriste", value: "juriste", width:"12rem" },
        { text: "Etat", value: "etat", width:"6rem" }
      ]
  }),
  created() {
    this.initialize();
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
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
  },
  methods: {
    getRendezVousService() {
      return new RendezVousService(
        this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getRendezVousForDossier();
    },
    ajouter(){
      this.rdv = Object.assign({}, this.defaultrdv);
      this.rdv.client = this.value.client;
      this.rdv.dossierId = this.value.oid;
      this.errors=[];
    },
    fermer(){
      this.rdv=null;
      this.errors=[];
    },
    getRendezVousForDossier() {
      this.getRendezVousService().getRendezVousForDossier(this.value.oid).then(response => {
        this.rdvs = response.data;
        this.errors=[];
      });
    },
    refund(rdv) {
      this.getrdvService().refundrdv(rdv.oid)
        .then(() => {
            this.getrdvs();
            this.vDialog=false;
            this.rdvAPayer=null;
            this.errors = [];
          })
          .catch(e => {
            this.errors=[];
            if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
            {
              let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
              for (let index = 0; index < oEvents.length; index++) {
                this.errors.push({
                  "type" : oEvents[index][1].niveau.toLowerCase(),
                  "msg" :  oEvents[index][1].libelle
                });
              }
            }
            else
            {
              this.errors.push({
                type:"error",
                msg:e.message
              });
            }
          });
    },
    remove(rdv) {
      this.getrdvService().removerdv(rdv.oid)
        .then(() => {
            this.getrdvs();
            this.vDialog=false;
            this.rdvAPayer=null;
            this.errors = [];
          })
          .catch(e => {
            this.errors=[];
            if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
            {
              let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
              for (let index = 0; index < oEvents.length; index++) {
                this.errors.push({
                  "type" : oEvents[index][1].niveau.toLowerCase(),
                  "msg" :  oEvents[index][1].libelle
                });
              }
            }
            else
            {
              this.errors.push({
                type:"error",
                msg:e.message
              });
            }
          });
    },
    memoriserPaiment(rdv) {
      this.getrdvService().removerdv(this.rdvInitialeId)
        .then(() => {
          this.getrdvService().mergerdv(rdv)
            .then(() => {
              this.getrdvs();
              this.vDialog=false;
              this.rdvAPayer=null;
              this.errors = [];
            })
            .catch(e => {
              this.errors=[];
              if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
              {
                let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
                for (let index = 0; index < oEvents.length; index++) {
                  this.errors.push({
                    "type" : oEvents[index][1].niveau.toLowerCase(),
                    "msg" :  oEvents[index][1].libelle
                  });
                }
              }
              else
              {
                this.errors.push({
                  type:"error",
                  msg:e.message
                });
              }
            });
        })
        .catch(e => {
          this.errors=[];
          if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
          {
            let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
            for (let index = 0; index < oEvents.length; index++) {
              this.errors.push({
                "type" : oEvents[index][1].niveau.toLowerCase(),
                "msg" :  oEvents[index][1].libelle
              });
            }
          }
          else
          {
            this.errors.push({
              type:"error",
              msg:e.message
            });
          }
        }); 
    },
    save() {
      this.getrdvService().mergerdv(this.rdv)
        .then(response => {
          this.rdv = response.data;
          this.getrdvs();
          this.rdv=null;
          this.errors = [];
        })
        .catch(e => {
          this.errors=[];
          if (e.response!=null && e.response.data != null && e.response.data.conteneur != null)
          {
            let oEvents = Object.entries(e.response.data.conteneur.mapEvenements);
            for (let index = 0; index < oEvents.length; index++) {
              this.errors.push({
                "type" : oEvents[index][1].niveau.toLowerCase(),
                "msg" :  oEvents[index][1].libelle
              });
            }
          }
          else
          {
            this.errors.push({
              type:"error",
              msg:e.message
            });
          }
        });
    }
  }
};
</script>