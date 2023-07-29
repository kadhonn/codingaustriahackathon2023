<script setup lang="ts">
defineProps<{
  variant?: 'primary' | 'secondary'
}>()
defineEmits(['click'])
</script>

<template>
  <button
      :class="{
      primary: !variant || variant === 'primary',
      secondary: variant === 'secondary',
      }"
      @click="$emit('click')"
  >
    <slot/>
  </button>
</template>

<style scoped>
.primary, .secondary {
  padding: 16px 24px;
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
}

.secondary {
  padding: 8px 16px;
}


</style>
