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
            <span class="headline">{{$t("pmeflow.ui.ticket.title.multiple")}}</span>
            </v-col>
            <v-col cols="12" md="3" sm="12">
              <v-text-field v-model="search" append-icon="mdi-magnify" 
                :label="$t('pmeflow.ui.generic.label.search')"
                single-line hide-details></v-text-field>
            </v-col>
          </v-row>
        </v-card-title>
        <v-card-subtitle class="mt-2">
          {{$t('pmeflow.ui.configuration.ticket.message.subtitleConfig')}}
        </v-card-subtitle>
        <v-card-text>
          <v-row>
            <v-col cols="6" md="6" sm="12">
              <v-card>
                <v-card-title>
                  <v-row>
                    <v-col cols="12" md="8" sm="12">
                    <span class="headline">Statuts personnalisés</span>
                    </v-col>
                    <v-col cols="12" md="4"  sm="12" style="text-align:right">
                      <v-btn color="primary" rounded outlined class="mr-2" @click="ajouter">Ajouter</v-btn>
                    </v-col>
                  </v-row>
                  
                  <v-sub/>
                  <v-btn>test</v-btn>
                </v-card-title>
                <v-card-subtitle>Gérer vos statuts pour le module Tickets</v-card-subtitle>
                <v-card-text>blal</v-card-text>
              </v-card>
            </v-col>
            <v-col cols="6" md="6" sm="12">
              <v-text-field v-model="search" append-icon="mdi-magnify" 
                :label="$t('pmeflow.ui.generic.label.search')"
                single-line hide-details></v-text-field>
            </v-col>
          </v-row>
          
        </v-card-text>
      </v-card>
      <ticket-sidebar v-if="ticket!=null" v-model="ticket" v-on:input="loadTickets"/>
    </v-col>
  </v-row>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import menuConfiguration from "@/components/menu/configuration"
import ticketSidebar from "@/components/ui/ticket/ticket-sidebar.vue";

export default {
  components: {
    menuConfiguration,
    ticketSidebar
  },

  data: () => ({  
    defaultTicket:{
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
    ticket:null,    
    search:"",
    viewSidebar:false,
  }),

  computed:{    
    ticketsHeaders(){
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
    addTicket(){
      this.ticket = Object.assign({}, this.defaultTicket);
      eventBus.$emit("errors-loaded", []);
    },
    editTicket(item) {
      this.ticket=item;
    },
    loadTickets(){
      alert('reload tickets');
    }
  }
};
</script>