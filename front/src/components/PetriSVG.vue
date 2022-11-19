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
      <button class="border-2 border-black border-l-0 p-2 items-center" v-on:click="addTransition">
        <SquareIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Transition</span>
      </button>
      <button class="border-2 border-black border-l-0 border-r-0 p-2 items-center" v-on:click="deleteElement">
        <RemoveIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Remove</span>
      </button>
      <button class="border-2 border-black rounded-br-xl rounded-tr-xl p-2 items-center" v-on:click="clear">
        <ClearIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Clear</span>
      </button>
    </div>
  </div>
  <div class="mx-8 my-4 border-2 border-black rounded-xl h-4/5">
    <svg ref="box" class="bg-gray-300 rounded-xl box" height="100%" width="100%" xmlns="http://www.w3.org/2000/svg">
      <template v-for="(child, index) in children" :key="child.name">
        <component v-if='this.elements[index+1].name.substring(1,2) == "C"' :id='this.elements[index+1].name' :is="child"
          :cx="this.elements[index+1].x" :cy="this.elements[index+1].y"
          @start-drag="startDrag(index+1)" @end-drag="endDrag" @mouseover="setHoveredID(index+1)" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "T"' :id='this.elements[index+1].name' :is="child"
          :x="this.elements[index+1].x" :y="this.elements[index+1].y"
          :width="this.transition_width" :height="this.transition_height"
          @start-drag="startDrag(index+1)" @end-drag="endDrag(); endConnectionDrag()" @mouseover="setHoveredID(index+1)" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "A"' :id='this.elements[index+1].name' :is="child"
          :x1="this.elements[findFirstConnection(this.elements[index+1].name)].x + offset(this.elements[findFirstConnection(this.elements[index+1].name)].x, this.elements[this.elements.length - 1].x2)" :y1="this.elements[findFirstConnection(this.elements[index+1].name)].y" :x2="this.elements[3].x2 + offset(this.elements[3].x2, this.elements[current_target].x)" :y2="this.elements[3].y2"
          @end-drag="endConnectionDrag">
        </component>
      </template>
    </svg>
  </div>
  <div class="flex w-full h-16 items-center justify-center">
    <div class="items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="importNet">
        <ImportIcon class="inline-block align-middle" />
        <span class="inline-block align-middle">Import</span>
      </button>
      <input ref='import' type='file' class='hidden' @change='importNetChanged'>
      <button class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="exportNet">
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
import { defineComponent, markRaw } from 'vue';
import CircleIcon from 'vue-material-design-icons/CircleOutline.vue';
import SquareIcon from 'vue-material-design-icons/SquareOutline.vue';
import RemoveIcon from 'vue-material-design-icons/Close.vue';
import ClearIcon from 'vue-material-design-icons/CloseCircleOutline.vue';
import RunIcon from 'vue-material-design-icons/Play.vue';
import StopIcon from 'vue-material-design-icons/Stop.vue';
import ImportIcon from 'vue-material-design-icons/ArrowBottomRight.vue';
import ExportIcon from 'vue-material-design-icons/ArrowTopRight.vue';
import SaveIcon from 'vue-material-design-icons/ContentSaveAll.vue';

const Circle = markRaw({
  template: `
    <circle class="element" r="40" stroke="deeppink" stroke-width="2" fill="#ffe6ee" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Square = markRaw({
  template: `
    <rect class="element" stroke="rgb(0,0,0)" stroke-width="2" fill="rgb(0,0,255)" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Connection = markRaw({
  template: `
    <line class="element" stroke="rgb(0,0,0)" stroke-width="2" fill="rgb(0,0,255)" @mouseup="$emit('end-drag')"/>
  `
});

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
        name: '',
        x: 100,
        y: 100
      },
      elements: [
        {
          name: '',
          x: 100,
          y: 100,
          x2: 0,
          y2: 0
        }
      ],
      connections: [] as any,
      connection_edit: false,
      current_connection: '',
      ctrl_pressed: false,
      current_target: 0,
      hovered_target: 0,
      transition_width: 30,
      transition_height: 60,
      children: [] as any,
      counter: 0,
      dest: null as any,
      selecting: false,
      selectedFile: null
    };
  },

  mounted() {
    window.addEventListener('keydown', (e) => {
      if (e.ctrlKey) {
        this.ctrl_pressed = true;
      }
    });
    window.addEventListener('keyup', (e) => {
      this.ctrl_pressed = false;
    });
  },

  methods: {
    test() {
      if (localStorage.getItem('mail') != null) {
        return true;
      }
      return false;
    },

    setHoveredID(index: number) {
      // console.log(index);
      this.hovered_target = index;
    },

    startDrag(index: number) {
      this.current_target = index;
      if (this.ctrl_pressed) {
        this.addConnection();
        this.current_connection = this.elements[this.elements.length - 1].name;
        this.connection_edit = true;
        (this.$refs.box as any).addEventListener('mousemove', this.connection_drag);
      } else {
        (this.$refs.box as any).addEventListener('mousemove', this.drag);
      }
    },

    offset(offset1: number, offset2: number) {
      if (offset1 > offset2) {
        return -5;
      } else {
        return 5;
      }
    },

    connection_drag(event: any) {
      try {
        this.elements[this.elements.length - 1].x2 = event.offsetX - 5;
        this.elements[this.elements.length - 1].y2 = event.offsetY - 5;
      } catch (error) {
      }
    },

    drag(event: any) {
      const idx = this.current_target;
      try {
        if (event.target.nodeName === 'rect') {
          this.elements[idx].x = event.offsetX - 15;
          this.elements[idx].y = event.offsetY - 30;
        } else {
          this.elements[idx].x = event.offsetX;
          this.elements[idx].y = event.offsetY;
        }
      } catch (error) {

      }
    },

    endDrag() {
      (this.$refs.box as any).removeEventListener('mousemove', this.drag);
      if (this.connection_edit) {
        (this.$refs.box as any).removeEventListener('mousemove', this.drag);
        (this.$refs.box as any).removeEventListener('mousemove', this.connection_drag);
        if (this.hovered_target === 0) {
          this.children.splice(-1, 1);
          this.elements.splice(-1, 1);
          this.counter--;
        } else {
          this.elements[this.elements.length - 1].x2 = this.elements[this.hovered_target].x;
          this.elements[this.elements.length - 1].y2 = this.elements[this.hovered_target].y;
          this.connections[this.connections.length - 1].ST = this.hovered_target;
          // console.log(this.connections);
          document.getElementById(this.current_connection)?.removeEventListener('mousemove', this.connection_drag);
        }
        this.current_connection = '';
        this.connection_edit = false;
      }
    },

    endConnectionDrag() {
      return 1;
    },

    // updateConnection() {

    // },

    addPlace() {
      this.counter++;
      this.elements.push({ name: 'EC' + this.counter, x: this.element.x, y: this.element.y, x2: 0, y2: 0 });
      this.children.push(Circle);
    },

    addTransition() {
      this.counter++;
      this.elements.push({ name: 'ET' + this.counter, x: this.element.x, y: this.element.y, x2: 0, y2: 0 });
      this.children.push(Square);
    },

    addConnection() {
      const target = this.current_target;
      this.counter++;
      this.elements.push({ name: 'EA' + this.counter, x: this.elements[target].x, y: this.elements[target].y, x2: this.elements[target].x, y2: this.elements[target].y });
      this.connections.push({ name: 'EA' + this.counter, FT: target });
      this.children.push(Connection);
    },

    findFirstConnection(index: string) {
      let firstElement;
      for (let i = 0; i < this.connections.length; i++) {
        if (this.connections[i].name === index) {
          firstElement = this.connections[i].FT;
        }
      }
      return firstElement;
    },

    findSecondConnection(index: string) {
      let secondElement = 0;
      for (let i = 0; i < this.connections.length; i++) {
        if (this.connections[i].name === index) {
          secondElement = this.connections[i].ST;
        }
      }

      if (secondElement === undefined) {
        secondElement = this.elements.length - 1;
      }

      console.log(secondElement);

      return secondElement;
    },

    deleteElement() {
      const index = this.current_target;
      this.children.splice(index - 1, 1);
      this.elements.splice(index, 1);
    },

    clear() {
      this.children.splice(0);
      this.elements = [{ name: '', x: 100, y: 100, x2: 0, y2: 0 }];
      this.counter = 0;
    },

    exportNet() {
      const data = JSON.stringify(this.elements.slice(1));
      const blob = new Blob([data], { type: 'text/plain' });
      const anchor = document.createElement('a');
      anchor.download = 'PetriNet_import.txt';
      anchor.href = window.URL.createObjectURL(blob);
      anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');
      anchor.click();
    },

    importNet() {
      this.selecting = true;

      window.addEventListener('focus', () => {
        this.selecting = false;
      }, { once: true });

      (this.$refs.import as any).click();
    },

    importNetChanged(e: any) {
      this.selectedFile = e.target.files[0];
      const reader = new FileReader();
      if ((this.selectedFile as any).name.includes('.txt')) {
        reader.onload = (res) => {
          this.dest = res.target?.result;
          this.clear();
          try {
            for (let i = 0; i < JSON.parse(this.dest).length; i++) {
              const objectType = JSON.parse(this.dest)[i].name.substring(1, 2);
              if (objectType === 'C') {
                this.counter++;
                this.children.push(Circle);
              } else if (objectType === 'T') {
                this.counter++;
                this.children.push(Square);
              } else {
                console.log('Zły plik');
                break;
              }
              this.elements.push(JSON.parse(this.dest)[i]);
            }
          } catch (e) {
            console.log('Zły plik');
          }
        };
        if (this.selectedFile != null) {
          reader.readAsText(this.selectedFile);
        }
      } else {
        console.log('zly plik');
      }
    }
  }
});
</script>
