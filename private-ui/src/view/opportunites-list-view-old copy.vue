<template>

    <v-container>
        <v-row no-gutters>
            <v-col cols="9">
                <v-card class="text-h5 font-weight-bold" elevation="0">
                    {{ title | capitalize }}
                </v-card>
            </v-col>
            <v-col cols="3">
                <div class="buttonContainer">
                    <v-btn outlined>
                        Exporter
                    </v-btn>
                    <v-btn class="addButton">
                        Ajouter {{ title.slice(0, -1) }}
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
        <v-row no-gutters>
            <div  v-for="(header) in headers" :key="header">
                <v-col class="steps" cols="2">
                    <!--<v-hover v-slot="{ hover }">-->
                    <h3 class="title">Nouveau</h3>
                    <draggable class="list-group" :list="opportunites" group="people" @change="log">
                        <div v-for="(element) in opportunites" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <opportunites :objet="element"/> 
                        </div>
                    </draggable>
                    <!--</v-hover>  -->
                </v-col>
                <v-col class="steps" cols="2">
                    <!--<v-hover v-slot="{ hover }">-->
                    <h3 class="title">Contacté</h3>
                    <draggable class="list-group" :list="opportunites" group="people" @change="log">
                        <div v-for="(element) in opportunites" :key="element.opportunite" v-on:click="oppClick(element.id)">
                            <opportunites :objet="element"/> 
                        </div>
                    </draggable>
                    <!--</v-hover>  -->
                </v-col>
            </div>
            <!-- <v-col class="steps" cols="2">
                <h3 class="title">Contacté</h3>
                <draggable class="list-group" :list="contacted" group="people" @change="log">
                    <div class="list-group-item" v-for="(element) in contacted" :key="element.opportunite">
                    {{ element.entreprise }} {{ element.opportunite }} {{ element.prix }}
                    <v-chip class="chipDate">
                      {{element.date}}
                    </v-chip>
                    <v-chip class="chipProg">
                      {{element.progression}}
                    </v-chip>    
                    </div>
                </draggable>
            </v-col>
            <v-col class="steps" cols="2">
                <h3>Rendez-vous prévu</h3>
                <draggable class="list-group" :list="rdv" group="people" @change="log">
                    <div class="list-group-item" v-for="(element) in rdv" :key="element.opportunite">
                    {{ element.entreprise }} {{ element.opportunite }} {{ element.prix }}
                    <v-chip class="chipDate">
                      {{element.date}}
                    </v-chip>
                    <v-chip class="chipProg">
                      {{element.progression}}
                    </v-chip>    
                    </div>
                </draggable>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="title">Devis envoyé</h3>
                <draggable class="list-group" :list="devis" group="people" @change="log">
                    <div class="list-group-item" v-for="(element) in devis" :key="element.opportunite">
                    {{ element.entreprise }} {{ element.opportunite }} {{ element.prix }}
                    <v-chip class="chipDate">
                      {{element.date}}
                    </v-chip>
                    <v-chip class="chipProg">
                      {{element.progression}}
                    </v-chip>   
                    </div>
                </draggable>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="title">Accepté</h3>
                <draggable class="list-group" :list="accepter" group="people" @change="log">
                    <div class="list-group-item" v-for="(element) in accepter" :key="element.opportunite">
                    {{ element.entreprise }} {{ element.opportunite }} {{ element.prix }}
                    <v-chip class="chipDate">
                      {{element.date}}
                    </v-chip>
                    <v-chip class="chipProg">
                      {{element.progression}}
                    </v-chip>    
                    </div>
                </draggable>
            </v-col>
            <v-col class="steps" cols="2">
                <h3 class="title">Refusé</h3>
                <draggable class="list-group" :list="refus" group="people" @change="log">
                    <div class="list-group-item" v-for="(element) in refus" :key="element.opportunite">
                    {{ element.entreprise }} {{ element.opportunite }} {{ element.prix }}
                    <v-chip class="chipDate">
                      {{element.date}}
                    </v-chip>
                    <v-chip class="chipProg">
                      {{element.progression}}
                    </v-chip>   
                    </div>
                </draggable>
            </v-col> -->
        </v-row>

    </v-container>

</template>
  
<script>
import { eventBus } from "@/plugins/event-bus.js";
import draggable from 'vuedraggable';
import opportunites from '@/components/ui/opportunites-component';
export default {
    components: {
        draggable,
        opportunites,
    },
    data() {
        return {
            title: this.$route.params.title,
            opportunites: [
               {
                    id : '1',
                    entreprise : "Entreprise 5",
                    opportunite : "opportunite",
                    prix : "150€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "nouveau"
               },

               {
                    id : '2',
                    entreprise : "Entreprise 4",
                    opportunite : "opportunite",
                    prix : "50€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "contacted"
               }
            ],

            headers: [
              { Nouveau :
              [
               {
                    id : '1',
                    entreprise : "Entreprise 5",
                    opportunite : "opportunite",
                    prix : "150€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "Nouveau"
               },

               {
                    id : '2',
                    entreprise : "Entreprise 4",
                    opportunite : "opportunite",
                    prix : "50€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "Contacted"
               }
            ]
              },

              { Contacté :
              [
               {
                    id : '3',
                    entreprise : "Entreprise 6",
                    opportunite : "opportunite",
                    prix : "150€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "Nouveau"
               },

               {
                    id : '4',
                    entreprise : "Entreprise 8",
                    opportunite : "opportunite",
                    prix : "50€",
                    date : "11/11/11",
                    progression : "0%",
                    etat : "Contacted"
               }
            ]
              },
            
            ],
        
            // contacted: [
            //    {
            //         id : '2',
            //         entreprise : "Entreprise 2",
            //         opportunite : "opportunite",
            //         prix : "50€",
            //         date : "11/11/11",
            //         progression : "0%",
            //    }
            // ],

            // rdv: [
            //    {
            //         id : '3',
            //         entreprise : "Entreprise 3",
            //         opportunite : "opportunite",
            //         prix : "15000€",
            //         date : "11/11/11",
            //         progression : "0%",
            //    }
            // ],

            // devis: [
            //    {
            //         id : '4',
            //         entreprise : "Entreprise 4",
            //         opportunite : "opportunite",
            //         prix : "50000000€",
            //         date : "11/11/11",
            //         progression : "0%",
            //    }
            // ],

            // accepter: [
            //    {
            //         id : '5',
            //         entreprise : "Entreprise 5",
            //         opportunite : "opportunite",
            //         prix : "300€",
            //         date : "11/11/11",
            //         progression : "0%",
            //    }
            // ],

            // refus: [
            //    {
            //         id : '6',
            //         entreprise : "Entreprise 6",
            //         opportunite : "opportunite",
            //         prix : "286€",
            //         date : "11/11/11",
            //         progression : "0%",
            //    }
            // ],
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
            this.$router.push({ name: 'opportunites_details', params: { opportunite_id: id } });
            // this.$router.push({ name: 'opportunites_details', params: { opportunite_id: item.id } });
            console.log("prout");
        },
    },
    filters: {
        capitalize: function (value) {
            if (!value) return ''
            value = value.toString()
            return value.charAt(0).toUpperCase() + value.slice(1)
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

.steps {
    border:1px solid #EEE;
    background-color: #CCD0D0;
}

.list-group {
    border:1px solid #EEE;
    background-color: #CCD0D0;
}
.chipDate{
    color: #00b2b2;
}

.chipProg {
    color: #00b2b2;
}
.title{
    margin-bottom: 22px;
}
</style>