<script setup lang="ts">
import {useAuthStore} from "@/stores/auth";
import ButtonComponent from "@/components/ButtonComponent.vue";
import {ref} from "vue";
import ListComponent from "@/components/ListComponent.vue";
import type {Coordinates, ShoppingItem, ShoppingTrip} from "@/api/dto";
import router from "@/router";

const authStore = useAuthStore()

const items = ref(authStore.list ?? [] as ShoppingItem[]);
const error = ref("")

const isLoading = ref(false)

const getShoppingRoute = () => {
  navigator.geolocation.getCurrentPosition(
      async position => {
        const location = {latitude: position.coords.latitude, longitude: position.coords.longitude} as Coordinates
        authStore.setLocation(location)

        isLoading.value = true

        const response = await fetch(`https://api.nicerpricer.at/shop`, {
          method: "POST",
          headers:{'content-type': 'application/json'},
          body: JSON.stringify({items: items.value, location}),
        })
        const trips = await response.json() as ShoppingTrip[]
        authStore.setTrips(trips)

        await router.push({path: '/trips'})
        isLoading.value = false
      },
      e => {
        error.value = e.message
        isLoading.value = false
      },
  )
}

const updateItems = (value: ShoppingItem[]) => {
  items.value = value
  authStore.setList(value)
}
</script>

<template>
  <div class="background">
    <div class="container">
      <h1>Erstelle deine Einkaufsliste</h1>

      <ListComponent class="shoppingListContainer" :items="items" @change="value => updateItems(value)"/>

      <span class="spacer"/>

      <p v-if="error" class="error">Bitte erlaube uns auf deinen Standort zuzugreifen um die beste Einkaufsroute zu
        erstellen.</p>
      <ButtonComponent class="button" @click="getShoppingRoute" :is-loading="isLoading">Jetzt einkaufen</ButtonComponent>
    </div>
  </div>
</template>

<style scoped>
.background {
  background: var(--background-gradient);
  width: 100%;
  height: 100%;
  padding: 32px 20%;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.container {
  background: white;
  width: 100%;
  height: 100%;
  padding: 32px;

  border-radius: 12px;
  box-shadow: var(--drop-shadow);

  display: flex;
  flex-direction: column;
  align-items: center;
}

.button {
  align-self: end;
}

.shoppingListContainer {
  flex: 1 1 auto;

  margin-top: 24px;

  overflow: auto;
}

.error {
  background: #fbe9e9;
  padding: 16px 32px;
  border-radius: 12px;
  border: 1px solid #e05151;
  color: #e05151;
  text-align: center;
  font-weight: bold;
}

@media only screen and (max-width: 800px) {
  .background {
    padding: 16px;
  }
}
</style>
