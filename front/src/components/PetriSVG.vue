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
      <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center" v-on:click="addPlace">
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
  <div class="mx-8 my-4 border-2 border-black rounded-xl h-4/5">
    <svg ref="box" class="bg-gray-300 rounded-xl box" height="100%" width="100%" xmlns="http://www.w3.org/2000/svg">
      <component v-for="(child, index) in children" :key="index" :is="child"></component>
      <circle class="element" :cx="element.x" :cy="element.y" r="40" stroke="deeppink" stroke-width="2" fill="#ffe6ee" v-on:mousedown="startDrag" v-on:mouseup="endDrag" />
    </svg>
  </div>
  <div class="flex w-full h-16 items-center justify-center">
    <div class="items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center">
        <ImportIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Import</span>
      </button>
      <button class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center">
        <ExportIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Export</span>
      </button>
      <button v-if="test()" class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center">
        <SaveIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Save</span>
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import CircleIcon from 'vue-material-design-icons/CircleOutline.vue';
import SquareIcon from 'vue-material-design-icons/SquareOutline.vue';
import RemoveIcon from 'vue-material-design-icons/Close.vue';
import ClearIcon from 'vue-material-design-icons/CloseCircleOutline.vue';
import RunIcon from 'vue-material-design-icons/Play.vue';
import StopIcon from 'vue-material-design-icons/Stop.vue';
import ImportIcon from 'vue-material-design-icons/ArrowBottomRight.vue';
import ExportIcon from 'vue-material-design-icons/ArrowTopRight.vue';
import SaveIcon from 'vue-material-design-icons/ContentSaveAll.vue';

const Circle = {
  template: `
    <circle class="element" :cx="element.x" :cy="element.y" r="40" stroke="deeppink" stroke-width="2" fill="#ffe6ee" v-on:mousedown="startDrag" v-on:mouseup="endDrag" />
  `,

  data() {
    return {
      element: {
        x: 100,
        y: 100
      }
    };
  }
};

export default defineComponent({
  name: 'PetriSVG',
  components: {
    CircleIcon,
    SquareIcon,
    RemoveIcon,
    ClearIcon,
    RunIcon,
    StopIcon,
    ImportIcon,
    ExportIcon,
    SaveIcon
  },
  data() {
    return {
      element: {
        x: 100,
        y: 100
      },

      children: [] as any
    };
  },

  methods: {
    test() {
      if (localStorage.getItem('mail') != null) {
        return true;
      }
      return false;
    },

    startDrag() {
      (this.$refs.box as any).addEventListener('mousemove', this.drag);
    },

    drag(event: MouseEvent) {
      this.element.x = event.offsetX;
      this.element.y = event.offsetY;
    },

    endDrag() {
      (this.$refs.box as any).removeEventListener('mousemove', this.drag);
    },

    addPlace() {
      this.children.push(Circle);
    }
  }
});
</script>
