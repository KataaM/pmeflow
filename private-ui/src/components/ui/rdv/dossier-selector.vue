<template>
  <div>
    <v-card>
      <v-card-title v-if="noTitle=='false'">
        <span class="headline">{{$t("pmeflow.ui.rdv.title.dossier")}}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row v-if="noTitle=='false'">
            <v-col md="12" cols="12">
              {{$t("pmeflow.ui.dossier.message.choixDossier")}}
            </v-col>
          </v-row>
          <v-row>
            <v-col md="12" cols="12">
              Merci de faire votre choix.
              <v-radio-group v-model="option">
                <v-radio :label="$t('pmeflow.ui.rdv.label.noDossier')" value="no"/>
                <v-radio :label="$t('pmeflow.ui.rdv.label.createDossier')" value="create"/>
                <v-radio :label="$t('pmeflow.ui.rdv.label.selectDossier')" value="select"/>
              </v-radio-group>
            </v-col>
          </v-row>
          <v-row v-if="option=='create'">
            <v-col md="12" cols="12">
                <v-col md="12" cols="12">
                  <v-text-field v-model="libelle" :label="$t('pmeflow.ui.generic.label.libelle')" class="required" :rules="formValidation.required"/>
                </v-col>
                <v-col md="12" cols="12">
                  <v-textarea v-model="description" :label="$t('pmeflow.ui.generic.label.description')" class="required" rows="2" :rules="formValidation.required"/>
                </v-col>
            </v-col>
          </v-row>
          <v-row v-if="option=='select'">
              <v-col cols="12" md="12">
                <v-select return-object v-model="dossier" :items="dossiersActifs" item-text="libelle" item-value=""  menu-props="auto" 
                  :label="$t('pmeflow.ui.rdv.label.choisirDossier')" hide-details prepend-icon="mdi-book-open" single-line >
                </v-select>
              </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
          <v-spacer v-if="noTitle=='false'"></v-spacer>
          <v-btn color="red darken-1" rounded outlined @click="annuler" v-if="noTitle=='false'">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined @click="saveLink">{{$t('pmeflow.ui.rdv.button.lier')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>  
  </div>
</template>

<script>
import DossierService from "@/api/DossierService.js";

export default {
  components: {
  },
  props:{
    noTitle: {
      type: String,
      default: "false"
    },
    value: String,
    client: Object,
    juriste: Object,
  },
  data: () => ({ 
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
      etat: "nouveau"
    },
    dossierId:"",
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    option:"no",
    libelle:"",
    description:"",
    dossier:null
  }),
  mounted(){
    this.dossierId = this.value;
  },
  watch: { 
    value: function(newVal) {
      this.dossierId=newVal;
    }, 
  }, 
  asyncComputed: {
    dossiersActifs: {
      get() {
        return this.getDossiersActifs().then(response=>response.data);
      },
      default: []
    },
  },
  computed:{
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
  },
  methods: {
    getDossierService() {
      return new DossierService(
        this.$store.state.configuration.services.dossierServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getDossiersActifs(){
      return this.getDossierService().getDossiersActifs();
    },
    annuler(){
      this.$emit("annuler");
    },
    saveLink(){
      if (this.option=="create" && this.libelle!="" && this.description!="")
      {
        this.dossier=Object.assign({}, this.defaultDossier);
        this.dossier.client=this.client;
        this.dossier.juriste=this.juriste;
        this.dossier.libelle=this.libelle;
        this.dossier.description=this.description;
        this.getDossierService().mergeDossier(this.dossier) 
          .then(response => {
            this.dossier = response.data;
            this.dossierId = this.dossier.oid;
            this.$emit("input", this.dossierId);
            this.$emit('update:dossier', this.dossier);
            this.$emit("close");
          });
      }
      if (this.option=='select'){
        this.dossierId = this.dossier.oid;
        this.$emit("input", this.dossierId);
        this.$emit('update:dossier', this.dossier);
        this.$emit("close");
      }
      if (this.option=='no'){
        this.$emit("close");
      }    
    },
  }
};
</script>