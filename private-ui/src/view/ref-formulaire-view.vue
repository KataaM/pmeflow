<template>
  <v-col col="12" sm="12" md="12">
    <v-card v-if="this.formulaire!=null" class="card-view">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-formulaire.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-tabs vertical>
          <v-tab>{{$t("pmeflow.ui.ref-formulaire.tab.general")}}</v-tab>
          <v-tab active-class=true>{{$t("pmeflow.ui.ref-formulaire.tab.steps")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.ref-formulaire.tab.general")}}</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-text-field v-model="formulaire.libelle" :label="$t('pmeflow.ui.generic.label.libelle')"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="12" cols="12">
                      <v-textarea class="required" rows="2" v-model="formulaire.description" :label="$t('pmeflow.ui.generic.label.description')"/>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
            </v-card>
          </v-tab-item>
          <v-tab-item active-class>
            <ref-formulaire-step v-model="formulaire.steps" v-on:input="view"/>
          </v-tab-item>
        </v-tabs>  
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>                            
        <v-btn color="red darken-1" rounded outlined @click="annuler">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
        <v-btn color="primary" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-if="this.formulaire==null" class="card-view">
      <v-card-title>
        <v-row>
          <v-col cols="12" md="4" sm="12">
          <span class="headline">{{$t("pmeflow.ui.ref-formulaire.title.multiple")}}</span>
          </v-col>
          <v-col cols="12" md="3" sm="12">
            <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
          </v-col>
          <v-col cols="12" md="5"  sm="12" style="text-align:right">
            <v-btn color="primary" rounded outlined class="mr-2" @click="ajouter">{{$t("pmeflow.ui.ref-formulaire.button.ajouter")}}</v-btn>
          </v-col>
        </v-row>
      </v-card-title>

      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="formulaires"
            :sort-by="['nom']"
            :sort-desc="[false]"
            multi-sort
            @click:row="editerFormulaire($event)">            
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
        </v-data-table>
      </v-card-text>      
    </v-card>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import RefFormulaireService from "@/api/RefFormulaireService.js";
import RefFormulaireStep from "@/components/ui/ref-formulaire/ref-formulaire-step";

export default {
  components: {
    RefFormulaireStep
  },
  data: () => ({
    selectedPrestation:null,
    search:"",
    defaultFormulaire: {
      oid: "",
      libelle: "",
      description: "",
      steps: []
    },
    formulaire:null,
    headers: [
      { text: "Libellé", value: "libelle", width:"25%" },
      { text: "Description", value: "description" },
    ],
    formulaires: [],
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  computed:{
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
    externalUri(uri){
      return process.env.VUE_APP_PUBLIC_FORMULAIRE_URI+"-"+uri;
    },
    getRefFormulaireService() {
      return new RefFormulaireService(
        this.$store.state.configuration.services.refFormulaireServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getFormulaires();
    },
    ajouter(){
      this.formulaire=this.defaultFormulaire;
    },
    annuler(){
      this.formulaire=null;
    },
    getFormulaires() {
      this.getRefFormulaireService().getFormulaires().then(response => {
        this.formulaires = response.data;
      });
    },
    editerFormulaire(item) {
      this.editedIndex = this.formulaires.indexOf(item);
      this.formulaire = Object.assign({}, item);
    },
    save() {
      this.getRefFormulaireService().mergeFormulaire(this.formulaire)
        .then(response => {
          this.formulaire = response.data;
          if (this.editedIndex > -1) {
            Object.assign(this.formulaires[this.editedIndex], this.formulaire);
          } else {
            this.formulaires.push(this.formulaire);
          }
          eventBus.$emit("errors-loaded", []);
          this.formulaire=null;
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