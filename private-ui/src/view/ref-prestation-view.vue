<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-prestation.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-text-field v-model="search" append-icon="mdi-magnify" label="Search" single-line hide-details></v-text-field>
        <v-spacer></v-spacer>
        <v-dialog v-model="dialog" :width="dialogSize.width" :height="dialogSize.height">
            <template v-slot:activator="{ on }">
                <v-btn color="primary" rounded outlined class="mb-2" v-on="on">{{$t("pmeflow.ui.ref-prestation.button.ajouter")}}</v-btn>
            </template>
            <v-card flat>
                <v-card-title>
                  <span class="headline">{{$t("pmeflow.ui.ref-prestation.title.simple")}}</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-row>
                            <v-col md="6" cols="12">
                              <v-text-field class="required" v-model="editedPrestation.code" :label="$t('pmeflow.ui.generic.label.code')" :rules="formValidation.required"/>
                            </v-col>
                            <v-col md="6" cols="12">
                              <v-text-field class="required" v-model="editedPrestation.libelle" :label="$t('pmeflow.ui.generic.label.libelle')" :rules="formValidation.required"/>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col md="12" cols="12">
                              <v-textarea class="required" rows="2" v-model="editedPrestation.description" :label="$t('pmeflow.ui.generic.label.description')" :rules="formValidation.required"/>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col md="6" cols="12">
                              <v-text-field class="required" v-model="editedPrestation.creditsRdv" :label="$t('pmeflow.ui.ref-prestation.label.creditsRdv')" :rules="formValidation.entierRules"/>
                            </v-col>
                            <v-col md="6" cols="12">
                              <v-text-field class="required" v-model="editedPrestation.creditsDossier" :label="$t('pmeflow.ui.ref-prestation.label.creditsDossier')" :rules="formValidation.entierRules"/>
                            </v-col>
                        </v-row>                        
                        <v-row>
                            <v-col cols="12" md="6">
                              <v-text-field class="required" v-model="editedPrestation.prixHT" :label="$t('pmeflow.ui.generic.label.prixHT')" :rules="formValidation.prixRules">
                                <span slot="append">€</span>                                
                              </v-text-field>
                            </v-col>
                            <v-col cols="12" md="6">
                              <v-switch v-model="editedPrestation.etat" :label="$t('pmeflow.ui.generic.label.etat')"></v-switch>
                            </v-col>
                        </v-row>
                        <v-row v-if="editedPrestation!=null && editedPrestation.oid!=null">
                          <v-col cols="12" md="12">
                            <v-text-field :value="toExternalUri(editedPrestation.oid)" readonly :label="$t('pmeflow.ui.ref-abonnement.label.urlPublique')"/>
                          </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="red darken-1" rounded outlined @click="close">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
                    <v-btn color="blue darken-1" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
      </v-card-title>

      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="prestations"
            :sort-by="['duree', 'nom']"
            :sort-desc="[false, false]"
            multi-sort
            @click:row="editPrestation($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.etat`]="{ item }">
              <v-chip :color="getColor(item.etat)" dark>&nbsp;</v-chip>
            </template>
            <template v-slot:[`item.prixHT`]="{ item }">
              {{item.prixHT}} €
            </template>
            <template v-slot:[`item.validiteCreditRdv`]="{ item }">
              {{item.validiteCreditRdv}} jours
            </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import RefPrestationService from "@/api/RefPrestationService.js";

export default {
  components: {
  },
  data: () => ({
    search:"",
    info: null,
    loading: true,
    errored: false,
    dialog: false,
    headers: [
      { text: "Code", value: "code", width:"15%" },
      { text: "Libellé", value: "libelle"},
      { text: "Prix H.T.", value: "prixHT", width:"10%" },
      { text: "Etat", value: "etat", width:"5%" },
    ],
    prestations: [],
    editedIndex: -1,
    editedPrestation: {
      oid: "",
      code: "",
      libelle:"",
      description: "",
      prixHT: 0.0,
      creditsRdv: 0,
      creditsDossier: 0,
      etat:true,
      stripeProductId:""
    },
    default: {
      oid: "",
      code: "",
      libelle:"",
      description: "",
      prixHT: 0.0,
      creditsRdv: 0,
      creditsDossier: 0,
      etat:true,
      stripeProductId:""
    }
  }),
  watch: {
    dialog(val) {
      val || this.close();
    }
  },
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  computed:{
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
        prixRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^(?:\d+(?:\.\d*)?|\.\d+)$/
            return pattern.test(value) || this.$t("pmeflow.ui.ref-abonnement.message.mauvaisFormatPrix");
          },
        ],
        entierRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^[0-9]\d*$/
            return pattern.test(value) || this.$t("pmeflow.ui.generic.message.mauvaisFormatEntier");
          },
        ],
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
    toExternalUri(uri){
      return process.env.VUE_APP_PUBLIC_PRESTATION_URI+"-"+uri;
    },
    getRefPrestationService() {
      return new RefPrestationService(
        this.$store.state.configuration.services.refPrestationServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getPrestations();
    },
    getMessage(key) {
      console.log(this.$t(key));
      return this.$t(key);
    },
    getColor(state) {
      if (state) return "green";
      else return "red";
    },
    getPrestations() {
      this.getRefPrestationService().getPrestations().then(response => {
        this.prestations = response.data;
      });
    },
    editPrestation(item) {
      this.editedIndex = this.prestations.indexOf(item);
      this.editedPrestation = Object.assign({}, item);
      this.dialog = true;
    },
    close() {
      eventBus.$emit("errors-loaded", []);
      this.dialog = false;
      setTimeout(() => {
        this.editedPrestation = Object.assign({}, this.default);
        this.editedIndex = -1;
      }, 300);
    },
    save() {
      this.getRefPrestationService().mergePrestation(this.editedPrestation)
        .then(response => {
          this.editedPrestation = response.data;
          if (this.editedIndex > -1) {
            Object.assign(this.prestations[this.editedIndex], this.editedPrestation);
          } else {
            this.prestations.push(this.editedPrestation);
          }
          eventBus.$emit("errors-loaded", []);
          this.close();
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