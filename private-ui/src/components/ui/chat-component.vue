<template>
  <div>
    <v-card>
      <v-card-text>
        <v-container>
          <v-row no-gutters>
            <v-col>
              <div class="d-flex flex-row align-center">
                <v-text-field v-model="msg" :label="$t('pmeflow.ui.echange.label.saisieMessage')" @keypress.enter="send"></v-text-field>
                <v-btn icon class="ml-4" @click="send"><v-icon>mdi-send</v-icon></v-btn>
              </div>
            </v-col>
          </v-row>
          <v-row v-for="item in echanges" :key="item.oid"  :class="style(item)">
            <v-col md="9" cols="12">
              <div class="media mt-2">
                <img class="mr-3 avatar-sm rounded-circle" src="@/assets/avatars/assistance.jpg" v-if="item.client!=true"/>
                <div class="media-body">
                  <span class="media-title">{{item.profil.nom}} {{item.profil.prenom}}</span><br/>
                  <span>{{new Date(item.date).toLocaleString("fr-FR").substr(0, 17).replace(",","")}}</span><br/> 
                  {{item.contenu}}
                </div>
                <img class="mr-3 avatar-sm rounded-circle" src="@/assets/avatars/client.png" v-if="item.client==true"/>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  components: {
  },
  props:{
    value: Array,
    profil: Object,
    isClient:Boolean,
  },
  data: () => ({ 
    msg:"",
    echanges: [],
  }),
  watch: { 
    value: function(newVal) {
      this.echanges=newVal;
    }, 
  }, 
  mounted(){
    this.echanges = this.value;
  },
  computed:{
    echange(){
      return {
        oid:"",
        date: Date.now(),
        profil: this.profil,
        client : this.isClient,
        contenu:this.msg
      };
    },
  },
  methods: {
    style(item){
      let style="";
       if (item.client==true){
         style="client-line";
       }
       return style;
    },
    send() {
      if (this.msg!==""){
        this.echanges.push(this.echange);
        this.$emit('input', this.echanges);
        this.msg="";
      }
    },
  }
};
</script>
<style scoped>
  .media {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;
  }
  .media-body {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
  }
  .media-title{
    font-weight: bold;
    font-size: 1.0rem;
  }
  .avatar-sm {
    height: 3rem;
    width: 3rem;
  }
  .rounded-circle{
    border-radius: 50% !important;
  }
  .client-line{
    background-color:#e3e3e3
  }
  .client-line .media{
    float:right;
    text-align: right;
  }
</style>