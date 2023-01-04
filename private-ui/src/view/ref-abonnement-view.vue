<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view" v-if="this.abonnement!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-abonnement.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-tabs vertical>
          <v-tab>{{$t("pmeflow.ui.ref-abonnement.tab.general")}}</v-tab>
          <v-tab>{{$t("pmeflow.ui.ref-abonnement.tab.prestations")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.ref-abonnement.tab.general")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="abonnement.libelle" :label="$t('pmeflow.ui.generic.label.libelle')" class="required" :rules="formValidation.required"/>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="abonnement.dureeEngagement" :label="$t('pmeflow.ui.ref-abonnement.label.dureeEngagement')" class="required" :rules="formValidation.entierRules">
                      <span slot="append">mois</span>
                    </v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="abonnement.prixHT" :label="$t('pmeflow.ui.generic.label.prixHTMensuel')" class="required" :rules="formValidation.prixRules">
                        <span slot="append">€</span>
                      </v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-textarea v-model="abonnement.description" :label="$t('pmeflow.ui.generic.label.description')" class="required" rows="2" :rules="formValidation.required"/>
                    </v-col>
                  </v-row>
                  <v-row v-if="abonnement!=null && abonnement.oid!=null">
                    <v-col cols="12" md="12">
                      <v-text-field :value="toExternalUri(abonnement.oid)" readonly :label="$t('pmeflow.ui.ref-abonnement.label.urlPublique')"/>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
            </v-card>
          </v-tab-item>
          <v-tab-item>            
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.ref-abonnement.tab.prestations")}}</span>
                <v-spacer/>
                <v-btn color="primary" rounded outlined @click="dialogPresta=true">{{$t("pmeflow.ui.ref-prestation.button.ajouter")}}</v-btn>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-dialog v-model="dialogPresta" :width="dialogSize.width" :height="dialogSize.height">
                    <v-card flat>
                        <v-card-title>
                          <span class="headline">{{$t("pmeflow.ui.ref-abonnement.title.prestation")}}</span>
                        </v-card-title>
                        <v-card-text>
                            <v-container>                     
                                <v-row>
                                    <v-col cols="12" md="12">
                                      <v-select return-object v-model="selectedPrestation" :items="prestationsActives" item-text="libelle" item-value=""  menu-props="auto" 
                                        :label="$t('pmeflow.ui.ref-abonnement.label.choisirPrestation')" hide-details prepend-icon="mdi-book-open" single-line >
                                      </v-select>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="red darken-1" rounded outlined @click="dialogPresta=false">{{$t('pmeflow.ui.generic.button.entry.fermer')}}</v-btn>
                            <v-btn color="primary" rounded outlined @click="ajouterPrestation(selectedPrestation)">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
                        </v-card-actions>
                    </v-card>
                  </v-dialog>
                  <v-data-table
                    :headers="headersPresta"
                    :items="abonnement.prestationsIncluses"
                    :sort-by="['code']"
                    :sort-desc="[false]"
                    multi-sort>            
                    <template v-slot:no-data>
                      <v-btn color="primary" @click="initialize">Reset</v-btn>
                    </template>            
                    <template v-slot:[`item.cout`]="{ item }">
                      {{item.cout}} €
                    </template>
                    <template v-slot:[`item.action`]="{ item }">
                      <v-btn icon color="pink" @click="supprimerPrestation(item.code)"><v-icon>mdi-delete</v-icon></v-btn>
                    </template>
                  </v-data-table>
                </v-container>
              </v-card-text>
            </v-card>
          </v-tab-item>
        </v-tabs>  
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>                            
        <v-btn color="red darken-1" rounded outlined @click="annuler">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
        <v-btn color="primary" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card class="card-view" v-if="this.abonnement==null">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.ref-abonnement.title.multiple")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
          <v-col cols="12" md="5"  sm="12" style="text-align:right">
            <v-btn color="primary" rounded outlined class="mr-2" @click="ajouter">{{$t("pmeflow.ui.ref-abonnement.button.ajouter")}}</v-btn>
          </v-col>
        </v-row>
      </v-card-title>

      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="abonnements"
            :sort-by="['nom']"
            :sort-desc="[false]"
            multi-sort
            @click:row="editerAbonnement($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
            <template v-slot:[`item.dureeEngagement`]="{ item }">
              {{item.dureeEngagement}} mois
            </template>
            <template v-slot:[`item.prixHT`]="{ item }">
              {{item.prixHT}}€
            </template>
            <template v-slot:[`item.url`]="{ item }">
              {{toExternalUri(item.oid)}}
            </template>
        </v-data-table>
      </v-card-text>      
    </v-card>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import RefAbonnementService from "@/api/RefAbonnementService.js";
import RefPrestationService from "@/api/RefPrestationService.js";

export default {
  components: {
  },
  data: () => ({
    selectedPrestation:null,
    dialogPresta:false,
    search:"",
    defaultAbonnement: {
      oid: "",
      libelle: "",
      description: "",
      prixHT : 0.0,
      dureeEngagement: 0,
      planId: "",
      etat: true,
      prestationsIncluses: [],
      formulaire : null
    },
    abonnement:null,
    headers: [
      { text: "Libellé", value: "libelle", width:"25%" },
      { text: "Description", value: "description", width:"45%" },
      { text: "Durée d'engagement", value: "dureeEngagement" },
      { text: "Prix H.T. / Mois", value: "prixHT" }
    ],
    headersPresta: [
      { text: "Code", value: "code" },
      { text: "Libellé", value: "libelle"},
      { text: "Description", value: "description" },
      { text: "Prix H.T.", value: "prixHT" },
      { text: "Action", value: "action" },
    ],
    abonnements: [],
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  asyncComputed: {
    prestationsActives: {
      get() {
        return this.getPrestationsActives().then(response=>response.data);
      },
      default: []
    },
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
      let width = `${window.innerWidth*0.6}px`;
      let height = `${window.innerHeight*0.6}px`;
      return {
        "width":width,
        "height":height
      };
    }
  },
  methods: {
    toExternalUri(uri){
      return process.env.VUE_APP_PUBLIC_ABONNEMENT_URI+"-"+uri;
    },
    getRefAbonnementService() {
      return new RefAbonnementService(
        this.$store.state.configuration.services.refAbonnementServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getRefPrestationService() {
      return new RefPrestationService(
        this.$store.state.configuration.services.refPrestationServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getPrestationsActives(){
      return this.getRefPrestationService().getPrestations();
    },
    initialize() {
      this.getAbonnements();
    },
    ajouter(){
      this.abonnement=this.defaultAbonnement;
    },
    annuler(){
      this.abonnement=null;
    },
    supprimerPrestation(codePrestation){
      let filtered = [];
      this.abonnement.prestationsIncluses.forEach(prestation => {
        if (prestation.code !== codePrestation){
          filtered.push(prestation);
        }
      });
      this.abonnement.prestations = filtered;
    },
    ajouterPrestation(addedPrestation){
      let filtered = [];
      this.abonnement.prestationsIncluses.forEach(prestation => {
        if (prestation.code !== addedPrestation.code){
          filtered.push(prestation);
        }
      });
      filtered.push(addedPrestation);
      this.abonnement.prestationsIncluses = filtered;
      this.dialogPresta=false;
    },
    getAbonnements() {
      this.getRefAbonnementService().getAbonnements().then(response => {
        this.abonnements = response.data;
      });
    },
    editerAbonnement(item) {
      this.editedIndex = this.abonnements.indexOf(item);
      this.abonnement = Object.assign({}, item);
    },
    save() {
      if (this.abonnement.formulaire===""){
        this.abonnement.formulaire=null;
      }
      this.getRefAbonnementService().mergeAbonnement(this.abonnement)
        .then(response => {
          this.abonnement = response.data;
          if (this.editedIndex > -1) {
            Object.assign(this.abonnements[this.editedIndex], this.abonnement);
          } else {
            this.abonnements.push(this.abonnement);
          }
          eventBus.$emit("errors-loaded", []);
          this.abonnement=null;
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