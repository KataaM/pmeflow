<template>
    <v-container class="pa-0">
        <v-dialog v-model="dialog" max-width="1000px">
            <template v-slot:activator="{ on, attrs }">
                <v-btn v-bind="attrs" v-on="on" class="addButton">
                    Ajouter un contact
                </v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="text-h5">Ajouter un contact</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-row>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Prenom*" v-model="contactJson.donneesPerso.prenom" required>
                                </v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Nom*" v-model="contactJson.donneesPerso.nom" required>
                                </v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Date de naissance*"
                                    v-model="contactJson.donneesPerso.dateNaissance" required></v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-select :items="['Masculin', 'Féminin', 'Autres', 'Inconnu']"
                                    v-model="contactJson.donneesPerso.genre" label="Genre*" required></v-select>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-autocomplete :items="['Français', 'Anglais', 'Autres TODO']"
                                    v-model="contactJson.donneesPerso.langues" label="Langues" multiple
                                    chips></v-autocomplete>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Mobile*" v-model="contactJson.donneesPerso.mobile"
                                    required></v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Téléphone*" v-model="contactJson.donneesPerso.telephone"
                                    required></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Email*" v-model="contactJson.donneesPerso.email"
                                    hint="example@example.com" persistent-hint required></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row>

                            <v-col cols="12" sm="6" md="4">
                                <v-select :items="['France', 'Angleterre', 'Autres TODO']"
                                    v-model="contactJson.donneesPerso.adresses[0].pays" label="Pays*"
                                    required></v-select>
                            </v-col>

                            <v-col cols="12" sm="2" md="2">
                                <v-text-field label="Numéro"
                                    v-model="contactJson.donneesPerso.adresses[0].numero"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="6">
                                <v-text-field label="Rue*" required
                                    v-model="contactJson.donneesPerso.adresses[0].rue"></v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Ville*" required
                                    v-model="contactJson.donneesPerso.adresses[0].ville"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Code Postal*" required
                                    v-model="contactJson.donneesPerso.adresses[0].codePostal"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-select :items="['Var', 'Bouche du Rhônes', 'Autres TODO']" label="Département*"
                                    required v-model="contactJson.donneesPerso.adresses[0].departement"></v-select>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Site web" hint="https://example.com" persistent-hint
                                    v-model="contactJson.donneesPerso.siteWeb"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-autocomplete :items="['Ici', 'Tous', 'Les tags']" label="Tags" multiple chips
                                    v-model="contactJson.tags"></v-autocomplete>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Fax*" required
                                    v-model="contactJson.donneesPerso.fax"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Siret*" hint="Doit compoter 14 chiffres" persistent-hint required
                                    v-model="contactJson.coordsFinancieres.siret"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="4">
                                <v-text-field label="N° de TVA*" required
                                    v-model="contactJson.coordsFinancieres.tva"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="4">
                                <v-select :items="['Paiement comptant', 'Fin du mois', 'Autres TODO']"
                                    label="Conditions de paiement*" required
                                    v-model="contactJson.coordsFinancieres.conditionsPaiement"></v-select>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-text-field label="N° Compte IBAN*" required
                                    v-model="contactJson.coordsFinancieres.iban"></v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-text-field label="Code BIC*" required
                                    v-model="contactJson.coordsFinancieres.bicSwift"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="4">
                                <v-text-field label="PRIX ???????*" required
                                    v-model="contactJson.coordsFinancieres.prix"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="4">
                                <v-select :items="['Appliqué', 'Non appliqué']" label="Tva appliqué*" required
                                    v-model="contactJson.coordsFinancieres.tvaBool"></v-select>
                            </v-col>


                        </v-row>
                    </v-container>
                    <small>* Indique les champs requis</small>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" text @click="(dialog = false)">
                        Annuler
                    </v-btn>
                    <v-btn color="blue darken-1" text @click="addContact()">
                        Ajouter
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>


<script>
import {
    eventBus
} from "@/plugins/event-bus.js";
import json from '../assets/grc/contact.json';
import axios from 'axios';

export default {
    components: {},
    data() {
        return {
            contact_id: this.$route.params.contact_id,
            searchDataTable: '',
            contact: [],
            contactJson: json,
            dialog: false,
        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
        this.contactJson = json
    },
    methods: {

        addContact() {
            console.log("addContact called")

            this.dialog = false

            this.contactJson.coordsFinancieres.tvaBool == "Appliqué" ? this.contactJson.coordsFinancieres.tvaBool = true : this.contactJson.coordsFinancieres.tvaBool = false;

            axios.post('http://localhost:15001/pmeflow/api/v1/contact/ajouter', this.contactJson).then(response => {
                if (response.status === 200) {
                    this.$parent.addContactToList(this.contactJson);
                    this.emptyModel()
                }
            }).catch(error => console.log("Error while posting the json :" + error))

        },
        emptyModel() {
            console.log("Empty model called")
        },
    },
};
</script>

<style scoped>
.addButton {
    color: white !important;
    background-color: #00b2b2 !important;
    border: 1px solid #008a8c;
    text-transform: none;
}
</style>