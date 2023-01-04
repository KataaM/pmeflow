<template>
    <v-menu bot :offset-x="offset">
        <template v-slot:activator="{ on, attrs }">
            <v-btn fab x-small elevation="0" color="#99c9ff" class="white--text" v-bind="attrs" v-on="on">pl</v-btn>
        </template>

        <v-list>
            <v-list-item v-for="(item, index) in menuItems" :key="index">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
        </v-list>
    </v-menu>
</template>

<script>
export default {
    name: "loginButton",
    inheritAttrs: false,
    props: {
        label: String
    },
    data: () => ({
        menuItems: [
            { title: 'My Profile' },
            { title: 'Sign out' }
        ],
    })
};
</script>