<template>
  <div>
    <errors :items="errors"/>
    <v-card v-if="this.prestation!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.prestation.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col md="12" cols="12">
            <v-text-field v-model="prestation.libelle" :label="$t('pmeflow.ui.generic.label.libelle')"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="12" cols="12">
            <v-textarea v-model="prestation.description" :label="$t('pmeflow.ui.generic.label.description')" rows="3"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="4" cols="12">
            <v-text-field class="required" v-model="prestation.prixHT" :label="$t('pmeflow.ui.generic.label.prixHT')">
              <span slot="append">€</span>
            </v-text-field>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="red darken-1" rounded outlined @click="fermer">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
        <v-btn color="blue darken-1" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-if="this.prestation==null">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.prestation.title.multiple")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
          <v-spacer></v-spacer>
        <v-btn style="float:right" color="blue darken-1" rounded outlined @click="ajouter" v-if="isJuriste==true">{{$t('pmeflow.ui.dossier.prestation.button.ajouter')}}</v-btn>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="prestations"
            :sort-by="['dateAchat']"
            :sort-desc="[true]"
            multi-sort>            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.dateAchat`]="{ item }">
              <span v-if="item.dateAchat!==null">
              {{new Date(item.dateAchat).toLocaleString("fr-FR").substr(0, 17).replace(",","")}}
              </span>
            </template>
            <template v-slot:[`item.etat`]="{ item }">
               <span v-if="item.etat=='A_PAYER'" style="color:orange">{{item.etat}}</span>
               <span v-if="item.etat=='PAYE'" style="color:green">{{item.etat}}</span>
            </template>
            <template v-slot:[`item.actions`]="{ item }">               
              <v-btn class="mr-2" color="primary" icon @click="preparerPrestationAPayer(item)" v-if="item.etat!='PAYE'"><v-icon>mdi-credit-card-outline</v-icon></v-btn>
              <v-btn class="mr-2" color="red" icon @click="remove(item)" v-if="item.etat=='A_PAYER' && isJuriste"><v-icon>mdi-delete-outline</v-icon></v-btn>
              <a v-bind:href="factureUri(item.oid)" target="_blank" v-if="item.etat=='PAYE'">
                <v-btn class="mr-2" color="primary" icon><v-icon>mdi-eye-arrow-right</v-icon></v-btn>
              </a>
            </template>
            <template v-slot:[`item.prixHT`]="{ item }">
              {{item.prixHT}}€
            </template>
        </v-data-table>
        <v-dialog v-model="vDialog" width="500">
          <v-card>
            <v-card-text>
              <stripe-payment type="prestation" v-model="prestationAPayer" v-on:input="memoriserPaiment"/>
            </v-card-text>
          </v-card>
        </v-dialog>
      </v-card-text>      
    </v-card>
  </div>
</template>

<script>
import stripePayment from "@/components/ui/stripe-payment.vue";
import PrestationService from "@/api/PrestationService.js";
import errors from '@/components/ui/errors-render.vue';

export default {
  components: {
    errors,
    stripePayment
  },  
  props:{
    value: Object,
    profil: Object
  },
  data: () => ({
    prestationInitialeId:"",
    prestation:null,
    prestationAPayer:null,
    prestations: [],
    vDialog:false,
    defaultPrestation:{
      dateAchat:null,
      libelle:"",
      description:"",
      prixHT:"",
      etat:"A_PAYER",
      dossierId:"",    
      client:null
    },
    dialog:false,
    motif:"",
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    errors: [],
    headers : [
        { text: "Date", value: "dateAchat", width:"15%" },
        { text: "Libellé", value: "libelle", width:"20%" },
        { text: "Description", value: "description", width:"35%" },
        { text: "Prix H.T.", value: "prixHT" },
        { text: "Etat", value: "etat" },
        { text: "Actions", value: "actions" }
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
    factureUri(paypalObjectId){
      return this.$store.state.configuration.services.facturationServiceUri+"/FR_FR/prestation/"+paypalObjectId;
    },
    preparerPrestationAPayer(prestation){
      this.prestationAPayer=prestation;
      this.prestationInitialeId=prestation.oid;
      this.vDialog=true;
    },
    getPrestationService() {
      return new PrestationService(
        this.$store.state.configuration.services.prestationServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getPrestations();
    },
    ajouter(){
      this.prestation = Object.assign({}, this.defaultPrestation);
      this.prestation.client = this.value.client;
      this.prestation.dossierId = this.value.oid;
      this.errors=[];
    },
    fermer(){
      this.prestation=null;
      this.errors=[];
    },
    getPrestations() {
      this.getPrestationService().getPrestationsByDossier(this.value.oid).then(response => {
        this.prestations = response.data;
        this.errors=[];
      });
    },
    remove(prestation) {
      this.getPrestationService().removePrestation(prestation.oid)
        .then(() => {
            this.getPrestations();
            this.vDialog=false;
            this.prestationAPayer=null;
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
    memoriserPaiment(prestation) {
      this.getPrestationService().removePrestation(this.prestationInitialeId)
        .then(() => {
          this.getPrestationService().mergePrestation(prestation)
            .then(() => {
              this.getPrestations();
              this.vDialog=false;
              this.prestationAPayer=null;
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
      this.getPrestationService().mergePrestation(this.prestation)
        .then(response => {
          this.prestation = response.data;
          this.getPrestations();
          this.prestation=null;
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