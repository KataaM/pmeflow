<template>
  <v-dialog v-model="dialog" max-width="500px" v-if="client!=null">
    <v-card flat>
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.client.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
          <v-container>
              <v-row>
                  <v-col md="6" cols="12">
                  <v-text-field v-model="client.nom" :label="$t('pmeflow.ui.generic.label.nom')" class="required" :rules="formValidation.required"/>
                  </v-col>
                  <v-col md="6" cols="12">
                  <v-text-field v-model="client.prenom" :label="$t('pmeflow.ui.generic.label.prenom')" class="required" :rules="formValidation.required"/>
                  </v-col>
              </v-row>
              <v-row>
                  <v-col md="12" cols="12">
                  <v-textarea rows="2" v-model="client.adresse" :label="$t('pmeflow.ui.generic.label.adresse')"></v-textarea>
                  </v-col>
              </v-row>
              <v-row>
                  <v-col cols="12" md="6">
                  <v-text-field v-model="client.codePostal" :label="$t('pmeflow.ui.generic.label.codePostal')"></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                  <v-text-field v-model="client.ville" :label="$t('pmeflow.ui.generic.label.ville')"></v-text-field>
                  </v-col>
              </v-row>
              <v-row>
                  <v-col cols="12" md="6">
                      <v-text-field v-model="client.email" :label="$t('pmeflow.ui.generic.label.email')"  class="required" :rules="formValidation.emailRules"/>
                  </v-col>
                  <v-col cols="12" md="6">
                      <v-text-field v-model="client.telephone" :label="$t('pmeflow.ui.generic.label.telephone')"></v-text-field>
                  </v-col>
              </v-row>
              <v-row>
                  <v-col cols="12" md="6">
                      <v-select :items="modesPriseContact" v-model="client.priseContact" :label="$t('pmeflow.ui.client.label.priseContactPreferee')"  class="required"/>
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
</template>

<script>
import ClientService from "@/api/ClientService.js";

export default {
  props: {
    value: Object,
    clients: Array,
  },
  data: () => ({
    errors:[],
    dialog:false,
    modesPriseContact : ['MAIL', 'TEL', 'NO_PREFER'],    
    client: null,
  }),
  computed: {     
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
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
      return this.editedIndex === -1 ? "Création d'un client" : "Modification d'un client";
    },
  },
  watch: { 
    errors: function(newVal){
      this.errors = newVal;
      this.$emit('update:errors', newVal);
    },
    value: function(newVal) {
      this.client=newVal;
    },    
    client: function(newVal) {
      if (newVal==null){
        this.dialog=false;
      }
      else{
        this.dialog=true;
      }
      this.client=newVal;
    },
  },
  methods: {
    index(items, item) {
      let idx = -1
      for (let index = 0; index < items.length; index++) {
        if (items[index].email === item.email)
        {
          idx=index;
        }
      }
      return idx;
    },
    getClientService() {
      return new ClientService(
        this.$store.state.configuration.services.clientServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    close() {
      this.client = null;
      this.errors = [];
    },
    save() {
      if (this.client.priseContact=='TEL' && (this.client.telephone==null || this.client.telephone=="")) {
        this.errors.push({
          "type" : "error",
          "msg" :  this.$t("pmeflow.ui.client.message.saisirTelephone")
        });
        return;
      }
      this.getClientService()
        .mergeClient(this.client)
          .then(response => {
            Object.assign(this.client, response.data);
            this.$emit('input', this.client);
            let clientsTmp = [];
            Object.assign(clientsTmp, this.clients);

            if (this.index(clientsTmp, this.client) > -1) {
              Object.assign(clientsTmp[this.index(clientsTmp, this.client)], this.client);
            } 
            else {
              clientsTmp.push(this.client);
            }
            this.$emit('update:clients', clientsTmp);
            this.close();
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