<template>
  <v-row>
    <v-col col="2" sm="12" md="2">
      <menu-configuration :roles="roles"/>
    </v-col>
    <v-col col="10" sm="12" md="10" style="background-color:lightgray">
      <v-card flat style="margin:20px;">
        <v-card-title>
          <v-row>
            <v-col cols="12" md="4" sm="12">
            <span class="headline">{{$t("pmeflow.ui.lieu.title.multiple")}}</span>
            </v-col>
            <v-col cols="12" md="3" sm="12">
              <v-text-field v-model="search" append-icon="mdi-magnify" 
                :label="$t('pmeflow.ui.generic.label.search')"
                single-line hide-details></v-text-field>
            </v-col>
            <v-col cols="12" md="5"  sm="12" style="text-align:right">
              <v-btn color="primary" rounded outlined class="mr-2" @click="addLieu">{{$t("pmeflow.ui.lieu.button.ajouter")}}</v-btn>
            </v-col>
          </v-row>
        </v-card-title>
        <v-card-subtitle class="mt-2">
          {{$t('pmeflow.ui.lieu.message.subtitleConfig')}}
        </v-card-subtitle>
        <v-card-text>
          <v-data-table
              :headers="lieuxHeaders"
              :search="search"
              :items="lieux"
              :sort-by="['nom']"
              :sort-desc="[false]"
              multi-sort
              @click:row="editLieu($event)">
          </v-data-table>
        </v-card-text>
      </v-card>
      <lieu-sidebar v-if="lieu!=null" v-model="lieu" v-on:input="loadLieux"/>
    </v-col>
  </v-row>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import menuConfiguration from "@/components/menu/configuration"
import lieuSidebar from "@/components/ui/lieu/lieu-sidebar.vue";

export default {
  components: {
    menuConfiguration,
    lieuSidebar
  },

  data: () => ({  
    defaultLieu:{
     oid:null,
     nom:"",
     adresse:"",
     codePostal:"",
     ville:"",
     pays:{code:"", nom:""},
     telephone:"",
     fax:"",
     email:"",
     site:"",
     notes:""
    },
    lieu:null,    
    search:"",
    viewSidebar:false,
  }),

  computed:{    
    lieuxHeaders(){
      return [
        { text: this.$t('pmeflow.ui.generic.label.nom'), value: "nom", width:"20%" },
        { text: this.$t('pmeflow.ui.generic.label.adresse'), value: "adresse", width:"25%" },
        { text: this.$t('pmeflow.ui.generic.label.ville'), value: "ville", width:"25%" },
        { text: this.$t('pmeflow.ui.generic.label.telephone'), value: "telephone" },
        { text: this.$t('pmeflow.ui.generic.label.email'), value: "email" }
      ];
    },
  },

  created() {
    this.$vuetify.lang.current = 'fr';
    eventBus.$emit("errors-loaded", []);
    this.roles = sessionStorage.getItem("oidc-roles");
  },

  methods: {  
    addLieu(){
      this.lieu = Object.assign({}, this.defaultLieu);
      eventBus.$emit("errors-loaded", []);
    },
    editLieu(item) {
      this.lieu=item;
    },
    loadLieux(){
      alert('reload lieux');
    }
  }
};
</script>