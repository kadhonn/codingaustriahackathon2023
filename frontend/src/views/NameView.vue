<script setup lang="ts">
import ButtonComponent from "@/components/ButtonComponent.vue";
import {useAuthStore} from "@/stores/auth";
import router from "@/router";

const authStore = useAuthStore()

let name = ''

const login = () => {
  authStore.login(name)
  router.push({path: '/list', replace: true})
}

</script>

<template>
  <div class="background">
    <div class="container">
      <h1 class="title">Hi, wie heißt du?</h1>
      <input
          class="textInput"
          type="text"
          placeholder="Dein Name"
          v-model="name"
      />
      <ButtonComponent class="button" @click="login" variant="secondary">Los geht's</ButtonComponent>
    </div>
  </div>
</template>

<style scoped>
.background {
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  background: var(--background-gradient);

  display: flex;
  align-items: center;
  justify-content: center;
}

.container {
  width: 50%;
  margin: auto;

  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 32px;
}

.title {
  color: white;
  align-self: start;
}

.textInput {
  width: 100%;
  box-sizing: border-box;
  padding: 16px;
  background-color: white;
  font-size: 24px;
  border-radius: 8px;
  border: none;
  outline: none;
  box-shadow: var(--drop-shadow);
}

.button {
  align-self: end;
}

@media only screen and (max-width: 800px) {
  .container {
    width: 90%;
  }

  .textInput {
    font-size: 16px;
    padding: 12px;
  }
}
</style>
