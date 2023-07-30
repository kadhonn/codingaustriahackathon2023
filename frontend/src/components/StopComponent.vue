<script setup lang="ts">
import type {Place, ShoppingStop, ShoppingTrip} from "@/api/dto";
import {ref} from "vue";

const props = defineProps<{
  isStart?: boolean,
  isEnd?: boolean,
  stop?: ShoppingStop
}>()
const emit = defineEmits(['click'])

const currentList = ref(null)

const title = () => {
  if (props.isStart) {
    return "Abfahrt"
  }
  if (props.isEnd) {
    return "Ankunft"
  }
  return props.stop?.stop?.name
}

const location = () => {
  if (props.isStart || props.isEnd) {
    return "Dein Standort"
  }
  return props.stop?.stop?.vicinity
}

const showNavigation = (stop: Place) => {
  window.open(
      "https://www.google.com/maps/search/?api=1&query_place_id=" + stop.place_id + "&query=" + stop.vicinity,
      "_blank"
  );
}

const showList = (stop: ShoppingStop) => {
  currentList.value = stop
}

</script>

<template>
  <div class="stopContainer">
    <div class="stopContentContainer">
      <img class="stopIcon" v-if="stop?.stop?.icon"
           src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZwxpXR9RWTmbeEqqAFJU9FBYeo28dGSOarw&usqp=CAU"/>
      <div class="stopText">
        <h1 class="stopTitle">{{ title() }}</h1>
        <h1 class="stopLocation">{{ location() }}</h1>
      </div>
    </div>
    <div v-if="!isStart" class="stopButtons">
      <button v-if="!isEnd" class="listButton" @click="showList(stop)">Einkaufsliste</button>
      <button class="navigateButton" @click="showNavigation(stop.stop)">Navigation</button>
    </div>
  </div>

  <div v-if="currentList" class="modalContainer" @click.self="currentList = null">
    <div class="modalListContainer">
      <ul>
        <li v-for="item in currentList.items" :key="item">{{item.quantity}} {{item.name}}</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.stopContainer {
  width: 100%;
  background: white;
  border-radius: 8px;
  padding: 12px;

  display: flex;
  flex-flow: column;
}

.stopItemsContainer, .modalListContainer {
  width: 90%;
  background: white;
  border-radius: 8px;
  padding: 12px;

  display: flex;
  flex-flow: column;
}

.modalListContainer {
  max-width: 600px;
}

.stopContentContainer {
  width: 100%;

  display: flex;
  flex-flow: row;
  align-items: center;
}

.stopTitle {
  font-size: 18px;
  margin-top: 8px;
}

.stopLocation {
  font-size: 14px;
  margin-top: 4px;
  margin-bottom: 8px;
  font-weight: normal;
  color: #777777;
}

.stopIcon {
  width: 36px;
  height: 36px;
  margin-right: 12px;
}

.stopButtons {
  width: 100%;
  display: flex;
  flex-flow: row;
  gap: 8px;
  margin-top: 16px;
}

.listButton, .navigateButton {
  width: 100%;
  padding: 8px 24px;
  border-radius: 8px;
  box-shadow: var(--drop-shadow);
  border: none;
  font-weight: bolder;

  font-size: 18px;
  text-decoration: none;

  overflow: hidden;
  white-space: nowrap;
  display: block;
  text-overflow: ellipsis;

  background: white;
  color: var(--np-text-color);
}

.modalContainer {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  background: rgba(0, 0, 0, 0.8);

  display: flex;
  align-items: center;
  justify-content: center;
}

</style>
