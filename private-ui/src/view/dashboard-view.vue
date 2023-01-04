<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.dashboard.title")}}</span>
      </v-card-title>
      <v-card-text>
        <v-row class="mt-3">
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :to="{ name: 'dossiers' }">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/dossiers.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.dossier.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.dossier.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :to="{ name: 'mon-profil' }">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/securite.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.securite.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.securite.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :href="externalUri('services')">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/service.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.service.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.service.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
        </v-row>
        <v-row class="mt-3 mb-3">
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :to="{ name: 'abonnements' }">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/abonnements.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.abonnement.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.abonnement.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :to="{ name: 'prestations' }">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/prestations.jpg"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.prestation.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.prestation.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
          <v-col col="4" sm="12" md="4">
            <v-card class="mx-auto raccourci" max-width="344" elevation="2" shaped>
              <v-list-item three-line :href="externalUri('nous-contacter')">
                <v-list-item-avatar size="80">
                  <v-img src="@/assets/images/nous-contacter.png"/>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title class="text-h5 mb-1">
                    {{$t("pmeflow.ui.dashboard.label.contact.title")}}
                  </v-list-item-title>
                  <v-list-item-subtitle>{{$t("pmeflow.ui.dashboard.label.contact.description")}}</v-list-item-subtitle>
                </v-list-item-content>            
              </v-list-item>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
export default {
  components: {
  },
  data: () => ({
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
  },
  methods:{
    externalUri(destination){
      return this.$store.state.configuration.services.siteUri+destination;
    }
  }
};
</script>

<style>
  .raccourci {
      border-color: #b8b8b8 !important;
      border: 1px;
      border-style: solid;
  }
</style>