<template>
  <v-card>
    <v-card-text>
      <v-container>          
        <v-overlay :absolute="absolute" :value="overlay">
          {{$t('pmeflow.ui.rdv.msg.determinationHoraires')}}<br/>
          <v-progress-linear indeterminate color="teal"/>
        </v-overlay>
        <v-row v-if="rdv!=null && rdv.length>0">
          <v-col sm="1">
            <v-icon class="over-blue" @click="precedent" x-large>mdi-chevron-left</v-icon>
          </v-col>
          <v-col sm="10">
            <table class="rdv">
              <thead>
                <th v-for="header in rdv[0]" :key="header">{{header}}</th>
              </thead>
              <tbody>
                <tr v-for="line in rdv[1]" :key="line">
                  <td v-for="cell in line" :key="cell">
                    <span v-if="cell[0]!=''" @click="save(cell[1])">{{cell[0]}}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </v-col>
          <v-col sm="1">
            <v-icon class="over-blue" @click="suivant" x-large>mdi-chevron-right</v-icon>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
import RendezVousService from "@/api/RendezVousService.js"

export default {
  components: {
  },
  props:{
    value: Object,
    juriste:Object
  },
  data: () => ({ 
    dateDebut:Date.now(),
    overlay:false,
    absolute:true
  }),
  mounted(){
  }, 
  watch: { 
  }, 
  asyncComputed: {
    rdv: {
      get() { 
        this.overlay=true;
        return this.getRdvsPossiblesForJuriste(this.dateDebut, this.juriste.oid).then(response=>{
          this.overlay=false;
          return response.data;
        });
      },
      default: []
    },
  },
  methods: {
    getRendezVousService() {
      return new RendezVousService(
        "https://dev.pmeflow.client.lixtec.fr/backend/api/1.0/rendez-vous",
        //this.$store.state.configuration.services.rendezVousServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    getRdvsPossiblesForJuriste(start, juristeId){
      return this.getRendezVousService().determinerRdvsPossiblesForJuriste(start, juristeId);
    },
    precedent(){
      let dateOffset = (24*60*60*1000);
      let minDate = new Date().getTime();
      this.dateDebut= new Date(this.dateDebut-dateOffset).getTime();
      if (this.dateDebut<=minDate){
        this.dateDebut=minDate;
      }
    },
    suivant(){
      var dateOffset = (24*60*60*1000);
      this.dateDebut= new Date(this.dateDebut+dateOffset).getTime();
    },
    save(timestamp){
      this.$emit("input",timestamp);
    }
  }
};
</script>
<style>
  .over-blue:hover{
    color:brown;
  }
  table.rdv{
    text-align:center;
    width:100%;
  }
  
  .rdv thead{
    border-bottom: 1px black solid;
  }
  
  .rdv tr{
    line-height:2.2rem;
  }
  
  .rdv th td{
    border-bottom: 1px black solid;
  }
  
  .rdv td span {
    border-radius: 20px;
    background-color: #E0E0E0;
    padding: 5px;
    margin: 10px; 
  }
  .rdv td span:hover {
    border-radius: 20px;
    background-color: rgb(0, 165, 206);
    color:white;
    padding: 5px;
    margin: 10px;
  }
</style>