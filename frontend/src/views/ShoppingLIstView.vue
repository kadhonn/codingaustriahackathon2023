<script setup lang="ts">
import {useAuthStore} from "@/stores/auth";
import ButtonComponent from "@/components/ButtonComponent.vue";
import {ref} from "vue";
import ListComponent from "@/components/ListComponent.vue";
import type {ShoppingItem} from "@/api/dto";

const authStore = useAuthStore()

const items = ref([] as ShoppingItem[]);
</script>

<template>
  <div class="background">
    <div class="container">
      <h1>Erstelle deine Einkaufsliste</h1>


      <ListComponent class="shoppingListContainer" :items="items" @change="value => items = value"/>

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

.shoppingListContainer {
  flex: 1 1 auto;

  overflow: auto;
}

@media only screen and (max-width: 800px) {
  .background {
    padding: 16px;
  }
}
</style>
