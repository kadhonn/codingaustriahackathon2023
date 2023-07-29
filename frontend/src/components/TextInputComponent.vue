<script setup lang="ts">
defineProps<{
  type?: string
  placeholder?: string
}>()
defineEmits<{
  change: [value: string]
}>()

let value = ''
</script>

<template>
  <div class="textInputContainer">
    <input
        class="textInput"
        :type="type ?? 'text'"
        :placeholder="placeholder"
        v-model="value"
        @change="$emit('change', value)"
    />
  </div>
</template>

<style scoped>
.textInputContainer {
  margin: 8px 0;
  box-shadow: var(--drop-shadow);
}

.textInput {
  width: 100%;
  box-sizing: border-box;
  padding: 16px;
  background-color: white;
  font-size:16px;
  border-radius: 8px;
  border: none;
  outline: none;
  box-shadow: var(--drop-shadow);
}
</style>
