<template>
  <div>
    <v-card v-if="this.suivi!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.suivi.title.simple")}}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col md="6" cols="12">
              <v-datetime-picker :label="$t('pmeflow.ui.suivi.label.date')" v-model="suivi.date" locale="fr-FR"
                :text-field-props="textFieldProps" :date-picker-props="dateProps" :time-picker-props="timeProps" class="required" :rules="formValidation.required">
                  <template slot="dateIcon">
                    <v-icon>mdi-calendar-month</v-icon>
                  </template>
                  <template slot="timeIcon">
                    <v-icon>mdi-clock-outline</v-icon>
                  </template>
              </v-datetime-picker>
            </v-col>
          </v-row>    
          <v-row>
            <v-col md="12" cols="12">
              <v-textarea rows="4" v-model="suivi.contenu" :label="$t('pmeflow.ui.suivi.label.contenu')" class="required" :rules="formValidation.required"/>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" rounded outlined @click="annulerSuivi">{{$t('pmeflow.ui.generic.button.entry.annuler')}}</v-btn>
          <v-btn color="blue darken-1" rounded outlined @click="saveSuivi" v-if="isEditable==true">{{$t('pmeflow.ui.suivi.button.sauver')}}</v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-if="this.suivi==null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.suivi.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-btn style="float:right" color="blue darken-1" rounded outlined @click="ajouterSuivi" v-if="isEditable==true">{{$t('pmeflow.ui.suivi.button.ajouter')}}</v-btn>
      </v-card-title>
      <v-card-text>
        <v-data-table :headers="headers" :search="search" :items="suivis"
            :sort-by="['date']" :sort-desc="true" multi-sort>
            <template v-slot:[`item.date`]="{ item }">
              {{item.date.toLocaleString("fr-FR").substr(0, 17)}}
            </template>
            <template v-slot:[`item.actions`]="{ item }">
              <v-btn class="mr-2" color="error" icon @click="supprimerSuivi(item)" v-if="isEditable==true"><v-icon>mdi-delete</v-icon></v-btn>
              <v-btn class="mr-2" color="primary" icon @click="editSuivi(item)"><v-icon>mdi-eye-arrow-right</v-icon></v-btn>
            </template>
        </v-data-table>
      </v-card-text>
    </v-card>
    <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>  
  </div>
</template>

<script>
export default {
  components: {
  },
  props:{
    value: Array,
    editable:{ default: false, type: Boolean }
  },
  data: () => ({ 
    textFieldProps: {
        appendIcon: 'mdi-calendar-clock'
    },
    dateProps: {
      locale: 'fr-FR'
    },
    timeProps: {
      locale: 'fr-FR',
      format:"24hr"
    },
    editedIndex:0,
    suivi:null,
    msg:{
      type:"info",
      text:"",
      presence:false
    },
    search:"",
    headers: [
      { text: "Date", value: "date", width:"10rem" },
      { text: "Contenu", value: "contenu" },
      { text: "Actions", value: "actions", width:"8rem" },
    ],
    suivis: [],
  }),
  mounted(){
    this.suivis = this.value;
  },
  watch: { 
    value: function(newVal) {
      this.suivis=newVal;
    }, 
  }, 
  computed:{
    now(){
      let nowTime = (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10)+" "+
            (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(11, 5);
      return nowTime;
    },
    isEditable(){
      let isEditable = false;
      if (this.editable!=null){
        isEditable = this.editable;
      }
      return isEditable;
    },
    defaultSuivi(){
      return {
        oid:"",
        date: this.now,
        contenu:""};
    },
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    }
  },
  methods: {
    index(items, item) {
      let idx = -1;
      for (let index = 0; index < items.length; index++) {
        if (items[index].oid == item.oid){
          idx=index;
        }
      }
      return idx;
    },
    ajouterSuivi(){
      this.editedIndex=this.suivis.length;
      this.suivi=JSON.parse(JSON.stringify(this.defaultSuivi));
    },
    annulerSuivi(){
      this.suivi=null;
    },
    supprimerSuivi(item){
      let filtered = [];
      this.suivis.forEach(suivi => {
        if (suivi.oid !== item.oid){
          filtered.push(suivi);
        }
      });
      this.suivis = filtered;
      this.$emit('input', this.suivis);
    },
    saveSuivi() {
      this.suivi.date=new Date(this.suivi.date).toISOString().substr(0, 10)+" "+
            (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(11, 5)
      if (this.index(this.suivis, this.suivi)>=0) {
        this.suivis.slice(this.editedIndex, 1, this.suivi);
      }
      else {
        this.suivis.push(this.suivi);
      }
      this.$emit('input', this.suivis);
      this.suivi=null;
    },
    editSuivi(item) {
      this.editedIndex = this.suivis.indexOf(item);
      this.suivi = item;
    },
  }
};
</script>