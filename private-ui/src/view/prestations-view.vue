<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view" v-if="this.prestation!=null">
      <v-dialog v-model="dialog" width="500">
        <v-card>
          <v-card-text>
            <stripe-payment type="prestation" v-model="prestation" v-on:input="memoriserPaiment"/>
          </v-card-text>
        </v-card>
      </v-dialog>
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.prestation.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-tabs vertical>
          <v-tab>{{$t("pmeflow.ui.prestation.tab.resume")}}</v-tab>
          <v-tab v-if="prestation.etat=='PAYE'">{{$t("pmeflow.ui.prestation.tab.facture")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.prestation.tab.resume")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-text-field v-model="prestation.libelle" :label="$t('pmeflow.ui.generic.label.libelle')" readonly/>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-textarea v-model="prestation.description" :label="$t('pmeflow.ui.generic.label.description')" rows="3" readonly/>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="4" cols="12">
                      <v-text-field class="required" v-model="prestation.prixHT" :label="$t('pmeflow.ui.generic.label.prixHT')" readonly>
                        <span slot="append">€</span>
                      </v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
            </v-card>

            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.prestation.label.prestationsIncluses")}}</span>
              </v-card-title>
              <v-card-text>
                <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field class="required" v-model="prestation.creditsRdv" :label="$t('pmeflow.ui.ref-prestation.label.creditsRdv')" readonly/>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-text-field class="required" v-model="prestation.creditsDossier" :label="$t('pmeflow.ui.ref-prestation.label.creditsDossier')" readonly/>
                    </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-tab-item>
          <v-tab-item>  
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.prestation.tab.facture")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-overlay v-if="!loaded" :absolute="true" :value="!loaded" opacity="0.5" style="backdrop-filter: blur(5px);">
                    <v-progress-circular indeterminate size="64"></v-progress-circular>
                  </v-overlay>
                  <v-btn color="primary" outlined class="mb-2" @click="rotate += 90">&#x27F3;</v-btn>
                  <v-btn color="primary" outlined class="mb-2" @click="rotate -= 90">&#x27F2;</v-btn>
                  <div style="width: 70%">
                    <pdf style="border: 1px solid red" :src="factureUri(prestation.oid)" 
                      :rotate="rotate" @loaded="loaded=true"/>
                  </div>
                </v-container>
              </v-card-text>
            </v-card>
          </v-tab-item>
        </v-tabs>  
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" rounded outlined @click="fermer">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
        <v-btn color="red darken-1" rounded outlined @click="preparerPrestationAPayer(prestation)" v-if="prestation.etat!='PAYE'"><v-icon>mdi-credit-card-outline</v-icon>&nbsp;{{$t('pmeflow.ui.generic.button.entry.payer')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card class="card-view" v-if="this.prestation==null">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.prestation.title.multiple")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="prestations"
            :sort-by="['nom']"
            :sort-desc="[false]"
            multi-sort
            @click:row="editerPrestation($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.dateAchat`]="{ item }">
              <span v-if="item.dateAchat!==null">
              {{new Date(item.dateAchat).toLocaleString("fr-FR").substr(0, 16)}}
              </span>
            </template>
            <template v-slot:[`item.client`]="{ item }">
               {{item.client.prenom}} {{item.client.nom}}
            </template>
            <template v-slot:[`item.prixHT`]="{ item }">
              {{item.prixHT}}€
            </template>
            <template v-slot:[`item.etat`]="{ item }">
              <span style="color:green" v-if="item.etat=='PAYE'">{{$t("pmeflow.ui.prestation.statut.payee")}}</span>
              <span style="color:red" v-if="item.etat=='EN_ERREUR'">{{$t("pmeflow.ui.prestation.statut.en_erreur")}}</span>
              <span style="color:orange" v-if="item.etat=='A_PAYER'">{{$t("pmeflow.ui.prestation.statut.a_payer")}}</span>
            </template>
        </v-data-table>
      </v-card-text>      
    </v-card>      
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
//import pdf from 'vue-pdf'
import PrestationService from "@/api/PrestationService.js";
import stripePayment from "@/components/ui/stripe-payment.vue";


export default {
  components: {    
    //pdf,
    stripePayment,
  },
  data: () => ({
    src:'',
    rotate: 0,
    loaded: false,

    dialog:false,
    motif:"",
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    prestation:null,
    headersPrestas: [
      { text: "Code", value: "code" },
      { text: "Libellé", value: "libelle" },
      { text: "Description", value: "description"}
    ],
    headers: [
      { text: "Date Achat", value: "dateAchat", width:"10rem" },
      { text: "Libellé", value: "libelle", width:"20rem" },
      { text: "Description", value: "description" },
      { text: "Prix H.T.", value: "prixHT", width:"6rem" },
      { text: "Client", value: "client", width:"10rem" },
      { text: "Etat", value: "etat", width:"7rem" }
    ],
    prestations: []
  }),
  created() {
    this.initialize();
  },
  computed:{
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
  },
  methods: {
    getPrestationService() {
      return new PrestationService(
        this.$store.state.configuration.services.prestationServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getPrestations();
    },    
    preparerPrestationAPayer(){
      this.dialog=true;
    },
    memoriserPaiment(prestation) {
      this.getPrestationService().mergePrestation(prestation)
        .then(() => {
          this.getPrestations();
          this.dialog=false;
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
        })
    },
    fermer(){
      this.prestation=null;
      eventBus.$emit("errors-loaded", []);
    },
    getPrestations() {
      if (this.$route.params.id!=null)
      {
        this.getPrestationService().getPrestationById(this.$route.params.id).then(response => {
          this.prestation = response.data;
          eventBus.$emit("errors-loaded", []);
        });
      }
      else
      {
        this.getPrestationService().getPrestations().then(response => {
          this.prestations = response.data;
          eventBus.$emit("errors-loaded", []);
        });
      }
    },
    editerPrestation(item) {
      this.editedIndex = this.prestations.indexOf(item);
      this.prestation = Object.assign({}, item);
      eventBus.$emit("errors-loaded", []);
    },    
    factureUri(prestationId){
      let uri = this.$store.state.configuration.services.facturationServiceUri+"/FR_FR/prestation/"+prestationId;
      return uri;
    },
  }
};
</script>