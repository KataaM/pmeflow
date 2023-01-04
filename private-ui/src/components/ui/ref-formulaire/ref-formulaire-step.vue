<template>
  <v-col col="12" sm="12" md="12">
    <v-card v-if="this.step!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-formulaire.step.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col md="12" cols="12">
              <v-text-field v-model="step.description" :label="$t('pmeflow.ui.generic.label.description')"></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col md="12" cols="12">
              <v-textarea rows="4" v-model="step.htmlContent" :label="$t('pmeflow.ui.ref-formulaire.step.label.htmlContent')"></v-textarea>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-text>
        <ref-formulaire-step-question v-model="step.questions"/>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" rounded outlined @click="annulerStep">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined @click="saveStep">{{$t('pmeflow.ui.ref-formulaire.step.button.sauver')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-if="this.step==null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-formulaire.step.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" rounded outlined @click="ajouterStep">{{$t('pmeflow.ui.ref-formulaire.step.button.ajouter')}}</v-btn>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="steps"
            :sort-by="['order']"
            :sort-desc="false"
            multi-sort>          
            <template v-slot:[`item.id`]="{ item }">
              {{index(item)}}
            </template>
            <template v-slot:[`item.actions`]="{ item }">
              <v-btn class="mr-2" color="error" icon @click="supprimerStep(item)"><v-icon>mdi-delete</v-icon></v-btn>
              <v-btn class="mr-2" color="secondary" icon @click="moveStep(item, index(item)-1, 0)"><v-icon>mdi-arrow-collapse-up</v-icon></v-btn>
              <v-btn class="mr-2" color="secondary" icon @click="moveStep(item, index(item)+1, 1)"><v-icon>mdi-arrow-collapse-down</v-icon></v-btn>
              <v-btn class="mr-2" color="primary" icon @click="editStep(item)"><v-icon>mdi-eye-arrow-right</v-icon></v-btn>
            </template>     
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
        </v-data-table>
      </v-card-text>
    </v-card>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>  
  </v-col>
</template>

<script>
import RefFormulaireStepQuestion from "@/components/ui/ref-formulaire/ref-formulaire-step-question";

export default {
  components: {
    RefFormulaireStepQuestion
  },
  props: {
    value: Object,
  },
  data: () => ({
    editedIndex:0,
    defaultStep:{
      oid:"",
      description:"",
      htmlContent:"",
      questions:[]
    },
    steps:[],
    step:null,
    dialogQuestion:false,
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    headers: [
      { text: "ID", value: "id" },
      { text: "Description", value: "description"},
      { text: "Actions", value: "actions" }
    ],
    headersQuestion: [
      { text: "Code", value: "code" },
      { text: "Libellé", value: "libelle"},
      { text: "Description", value: "description" },
      { text: "Coût", value: "cout" },
      { text: "Action", value: "action" },
    ]
  }),
  mounted(){
    this.steps = this.value;
  },
  computed:{
    formValidation(){
      return {
        coutRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^(?:\d+(?:\.\d*)?|\.\d+)$/
            return pattern.test(value) || this.$t("pmeflow.ui.ref-abonnement.message.mauvaisFormatPrix");
          },
        ],
        entierRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^[0-9]\d*$/
            return pattern.test(value) || this.$t("pmeflow.ui.generic.message.mauvaisFormatEntier");
          },
        ],
      }
    },    
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
    initialize(){
      alert(JSON.stringify(this.propObj));
    },
    index(item) {
      return this.steps.indexOf(item);
    },
    ajouterStep(){
      this.editedIndex=this.steps.length;
      this.step=JSON.parse(JSON.stringify(this.defaultStep));
    },
    moveStep(item, newPosition, sens){
      if (newPosition>=0 && newPosition<this.steps.length) {
        let ordered = [];
        for (let idx = 0; idx < this.steps.length; idx++) {
          const element = this.steps[idx];
          if (sens==0 && newPosition===idx)
          {
            ordered.push(item);
          }
          if (element.description!==item.description) {
            ordered.push(element);
          }
          if (sens==1 && newPosition===idx)
          {
            ordered.push(item);
          }
        }
        this.steps=ordered;
        this.$emit('input', this.steps);
      }
    },
    annulerStep(){
      this.step=null;
    },
    supprimerStep(item){
      let filtered = [];
      this.steps.forEach(step => {
        if (step.description !== item.description){
          filtered.push(step);
        }
      });
      this.steps = filtered;
      this.$emit('input', this.steps);
    },
    saveStep() {
      if (this.index(this.step)>=0) {
        this.steps.slice(this.editedIndex, 1, this.step);
      }
      else {
        this.steps.push(this.step);
      }
      this.step=null;
      this.$emit('input', this.steps);
    },
    editStep(item) {
      this.editedIndex = this.steps.indexOf(item);
      this.step = item;
    },
  }
};
</script>