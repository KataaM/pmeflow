<template>
  <v-app class="pmeflow">
    <v-overlay v-if="loading" :value="loading" opacity="0.5" style="backdrop-filter: blur(5px);">
      <v-progress-circular indeterminate size="48"></v-progress-circular>
    </v-overlay>
    <topbar />
    <menu-principal />

    <v-main>
      <v-card flat tile width="100%" class="errors-container" v-if="errors.length > 0">
        <v-card-text>
          <errors :items="errors" />
        </v-card-text>
      </v-card>
      <router-view></router-view>
    </v-main>

    <!-- <v-app-bar app flat height="50" style="background-color: var(--e-global-color-topbar) !important;">
      <v-col cols="2" class="text-left">
        <v-icon @click.stop="clickOnMenu">{{ menuconfig.icon }}</v-icon>
      </v-col>
      <v-col cols="7" class="text-center logo">PmeFlow</v-col>
      <v-col cols="3" class="text-right">
        <v-menu offset-y>
          <template v-slot:activator="{ on }">
            <span v-on="on" height="40px">
              <v-avatar color="#69aa8a" height="30">
                <span class="white--text headline">
                  {{ connected.initiales }}
                </span>
              </v-avatar>
              <span class="ml-2" v-if="screen.isDesktop">
                {{ connected.lastfirst }}
              </span>
            </span>
          </template>
          <v-list>
            <v-list-item @click="goTo('mon-profil')">
              <v-list-item-title>Mon profil</v-list-item-title>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item @click="logout()">
              <v-list-item-title>Se déconnecter</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-col>
    </v-app-bar> -->

    <!-- <v-navigation-drawer :absolute="menuconfig.absolute" :temporary="menuconfig.temporary"
      :mini-variant.sync="menuconfig.miniVariant" :permanent="menuconfig.permanent" v-model="menuconfig.drawer" app
      width="144" class="blue-grey darken-3">
      <menu-principal :roles="roles" />
    </v-navigation-drawer> -->

  </v-app>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import errors from '@/components/ui/errors-render.vue';
import menuPrincipal from '@/components/menu/principal.vue';
import topbar from '@/components/topbar/topbar-component.vue';
import router from "@/plugins/router";

export default {
  name: 'pmeflow-app',

  components: {
    errors,
    menuPrincipal,
    topbar
  },

  data: () => ({
    errors: [],
    roles: [],
    screen: {
      isMobile: false,
      isTablet: false,
      isDesktop: false,
      width: window.innerWidth,
      height: window.innerHeight
    },
    menuconfig: {
      absolute: false,
      drawer: false,
      miniVariant: false,
      permanent: false,
      temporary: false,
      icon: "mdi-menu",
    },
    loading: true,
    connected: {
      initiales: "IC",
      lastFirst: ""
    }
  }),

  created() {
    eventBus.$on("errors-loaded", errors => {
      this.errors = errors;
    });
    this.$vuetify.theme.dark = false;
    this.$store.commit("LOAD_CONFIGURATION");
    if (sessionStorage.getItem("oidc-post-load") == null) {
      sessionStorage.setItem("oidc-post-load", false);
    }
    this.keycloakService.authenticate();
    setTimeout(() => {
      this.roles = sessionStorage.getItem("oidc-roles");
      if (this.roles.length > 0) {
        this.loading = false;
        this.connected.initiales = this.keycloakService.getAvatarDatas().initiales;
        this.connected.lastfirst = this.keycloakService.getAvatarDatas().lastfirst;
      }
    }, 1000);
    setInterval(() => {
      this.keycloakService.keycloak
        .updateToken(15)
        .catch(error => {
          console.log(JSON.stringify(error));
        })
        .then(refreshed => {
          if (refreshed) {
            let token = {
              access_token: this.keycloakService.keycloak.token,
              refresh_token: this.keycloakService.keycloak.refreshToken,
            };
            sessionStorage.setItem("oidc-token", JSON.stringify(token));
            sessionStorage.setItem("oidc-access_token", token.access_token);
            sessionStorage.setItem("oidc-refresh_token", token.refresh_token);
            sessionStorage.setItem("oidc-roles", JSON.stringify(this.keycloakService.keycloak.tokenParsed.resource_access[process.env.VUE_APP_OIDC_CLIENT_ID].roles));
            console.info("Token refreshed: " + refreshed);
          }
          else {
            console.debug(
              "Token not refreshed, valid for " +
              Math.round(
                this.keycloakService.keycloak.tokenParsed.exp +
                this.keycloakService.keycloak.timeSkew -
                new Date().getTime() / 1000
              ) +
              " seconds"
            );
          }
        })
        .catch((error) => {
          console.error(JSON.stringify(error));
        });
    }, 10000);
  },
  computed: {
    dynamicAttrs: function () {
      return {
        "margin-left": this.reduce ? "56px" : "256px",
      };
    },
    keycloakService: function () {
      return this.$store.state.keycloakService;
    },
    currentYear: function () {
      return new Date().getFullYear();
    }
  },

  beforeDestroy() {
    if (typeof window !== 'undefined') {
      window.removeEventListener('resize', this.onResize, { passive: true })
    }
  },

  mounted() {
    this.onResize()
    window.addEventListener('resize', this.onResize, { passive: true })
    this.loading = false;
  },

  methods: {
    onResize() {
      this.screen.width = window.innerWidth;
      this.screen.height = window.innerHeight;
      this.screen.isMobile = window.innerWidth <= 680;
      this.screen.isTablet = (window.innerWidth > 680 && window.innerWidth <= 1024);
      this.screen.isDesktop = !(this.screen.isMobile || this.screen.isTablet);

      let sessionScreenState = JSON.parse(sessionStorage.getItem("app-screenState"));
      let sessionMenuConfig = JSON.parse(sessionStorage.getItem("app-menuConfig"));

      if (sessionScreenState != null && sessionMenuConfig != null &&
        sessionScreenState.isMobile == this.screen.isMobile &&
        sessionScreenState.isTablet == this.screen.isTablet &&
        sessionScreenState.isDesktop == this.screen.isDesktop) {
        this.menuconfig = sessionMenuConfig;
      }
      else {
        if (this.screen.isMobile) {
          this.menuconfig.absolute = true;
          this.menuconfig.miniVariant = false;
          this.menuconfig.permanent = false;
          this.menuconfig.temporary = true;
        }
        else if (this.screen.isTablet) {
          this.menuconfig.absolute = true;
          this.menuconfig.permanent = true;
          this.menuconfig.temporary = false;
          if (!this.menuconfig.miniVariant) {
            this.menuconfig.icon = "mdi-arrow-collapse-left";
          }
          else {
            this.menuconfig.icon = "mdi-arrow-collapse-right";
          }
        }
        else {
          this.menuconfig.absolute = true;
          this.menuconfig.permanent = true;
          this.menuconfig.temporary = false;
          if (!this.menuconfig.miniVariant) {
            this.menuconfig.icon = "mdi-arrow-collapse-left";
          }
          else {
            this.menuconfig.icon = "mdi-arrow-collapse-right";
          }
        }
        if (this.screen.isTablet) { this.menuconfig.miniVariant = true; }
        if (this.screen.isMobile || this.screen.isDesktop) { this.menuconfig.miniVariant = false; }
      }
      sessionStorage.setItem("app-screenState", JSON.stringify(this.screen));
      sessionStorage.setItem("app-menuConfig", JSON.stringify(this.menuconfig));
    },
    viewMenu(roles) {
      console.debug(JSON.stringify(roles));
      return true;
    },
    clickOnMenu() {
      if (this.screen.isMobile) {
        this.menuconfig.drawer = !this.menuconfig.drawer;
      }
      else {
        this.menuconfig.miniVariant = !this.menuconfig.miniVariant;
        if (!this.menuconfig.miniVariant) {
          this.menuconfig.icon = "mdi-minus-box";
        }
        else {
          this.menuconfig.icon = "mdi-arrow-collapse-right";
        }
      }
      sessionStorage.setItem("app-menuConfig", JSON.stringify(this.menuconfig));
    },
    logout() {
      this.keycloakService.logout();
    },
    goTo(route) {
      router.push(route);
    }
  }
};
</script>
<style>
@import url('https://fonts.googleapis.com/css?family=Inter');

.pmeflow {
  font-family: Inter, trebuchet ms, Verdana, Arial, sans-serif;
}

.v-application .v-main {
  background-color: #f7f7fa;
}

/* .v-navigation-drawer {
  background-image: linear-gradient(55deg, rgba(208, 208, 208, 0.03) 0%, rgba(208, 208, 208, 0.03) 20%, rgba(55, 55, 55, 0.03) 20%, rgba(55, 55, 55, 0.03) 40%, rgba(81, 81, 81, 0.03) 40%, rgba(81, 81, 81, 0.03) 60%, rgba(208, 208, 208, 0.03) 60%, rgba(208, 208, 208, 0.03) 80%, rgba(191, 191, 191, 0.03) 80%, rgba(191, 191, 191, 0.03) 100%), linear-gradient(291deg, rgba(190, 190, 190, 0.02) 0%, rgba(190, 190, 190, 0.02) 14.286%, rgba(105, 105, 105, 0.02) 14.286%, rgba(105, 105, 105, 0.02) 28.572%, rgba(230, 230, 230, 0.02) 28.572%, rgba(230, 230, 230, 0.02) 42.858%, rgba(216, 216, 216, 0.02) 42.858%, rgba(216, 216, 216, 0.02) 57.144%, rgba(181, 181, 181, 0.02) 57.144%, rgba(181, 181, 181, 0.02) 71.42999999999999%, rgba(129, 129, 129, 0.02) 71.43%, rgba(129, 129, 129, 0.02) 85.71600000000001%, rgba(75, 75, 75, 0.02) 85.716%, rgba(75, 75, 75, 0.02) 100.002%), linear-gradient(32deg, rgba(212, 212, 212, 0.03) 0%, rgba(212, 212, 212, 0.03) 12.5%, rgba(223, 223, 223, 0.03) 12.5%, rgba(223, 223, 223, 0.03) 25%, rgba(11, 11, 11, 0.03) 25%, rgba(11, 11, 11, 0.03) 37.5%, rgba(86, 86, 86, 0.03) 37.5%, rgba(86, 86, 86, 0.03) 50%, rgba(106, 106, 106, 0.03) 50%, rgba(106, 106, 106, 0.03) 62.5%, rgba(220, 220, 220, 0.03) 62.5%, rgba(220, 220, 220, 0.03) 75%, rgba(91, 91, 91, 0.03) 75%, rgba(91, 91, 91, 0.03) 87.5%, rgba(216, 216, 216, 0.03) 87.5%, rgba(216, 216, 216, 0.03) 100%), linear-gradient(312deg, rgba(113, 113, 113, 0.01) 0%, rgba(113, 113, 113, 0.01) 14.286%, rgba(54, 54, 54, 0.01) 14.286%, rgba(54, 54, 54, 0.01) 28.572%, rgba(166, 166, 166, 0.01) 28.572%, rgba(166, 166, 166, 0.01) 42.858%, rgba(226, 226, 226, 0.01) 42.858%, rgba(226, 226, 226, 0.01) 57.144%, rgba(109, 109, 109, 0.01) 57.144%, rgba(109, 109, 109, 0.01) 71.42999999999999%, rgba(239, 239, 239, 0.01) 71.43%, rgba(239, 239, 239, 0.01) 85.71600000000001%, rgba(54, 54, 54, 0.01) 85.716%, rgba(54, 54, 54, 0.01) 100.002%), linear-gradient(22deg, rgba(77, 77, 77, 0.03) 0%, rgba(77, 77, 77, 0.03) 20%, rgba(235, 235, 235, 0.03) 20%, rgba(235, 235, 235, 0.03) 40%, rgba(215, 215, 215, 0.03) 40%, rgba(215, 215, 215, 0.03) 60%, rgba(181, 181, 181, 0.03) 60%, rgba(181, 181, 181, 0.03) 80%, rgba(193, 193, 193, 0.03) 80%, rgba(193, 193, 193, 0.03) 100%), linear-gradient(80deg, rgba(139, 139, 139, 0.02) 0%, rgba(139, 139, 139, 0.02) 14.286%, rgba(114, 114, 114, 0.02) 14.286%, rgba(114, 114, 114, 0.02) 28.572%, rgba(240, 240, 240, 0.02) 28.572%, rgba(240, 240, 240, 0.02) 42.858%, rgba(221, 221, 221, 0.02) 42.858%, rgba(221, 221, 221, 0.02) 57.144%, rgba(74, 74, 74, 0.02) 57.144%, rgba(74, 74, 74, 0.02) 71.42999999999999%, rgba(201, 201, 201, 0.02) 71.43%, rgba(201, 201, 201, 0.02) 85.71600000000001%, rgba(187, 187, 187, 0.02) 85.716%, rgba(187, 187, 187, 0.02) 100.002%), linear-gradient(257deg, rgba(72, 72, 72, 0.03) 0%, rgba(72, 72, 72, 0.03) 16.667%, rgba(138, 138, 138, 0.03) 16.667%, rgba(138, 138, 138, 0.03) 33.334%, rgba(54, 54, 54, 0.03) 33.334%, rgba(54, 54, 54, 0.03) 50.001000000000005%, rgba(161, 161, 161, 0.03) 50.001%, rgba(161, 161, 161, 0.03) 66.668%, rgba(17, 17, 17, 0.03) 66.668%, rgba(17, 17, 17, 0.03) 83.33500000000001%, rgba(230, 230, 230, 0.03) 83.335%, rgba(230, 230, 230, 0.03) 100.002%), linear-gradient(47deg, rgba(191, 191, 191, 0.01) 0%, rgba(191, 191, 191, 0.01) 16.667%, rgba(27, 27, 27, 0.01) 16.667%, rgba(27, 27, 27, 0.01) 33.334%, rgba(66, 66, 66, 0.01) 33.334%, rgba(66, 66, 66, 0.01) 50.001000000000005%, rgba(36, 36, 36, 0.01) 50.001%, rgba(36, 36, 36, 0.01) 66.668%, rgba(230, 230, 230, 0.01) 66.668%, rgba(230, 230, 230, 0.01) 83.33500000000001%, rgba(93, 93, 93, 0.01) 83.335%, rgba(93, 93, 93, 0.01) 100.002%), linear-gradient(90deg, #ffffff, #ffffff) !important;
  background-color: blue;
} */

/* .pmeflow {
  --e-global-color-topbar: #a5d2ff;
  --e-global-color-page-title: #e2f3ff;
  --e-global-color-footer: #eaeaea;
}

.required label::after {
  content: " *";
  color: red !important;
}

.logo {
  font-family: 'dancing script';
  font-weight: 800;
  font-size: 2.5rem;
}

.v-dialog:not(.v-dialog--fullscreen) {
  max-height: 90vh;
  margin-top: 30px;
  margin-right: 0px;
  width: 40vw;
  right: 0px;
  margin-left: auto;
  margin-bottom: 0px;
  border-radius: 0px;
}

footer {
  background-color: var(--e-global-color-footer) !important;
}

.v-application {
  width: 100vw !important;
}

.v-main {
  max-width: 98.7%;
}

.v-navigation-drawer {
  height: 600vh !important;
  background-image: linear-gradient(55deg, rgba(208, 208, 208, 0.03) 0%, rgba(208, 208, 208, 0.03) 20%, rgba(55, 55, 55, 0.03) 20%, rgba(55, 55, 55, 0.03) 40%, rgba(81, 81, 81, 0.03) 40%, rgba(81, 81, 81, 0.03) 60%, rgba(208, 208, 208, 0.03) 60%, rgba(208, 208, 208, 0.03) 80%, rgba(191, 191, 191, 0.03) 80%, rgba(191, 191, 191, 0.03) 100%), linear-gradient(291deg, rgba(190, 190, 190, 0.02) 0%, rgba(190, 190, 190, 0.02) 14.286%, rgba(105, 105, 105, 0.02) 14.286%, rgba(105, 105, 105, 0.02) 28.572%, rgba(230, 230, 230, 0.02) 28.572%, rgba(230, 230, 230, 0.02) 42.858%, rgba(216, 216, 216, 0.02) 42.858%, rgba(216, 216, 216, 0.02) 57.144%, rgba(181, 181, 181, 0.02) 57.144%, rgba(181, 181, 181, 0.02) 71.42999999999999%, rgba(129, 129, 129, 0.02) 71.43%, rgba(129, 129, 129, 0.02) 85.71600000000001%, rgba(75, 75, 75, 0.02) 85.716%, rgba(75, 75, 75, 0.02) 100.002%), linear-gradient(32deg, rgba(212, 212, 212, 0.03) 0%, rgba(212, 212, 212, 0.03) 12.5%, rgba(223, 223, 223, 0.03) 12.5%, rgba(223, 223, 223, 0.03) 25%, rgba(11, 11, 11, 0.03) 25%, rgba(11, 11, 11, 0.03) 37.5%, rgba(86, 86, 86, 0.03) 37.5%, rgba(86, 86, 86, 0.03) 50%, rgba(106, 106, 106, 0.03) 50%, rgba(106, 106, 106, 0.03) 62.5%, rgba(220, 220, 220, 0.03) 62.5%, rgba(220, 220, 220, 0.03) 75%, rgba(91, 91, 91, 0.03) 75%, rgba(91, 91, 91, 0.03) 87.5%, rgba(216, 216, 216, 0.03) 87.5%, rgba(216, 216, 216, 0.03) 100%), linear-gradient(312deg, rgba(113, 113, 113, 0.01) 0%, rgba(113, 113, 113, 0.01) 14.286%, rgba(54, 54, 54, 0.01) 14.286%, rgba(54, 54, 54, 0.01) 28.572%, rgba(166, 166, 166, 0.01) 28.572%, rgba(166, 166, 166, 0.01) 42.858%, rgba(226, 226, 226, 0.01) 42.858%, rgba(226, 226, 226, 0.01) 57.144%, rgba(109, 109, 109, 0.01) 57.144%, rgba(109, 109, 109, 0.01) 71.42999999999999%, rgba(239, 239, 239, 0.01) 71.43%, rgba(239, 239, 239, 0.01) 85.71600000000001%, rgba(54, 54, 54, 0.01) 85.716%, rgba(54, 54, 54, 0.01) 100.002%), linear-gradient(22deg, rgba(77, 77, 77, 0.03) 0%, rgba(77, 77, 77, 0.03) 20%, rgba(235, 235, 235, 0.03) 20%, rgba(235, 235, 235, 0.03) 40%, rgba(215, 215, 215, 0.03) 40%, rgba(215, 215, 215, 0.03) 60%, rgba(181, 181, 181, 0.03) 60%, rgba(181, 181, 181, 0.03) 80%, rgba(193, 193, 193, 0.03) 80%, rgba(193, 193, 193, 0.03) 100%), linear-gradient(80deg, rgba(139, 139, 139, 0.02) 0%, rgba(139, 139, 139, 0.02) 14.286%, rgba(114, 114, 114, 0.02) 14.286%, rgba(114, 114, 114, 0.02) 28.572%, rgba(240, 240, 240, 0.02) 28.572%, rgba(240, 240, 240, 0.02) 42.858%, rgba(221, 221, 221, 0.02) 42.858%, rgba(221, 221, 221, 0.02) 57.144%, rgba(74, 74, 74, 0.02) 57.144%, rgba(74, 74, 74, 0.02) 71.42999999999999%, rgba(201, 201, 201, 0.02) 71.43%, rgba(201, 201, 201, 0.02) 85.71600000000001%, rgba(187, 187, 187, 0.02) 85.716%, rgba(187, 187, 187, 0.02) 100.002%), linear-gradient(257deg, rgba(72, 72, 72, 0.03) 0%, rgba(72, 72, 72, 0.03) 16.667%, rgba(138, 138, 138, 0.03) 16.667%, rgba(138, 138, 138, 0.03) 33.334%, rgba(54, 54, 54, 0.03) 33.334%, rgba(54, 54, 54, 0.03) 50.001000000000005%, rgba(161, 161, 161, 0.03) 50.001%, rgba(161, 161, 161, 0.03) 66.668%, rgba(17, 17, 17, 0.03) 66.668%, rgba(17, 17, 17, 0.03) 83.33500000000001%, rgba(230, 230, 230, 0.03) 83.335%, rgba(230, 230, 230, 0.03) 100.002%), linear-gradient(47deg, rgba(191, 191, 191, 0.01) 0%, rgba(191, 191, 191, 0.01) 16.667%, rgba(27, 27, 27, 0.01) 16.667%, rgba(27, 27, 27, 0.01) 33.334%, rgba(66, 66, 66, 0.01) 33.334%, rgba(66, 66, 66, 0.01) 50.001000000000005%, rgba(36, 36, 36, 0.01) 50.001%, rgba(36, 36, 36, 0.01) 66.668%, rgba(230, 230, 230, 0.01) 66.668%, rgba(230, 230, 230, 0.01) 83.33500000000001%, rgba(93, 93, 93, 0.01) 83.335%, rgba(93, 93, 93, 0.01) 100.002%), linear-gradient(90deg, #ffffff, #ffffff) !important;
}

.v-navigation-drawer .v-list-item,
.v-navigation-drawer .v-subheader {
  min-height: 2rem !important;
}

.v-navigation-drawer .v-list-item,
.v-navigation-drawer .v-subheader {
  height: 2.4rem !important;
}

.v-navigation-drawer .v-list-item,
.v-navigation-drawer .v-icon {
  color: rgb(113, 113, 113) !important;
}

.v-navigation-drawer .v-list-item__action,
.v-navigation-drawer .v-list-item__content {
  padding-top: 5px !important;
  padding-bottom: 5px !important;
  margin-top: 0px !important;
  margin-bottom: 0px !important;
}

.v-toolbar__content,
.v-toolbar__extension {
  margin-right: 16px;
}

.page-title {
  padding: 42px 42px 42px 26px;
  background-color: var(--e-global-color-page-title) !important;
  border-radius: 0px !important;
}

.page-title .v-list-item {
  flex: 1 1 auto;
}

.page-title .v-btn {
  margin-top: 10px;
}

.page-title-icon {
  font-size: 2rem;
  display: flex;
  align-items: center;
  align-content: center;
  text-align: center;
  padding: 13px;
  background: #fff;
  box-shadow: 0 0.46875rem 2.1875rem rgb(7 13 10 / 3%), 0 0.9375rem 1.40625rem rgb(7 13 10 / 3%), 0 0.25rem 0.53125rem rgb(7 13 10 / 5%), 0 0.125rem 0.1875rem rgb(7 13 10 / 3%);
  border-radius: 0.25rem;
  width: 60px !important;
  height: 60px !important;
  margin: 0;
}

.page-title-icon i {
  width: 40px !important;
  height: 40px !important;
  font-size: 35px !important;
  color: #69aa8a !important;
}

.sidebar-right {
  min-height: 42vw;
  border-radius: 0px !important;
}

@media only screen and (max-width: 680px) {
  .page-title .v-btn {
    margin-top: 20px;
  }

  .page-title {
    padding: 30px;
  }

  .page-title .v-list-item {
    padding: 0px
  }
} */
</style>