<script setup lang="ts">
import {useAuthStore} from "@/stores/auth";
import {ref} from "vue";
import TripComponent from "@/components/TripComponent.vue";
import NotFoundComponent from "@/components/NotFoundComponent.vue";
import type {ShoppingTrip} from "@/api/dto";
import router from "@/router";
import StopComponent from "@/components/StopComponent.vue";

const authStore = useAuthStore()

const trip = ref(authStore.selectedTrip as ShoppingTrip)
</script>

<template>
  <div class="container">
    <h1 class="title">Meine Einkaufstour</h1>

    <StopComponent is-start/>
    <StopComponent v-for="stop in trip.stops" :key="stop.stop.name" :stop="stop"/>
    <StopComponent is-end/>

  </div>
</template>

<style scoped>
.container {
  width: 100%;
  height: 100%;
  padding: 32px 20%;
  margin: 0;
  box-sizing: border-box;
  background: var(--background-gradient);

  display: flex;
  align-items: center;
  flex-direction: column;
  gap: 32px;
}

.title {
  color: white;
}

@media only screen and (max-width: 800px) {
  .container {
    padding: 16px;
  }
}
</style>
