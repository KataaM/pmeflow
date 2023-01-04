<template>

  <v-navigation-drawer app width="144">

    <!-- <v-list-item>
      <v-list-item-content>
        <v-list-item-title class="text-h6">
          Learning Vuetify
        </v-list-item-title>
        <v-list-item-subtitle> Navigation drawers</v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>  
    <v-divider></v-divider> -->
    <v-list dense>
      <v-list-item class="SelectedTile" active-class="SelectedTile-active" v-for="item in items" :key="item.title"
        :to="{ name: item.title }" link>
        <v-list-item-icon class="ListItemIcon">
          <v-icon>{{ item.icon }}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title class="ListItemTitles">{{ $t("pmeflow.application.menu." + item.title) }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>


  <!-- <v-list dense>
    <v-list-item :to="{ name: 'entreprises' }">
      <v-list-item-action>
        <v-icon>mdi-factory</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.entreprises") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'contacts' }">
      <v-list-item-action>
        <v-icon>mdi-contacts</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.contacts") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'opportunites' }">
      <v-list-item-action>
        <v-icon>mdi-check-circle-outline</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.opportunites") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'projets' }">
      <v-list-item-action>
        <v-icon>mdi-clipboard-text-multiple-outline</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.projets") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'suivitemps' }">
      <v-list-item-action>
        <v-icon>mdi-timer-outline</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.suivitemps") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
   </v-list>
   -->


  <!--
    <v-list-item :to="{ name: 'dossiers' }">
      <v-list-item-action>
        <v-icon>mdi-archive</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.dossiers") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'coaching_rdv' }">
      <v-list-item-action>
        <v-icon>mdi-calendar-month</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.coaching_rdv") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'abonnements' }">
      <v-list-item-action>
        <v-icon>mdi-timer</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.abonnements") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item :to="{ name: 'prestations' }">
      <v-list-item-action>
        <v-icon>mdi-cash</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.prestations") }}</v-list-item-title>
      </v-list-item-content>
      </v-list-item>

    <v-subheader></v-subheader>
    <v-list-item :to="{ name: 'mon-profil' }">
      <v-list-item-action>
        <v-icon>mdi-account-details</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.mon_profil") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item v-if="viewMenu(['JURISTE', 'ADMIN'])" :to="{ name: 'mon-planning' }">
      <v-list-item-action>
        <v-icon>mdi-calendar</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.mon_planning") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item v-if="viewMenu(['ADMIN_FONCTIONNEL', 'ADMIN'])" :to="{ name: 'clients' }">
      <v-list-item-action>
        <v-icon>mdi-account-multiple-outline</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.clients") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'utilisateurs' }">
      <v-list-item-action>
        <v-icon>mdi-account-supervisor</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.utilisateurs") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>

    <v-subheader></v-subheader>
    <v-list-item v-if="viewMenu(['ADMIN_FONCTIONNEL', 'ADMIN'])" :to="{ name: 'ref_abonnement' }">
      <v-list-item-action>
        <v-icon>mdi-archive-clock-outline</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.ref_abonnement") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-list-item v-if="viewMenu(['ADMIN_FONCTIONNEL', 'ADMIN'])" :to="{ name: 'ref_prestation' }">
      <v-list-item-action>
        <v-icon>mdi-book-open</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.ref_prestation") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>
    <v-subheader></v-subheader>
    <v-list-item v-if="viewMenu(['ADMIN'])" :to="{ name: 'configuration-organisations' }">
      <v-list-item-action>
        <v-icon>mdi-cog</v-icon>
      </v-list-item-action>
      <v-list-item-content>
        <v-list-item-title>{{ $t("pmeflow.application.menu.configuration.simple") }}</v-list-item-title>
      </v-list-item-content>
    </v-list-item>-->
</template>

<script>
export default {
  name: "menuGauche",
  inheritAttrs: false,
  props: {
    label: String,
    roles: [],
  },
  data: () => ({
    items: [
      { title: 'entreprises', icon: 'mdi-factory' },
      { title: 'contacts', icon: 'mdi-contacts-outline  ' },
      { title: 'opportunites', icon: 'mdi-check-circle-outline' },
      { title: 'projets', icon: 'mdi-clipboard-text-multiple-outline' },
      { title: 'suivitemps', icon: 'mdi-timer-outline' },
    ],
  }),
  computed: {
    inputListeners: function () {
      var vm = this;
      return Object.assign(
        {},
        this.$listeners,
        {
          input: function (event) {
            vm.$emit("input", event.target.value);
          }
        }
      );
    }
  },
  methods: {
    viewMenu(authorize) {
      let result = false;
      if (this.roles != null && this.roles.length > 0) {
        if (authorize != null && authorize.length > 0) {
          authorize.forEach(role => {
            result |= this.roles.includes(role);
          });
        }
        else {
          result = true;
        }
      }
      return result;
    }
  }
};
</script>

<style>
.v-application .v-navigation-drawer {
  background-color: #2a3b4d;
}

.v-navigation-drawer .v-icon {
  color: white !important;
}

/* Remove padding top for list*/
.v-list {
  padding-top: 0
}

/* Remove padding left and right for list items*/
.v-list-item {
  padding: 0;
  height: 69px;
}

.ListItemTitles {
  color: white;
  text-align: center;
}

.ListItemIcon {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  padding-bottom: 10px;
  margin: 0px !important;
}

.SelectedTile {
  display: block;
}

.SelectedTile:hover:not(.SelectedTile-active) {
  background: #364c63;
}

.SelectedTile-active {
  background: #00b2b2;
}
</style>