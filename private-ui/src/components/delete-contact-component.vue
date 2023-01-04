<template>
    <v-dialog v-model="dialog" max-width="1000px">
        <template v-slot:activator="{ on, attrs }">
            <v-btn class="ma-0" icon v-bind="attrs" v-on="on">
                <v-icon>mdi-delete-forever</v-icon>
            </v-btn>
        </template>
        <v-card>
            <v-card-title class="text-h5">
                Êtes-vous sûr de vouloir supprimer le contact : {{ firstname }} {{ lastname }} ?
            </v-card-title>
            <v-card-text>Vous n'aurez plus de possibilité de récupérer ce contact après suppression.</v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="dialog = false">
                    Annuler
                </v-btn>
                <v-btn color="red darken-1" text @click="deleteContact()">
                    Supprimer
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>


</template>


<script>
import {
    eventBus
} from "@/plugins/event-bus.js";
import axios from 'axios';

export default {
    components: {},
    props: {
        firstname: String,
        lastname: String,
        contactOid: String
    },
    data() {
        return {
            dialog: false,

        }
    },
    created() {
        eventBus.$emit("errors-loaded", []);
    },
    methods: {

        deleteContact() {
            console.log("deleteContact called")

            //Close the delete popup
            this.dialog = false;

            axios.delete('http://localhost:15001/pmeflow/api/v1/contact/supprimer?oidContact=' + this.contactOid).then(response => {
                if (response.status === 200) {
                    this.$router.push({ name: 'contacts', params: { title: "contacts" } });
                }
            }).catch(error => console.log("Error while deleting the contact :" + error))
        },
    },
};
</script>
