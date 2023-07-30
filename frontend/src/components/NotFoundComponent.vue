<script setup lang="ts">
import type {ShoppingItem} from "@/api/dto";

type GroceryItem = {
  name: string
}

const props = defineProps<{
  items: any
}>()
</script>

<template>
  <div class="tripContainer">
    <span class="tripBadge">Nicht gefunden</span>
    <p class="tripStops">Für {{ items.length }} Elemente wurden keine Geschäfte in der Nähe gefunden:</p>
  <ul>
    <li v-for="item in Object.keys(items)" :key="item">{{item.substring(item.indexOf('(', item.indexOf(')')))}}</li>
  </ul>
  </div>
</template>

<style scoped>
.tripContainer {
  width: 100%;
  background: white;
  border-radius: 8px;
  padding: 12px;

  display: flex;
  flex-flow: column;
  align-items: start;
}

.tripBadge {
  background: #888888;
  padding: 4px 8px;
  border-radius: 8px;
  color: white;
  box-shadow: var(--drop-shadow)
}

.tripStops {
  font-size: 18px;
  margin-top: 16px;
  margin-bottom: 0;
}

.tripPrice {
  font-size: 24px;
  margin-top: 16px;
  align-self: end;
  font-weight: normal;
}

</style>
