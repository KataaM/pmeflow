import Vue from "vue";
import VueI18n from "vue-i18n";

export function loadLocaleMessages() {
  const locales = require.context(
    "@/assets/lang",
    true,
    /[A-Za-z0-9-_,\s]+\.json$/i
  );
  const messages = {};
  locales.keys().forEach((key) => {
    const matched = key.match(/([A-Za-z0-9-_]+)\./i);
    if (matched && matched.length > 1) {
      const locale = matched[1];
      messages[locale] = locales(key);
    }
  });
  return messages;
}

let optionsI18n = {
  locale: process.env.VUE_APP_I18N_LOCALE || "fr",
  fallbackLocale: process.env.VUE_APP_I18N_FALLBACK_LOCALE || "fr",
  messages: loadLocaleMessages(),
};

Vue.use(VueI18n, optionsI18n);

export default new VueI18n(optionsI18n);