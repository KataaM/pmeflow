<template>
    <v-dialog v-model="dialog" persistent max-width="1200px">
        <template v-slot:activator="{ on, attrs }">
            <v-btn class="addButton" v-bind="attrs" v-on="on" outlined>
                Ajouter une entreprise
            </v-btn>
        </template>
        <v-toolbar dark class="addButton">
            <v-btn icon dark @click="dialog = false">
                <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Coordonnées Entreprise</v-toolbar-title>
            <v-spacer></v-spacer>
        </v-toolbar>
        <v-card>
            <v-card-title>
            </v-card-title>
            <v-card-text>
                <v-form v-model="valid">
                    <v-container>
                        <v-row>
                            <h5>Informations Entreprise</h5>
                        </v-row>
                        <v-row>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Entreprise *" v-model="entrepriseJson.donneesEntreprise.libelle"
                                    :rules="nameRules" required>
                                </v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Site Web" v-model="entrepriseJson.donneesEntreprise.siteWeb">
                                </v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Email" v-model="entrepriseJson.donneesEntreprise.email"
                                    :rules="emailRules">
                                </v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Telephone" v-model="entrepriseJson.donneesEntreprise.telephone">
                                </v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Fax " v-model="entrepriseJson.donneesEntreprise.fax"
                                    :rules="nameRules" required>
                                </v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-text-field label="Secteur" v-model="entrepriseJson.donneesEntreprise.secteur">
                                </v-text-field>
                            </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="Type d'entreprise"
                        v-model="entrepriseJson.donneesEntreprise.typeEntreprise">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="3">
                    <v-select label="Langue" :items="['Anglais', 'Français', 'Chinois']" v-model="entrepriseJson.donneesEntreprise.langues">
                    </v-select>
                </v-col>
                <v-col cols="12" sm="6" md="12">
                    <h5>Adresse</h5>
                    <v-row>
                    <v-col cols="12" sm="6" md="3">
                        <v-text-field label="Numero" v-model="entrepriseJson.donneesEntreprise.adresses[0].numero"></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="6">
                        <v-text-field label="Rue" v-model="entrepriseJson.donneesEntreprise.adresses[0].rue"></v-text-field>
                    </v-col>
                    <v-divider vertical></v-divider>
                    <v-col cols="12" sm="6" md="3">
                        <v-text-field label="Pays" v-model="entrepriseJson.donneesEntreprise.adresses[0].pays"></v-text-field>
                    </v-col>
                    </v-row>
                    <v-row>
                    <v-col cols="12" sm="6" md="3">
                        <v-text-field label="Code Postal" v-model="entrepriseJson.donneesEntreprise.adresses[0].codePostal"></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="6">
                        <v-text-field label="Ville" v-model="entrepriseJson.donneesEntreprise.adresses[0].ville"></v-text-field>
                    </v-col>
                    <v-divider vertical></v-divider>
                    <v-col cols="12" sm="6" md="3">
                        <v-text-field label="Département" v-model="entrepriseJson.donneesEntreprise.adresses[0].departement"></v-text-field>
                    </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-spacer></v-spacer>
            <v-row>
                <h5>Informations Financières</h5>
            </v-row>
            <v-row>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="SIRET" v-model="entrepriseJson.coordsFinancieres.siret">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="IBAN" v-model="entrepriseJson.coordsFinancieres.iban">
                    </v-text-field>
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="BIC SWIFT"
                        v-model="entrepriseJson.coordsFinancieres.bicSwift">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="Conditions Paiement" v-model="entrepriseJson.coordsFinancieres.conditionsPaiement">
                    </v-text-field>
                </v-col>

            </v-row>
  
            <v-spacer></v-spacer>
            <v-row>
                <h5>Industrie</h5>
            </v-row>
            <v-row>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field label="Code APE" v-model="entrepriseJson.donneesEntreprise.ape">
                    </v-text-field>
                </v-col>
            </v-row>
            </v-container>
        </v-form>
        <v-card-subtitle></v-card-subtitle>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            class="closeButton"
            @click="dialog = false"
          >
            Annuler
          </v-btn>
          <v-btn
            class="saveButton"
            @click="addEntreprise()"
          >
            Enregistrer
          </v-btn>
        </v-card-actions>
        <small>*champs requis</small>
        </v-card-text>
        <v-card-actions>
        <v-spacer></v-spacer>
        </v-card-actions>
    </v-card>
    </v-dialog>
</template>
  
<script>
import {
    eventBus
} from "@/plugins/event-bus.js";
import json from '../assets/grc/entreprise.json';

export default {
    components: {

    },
    props: {
        title: String
    },
    data() {
        return {
            entreprise_id: this.$route.params.entreprise_id,
            searchDataTable: '',
            entreprise: [],
            entrepriseJson: json,
            dialog: false,
            valid: false,
            website: '',
            nameRules: [
                v => !!v || 'La nomination est requise',
                v => v.length <= 10 || 'Name must be less than 10 characters',
            ],
            email: '',
            emailRules: [
                v => /.+@.+/.test(v) || 'E-mail must be valid',
            ],
            webSiteRules: [
                v => /www.+..+/.test(v) || 'Web Site must be valid',
            ],
        }

    },
    created() {
        eventBus.$emit("errors-loaded", []);
        this.entrepriseJson = json
    },
    methods: {
        addEntreprise() {
            console.log("addEntreprise called")
            this.dialog = false


            console.log(this.entrepriseJson.donneesEntreprise.adresses[0].codePostal)


            this.entrepriseJson.coordsFinancieres.tvaBool == "Appliqué" ? this.entrepriseJson.coordsFinancieres.tvaBool = true : this.entrepriseJson.coordsFinancieres.tvaBool = false;

            console.log(JSON.stringify(this.entrepriseJson))
            this.$http.post("http://localhost:15001/pmeflow/api/v1/entreprise/ajouter", this.entrepriseJson)
                .then(response => {
                    //success 
                    return response
                }, response => {
                    //error
                    console.log("error :")
                    console.log(JSON.stringify(response))
                }
                ).then(function (json) {

                    console.log("RESPONSE : " + JSON.stringify(json.status))
                }
                );

            this.$parent.addEntrepriseToList(this.entrepriseJson);
            this.emptyModel()
        },
        emptyModel() {
            console.log("Empty model called")
            // this.entrepriseJson = JSON.clone(json)
        },
    }
}
</script>
  
<style lang="scss" scoped>
.addButton {
    color: white !important;
    background-color: #00b2b2 !important;
    border: 1px solid #008a8c;
}

.saveButton {
    color: white !important;
    background-color: #00b2b2 !important;
    border: 1px solid #008a8c;
}

.closeButton {
    color: grey !important;
    border: 1px solid #008a8c;
}
</style>