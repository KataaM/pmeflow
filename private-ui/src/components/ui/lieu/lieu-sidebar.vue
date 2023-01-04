<template>
  <v-dialog transition="dialog-bottom-transition" v-model="dialog">
    <form @submit.prevent="save">
    <v-card class="sidebar-right">
      <v-toolbar flat dark color="primary">
        <v-btn icon dark @click="dialog = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title>{{title}}</v-toolbar-title>
      </v-toolbar>
      <v-card-text>
        <v-row>
          <v-col md="6" cols="12">
              <v-text-field v-model="lieu.nom" :label="$t('pmeflow.ui.generic.label.nom')" required class="required"
                  :rules="formValidation.required" />
          </v-col>
        </v-row>
        <v-row>
          <v-col md="6" cols="12">
              <v-textarea rows="4" v-model="lieu.adresse" :label="$t('pmeflow.ui.generic.label.adresse')"/>
          </v-col>
          <v-col md="6" cols="12">
              <v-text-field v-model="lieu.codePostal" :label="$t('pmeflow.ui.generic.label.codePostal')"/>
              <v-text-field v-model="lieu.ville" :label="$t('pmeflow.ui.generic.label.ville')"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="6" cols="12">
              <v-text-field v-model="lieu.pays" :label="$t('pmeflow.ui.generic.label.pays')"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="6" cols="12">
              <v-text-field v-model="lieu.telephone" :label="$t('pmeflow.ui.generic.label.telephone')"/>
          </v-col>
          <v-col md="6" cols="12">
              <v-text-field v-model="lieu.fax" :label="$t('pmeflow.ui.generic.label.fax')"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="12" cols="12">
              <v-text-field v-model="lieu.siteWeb" :label="$t('pmeflow.ui.generic.label.siteWeb')"/>
          </v-col>
        </v-row>
        <v-row>
          <v-col md="12" cols="12">
            <v-textarea dense v-model="lieu.notes" :label="$t('pmeflow.ui.generic.label.notes')"/>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <button type="submit" class="mr-2 v-btn v-btn--outlined v-btn--rounded theme--light v-size--default primary--text">
          {{$t('pmeflow.ui.generic.button.save')}}
        </button>
      </v-card-actions>
    </v-card>
    </form> 
  </v-dialog>
</template>
<script>
export default {
  name: "lieu-sidebar",
  components: {},
  props: {
    value: Object,
  },
  data: () => ({
    lieu: null,
    dialog:false
  }),
  watch: {
    value: function (val) {
      this.lieu=val;
      val!=null?this.dialog=true:this.dialog=false;
    },
  },
  created: {},
  computed:{
    formValidation(){
      return {
        required:[value => !!value || this.$t("pmeflow.ui.generic.message.requis")],
      }
    },
    title(){
      let title = this.$t("pmeflow.ui.lieu.title.ajout");
      if (this.lieu!=null && this.lieu.oid!=null)
      {
        title = this.$t("pmeflow.ui.lieu.title.modification")+this.lieu.nom;
      }
      return title;
    }
  },
  mounted: {},
  methods: {
    annuler(){
      this.lieu=null;
    },
    save(e){
      alert(JSON.stringify(this.lieu));
      e.preventDefault();
    }
  },
};
</script>