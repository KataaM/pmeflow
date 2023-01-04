<template>

    <v-container>
        <v-row no-gutters>
            <v-col cols="9">
                <v-card class="text-h5 font-weight-bold" elevation="0">
                    Contacts
                </v-card>
            </v-col>

            <div class="buttonContainer  col-3" justify="center">
                <v-btn outlined>
                    Exporter
                </v-btn>
                <v-btn outlined>
                    Importer
                </v-btn>
                <addContactComponentVue />
                <!-- <v-btn class="addButton">
                    Ajouter {{ title.slice(0, -1) }}
                </v-btn> -->
            </div>

        </v-row>
        <v-row no-gutters class="mt-n6">
            <v-col cols="2">
                <v-text-field label="Rechercher" outlined v-model="searchDataTable" :append-icon="'mdi-magnify'"
                    placeholder="" dense>
                </v-text-field>

            </v-col>
            <!-- <v-col cols="1" offset="1">
                <v-btn outlined>
                    Tout afficher
                </v-btn>
            </v-col> -->
        </v-row>
        <v-row no-gutters class="mt-n12">
            <v-col cols="12">
                <v-data-table :headers="headers" :items="contacts" :search="searchDataTable"
                    loading-text="Loading... Please wait" class="elevation-1" @click:row="rowClick">
                </v-data-table>
            </v-col>
        </v-row>
    </v-container>

</template>

<script>

import { eventBus } from "@/plugins/event-bus.js";
import addContactComponentVue from "../components/add-contact-component.vue";
export default {
    components: {
        addContactComponentVue
    },
    data() {
        return {
            searchDataTable: '',
            headers: [
                {
                    text: 'Nom de famille',
                    align: 'start',
                    value: 'donneesPerso.nom',
                },
                { text: 'Prénom', value: 'donneesPerso.prenom' },
                { text: 'Adresse email', value: 'donneesPerso.email' },
                { text: 'Mobile', value: 'donneesPerso.mobile' },
                { text: 'Ville', value: 'donneesPerso.adresses[0].ville' },
                { text: 'Tags', value: 'tags' },
            ],
            contacts: [],
        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
        this.getData();
    },
    methods: {
        // externalUri(destination) {
        //     return this.$store.state.configuration.services.siteUri + destination;
        // },
        rowClick(item) {
            // Redirect on row click to contacts_details view
            this.$router.push({ name: 'contacts_details', params: { contact_id: item.oid } });
        },

        async getData() {
            try {
                //Connect to local API
                const response = await this.$http.get("http://localhost:15001/pmeflow/api/v1/contact/listNotDeleted");
                this.contacts = response.data;
            } catch (error) {
                console.log(error);
            }
        },
        addContactToList(contactJson) {
            this.contacts.push(contactJson)
        }
    },

};
</script>
  
<style scoped>
.v-btn {
    text-transform: none;
}

.buttonContainer {
    justify-content: space-between;
    display: flex;
    margin-right: 0px;
}

.addButton {
    color: white !important;
    background-color: #00b2b2 !important;
    border: 1px solid #008a8c;
}

.v-application .v-main .v-card {
    background-color: inherit;
}


.v-application .v-main .row {
    padding: 42px 66px 0px 66px;
    margin: 0;
}

.v-application .v-main h2 {
    background-color: red;
}
</style>