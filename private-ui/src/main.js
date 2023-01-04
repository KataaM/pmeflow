import Vue from "vue";
import AsyncComputed from 'vue-async-computed'
import App from "@/App.vue";
import vuetify from "@/plugins/vuetify";
import router from "@/plugins/router.js";
import store from "@/plugins/store";
import i18n from "@/plugins/i18n";
import axios from 'axios';
Vue.prototype.$http = axios;
Vue.config.productionTip = false;

//import LoadScript from 'vue-plugin-load-script';
import DatetimePicker from 'vuetify-datetime-picker'
 
Vue.use(DatetimePicker)
Vue.use(AsyncComputed);
//Vue.use(LoadScript);

Vue.config.productionTip = false;
new Vue({
  store,
  i18n,
  //LoadScript,
  vuetify,
  router,
  render: (h) => h(App),
}).$mount("#app");