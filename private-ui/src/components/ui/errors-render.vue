<template>
  <v-card flat tile width="100%" v-if="errors.length>0">
    <v-card-text>
      <v-alert v-for="error in errors" :key="error.msg"
        outlined :type="error.type" border="left">
        {{error.msg}}
      </v-alert>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: "errors-render",
  props: {
    items: [],
  },
  data: () => ({    
  }),
  computed: {
    errors(){
      let errors = [];
      this.items.forEach(item => {
        errors.push(item);
      });
      return errors;
    },
    errorPosition(){
      let top = `${window.innerHeight+300}px`;
      return {
        "top":top,
      };
    },
  }
};
</script>