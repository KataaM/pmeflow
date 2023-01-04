<template>
    <v-app-bar app dense color="white" elevation="0">
        <!-- <loginButton /> -->

        <!-- Entreprise à la palce de Liste ?? -->
        <v-toolbar-title>Liste</v-toolbar-title>

        <v-spacer></v-spacer>
        <SearchBarComponent />

        <v-btn icon color="#00b2b2">
            <v-icon> mdi-plus-box-outline </v-icon>
        </v-btn>
        <v-btn icon>
            <v-icon> mdi-bell-outline </v-icon>
        </v-btn>
        <v-btn icon>
            <v-icon> mdi-account-group-outline </v-icon>
        </v-btn>
        <v-btn icon>
            <v-icon> mdi-timer-outline </v-icon>
        </v-btn>

        <LoginButtonComponent label="TODO INITIAL" />

    </v-app-bar>
</template>

<script>


// import loginButton from '@/components/topbar/loginButton-component.vue'

import LoginButtonComponent from './loginButton-component.vue';
import SearchBarComponent from './searchBar-component.vue';

export default {
    components: {
        LoginButtonComponent,
        SearchBarComponent
    },
};
</script>

<style>
.v-application .v-toolbar__content {
    box-shadow: inset 0 -1px 0 0 #e4e4e6;
}

.v-application .v-toolbar__title {
    box-shadow: inset 0 -3px 0 0 #00b2b2;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: .6px;
    text-transform: uppercase;
    margin: 6px;
}
</style>
