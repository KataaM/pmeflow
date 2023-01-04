<template>
  <div class="list-group-item">
     {{ objet.entreprise }} {{ objet.opportunite }} {{ objet.prix }}
     <v-chip class="chipDate"> 
      {{objet.date}}
    </v-chip>
    <v-chip class="chipProg">
      {{objet.progression}}    
    </v-chip>   
  </div>    


</template>

<script>
export default {
  props: {
    objet: Object,
  },
  data() {
    return {
        }
  }
};
</script>
<style scoped>
</style>