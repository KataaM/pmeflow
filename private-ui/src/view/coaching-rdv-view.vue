<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>        
        <span class="headline">{{$t("pmeflow.ui.rdv.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-btn color="primary" rounded outlined class="mb-2" @click="dialog=true;">{{$t("pmeflow.ui.rdv.button.ajouter")}}</v-btn>
        </v-card-title>
      <v-card-text>  
        <v-row>
          <v-col lg="4" md="6" sm="12" v-for="rdv in rdvs" :key="rdv.oid">
            <rendezVousCard :value="rdv" v-on:remove="reload"/>
          </v-col>
        </v-row>                 
      </v-card-text>
    </v-card>
    <v-dialog v-model="dialog" width="50vw">
      <rendezVousStepper :clear="dialog" v-on:input="reload" v-on:close="dialog=false"/>
    </v-dialog>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import rendezVousCard from '@/components/ui/rdv/rendez-vous-card.vue';
import rendezVousStepper from '@/components/ui/rdv/rendez-vous-stepper.vue';
import RendezVousService from "@/api/RendezVousService.js"

export default {
  components: {
    rendezVousCard,
    rendezVousStepper
  },
  data: () => ({
    dialog:false,
    rdvs:[]
  }),
  watch:{
    dialog:function(newValue){
      if(!newValue){
        this.rendezVous=null;
      }
    }
  },
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  methods: {
    getRendezVousService() {
      return new RendezVousService(
        this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },    
    getRendezVous(){
      return this.getRendezVousService().getRendezVous();
    },
    initialize() {
      this.getRendezVousActifs();
    },
    getRendezVousActifs(){
      this.getRendezVousService().getRendezVousActifs().then(response => {
        this.rdvs = response.data;
      });
    },
    reload(){
      setTimeout(()=>{this.getRendezVousActifs();}, 1000);
      
    }
  }
};
</script>