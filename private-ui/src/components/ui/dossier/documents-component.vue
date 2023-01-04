<template>
  <div>
    <v-card v-if="this.document!=null">
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
    <v-card v-if="this.document==null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.dossier.document.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-btn style="float:right" color="blue darken-1" rounded outlined @click="ajouterDocument">{{$t('pmeflow.ui.dossier.document.button.ajouter')}}</v-btn>
      </v-card-title>
      <v-card-text>
        <v-data-table :headers="headers" :items="documents"
            :sort-by="['date']" :sort-desc="true" multi-sort>
            <template v-slot:[`item.dateDepot`]="{ item }">
              {{new Date(item.dateDepot).toLocaleString("fr-FR").substr(0, 16)}}
            </template>
            <template v-slot:[`item.actions`]="{ item }">
              <a v-bind:href="docUri(item.oid)" target="_blank">
                <v-btn class="mr-2" color="primary" icon><v-icon>mdi-eye-arrow-right</v-icon></v-btn>
              </a>
            </template>
            <template v-slot:[`item.profil`]="{ item }">
              {{item.profil.nom}} {{item.profil.prenom}}
            </template>
        </v-data-table>
      </v-card-text>
    </v-card>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>  
  </div>
</template>

<script>
import DocumentService from "@/api/DocumentService.js";

export default {
  components: {
  },
  props:{
    value: Array,
    profil: Object
  },
  data: () => ({ 
    currentFile: undefined,
    progress: 0,
    message: "",
    fileInfos: [],

    document:null,
    headers: [
      { text: "Date", value: "dateDepot" },
      { text: "Libellé", value: "libelle" },
      { text: "Mimetype", value: "mimetype" },
      { text: "Transmis par", value: "profil" },
      { text: "Actions", value: "actions" },
    ],
    documents: [],
    msg:{
      type:"info",
      text:"",
      presence:false
    }
  }),
  mounted(){
    this.documents = this.value;
  },
  watch: { 
    value: function(newVal) {
      this.documents=newVal;
    }, 
  }, 
  computed:{
    defaultDocument(){
      return {
        oid: (new Date()).getTime()+"",
        dateDepot: Date.now(),
        libelle:"",
        mimetype : "",
        profil : this.profil,
        key: null,
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
    docUri(oid){
      return this.$store.state.configuration.services.documentServiceUri+"/FR_FR/"+oid+"?access_token="+sessionStorage.getItem("oidc-access_token");
    },
    getDocumentService() {
      return new DocumentService(
        this.$store.state.configuration.services.documentServiceUri,
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
          this.progress = Math.round((100 * event.loaded) / event.total);
        })
        .then(() => {
          return;
        })
        .catch(() => {
          this.progress = 0;
          this.message = {type:"error", text:"Could not upload the file!", presence:true};
        });
      return;
    },
    ajouterDocument(){
      this.document=this.defaultDocument;
    },
    annulerDocument(){
      this.document=null;
    },
    async saveDocument() {
      this.document.oid= await this.getDocumentService().getIdForDocument();
      await this.upload(this.document.oid);
      this.documents.push(this.document);
      this.$emit('input', this.documents);
      this.document=null;
    },
  }
};
</script>