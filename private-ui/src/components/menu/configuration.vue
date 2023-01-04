<template>
    <v-list dense class="mt-2 mb-2 ml-2 mr-2">
      <strong>{{$t('pmeflow.application.menu.configuration.personnaliser')}}</strong>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-organisations' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.organisations") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-rh' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.rh") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-comptabilite' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.banque") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-templates' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.templates") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <strong><br/>{{$t('pmeflow.application.menu.configuration.preferences')}}</strong>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-opportunites' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.opportunites") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-commandes' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.commandes") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-factures' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.factures") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-interventions' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.interventions") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-planning' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.planning") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-projets' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.projets") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-tickets' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.tickets") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-lieux' }">
        <v-list-item-content>
          <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.lieux") }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
</template>

<script>
export default {
  name: "menuGauche",
  inheritAttrs: false,
  props: {
    label:String, 
    roles:[],
  },
  computed: {
    inputListeners: function() {
      var vm = this;
      return Object.assign(
        {},
        this.$listeners,
        {
          input: function(event) {
            vm.$emit("input", event.target.value);
          }
        }
      );
    }
  },
  methods:{
    viewMenu(authorize){
      let result = false;
      if (this.roles!=null && this.roles.length>0 ){
        if (authorize!=null && authorize.length>0) {
          authorize.forEach(role => {
            result |= this.roles.includes(role);
          }); 
        }
        else{
          result=true;
        }
      }
      return result;
    }
  }
};
</script>