<template>

    <v-container>
        <v-row no-gutters>
            <v-col cols="9">
                <v-card class="text-h5 font-weight-bold" elevation="0">
                    Entreprises
                </v-card>
            </v-col>

            <div class="buttonContainer  col-3">
                <v-btn outlined>
                    Exporter
                </v-btn>
                <v-btn outlined>
                    Importer
                </v-btn>
                <addEntrepriseComponentVue />
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
                <v-data-table :headers="headers" :items="entreprises" :search="searchDataTable"
                    loading-text="Loading... Please wait" class="elevation-1" @click:row="rowClick">
                </v-data-table>
            </v-col>
        </v-row>
    </v-container>
</template>
  
<script>
import { eventBus } from "@/plugins/event-bus.js";
import addEntrepriseComponentVue from "../components/add-entreprise-component";
export default {
    components: {
        addEntrepriseComponentVue
    },
    data() {
        return {
            searchDataTable: '',
            headers: [
                {
                    text: 'Nom',
                    align: 'start',
                    value: 'donneesEntreprise.libelle',
                },
                { text: 'Adresse email', value: 'donneesEntreprise.email' },
                { text: 'Téléphone', value: 'donneesEntreprise.telephone' },
                { text: 'Site Web', value: 'donneesEntreprise.siteWeb' },
                { text: 'Tags', value: 'tags' },
            ],
            entreprises: [],
        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
        this.getData();
    },
    methods: {
        externalUri(destination) {
            return this.$store.state.configuration.services.siteUri + destination;
        },
        rowClick(item) {
            // Redirect on row click to entreprises_details
            this.$router.push({ name: 'entreprises_details', params: { entreprise_id: item.oid } });
        },
        exportOnClick() {
            //fonction pour exporter la table des contacts
        },
        importOnClick() {
            //fonction pour importer la table des contacts
        },
        async getData() {
            try {
                //Local request
                const response = await this.$http.get("http://localhost:15001/pmeflow/api/v1/entreprise/lister");
                this.entreprises = response.data;
            } catch (error) {
                console.log(error);
            }
        },
        addEntrepriseToList(entrepriseJson) {
            this.entreprises.push(entrepriseJson);
        }
    },

};
</script>
  
<style scoped>
.v-application .v-main {
    background-color: #f7f7fa;
}

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