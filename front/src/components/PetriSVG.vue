<template>
  <div class="flex w-full h-16 items-center justify-center">
    <div class="items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center">
        <RunIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Run</span>
      </button>
      <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-2 items-center">
        <StopIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Stop</span>
      </button>
    </div>
    <div class="ml-4 items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center">
        <CircleIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Place</span>
      </button>
      <button class="border-2 border-black border-l-0 p-2 items-center">
        <SquareIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Transition</span>
      </button>
      <button class="border-2 border-black border-l-0 border-r-0 p-2 items-center">
        <RemoveIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Remove</span>
      </button>
      <button class="border-2 border-black rounded-br-xl rounded-tr-xl p-2 items-center">
        <ClearIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Clear</span>
      </button>
    </div>
  </div>
  <svg width="100" height="100" xmlns="http://www.w3.org/2000/svg">
    <circle cx="50" cy="50" r="40" stroke="deeppink" stroke-width="2" fill="#ffe6ee" />
  </svg>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import CircleIcon from 'vue-material-design-icons/CircleOutline.vue';
import SquareIcon from 'vue-material-design-icons/SquareOutline.vue';
import RemoveIcon from 'vue-material-design-icons/Close.vue';
import ClearIcon from 'vue-material-design-icons/CloseCircleOutline.vue';
import RunIcon from 'vue-material-design-icons/Play.vue';
import StopIcon from 'vue-material-design-icons/Stop.vue';

export default defineComponent({
  name: 'PetriSVG',
  components: {
    CircleIcon,
    SquareIcon,
    RemoveIcon,
    ClearIcon,
    RunIcon,
    StopIcon
  }
});
</script>
