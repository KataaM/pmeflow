<template>
  <v-col col="12" sm="12" md="12">
    <v-card class="card-view"  v-if="profil!=null">
      <v-card-title>
        <span class="headline">{{$t("pmeflow.ui.mon-profil.title")}}</span>
      </v-card-title>

      <v-card-text>
        <v-tabs vertical>
          <v-tab><v-icon left>mdi-account</v-icon>{{$t("pmeflow.ui.mon-profil.tab.profil")}}</v-tab>
          <v-tab><v-icon left>mdi-access-point</v-icon>{{$t("pmeflow.ui.mon-profil.tab.communication")}}</v-tab>
          <v-tab><v-icon left>mdi-lock</v-icon>{{$t("pmeflow.ui.mon-profil.tab.securite")}}</v-tab>
          <v-tab v-if="isJuriste"><v-icon left>mdi-calendar</v-icon>{{$t("pmeflow.ui.mon-profil.tab.calendar")}}</v-tab>

          <v-tab-item>
            <v-card flat>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="profil.nom" :label="$t('pmeflow.ui.generic.label.nom')"></v-text-field>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="profil.prenom" :label="$t('pmeflow.ui.generic.label.prenom')"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row v-if="isJuriste">
                    <v-col md="12" cols="12">
                    <v-textarea rows="2" v-model="profil.specialite" :label="$t('pmeflow.ui.mon-profil.label.specialite')"></v-textarea>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col md="12" cols="12">
                    <v-textarea rows="2" v-model="profil.adresse" :label="$t('pmeflow.ui.generic.label.adresse')"></v-textarea>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="profil.codePostal" :label="$t('pmeflow.ui.generic.label.codePostal')"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="profil.ville" :label="$t('pmeflow.ui.generic.label.ville')"></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
              </v-card-actions>
            </v-card>
          </v-tab-item>
          <v-tab-item>
            <v-card flat>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="profil.email" :label="$t('pmeflow.ui.generic.label.email')" :rules="formValidation.emailRules"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                      <v-text-field v-model="profil.telephone" :label="$t('pmeflow.ui.generic.label.telephone')"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-select v-model="profil.priseContact" :label="$t('pmeflow.ui.mon-profil.label.priseContactPreferee')"
                        :items="this.typePriseContact"/>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
              </v-card-actions>
            </v-card>
          </v-tab-item>
          <v-tab-item>
            <v-card flat>
              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="password" :append-icon="show1?'mdi-eye':'mdi-eye-off'" :type="show1?'text':'password'" 
                        :label="$t('pmeflow.ui.mon-profil.label.password')" counter @click:append="show1 = !show1"></v-text-field>
                    </v-col>
                    <v-col md="6" cols="12">
                      <v-text-field v-model="verifPassword" :append-icon="show2?'mdi-eye':'mdi-eye-off'" :type="show2?'text':'password'" 
                        :label="$t('pmeflow.ui.mon-profil.label.verifyPassword')" counter @click:append="show2 = !show2"></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" rounded outlined @click="changePassword">{{$t('pmeflow.ui.generic.button.entry.changer')}}</v-btn>
              </v-card-actions>
            </v-card>
          </v-tab-item>
          <v-tab-item>
            <v-card flat>
              <v-card-text>
                <v-container>
                  <v-simple-table>
                    <template v-slot:default>
                      <thead>
                        <th style="width:30%">Jour</th>
                        <th style="width:35%">Matin</th>
                        <th style="width:35%">Après-midi</th>
                      </thead>
                      <tbody>
                        <tr>
                          <td>Lundi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.lundi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.lundi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.lundi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.lundi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Mardi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.mardi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.mardi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.mardi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.mardi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Mercredi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.mercredi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.mercredi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.mercredi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.mercredi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Jeudi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.jeudi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.jeudi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.jeudi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.jeudi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Vendredi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.vendredi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.vendredi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.vendredi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.vendredi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Samedi</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.samedi.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.samedi.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.samedi.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.samedi.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                        <tr>
                          <td>Dimanche</td>
                          <td>
                            <v-row>
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.dimanche.matin.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                                <v-text-field class="col-md-5" v-model="profil.disponibilite.dimanche.matin.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                          <td>
                            <v-row>
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.dimanche.apresmidi.ouverture" :label="$t('pmeflow.ui.mon-planning.label.ouverture')"/> à 
                              <v-text-field class="col-md-5" v-model="profil.disponibilite.dimanche.apresmidi.fermeture" :label="$t('pmeflow.ui.mon-planning.label.fermeture')"/>
                            </v-row>
                          </td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" rounded outlined @click="save">{{$t('pmeflow.ui.generic.button.entry.save')}}</v-btn>
              </v-card-actions>
            </v-card>
          </v-tab-item>
        </v-tabs>  
        <v-alert outlined :type="msg.type" prominent border="left" v-if="msg.presence">{{msg.text}}</v-alert>      
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import ProfilService from "@/api/ProfilService.js";

export default {
  components: {
  },
  data: () => ({
    msg:{
      type:"info",
      text:"",
      presence:false
    },    
    verifPassword:false,
    password:false,
    show1:false,
    show2:false,
    typePriseContact : [ "MAIL", "TEL", "NO_PREFER" ],
    profil: null,
    disponibilite:{
      lundi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      mardi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      mercredi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },       
      jeudi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      vendredi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:13,
          fermeture:19
        }
      },        
      samedi:{
        matin:{
          ouverture:8,
          fermeture:12
        },
        apresmidi:{
          ouverture:null,
          fermeture:null,
        }
      },
      dimanche:{
        matin:{
          ouverture:null,
          fermeture:null
        },
        apresmidi:{
          ouverture:null,
          fermeture:null,
        }
      }
    }
  }),
  created() {
    eventBus.$emit("errors-loaded", []);
    this.initialize();
  },
  computed:{
    isJuriste(){
      let result = false;
      let roles = sessionStorage.getItem("oidc-roles");
      let authorize = ['ADMIN', 'JURISTE'];
      authorize.forEach(role => {
        result |= roles.includes(role);
      });
      return result;
    },
    formValidation(){
      return {
        emailRules: [
          value => !!value || this.$t("pmeflow.ui.generic.message.requis"),
          value => {
            const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            return pattern.test(value) || this.$t("pmeflow.ui.generic.message.mauvaisFormatMail");
          },
        ],
      }
    },
  },
  methods: {
    getProfilService() {
      return new ProfilService(
        this.$store.state.configuration.services.profilServiceUri,
        sessionStorage.getItem("oidc-access_token")
      );
    },
    initialize() {
      this.getProfil();
    },
    getProfil() {
      this.getProfilService().getProfil().then(profil => {
        this.profil = profil;
        if (profil.disponibilite==null || JSON.stringify(profil.disponibilite.lundi)==="{}")
        {
          this.profil.disponibilite = this.disponibilite;
          this.save();
        }
      });
    },
    save() {
      this.getProfilService().mergeProfil(this.profil).then(profil => {
        this.profil = profil;
        this.sendAlert("info", this.$t('pmeflow.ui.mon-profil.message.profilChange'));
      });
    },
    changePassword() {
      if (this.verifPassword==this.password)
      {
        this.getProfilService().changePassword(this.password).then(()=>{
          this.sendAlert("info", this.$t('pmeflow.ui.mon-profil.message.passwordChange'));
        });
      }
      else 
      {
        this.sendAlert("error", this.$t('pmeflow.ui.mon-profil.message.passwordsDifferents'));
      }
    },
    sendAlert(type, msg) {
      this.msg.presence = true;
      this.msg.type = type;
      this.msg.text = msg;
      setTimeout(()=>{this.msg.presence=false},5000);
    }
  }
};
</script>
<style>
  td .row {
    padding-top: 20px;
  }
  tr:nth-child(even) td {
    background-color: #ededed;
  }
</style>