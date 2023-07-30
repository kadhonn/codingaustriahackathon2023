<script setup lang="ts">
import {useAuthStore} from "@/stores/auth";
import {ref} from "vue";
import TripComponent from "@/components/TripComponent.vue";
import NotFoundComponent from "@/components/NotFoundComponent.vue";
import type {ShoppingTrip} from "@/api/dto";
import router from "@/router";

const authStore = useAuthStore()

const trips = ref(authStore.trips)

const selectTrip = (trip: ShoppingTrip) => {
  authStore.setSelectedTrip(trip)
  router.push({path: '/trip'})
}

</script>

<template>
  <div class="container">
    <h1 class="title">Touren</h1>

    <TripComponent v-for="trip in trips" :key="trip.name" :trip="trip" @click="selectTrip(trip)"/>
    <span class="spacer"/>
    <NotFoundComponent v-if="trips[0]?.unreachable" :items="trips[0]?.unreachable ?? []"/>

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
