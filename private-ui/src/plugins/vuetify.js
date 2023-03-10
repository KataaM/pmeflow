import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';
import i18n from '@/plugins/i18n.js'

Vue.use(Vuetify);

export default new Vuetify({
    lang:{
        t: (key, ...params)=> i18n.t(key, params),
    }
});
