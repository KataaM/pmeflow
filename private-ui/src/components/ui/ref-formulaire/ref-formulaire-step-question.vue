<template>
  <v-col col="12" sm="12" md="12">
    <v-card v-if="this.question!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-formulaire.step.question.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col md="6" cols="12">
              <v-text-field v-model="question.codeForDoc" :label="$t('pmeflow.ui.ref-formulaire.step.question.label.codeForDoc')"></v-text-field>
            </v-col>
            <v-col md="6" cols="12">
              <v-text-field v-model="question.libelle" :label="$t('pmeflow.ui.generic.label.libelle')"></v-text-field>
            </v-col>
          </v-row>    
          <v-row>
            <v-col md="12" cols="12">
              <v-text-field v-model="question.description" :label="$t('pmeflow.ui.generic.label.description')"></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col md="6" cols="12">
              <v-text-field v-model="question.defaultValue" :label="$t('pmeflow.ui.ref-formulaire.step.question.label.defaultValue')"></v-text-field>
            </v-col>
            <v-col md="6" cols="12">
              <v-select :items="typesReponse" v-model="question.type" :label="$t('pmeflow.ui.ref-formulaire.step.question.label.type')"></v-select>
              <v-text-field 
                v-if="question.type==='MULTIPLE_RADIO' || question.type==='MULTIPLE_SELECT'" 
                v-model="question.value" 
                :label="$t('pmeflow.ui.ref-formulaire.step.question.label.value')"
                :messages="['valeurs spérarées par un ;']"></v-text-field>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" rounded outlined @click="annulerQuestion">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined @click="saveQuestion">{{$t('pmeflow.ui.ref-formulaire.step.question.button.sauver')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-if="this.question==null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.ref-formulaire.step.question.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-btn style="float:right" color="blue darken-1" rounded outlined @click="ajouterQuestion">{{$t('pmeflow.ui.ref-formulaire.step.question.button.ajouter')}}</v-btn>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="questions"
            :sort-by="['order']"
            :sort-desc="false"
            multi-sort>          
            <template v-slot:[`item.id`]="{ item }">
              {{index(item)}}
            </template>
            <template v-slot:[`item.actions`]="{ item }">
              <v-btn class="mr-2" color="error" icon @click="supprimerQuestion(item)"><v-icon>mdi-delete</v-icon></v-btn>
              <v-btn class="mr-2" color="secondary" icon @click="moveQuestion(item, index(item)-1, 0)"><v-icon>mdi-arrow-collapse-up</v-icon></v-btn>
              <v-btn class="mr-2" color="secondary" icon @click="moveQuestion(item, index(item)+1, 1)"><v-icon>mdi-arrow-collapse-down</v-icon></v-btn>
              <v-btn class="mr-2" color="primary" icon @click="editQuestion(item)"><v-icon>mdi-eye-arrow-right</v-icon></v-btn>
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
export default {
  components: {
  },
  props:{
    value: Object,
  },
  data: () => ({
	typesReponse:['LIBRE', 'LIBRE_AREA', 'MULTIPLE_SELECT', 'MULTIPLE_RADIO'],
    editedIndex:0,
    defaultQuestion:{
      oid:"",
      libelle:"",
      codeForDoc:"",
      description:"",
      value:"",
      type:"",
      defaultValue:""
    },
    question:null,
    dialogQuestion:false,
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    headers: [
      { text: "ID", value: "id" },
      { text: "Code pour doc", value: "codeForDoc" },
      { text: "Libellé", value: "libelle"},
      { text: "Type", value: "type" },
      { text: "Actions", value: "actions" },
    ],
    questions: [],
  }),
  created() {
    this.initialize();
  },
  mounted(){
    this.questions = this.value;
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
  },
  methods: {
    index(item) {
      return this.questions.indexOf(item);
    },
    initialize() {
    },
    ajouterQuestion(){
      this.editedIndex=this.questions.length;
      this.question=JSON.parse(JSON.stringify(this.defaultQuestion));
    },
    moveQuestion(item, newPosition, sens){
      if (newPosition>=0 && newPosition<this.questions.length) {
        let ordered = [];
        for (let idx = 0; idx < this.questions.length; idx++) {
          const element = this.questions[idx];
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
        this.questions=ordered;
        this.$emit('input', this.questions);
      }
    },
    annulerQuestion(){
      this.question=null;
    },
    supprimerQuestion(item){
      let filtered = [];
      this.questions.forEach(question => {
        if (question.description !== item.description){
          filtered.push(question);
        }
      });
      this.questions = filtered;
      this.$emit('input', this.questions);
    },
    saveQuestion() {
      if (this.index(this.question)>=0) {
        this.questions.slice(this.editedIndex, 1, this.question);
      }
      else {
        this.questions.push(this.question);
      }
      this.$emit('input', this.questions);
      this.question=null;
    },
    editQuestion(item) {
      this.editedIndex = this.questions.indexOf(item);
      this.question = item;
    },
  }
};
</script>