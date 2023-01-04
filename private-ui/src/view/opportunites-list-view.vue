<template>

    <v-container>
        <v-row no-gutters>
            <v-col cols="9">
                <v-card class="text-h5 font-weight-bold" elevation="0">
                    Opportunités
                </v-card>
            </v-col>
            <v-col cols="3">
                <div class="buttonContainer">
                    <v-btn outlined>
                        Exporter
                    </v-btn>
                    <v-btn class="addButton">
                        Ajouter une opportunité
                    </v-btn>
                </div>
            </v-col>
        </v-row>
        <v-row no-gutters class="mt-n6">
            <v-col cols="2">
                <v-text-field label="Rechercher" outlined v-model="searchDataTable" :append-icon="'mdi-magnify'"
                    placeholder="" dense>
                </v-text-field>
            </v-col>
        </v-row>
        <v-layout row>
            <v-col class="steps" cols="2">
                <h3 class="progress">Nouveau</h3>
                <v-card elevation="2" outlined class="mx-auto">
                    <draggable class="list-group" :list="nouveau" group="people" @change="log" >
                        <div class="list-group-item" v-for="(element) in nouveau" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{ element.date }}
                                </v-chip>
                                <v-chip small>
                                    {{ element.progression }}
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="progress">Contacté</h3>
                <v-card elevation="2" outlined>
                    <draggable class="list-group" :list="contacted" group="people" @change="log">
                        <div class="list-group-item" v-for="(element) in contacted" :key="element.opportunite" v-on:click="oppClick(element.id)">
                           <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{element.date}}    
                                </v-chip>
                                <v-chip small>
                                    {{element.progression}}    
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="progress">Rendez-vous prévu</h3>
                <v-card elevation="2" outlined>
                    <draggable class="list-group" :list="rdv" group="people" @change="log">
                        <div class="list-group-item" v-for="(element) in rdv" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{ element.date }}
                                </v-chip>
                                <v-chip small>
                                    {{ element.progression }}
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="progress">Devis envoyé</h3>
                <v-card elevation="2" outlined>
                    <draggable class="list-group" :list="devis" group="people" @change="log">
                        <div class="list-group-item" v-for="(element) in devis" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{ element.date }}
                                </v-chip>
                                <v-chip small>
                                    {{ element.progression }}
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="progress">Accepté</h3>
                <v-card elevation="2" outlined>
                    <draggable class="list-group" :list="accepter" group="people" @change="log">
                        <div class="list-group-item" v-for="(element) in accepter" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{ element.date }}
                                </v-chip>
                                <v-chip small>
                                    {{ element.progression }}
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="progress">Refusé</h3>
                <v-card elevation="2" outlined>
                    <draggable class="list-group" :list="refus" group="people" @change="log">
                        <div class="list-group-item" v-for="(element) in refus" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <div class="firstLine">
                                <v-list-item-title class="text-h6 mb-1">
                                    {{ element.entreprise }}
                                </v-list-item-title>
                                <v-avatar class="mr-1 mt-1" color="blue" size="28">
                                    <span class="white--text text-subtitle-2">{{ element.initials }}</span>
                                </v-avatar>
                            </div>
                            <v-list-item-subtitle>
                                {{ element.opportunite }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle class="text-right text-subtitle-1 font-weight-bold">
                                {{ element.prix }}
                            </v-list-item-subtitle>
                            <div class="chips pb-1 px-1">
                                <v-chip small>
                                    {{ element.date }}
                                </v-chip>
                                <v-chip small>
                                    {{ element.progression }}
                                </v-chip>
                            </div>
                        </div>
                    </draggable>
                </v-card>
            </v-col>
        </v-layout>

    </v-container>

</template>
  
<script>
import { eventBus } from "@/plugins/event-bus.js";
import draggable from 'vuedraggable';
export default {
    components: {
        draggable
    },
    data() {
        return {
            nouveau: [
                {
                    id: 1,
                    entreprise: "Apple",
                    opportunite : "Meeting",
                    date : "11/11/11",
                    progression : "30%",
                    prix : "1000€",
                    initials : "CC",
               }
            ],

            contacted: [
                {
                    id: 2,
                    entreprise: "Microsoft",
                    opportunite: "Oui",
                    date: "11/11/11",
                    progression: "30%",
                    prix: "1000€",
                    initials: "GF",
               }
            ],

            rdv: [
                {
                    id: 2,
                    entreprise: "Micromania",
                    opportunite: "rdv prit",
                    date: "11/11/11",
                    progression: "30%",
                    prix: "1000€",
                    initials: "TB",
               }
            ],

            devis: [
                {
                    id: 2,
                    entreprise: "Flixbus",
                    opportunite: "je suis un bus",
                    date: "11/11/11",
                    progression: "30%",
                    prix: "1000€",
                    initials: "CC",
               }
            ],

            accepter: [
                {
                    id: 2,
                    entreprise: "Auchan",
                    opportunite: "c bon",
                    date: "11/11/11",
                    progression: "30%",
                    prix: "1000€",
                    initials: "MG",
               }
            ],

            refus: [
                {
                    id: 2,
                    entreprise: "Pfizer",
                    opportunite: "non",
                    date: "11/11/11",
                    progression: "30%",
                    prix: "1000€",
                    initials: "YP",
               }
            ],
        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
    },
    methods: {
        externalUri(destination) {
            return this.$store.state.configuration.services.siteUri + destination;
        },
        oppClick(id) {
            // Redirect on row click to opportunites_details
            this.$router.push({ name: 'opportunites_details', params: { opportunite_id: id } });
        },
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

.steps {
    border: 3px solid #EEE;
    background-color: #CCD0D0;
}

.buttonContainer {
    justify-content: space-between;
    display: flex;
    margin-right: 0px;
}

.firstLine {
    justify-content: space-between;
    display: flex;
    margin-right: 0px;
}

.v-application .v-main .v-card {
    background-color: inherit;
}

.progress {
    background-color: #CCD0D0;
    font-size: 12px;
    margin-bottom: 15px;
    opacity: 0.7;
}

.v-application .v-main .row {
    padding: 42px 66px 0px 66px;
    margin: 0;
}

.v-application .v-main h2 {
    background-color: red;
}



.chips {
    display: flex;
    justify-content: space-between;
}

.addButton {
    color: white !important;
    background-color: #00b2b2 !important;
    border: 1px solid #008a8c;
}
</style>