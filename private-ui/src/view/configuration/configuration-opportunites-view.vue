<template>
  <v-row>
    <v-col col="2" sm="12" md="2">
      <menu-configuration :roles="roles"/>
    </v-col>
    <v-col col="10" sm="12" md="10">
      2
    </v-col>
  </v-row>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import menuConfiguration from "@/components/menu/configuration"

export default {
  components: {
    menuConfiguration,
  },

  data: () => ({  
    roles:[],
  }),

  created() {
    eventBus.$emit("errors-loaded", []);
    this.roles = sessionStorage.getItem("oidc-roles");
  },

  methods: {    
  }
};
</script>