<script setup lang="ts">
import {useAuthStore} from "@/stores/auth";
import ButtonComponent from "@/components/ButtonComponent.vue";
import {ref} from "vue";
import AutoComplete from "primevue/autocomplete";
import ListComponent from "@/components/ListComponent.vue";

const authStore = useAuthStore()

const currentInput = ref("");
const suggestions = ref([]);

const items = ref(["Karrotten", "Bananen", "Schokolade", "Bier"]);

const search = (event) => {
  suggestions.value = [...Array(10).keys()].map((item) => event.query + '-' + item);
}
</script>

<template>
  <div class="background">
    <div class="container">
      <h1>Erstelle deine Einkaufsliste</h1>


      <ListComponent :items="items"/>

      <AutoComplete
          v-model="currentInput"
          :suggestions="suggestions"
          @complete="search"
          placeholder="Bier"/>
      <span class="spacer"/>

      <ButtonComponent class="button">Jetzt einkaufen</ButtonComponent>
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
  gap: 32px;
}

.button {
  align-self: end;
}

.listContainer {
  align-self: start;
}

@media only screen and (max-width: 800px) {
  .background {
    padding: 32px;
  }
}
</style>
