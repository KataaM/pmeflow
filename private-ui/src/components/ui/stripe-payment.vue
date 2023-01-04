<template>
    <v-card>
    <form @submit.prevent="pay">
      <v-card-title>{{$t('pmeflow.ui.generic.label.votreReglement')}}</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="12">
            <div id="payment-element"></div>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-btn type="submit" color="blue darken-1" rounded outlined>{{$t('pmeflow.ui.generic.button.entry.payer')}}</v-btn>
      </v-card-actions>
  </form>
  </v-card>
</template>

<script>
import { eventBus } from "@/plugins/event-bus.js";
import { loadStripe } from "@stripe/stripe-js";
export default {
  name: "stripe-payment",
  props: {
    value: Object,
    type: {default: "prestation", type: String },
  },
  data: () => ({
    publicKey: {default: "pk_test_51LVYgTBgYaLiNQYa9E60WcERWEUXM0ycpmSS0UBb86d5kY4yccqPYKTKJ1AdamHPxqsGLuNQVGC0vMbKngmLgoVR00MqXUoQzL", type: String },
    stripe:null,
    elements:null,
    abonnement:null,
    prestation:null,
    elementsOptions: {
      clientSecret:"",
      appearance: {},
    },
    confirmParams: {
      return_url: 'http://localhost:8080/success', // success url
    },
  }),
  mounted(){
    this.publicKey=this.$store.state.configuration.parametres.stripePublicKey;    
    if (this.type=="prestation")
    {
      this.prestation = this.value;
      this.elementsOptions.clientSecret = this.value.paymentIntentId;
    }
    if (this.type=="abonnement"){      
      this.abonnement = this.value;
      this.elementsOptions.clientSecret = this.value.verifPaymentId;
    }
    loadStripe(this.publicKey, {betas: ['server_side_confirmation_beta_1'],})
      .then((stripe) => {
        this.stripe=stripe;
        this.elements=this.stripe.elements(this.elementsOptions);
        let paymentElement = this.elements.create('payment');
        paymentElement.mount("#payment-element");      
      });
  },
  methods: {
    pay(e) {
      e.preventDefault();
      let elements = this.elements;
      this.stripe.confirmPayment({elements, redirect:'if_required', confirmParams: {return_url: `${window.location.origin}/return`}})
      .then((result) => {
        if (result.error) {
          let errors=[];
          if (result.error.type=="card_error"){
            errors.push({
              type:"error",
              msg:this.$t("pmeflow.ui.generic.message.erreurCarte")
            });
          }
          else if (result.error.type=="validation_error"){
            errors.push({
              type:"error",
              msg:this.$t("pmeflow.ui.generic.message.erreurSaisieCarte")
            });
          }
          else{
            errors.push({
              type:"error",
              msg:result.error
            });
          }
          eventBus.$emit("errors-loaded", errors);
        } 
        else {
          if (this.type=="abonnement"){
            this.abonnement.verifPaymentId=result.paymentIntent.id;
            this.abonnement.paymentMethodId=result.paymentIntent.payment_method;
            this.abonnement.etat="ACTIF";
            this.$emit('input', this.abonnement);
          }
          if (this.type=="prestation"){
            this.prestation.paymentIntentId=result.paymentIntent.id;
            this.prestation.etat="PAYE";
            this.$emit('input', this.prestation);
          }
          eventBus.$emit("errors-loaded", []);
        }
      });
    }
  }
};
</script>