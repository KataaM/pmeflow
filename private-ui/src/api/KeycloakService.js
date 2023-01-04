import Keycloak from "keycloak-js";

export default class KeycloakService {
  keycloak = null;

  constructor(keycloakServiceUri) {
    this.keycloak = new Keycloak({
      url:keycloakServiceUri,
      realm: process.env.VUE_APP_OIDC_REALM,
      clientId: process.env.VUE_APP_OIDC_CLIENT_ID,
    });
  }

  authenticate() {
    this.keycloak
      .init({ onLoad: "login-required" })
      .then((auth) => {
        if (!auth) {
          console.log("NOT Authenticated");
          sessionStorage.removeItem("oidc-token");
          sessionStorage.removeItem("oidc-access_token");
          sessionStorage.removeItem("oidc-refresh_token");
          sessionStorage.removeItem("oidc-roles");
          sessionStorage.setItem("oidc-post-load",false);
        } 
        else {
          console.log("Authenticated");
          let token = {
            access_token: this.keycloak.token,
            refresh_token: this.keycloak.refreshToken,
          };                 
          sessionStorage.setItem("oidc-token", JSON.stringify(token));
          sessionStorage.setItem("oidc-access_token", token.access_token);
          sessionStorage.setItem("oidc-refresh_token", token.refresh_token);
          sessionStorage.setItem("oidc-roles", JSON.stringify(this.keycloak.tokenParsed.resource_access[process.env.VUE_APP_OIDC_CLIENT_ID].roles));
          let postLoad = sessionStorage.getItem("oidc-post-load");
          if (postLoad=="false"){
            sessionStorage.setItem("oidc-post-load",true);
          }
        }
      })
      .catch((error) => {
        console.log("Authenticated Failed\n" + error);
      });
  }


  logout() {
    sessionStorage.removeItem("oidc-token");
    sessionStorage.removeItem("oidc-access_token");
    sessionStorage.removeItem("oidc-refresh_token");
    sessionStorage.removeItem("oidc-roles");
    sessionStorage.setItem("oidc-post-load",false);
    this.keycloak.logout({ redirectUri: process.env.VUE_APP_UI_URL });
  }

  getToken() {
    let token = {
      access_token: this.keycloak.token,
      refresh_token: this.keycloak.refreshToken,
    };
    sessionStorage.setItem("oidc-token", JSON.stringify(token));
    sessionStorage.setItem("oidc-access_token", token.access_token);
    sessionStorage.setItem("oidc-refresh_token", token.refresh_token);
    sessionStorage.setItem("oidc-roles", JSON.stringify(this.keycloak.tokenParsed.resource_access[process.env.VUE_APP_OIDC_CLIENT_ID].roles));
    return token;
  }

  getAvatarDatas() {
    let avatar = { lastfirst: "", initiales: "IC" };
    if (this.keycloak.idTokenParsed != null) {
      avatar.lastfirst =
        this.keycloak.idTokenParsed.given_name +
        " " +
        this.keycloak.idTokenParsed.family_name;
      avatar.initiales =
        this.keycloak.idTokenParsed.given_name.substring(0, 1) +
        this.keycloak.idTokenParsed.family_name.substring(0, 1);
    }
    return avatar;
  }

  hasClientRole(client, role) {
    let hasRole = false;
    alert(JSON.stringify(this.keycloak.tokenParsed));
    if (
      this.keycloak.tokenParsed.resource_access[client]["roles"].includes(role)
    ) {
      hasRole = true;
    }
    return hasRole;
  }
}