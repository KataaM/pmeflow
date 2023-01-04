<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view" v-if="this.abonnement!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.abonnement.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-tabs vertical>
          <v-tab>{{$t("pmeflow.ui.abonnement.tab.resume")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.abonnement.tab.transactions")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.abonnement.tab.resume")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="abonnement.libelle" :label="$t('pmeflow.ui.generic.label.libelle')" readonly/>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-textarea v-model="abonnement.description" :label="$t('pmeflow.ui.generic.label.description')" rows="3" readonly/>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="3" cols="12">
                      <v-text-field :value="formatDate(abonnement.dateDebut)" :label="$t('pmeflow.ui.abonnement.label.dateDebut')" readonly/>
                    </v-col>
                    <v-col md="3" cols="12">
                      <v-text-field :value="formatDate(abonnement.dateDebut, abonnement.dureeEngagement)" :label="$t('pmeflow.ui.abonnement.label.finEngagement')" readonly/>
                    </v-col>
                    <v-col md="3" cols="12">
                      <v-text-field class="required" v-model="abonnement.prixHT" :label="$t('pmeflow.ui.generic.label.prixHTMensuel')" readonly>
                        <span slot="append">€</span>
                      </v-text-field>
                    </v-col>
                    <v-col md="3" cols="12">
                      <v-text-field class="required" :value="toPrixTTC(abonnement.prixHT)" :label="$t('pmeflow.ui.generic.label.prixTTCMensuel')" readonly>
                        <span slot="append">€</span>
                      </v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
            </v-card>

            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.abonnement.label.prestationsIncluses")}}</span>
              </v-card-title>
              <v-card-text>
                <v-data-table
                  :headers="headersPrestas"
                  :items="abonnement.prestationsIncluses"
                  :sort-by="['libelle']" :sort-desc="[false]"
                  multi-sort>
                </v-data-table>
              </v-card-text>
            </v-card>
          </v-tab-item>
          <v-tab-item>  
            <prestations v-model="abonnement"/>
          </v-tab-item>
        </v-tabs>  
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="red darken-1" rounded outlined @click="dialog = true" v-if="this.abonnement.etat=='ACTIF'">{{$t('pmeflow.ui.abonnement.button.resilier')}}</v-btn>
        <v-btn color="blue darken-1" rounded outlined @click="fermer">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
        <v-dialog v-model="dialog" max-width="390">
          <v-card>
            <v-card-title class="text-h5">{{$t("pmeflow.ui.abonnement.label.confirmation.resiliation")}}</v-card-title>    
            <v-card-text>
              {{$t("pmeflow.ui.abonnement.msg.confirmation.resiliation")}}
              <v-row>
                <v-col cols="12">
                  <v-textarea rows="4" v-model="motif" :label="$t('pmeflow.ui.abonnement.label.motifResiliation')" class="required" :rules="formValidation.required"/>
                </v-col>
              </v-row>
            </v-card-text>    
            <v-card-actions>
              <v-spacer></v-spacer>    
              <v-btn color="blue darken-1" rounded outlined @click="dialog = false">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>    
              <v-btn color="red darken-1" rounded outlined @click="resilier">{{$t('pmeflow.ui.generic.button.entry.confirmer')}}</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-actions>
    </v-card>
    <v-card class="card-view" v-if="this.abonnement==null">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.abonnement.title.multiple")}}</span>
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
            :items="abonnements"
            :sort-by="['dateDebut']"
            :sort-desc="[true]"
            multi-sort
            @click:row="editerAbonnement($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.dateDebut`]="{ item }">
              <span v-if="item.dateDebut!==null">
              {{new Date(item.dateDebut).toLocaleString("fr-FR").substr(0, 16)}}
              </span>
            </template>
            <template v-slot:[`item.etat`]="{ item }">
              <span style="color:green" v-if="item.etat=='ACTIF'">{{$t("pmeflow.ui.abonnement.statut.actif")}}</span>
              <span style="color:red" v-if="item.etat=='EN_ERREUR'">{{$t("pmeflow.ui.abonnement.statut.en_erreur")}}</span>
              <span style="color:black" v-if="item.etat=='RESILIE'">{{$t("pmeflow.ui.abonnement.statut.resilie")}}</span>
              <span style="color:orange" v-if="item.etat=='A_VERIFIER'">{{$t("pmeflow.ui.abonnement.statut.a_verifier")}}</span>
              <span style="color:orange" v-if="item.etat=='EN_COURS_DE_RESILIATION'">{{$t("pmeflow.ui.abonnement.statut.en_cours_de_resiliation")}}</span>
            </template>
            <template v-slot:[`item.client.nom`]="{ item }">
               {{item.client.prenom}} {{item.client.nom}}
            </template>
        </v-data-table>
      </v-card-text>      
    </v-card>      
  </v-col>
</template>

<script>
import AbonnementService from "@/api/AbonnementService.js";
import { eventBus } from "@/plugins/event-bus.js";
import prestations from '@/components/ui/abonnement/prestations-component.vue';


export default {
  components: {
    prestations,
  },
  data: () => ({
    dialog:false,
    motif:"",
    search:"",
    abonnement:null,
    headersPrestas: [
      { text: "Libellé", value: "libelle", width:"20rem" },
      { text: "Description", value: "description"}
    ],
    headers: [
      { text: "Date", value: "dateDebut", width:"10rem" },
      { text: "Libellé", value: "libelle", width:"20rem" },
      { text: "Description", value: "description" },
      { text: "Client", value: "client.nom", width:"10rem" },
      { text: "Etat", value: "etat", width:"7rem" }
    ],
    abonnements: [],
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
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
    toPrixTTC(prixHT){
      return ((parseFloat(this.$store.state.configuration.parametres.tva)+100)*prixHT)/100
    },
    formatDate(date, extension){
      let calculedDate = new Date(date);
      if (extension != null)
      {
        calculedDate = calculedDate.setMonth(calculedDate.getMonth() + extension);
      }
      return new Date(calculedDate).toISOString().substr(0, 10);
    },
    getColor(state) {
      if (state) return "green";
      else return "red";
    },
    getAbonnementService() {
      return new AbonnementService(
        this.$store.state.configuration.services.abonnementServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getAbonnements();
    },
    ajouter(){
      this.abonnement = Object.assign({}, this.defaultAbonnement);      
      eventBus.$emit("errors-loaded", []);
    },
    fermer(){
      this.abonnement=null;
      eventBus.$emit("errors-loaded", []);
    },
    getAbonnements() {
      this.getAbonnementService().getAbonnements().then(response => {
        this.abonnements = response.data;
        eventBus.$emit("errors-loaded", []);
      });
    },
    editerAbonnement(item) {
      this.editedIndex = this.abonnements.indexOf(item);
      this.abonnement = Object.assign({}, item);
      eventBus.$emit("errors-loaded", []);
    },
    resilier(){
      let errors=[];
      if (this.motif.length>0)
      {
        this.dialog=false;
        this.getAbonnementService().resilierAbonnement(this.abonnement.oid, this.motif)
          .then(response => {
            this.abonnement = response.data;
            if (this.editedIndex > -1) {
              Object.assign(this.abonnements[this.editedIndex], this.abonnement);
            } else {
              this.abonnements.push(this.abonnement);
            }
            this.abonnement=null;
            eventBus.$emit("errors-loaded", []);
          })
          .catch(e => {
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
  }
};
</script>