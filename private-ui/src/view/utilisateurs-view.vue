<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.utilisateur.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
        <v-spacer></v-spacer>
        <v-dialog v-model="dialog" max-width="500px">
            <template v-slot:activator="{ on }">
                <v-btn color="primary" rounded outlined class="mb-2" v-on="on">{{$t("pmeflow.ui.utilisateur.button.ajouter")}}</v-btn>
            </template>
            <v-card flat>
              <v-card-title>
                <span class="headline">{{$t("pmeflow.ui.utilisateur.title.simple")}}</span>
              </v-card-title>
              <v-card-text>
                  <v-container>
                      <v-row>
                          <v-col md="6" cols="12">
                          <v-text-field v-model="editedUtilisateur.nom" :label="$t('pmeflow.ui.generic.label.nom')" class="required" :rules="formValidation.required"/>
                          </v-col>
                          <v-col md="6" cols="12">
                          <v-text-field v-model="editedUtilisateur.prenom" :label="$t('pmeflow.ui.generic.label.prenom')" class="required" :rules="formValidation.required"/>
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col md="12" cols="12">
                          <v-textarea rows="2" v-model="editedUtilisateur.adresse" :label="$t('pmeflow.ui.generic.label.adresse')"></v-textarea>
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col cols="12" md="6">
                          <v-text-field v-model="editedUtilisateur.codePostal" :label="$t('pmeflow.ui.generic.label.codePostal')"></v-text-field>
                          </v-col>
                          <v-col cols="12" md="6">
                          <v-text-field v-model="editedUtilisateur.ville" :label="$t('pmeflow.ui.generic.label.ville')"></v-text-field>
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col cols="12" md="6">
                              <v-text-field v-model="editedUtilisateur.email" :label="$t('pmeflow.ui.generic.label.email')"  class="required" :rules="formValidation.emailRules"/>
                          </v-col>
                          <v-col cols="12" md="6">
                              <v-text-field v-model="editedUtilisateur.telephone" :label="$t('pmeflow.ui.generic.label.telephone')"></v-text-field>
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col cols="12" md="6">
                              <v-select :items="modesPriseContact" v-model="editedUtilisateur.priseContact" :label="$t('pmeflow.ui.utilisateur.label.priseContactPreferee')"  class="required"/>
                          </v-col>
                          <v-col cols="12" md="6">
                              <v-select :items="profils" v-model="editedUtilisateur.profils" :label="$t('pmeflow.ui.utilisateur.label.profils')" multiple class="required"/>
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
            :items="utilisateurs"
            :sort-by="['nom', 'prenom']"
            :sort-desc="[false, false]"
            multi-sort
            @click:row="editUtilisateur($event)">
            <template v-slot:[`item.color`]="{ item }">
            <div :style="swatchStyle(item)" v-on="on" />
            </template>
            <template v-slot:[`item.open`]="{ item }">
            <v-chip :color="getColor(item.open)" dark>&nbsp;</v-chip>
            </template>
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
import UtilisateurService from "@/api/UtilisateurService.js";

export default {
  components: {
  },
  data: () => ({
    modesPriseContact : ['MAIL', 'TEL', 'NO_PREFER'],
    profils: ['JURISTE','ADMIN_FONCTIONNEL','ADMIN'],
    search:"",
    info: null,
    loading: true,
    errored: false,
    dialog: false,
    headers: [
      { text: "Nom", value: "nom", width:"20%" },
      { text: "Prénom", value: "prenom", width:"25%" },
      { text: "Email", value: "email", width:"25%" },
      { text: "Téléphone", value: "telephone" },
      { text: "Profils", value: "profils" }
    ],
    utilisateurs: [],
    editedIndex: -1,
    editedUtilisateur: {
      oid: "",
      nom: "",
      prenom: "",
      adresse: "",
      codePostal: "",
      ville: "",
      email: "",
      telephone:"",
      priseContact:"TEL",
      profils:[]
    },
    default: {
      oid: "",
      nom: "",
      prenom: "",
      adresse: "",
      codePostal: "",
      ville: "",
      email: "",
      telephone:"",
      priseContact:"TEL",
      profils:[]
    },
  }),
  computed: {    
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
        entierRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^[0-9]\d*$/
            return pattern.test(value) || this.$t("pmeflow.ui.generic.message.mauvaisFormatEntier");
          },
        ],
        emailRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            return pattern.test(value) || this.$t("pmeflow.ui.generic.message.mauvaisFormatMail");
          },
        ]
      }
    },  
    formTitle() {
      return this.editedIndex === -1 ? "Création d'un Utilisateur" : "Modification d'un Utilisateur";
    },
  },
  watch: {
    dialog(val) {
      val || this.close();
    }
  },
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  methods: {
    getUtilisateurService() {
      return new UtilisateurService(
        this.$store.state.configuration.services.utilisateurServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getUtilisateurs();
    },
    getUtilisateurs() {
      this.getUtilisateurService().getUtilisateurs().then(data => {
        this.utilisateurs = data;
      });
    },
    editUtilisateur(item) {
      this.editedIndex = this.utilisateurs.indexOf(item);
      this.editedUtilisateur = Object.assign({}, item);
      this.dialog = true;
    },
    close() {
      this.dialog = false;
      setTimeout(() => {
        this.editedUtilisateur = Object.assign({}, this.default);
        this.editedIndex = -1;
      }, 300);
    },
    save() {
      let errors=[];
      if (this.editedUtilisateur.priseContact==='TEL' && this.editedUtilisateur.telephone==null) {
        errors.push({
          "type" : "error",
          "msg" :  this.$t("pmeflow.ui.utilisateur.message.saisirTelephone")
        });
        eventBus.$emit("errors-loaded", errors);
        return;
      }
      this.getUtilisateurService()
        .mergeUtilisateur(this.editedUtilisateur)
          .then(response => {
            this.editedUtilisateur = response.data;
            this.getUtilisateurs();
            eventBus.$emit("errors-loaded", []);
            this.close();
          })
          .catch(e => {
            errors=[];
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