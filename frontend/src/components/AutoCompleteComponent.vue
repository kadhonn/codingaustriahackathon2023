<script setup lang="ts">
import {ref} from "vue";
import debounce from 'lodash.debounce'

const props = defineProps<{
  value?: string
}>()
const emit = defineEmits<{
  change: [value: string]
}>()

type Suggestion = {
  name: string,
  isCategory: boolean
}

const suggestions = ref([] as Suggestion[])
const input = ref(props.value ?? "")

const getSuggestions = debounce(async () => {
  if(input.value.length < 1) {
    suggestions.value = []
    return
  }
  const response = await fetch(`https://api.nicerpricer.at/query?query=${input.value}`)
  suggestions.value = await response.json() as Suggestion[]
}, 300)

const selectSuggestion = (suggestion: string) => {
  input.value = ''
  suggestions.value = [] as Suggestion[]

  emit('change', suggestion)
}
</script>

<template>
  <div class="autoCompleteContainer">
    <input
        class="autoCompleteInput"
        type="text"
        v-model="input"
        placeholder="Tippe zum hinzufügen"
        @input="() => getSuggestions()"
    />
    <ul v-if="suggestions && suggestions.length > 0" class="autoCompleteSuggestionContainer">
      <li v-for="suggestion in suggestions"
          :key="suggestion.name"
          class="autoCompleteSuggestion"
          @click="selectSuggestion(suggestion.name)"
      >{{ suggestion.name }}{{ suggestion.isCategory ? ' (egal welche)' : '' }}
      </li>
    </ul>
  </div>
</template>

<style scoped>
.autoCompleteContainer {
  display: inline-block;
  width: 100%;
}

.autoCompleteInput, .autoCompleteSuggestionContainer {
  width: 100%;
  box-sizing: border-box;
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--drop-shadow);
}

.autoCompleteInput {
  width: 100%;
  font-size: 24px;
  border-radius: 8px;
  border: none;
  outline: none;
  font-family: 'Gloria Hallelujah', cursive;
  max-lines: 1;
  flex-grow: 1;
  padding: 2px 16px;
  height: 56px;
}

.autoCompleteSuggestionContainer {
  margin-top: 6px;
  padding: 4px;
}

.autoCompleteSuggestion {
  width: 100%;
  list-style: none;
  padding: 4px 12px;
}

.autoCompleteSuggestion:hover {
  background: #eeeeee;
  cursor: pointer;
}

@media only screen and (max-width: 800px) {
  .autoCompleteInput {
    height: 32px;
    font-size: 16px;
  }

  .autoCompleteSuggestion {
    font-size: 14px;
  }
}
</style>
