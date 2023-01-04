<template>
    <v-container>
        <v-row no-gutters class="pb-2">
            <v-col cols="2" offset="2">
                <v-btn class="buttonContainer" elevation="0" @click="goBack">
                    <v-icon right dark class="pr-5">
                        mdi-arrow-left-thin
                    </v-icon>
                    Vue d'ensemble des entreprises
                </v-btn>
            </v-col>
        </v-row>
        <v-row no-gutters>
            <v-col cols="4" offset="2">
                <v-card dense elevation="0" outlined>
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Informations entreprise
                        <v-spacer></v-spacer>
                        <v-icon>
                            mdi-pencil-outline
                        </v-icon>
                        <v-icon dense class="pl-1">
                            mdi-block-helper
                        </v-icon>
                        <v-icon class="pl-1">
                            mdi-delete-forever
                        </v-icon>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-row no-gutters class="pa-3" justify="center" align="center">
                        <v-col cols="2">
                            <v-img max-height="48" max-width="48"
                                src="https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg">
                            </v-img>
                        </v-col>
                        <v-col cols="10">
                            <v-card-text class="pl-0">{{ entreprises.donneesEntreprise.libelle }}</v-card-text>
                        </v-col>
                    </v-row>
                    <v-divider></v-divider>
                    <v-row>
                        <v-col cols="5" class="pt-4 pb-0">
                            <v-card-text class="py-1">Email</v-card-text>
                        </v-col>
                        <v-col cols="7" class="pt-4 pb-0">
                            <v-card-text class="py-1"><a href="#" class="text-decoration-none">
                                    {{ entreprises.donneesEntreprise.email }}</a>
                            </v-card-text>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="5" class="py-0">
                            <v-card-text class="py-0">Adresse</v-card-text>
                        </v-col>
                        <v-col cols="7" class="py-0">
                            <v-card-text class="py-0"><a href="#" class="text-decoration-none">
                                    {{ entreprises.donneesEntreprise.adresses[0].rue }} <br />
                                    {{ entreprises.donneesEntreprise.adresses[0].codePostal }}
                                    {{ entreprises.donneesEntreprise.adresses[0].ville }}
                                    <br /> {{ entreprises.donneesEntreprise.adresses[0].departement }}<br />
                                    {{ entreprises.donneesEntreprise.adresses[0].pays }}<br /></a>
                            </v-card-text>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="5" class="py-0">
                            <v-card-text class="py-0">Langue</v-card-text>
                        </v-col>
                        <v-col cols="7" class="py-0">
                            <v-card-text class="py-0">{{ entreprises.donneesEntreprise.langues[0] }}</v-card-text>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="5" class="py-0 pr-0">
                            <v-card-text>Contact commercial
                            </v-card-text>
                        </v-col>
                        <v-col cols="7" class="pt-0">
                            <v-card-text> {{ entreprises.donneesEntreprise.gestionnaireCompte.prenom }}
                                {{ entreprises.donneesEntreprise.gestionnaireCompte.nom }}</v-card-text>
                        </v-col>
                    </v-row>

                </v-card>
            </v-col>
            <v-col cols="3" offset="1">
                <v-card dense elevation="0" outlined>
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Infos générales
                        <v-spacer></v-spacer>
                        <v-icon>
                            mdi-pencil-outline
                        </v-icon>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-row no-gutters class="pa-3" justify="center" align="center">
                        <v-card-text>Sur des pages comme celle-ci, vous pouvez ajouter toutes les informations dont vous
                            avez besoin et voir d'un simple coup d'œil toute l'activité de cette entreprise.
                        </v-card-text>
                    </v-row>
                    <v-divider></v-divider>
                </v-card>
            </v-col>
        </v-row>
        <v-row no-gutters class="pt-5">
            <v-col cols="8" offset="2">
                <v-card dense outlined elevation="0">
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Opportunités
                        <v-spacer></v-spacer>
                        <v-icon>
                            mdi-plus-box-outline
                        </v-icon>
                    </v-card-title>
                    <v-row no-gutters class="pa-0">
                        <v-col cols="3" class="">
                            <v-card elevation="0" outlined class="rounded-0">
                                <v-card-title class="text-md-body-1 pa-4 font-weight-medium justify-center">
                                    TOUT (3)
                                </v-card-title>
                                <v-card-subtitle class="text-center">
                                    26.500,00 $
                                </v-card-subtitle>
                            </v-card>
                        </v-col>
                        <v-col cols="3" class="">
                            <v-card elevation="0" outlined class="rounded-0">
                                <v-card-title class="text-md-body-1 pa-4 font-weight-medium justify-center">
                                    OUVERT (2)
                                </v-card-title>
                                <v-card-subtitle class="text-center">
                                    26.500,00 $
                                </v-card-subtitle>
                            </v-card>
                        </v-col>
                        <v-col cols="3" class="">
                            <v-card elevation="0" outlined class="rounded-0">
                                <v-card-title class="text-md-body-1 pa-4 font-weight-medium justify-center">
                                    ACCEPTÉ(S) (1)
                                </v-card-title>
                                <v-card-subtitle class="text-center">
                                    26.500,00 $
                                </v-card-subtitle>
                            </v-card>
                        </v-col>
                        <v-col cols="3" class="">
                            <v-card elevation="0" outlined class="rounded-0">
                                <v-card-title class="text-md-body-1 pa-4 font-weight-medium justify-center">
                                    REFUSÉ(S) (0)
                                </v-card-title>
                                <v-card-subtitle class="text-center">
                                    26.500,00 $
                                </v-card-subtitle>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-col cols="12">
                            <v-data-table :headers="opportunityHeaders" :items="opportunities" :search="searchDataTable"
                                loading-text="Loading... Please wait" class="elevation-1 rounded-0"
                                @click:row="rowClick" hide-default-footer>
                            </v-data-table>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>

        </v-row>
        <v-row no-gutters class="pt-5">
            <v-col cols="8" offset="2">
                <v-card dense elevation="0" outlined>
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Contacts reliés
                        <v-spacer></v-spacer>
                        <v-icon>
                            mdi-plus-box-outline
                        </v-icon>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-row align="center">
                        <v-col cols="1" align="center" class="pr-0">
                            <v-img max-height=" 48" max-width="48"
                                src="https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg" />
                        </v-col>
                        <v-col cols="5" class="pl-0">
                            <v-card elevation="0">
                                <v-card-title class="pb-3">
                                    Directeur commercial
                                    <v-btn icon class="px-2" elevation="0">
                                        <v-icon>mdi-star</v-icon>
                                    </v-btn>
                                    <v-spacer></v-spacer>
                                    <v-icon>
                                        mdi-dots-vertical
                                    </v-icon>
                                </v-card-title>
                                <v-card-subtitle class="pb-0">
                                    CEO
                                </v-card-subtitle>
                                <v-card-text class="pt-0"><a href="#" class="text-decoration-none">
                                        contact-director@lixtec.fr</a>
                                </v-card-text>
                            </v-card>
                        </v-col>
                        <v-col cols="1" align="center" class="pr-0">
                            <v-img max-height="48" max-width="48"
                                src="https://upload.wikimedia.org/wikipedia/commons/4/4f/Twitter-logo.svg" />
                        </v-col>
                        <v-col cols="5" class="pl-0">
                            <v-card elevation="0">
                                <v-card-title class="pb-3">
                                    Directeur commercial
                                    <v-btn icon class="px-2" elevation="0">
                                        <v-icon>mdi-star-outline</v-icon>
                                    </v-btn>
                                    <v-spacer></v-spacer>
                                    <v-icon>
                                        mdi-dots-vertical
                                    </v-icon>
                                </v-card-title>
                                <v-card-subtitle class="pb-0">
                                    CEO
                                </v-card-subtitle>
                                <v-card-text class="pt-0"><a href="#" class="text-decoration-none">
                                        contact-director@lixtec.fr</a>
                                </v-card-text>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>
        </v-row>
        <v-row no-gutters class="pt-5">
            <v-col cols="8" offset="2">

                <v-card dense outlined elevation="0">
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Projets Actifs
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-row no-gutters>
                        <v-col cols="12">
                            <v-data-table :headers="projectHeaders" :items="projects" :search="searchDataTable"
                                loading-text="Loading... Please wait" class="elevation-1 rounded-0"
                                @click:row="rowClick" hide-default-footer>
                            </v-data-table>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>
        </v-row>
        <v-row no-gutters class="pt-5">
            <v-col cols="8" offset="2">
        
                <v-card dense outlined elevation="0">
                    <v-card-title class="text-md-body-1 pa-3 font-weight-medium">
                        Activité
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-row no-gutters>
                        <v-col cols="12">
                            <v-timeline dense clipped>
                                <v-timeline-item v-for="(item, i) in items" :key="i" :color="item.color" :icon="item.icon"
                                    fill-dot medium>
                                    <v-chip class="ml-0" small>
                                        Entreprise
                                    </v-chip>
                                    <v-row justify="space-between">
                                        <v-col cols="7" class="">
                                            <span class="text-body-2">contact pierrot: Un Collaborateur (test data) travaille
                                                comme Collaborateur chez Entreprise (test data)</span>
                                        </v-col>
                                        <v-col class="text-right" cols="5">
                                            <span class="text-body-2">11/11/11 15:26</span>
                                        </v-col>
                                    </v-row>
                                </v-timeline-item>
                            </v-timeline>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import {
    eventBus
} from "@/plugins/event-bus.js";
export default {
    components: {},
    data() {
        return {
            entreprise_id: this.$route.params.entreprise_id,
            entreprises: [],
            opportunityHeaders: [
                {
                    text: 'Titre opportunité',
                    align: 'start',
                    // filterable: false,
                    value: 'opportunitytitle',
                },
                { text: 'Responsable', value: 'resp' },
                { text: 'Phase', value: 'phase' },
                { text: 'Montant', value: 'total' },
                { text: 'Statut', value: 'status' },
            ],
            projectHeaders: [
                {
                    text: 'Nom',
                    align: 'start',
                    // filterable: false,
                    value: 'projectname',
                },
                { text: 'Budget alloué', value: 'budgetallocated' },
                { text: 'Budget dépensé', value: 'budgetspent' },
                { text: 'Date de fin', value: 'enddate' },
                { text: 'Phase', value: 'phase' },
            ],

            items: [
                {
                    color: 'grey',
                    icon: 'mdi-office-building',
                },
            ]
        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
        this.getData();

        // http://localhost:15001/pmeflow/api/v1/entreprise/chercherParOid?oidContact=C0A838012C76C5F7C5CFE393

    },
    methods: {
        goBack() {
            return this.$router.go(-1);
        },
        async getData() {
            try {
                //Local request
                const response = await this.$http.get("http://localhost:15001/pmeflow/api/v1/entreprise/chercherParOid?oidEntreprise="+this.entreprise_id);
                this.entreprises=response.data;
            }catch(error) {
                console.log(error);
            }
        }
    },

};
</script>

<style scoped>
.red {
    background-color: red;
}

.borderRight {
    border-right: 0.1px solid black;
}

.v-btn {
    text-transform: none;
}

.v-img {
    justify-content: center;
}
</style>