<script setup lang="ts">
import type {ShoppingTrip} from "@/api/dto";

const props = defineProps<{
  trip: ShoppingTrip
}>()
const emit = defineEmits(['click'])
//     <span class="tripBadge">Am wenigsten Stopps</span>
</script>

<template>
  <div class="tripContainer" @click="emit('click')">
    <h1 class="tripStops">{{trip.stops?.length}} Stopps</h1>
    <h1 class="tripPrice">Gesamtpreis: <strong>{{trip.totalPrice}} €</strong></h1>
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
  background: #CE37B9;
  padding: 4px 8px;
  border-radius: 8px;
  color: white;
  box-shadow: var(--drop-shadow)
}

.tripStops {
  font-size: 24px;
  margin-top: 16px;
}

.tripPrice {
  font-size: 24px;
  margin-top: 16px;
  align-self: end;
  font-weight: normal;
}

</style>
