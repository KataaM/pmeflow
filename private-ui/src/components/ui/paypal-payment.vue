<template>
    <v-card>
      <v-card-title v-if="client==null">{{$t('pmeflow.ui.generic.label.votreReglement')}}</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="12">
              <div id="paypal-button-container"></div>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
      </v-card-actions>
  </v-card>
</template>

<script src="https://www.paypal.com/sdk/js?client-id=${this.paypal.sandbox}"></script>
<script>

export default {
  name: "paypal-payment",
  props: {
    value: Object,
    credential: {default: "AVScQB-te2EOuHgNvWoEhKAQH-M7s_5iEZSULYmAxs0Z9i5_46LgvEYZHM7_a9zjzfs2kz6YBYSP6rvM", type: String },
    type: {default: "subscription", type: String },
  },
  data: () => ({
    abonnement:null,
    prestation:null,
    order:{
      orderId:""
    }
  }),
  mounted(){
    if (this.type=="subscription")
    {
      this.abonnement = this.value;
      this.$loadScript(`https://www.paypal.com/sdk/js?client-id=${this.credential}&vault=true&intent=subscription`)
      .then(() => {
        paypal.Buttons({
          style: {
            shape: 'rect',
            color: 'silver',
            layout: 'vertical',
            label: 'subscribe'
          },
          createSubscription: this.createSubscription,
          onApprove: this.onApproveAbonnement
        }).render('#paypal-button-container');
      })
    }
    if (this.type=="prestation")
    {
      this.prestation = this.value;
      this.$loadScript(`https://www.paypal.com/sdk/js?client-id=${this.credential}&currency=EUR`)
      .then(() => {
        paypal.Buttons({
          style: {
            shape: 'rect',
            color: 'silver',
            layout: 'vertical',
            label: 'pay'
          },
          createOrder: this.createOrder,
          onApprove: this.onApprovePrestation
        }).render('#paypal-button-container');
      })
    }
  },
  methods: {
    createOrder: function(data, actions) {
      return actions.order.create({
        purchase_units: [{"amount":{"currency_code":"EUR","value":this.prestation.prix}}]
      });
    },
    createSubscription: function(data, actions) {
       return actions.subscription.create({
        plan_id: this.abonnement.planId
      });
    },
    onApproveAbonnement: function(data, actions) {
      this.abonnement.oid=data.subscriptionID;
      this.$emit('input', this.abonnement);
      this.$emit('command-validate',data.orderId);
    },
    onApprovePrestation: function(data, actions) {
      this.prestation.oid=data.orderID;
      this.prestation.etat="PAYE";
      this.$emit('input', this.prestation);
    }
  }
};
</script>