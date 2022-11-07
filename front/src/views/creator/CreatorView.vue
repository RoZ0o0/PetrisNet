<template>
  <div class="px-6 py-6 w-full h-full">
    <div class="overflow-auto rounded-xl w-full h-full">
      <div class=" bg-gray-200 h-full p-4">
        <PetriSVG />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import PetriSVG from '../../components/PetriSVG.vue';

export default defineComponent({
  name: 'CreatorView',
  components: {
    PetriSVG
  }
});
</script>
