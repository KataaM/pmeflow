<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.client.title.multiple")}}</span>
        <v-spacer></v-spacer>
        <v-text-field v-model="search" append-icon="mdi-magnify" label="Search"
                single-line hide-details></v-text-field>
        <v-spacer></v-spacer>
        <v-btn color="primary" rounded outlined class="mb-2" @click="ajouter">{{$t("pmeflow.ui.client.button.ajouter")}}</v-btn>
        <client-dialog v-model="client" v-on:update:errors="errors = $event" v-bind:clients.sync="clients"/>
      </v-card-title>
      <v-card-text>
        <v-data-table
            :headers="headers"
            :search="search"
            :items="clients"
            :sort-by="['nom', 'prenom']"
            :sort-desc="[false, false]"
            multi-sort
            @click:row="editer($event)">
            <template v-slot:no-data>
            <v-btn color="primary" @click="initialize">Reset</v-btn>
            </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script>
import clientDialog from '@/components/ui/client/client-dialog.vue';
import ClientService from "@/api/ClientService.js";
import { eventBus } from "@/plugins/event-bus.js";

export default {
  components: {
    clientDialog
  },
  data: () => ({
    headers: [
      { text: "Nom", value: "nom" },
      { text: "Prénom", value: "prenom"},
      { text: "Email", value: "email" },
      { text: "Téléphone", value: "telephone" },
      { text: "Code postal", value: "codePostal" },
      { text: "Ville", value: "ville" }
    ],
    clients: [],
    defaultClient: {
      oid: "",
      nom: "",
      prenom: "",
      adresse: "",
      codePostal: "",
      ville: "",
      email: "",
      telephone:"",
      priseContact:"TEL",
      customerStripeId:""
    },
    client: null,
    search:""
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
    this.getClients();
  },
  methods: {
    getClientService() {
      return new ClientService(
        this.$store.state.configuration.services.clientServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getClients();
    },
    getClients() {
      this.getClientService().getClients().then(response => {
        this.clients = response.data;
      });
    },
    ajouter(){
      this.client = Object.assign({}, this.defaultClient);
    },
    editer(item) {
      this.client = Object.assign({}, item);
    },
  }
};
</script>