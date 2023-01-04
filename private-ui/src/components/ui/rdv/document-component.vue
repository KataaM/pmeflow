<template>
  <div>
    <v-card>
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.dossier.document.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col md="12" cols="12">
              <v-text-field v-model="document.libelle" :label="$t('pmeflow.ui.dossier.document.label.libelle')" class="required" :rules="formValidation.required"/>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-text>
        <v-container>
          <v-row v-if="currentFile">
            <v-col md="12" cols="12">
              <v-progress-linear v-model="progress" color="light-blue" height="25" reactive ><strong>{{ progress }} %</strong>
        </v-progress-linear>
            </v-col>
          </v-row>
          <v-row>
            <v-col md="12" cols="12">
              <v-file-input show-size label="File input" @change="selectFile"/>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" rounded outlined @click="annulerDocument">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined @click="saveDocument">{{$t('pmeflow.ui.dossier.document.button.sauver')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>  
  </div>
</template>

<script>
import DocumentService from "@/api/DocumentService.js";
import DossierService from "@/api/DossierService.js";

export default {
  components: {
  },
  props:{
    value: Object,
    profil: Object,
    new:Object,
  },
  data: () => ({ 
    currentFile: undefined,
    progress: 0,
    message: "",
    fileInfos: [],
    document:null,
    msg:{
      type:"info",
      text:"",
      presence:false
    }
  }),
  mounted(){
    this.dossierId = this.value;
    this.document = Object.assign({}, this.defaultDocument);
  },
  watch: { 
    value: function(newVal) {
      this.dossierId=newVal;
    }, 
    new: function(newVal) {
      if (newVal){
        this.document = Object.assign({}, this.defaultDocument);
      }
    }, 
  }, 
  computed:{
    defaultDocument(){
      return {
        oid: (new Date()).getTime()+"",
        dateDepot: Date.now(),
        libelle:"",
        mimetype : "",
        profil : this.profil
      };
    },
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
    id(){
      return this.getDocumentService().getIdForDocument();
    }

  },
  methods: {
    getDocumentService() {
      return new DocumentService(
        this.$store.state.configuration.services.documentServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getDossierService() {
      return new DossierService(
        this.$store.state.configuration.services.dossierServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },

    selectFile(file) {
      this.progress = 0;
      this.currentFile = file;
    },
    async upload(id) {
      if (!this.currentFile) {
        this.msg = {type:"error", text:"Merci de choisir un fichier", presence:true};
        return;
      }
      this.message = {type:"info", text:"", presence:false};
      this.getDocumentService()
        .mergeDocumentStorage(id, this.currentFile, (event) => {
          this.progress = Math.round((100 * event.loaded) / event.total)-5;
        })
        .then(() => {
          this.addDocumentToDossier(this.dossierId, this.document);
          return;
        })
        .catch(() => {
          this.progress = 0;
          this.message = {type:"error", text:"Could not upload the file!", presence:true};
        });
    },
    annulerDocument(){
      this.document=null;
      this.$emit("close");
    },
    async saveDocument() {
      this.document.oid= await this.getDocumentService().getIdForDocument();
      await this.upload(this.document.oid);
    },
    addDocumentToDossier(dossierId, document) {
      this.progress = 96.5;
      this.getDossierService().addDocumentToDossier(dossierId, document)        
        .then(() => {
          this.$emit('close');
          this.progress = 100;
          this.document=null;
        })
        .catch(() => {
          this.progress = 0;
          this.message = {type:"error", text:"Impossible d'attacher le document au dossier", presence:true};
        });
    },
  }
};
</script>