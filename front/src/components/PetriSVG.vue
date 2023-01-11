<!-- eslint-disable vue/no-v-text-v-html-on-component -->
<template>
  <div class="flex w-full h-16 items-center">
    <div class="flex w-full h-16 items-center justify-center">
      <div class="items-center petri-nav w-1/12">
        <button class="border-2 border-black rounded-bl-xl border-r-0 rounded-tl-xl p-1 items-center" v-on:click="addToken">
          <PlusIcon class="inline-block align-middle" />
        </button>
        <input class="border-2 border-black items-center text-center w-1/3" :value="this.elements[this.current_target].name" disabled>
        <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-1 items-center">
          <MinusIcon class="inline-block align-middle" />
        </button>
      </div>
      <div class="ml-4 items-center petri-nav">
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
          @start-drag="startDrag(index+1)" @end-drag="endDrag()" @mouseover="setHoveredID(index+1)" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "A"' :id='this.elements[index+1].name' :is="child"
          :x1="this.elements[findFirstConnection(this.elements[index+1].name)].x + offset(this.elements[findFirstConnection(this.elements[index+1].name)].x, this.elements[this.elements.length - 1].x2)"
          :y1="this.elements[findFirstConnection(this.elements[index+1].name)].y"
          :x2="this.elements[findSecondConnection(this.elements[index+1].name)].x + offset(this.elements[findSecondConnection(this.elements[index+1].name)].x, this.elements[findFirstConnection(this.elements[index+1].name)].x)"
          :y2="this.elements[findSecondConnection(this.elements[index+1].name)].y">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "E"' :id='this.elements[index+1].name' :is="child"
          :cx="this.elements[findCircle(this.elements[index+1].name)].x" :cy="this.elements[findCircle(this.elements[index+1].name)].y"
          @start-drag="startDrag(findCircle(this.elements[index+1].name))" @end-drag="endDrag" @mouseover="setHoveredID(findCircle(this.elements[index+1].name))" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "L"' :id='this.elements[index+1].name' :is="child"
          v-text="findToken(this.elements[index].name)"
          :x="this.elements[findCircle(this.elements[index].name)].x + textOffset(findToken(this.elements[index].name))" :y="this.elements[findCircle(this.elements[index].name)].y - 15"
          @mouseover="setHoveredID(findCircle(this.elements[index].name))" @mouseleave="setHoveredID(0)">
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
import PlusIcon from 'vue-material-design-icons/Plus.vue';
import MinusIcon from 'vue-material-design-icons/Minus.vue';

const Circle = markRaw({
  template: `
    <circle class="element" r="40" stroke="deeppink" stroke-width="2" fill="#ffe6ee" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const SmallCircle = markRaw({
  template: `
    <circle class="element" r="10" fill="black" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Square = markRaw({
  template: `
    <rect class="element" stroke="rgb(0,0,0)" stroke-width="2" fill="rgb(0,0,255)" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Connection = markRaw({
  template: `
    <line class="element" stroke="rgb(0,0,0)" stroke-width="2" fill="rgb(0,0,255)"/>
  `
});

const TokenText = markRaw({
  template: `
    <text class="fill-black text-center" disabled></text>
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
    SaveIcon,
    PlusIcon,
    MinusIcon
  },
  data() {
    return {
      elements: [
        {
          name: '',
          x: 100,
          y: 100,
          x2: 0,
          y2: 0
        }
      ],
      tokens: [] as any,
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

    textOffset(count: number) {
      const countString = count.toString();
      if (countString.length === 1) {
        return -5;
      } else {
        return -10;
      }
    },

    connection_drag(event: any) {
      try {
        this.elements[this.elements.length - 1].x = event.offsetX - 5;
        this.elements[this.elements.length - 1].y = event.offsetY - 5;
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
        if (this.hovered_target === 0 || this.hovered_target === this.current_target) {
          this.children.splice(-1, 1);
          this.elements.splice(-1, 1);
          this.connections.splice(-1, 1);
          this.counter--;
        } else {
          this.elements[this.elements.length - 1].x2 = this.elements[this.hovered_target].x;
          this.elements[this.elements.length - 1].y2 = this.elements[this.hovered_target].y;
          this.elements[this.elements.length - 1].x = this.elements[this.current_target].x;
          this.elements[this.elements.length - 1].y = this.elements[this.current_target].y;
          this.connections[this.connections.length - 1].ST = this.elements[this.hovered_target].name;
          document.getElementById(this.current_connection)?.removeEventListener('mousemove', this.connection_drag);

          if ((this.connections[this.connections.length - 1].FT.substring(1, 2) === 'C' &&
          this.connections[this.connections.length - 1].ST.substring(1, 2) === 'C') ||
          (this.connections[this.connections.length - 1].FT.substring(1, 2) === 'T' &&
          this.connections[this.connections.length - 1].ST.substring(1, 2) === 'T')) {
            this.children.splice(-1, 1);
            this.elements.splice(-1, 1);
            this.connections.splice(-1, 1);
            this.counter--;
          }
        }
        this.current_connection = '';
        this.connection_edit = false;
      }
    },

    addPlace() {
      this.counter++;
      this.elements.push({ name: 'EC' + this.counter, x: 100, y: 100, x2: 0, y2: 0 });
      this.children.push(Circle);
    },

    addTransition() {
      this.counter++;
      this.elements.push({ name: 'ET' + this.counter, x: 100, y: 100, x2: 0, y2: 0 });
      this.children.push(Square);
    },

    addConnection() {
      const target = this.current_target;
      this.counter++;
      this.elements.push({ name: 'EA' + this.counter, x: this.elements[target].x, y: this.elements[target].y, x2: this.elements[target].x, y2: this.elements[target].y });
      this.connections.push({ name: 'EA' + this.counter, FT: this.elements[target].name, ST: '' });
      this.children.push(Connection);
    },

    addToken() {
      if (this.current_target > 0) {
        let findCircle = false;
        for (let i = 0; i < this.tokens.length; i++) {
          if (this.tokens[i].circle === this.elements[this.current_target].name) {
            findCircle = true;
            this.tokens[i].token_amount++;
          }
        }

        if (!findCircle) {
          this.counter++;
          this.elements.push({ name: 'EE' + this.counter, x: this.elements[this.current_target].x, y: this.elements[this.current_target].y, x2: 0, y2: 0 });
          this.tokens.push({ name: 'EE' + this.counter, object_name: 'EE' + this.counter, label_name: 'EL' + (this.counter + 1), circle: this.elements[this.current_target].name, token_amount: 1 });
          this.children.push(SmallCircle);
          this.counter++;
          this.elements.push({ name: 'EL' + this.counter, x: this.elements[this.current_target].x, y: this.elements[this.current_target].y, x2: 0, y2: 0 });
          this.children.push(TokenText);
        }
      }
    },

    findCircle(index: string) {
      for (let i = 0; i < this.tokens.length; i++) {
        if (this.tokens[i].name === index) {
          for (let j = 0; j < this.elements.length; j++) {
            if (this.tokens[i].circle === this.elements[j].name) {
              return j;
            }
          }
        }
      }
    },

    findToken(index: string) {
      for (let i = 0; i < this.tokens.length; i++) {
        if (this.tokens[i].name === index) {
          return this.tokens[i].token_amount;
        }
      }
    },

    findFirstConnection(index: string) {
      let firstElement;
      for (let i = 0; i < this.connections.length; i++) {
        if (this.connections[i].name === index) {
          for (let j = 0; j < this.elements.length; j++) {
            if (this.elements[j].name === this.connections[i].FT) {
              firstElement = j;
            }
          }
        }
      }
      return firstElement;
    },

    findSecondConnection(index: string) {
      let secondElement = 0;
      for (let i = 0; i < this.connections.length; i++) {
        if (this.connections[i].name === index) {
          if (this.connections[i].ST === 0) {
            secondElement = (this.elements.length - 1);
          } else {
            for (let j = 0; j < this.elements.length; j++) {
              if (this.elements[j].name === this.connections[i].ST) {
                secondElement = j;
              }
            }
          }
        }
      }

      if (secondElement < 1) {
        secondElement = this.elements.length - 1;
      }

      return secondElement;
    },

    deleteElement() {
      if (this.current_target !== 0) {
        let deletedIndex = 0;
        let deletedIndex2 = 0;
        const connections2 = [...this.connections];
        const elements2 = [...this.elements];
        const children2 = [...this.children];
        this.connections.forEach((element: any, index: number) => {
          if (element.FT === this.elements[this.current_target].name || element.ST === this.elements[this.current_target].name) {
            this.elements.forEach((element2: any, index2: number) => {
              if (element2.name === element.name) {
                elements2.splice(index2 - deletedIndex2, 1);
                children2.splice((index2 - 1) - deletedIndex2, 1);
                deletedIndex2++;
              }
            });
            connections2.splice(index - deletedIndex, 1);
            deletedIndex++;
          }
        });
        this.connections = connections2;
        this.elements = elements2;
        this.children = children2;
        for (let i = 0; i < this.tokens.length; i++) {
          if (this.tokens[i].circle === this.elements[this.current_target].name) {
            for (let j = 0; j < this.elements.length; j++) {
              if (this.elements[j].name === this.tokens[i].object_name) {
                this.elements.splice(j, 1);
                this.children.splice(j - 1, 1);
                this.elements.splice(j, 1);
                this.children.splice(j - 1, 1);
              }
            }
            this.tokens.splice(i, 1);
          }
        }
        this.children.splice(this.current_target - 1, 1);
        this.elements.splice(this.current_target, 1);
        this.current_target = 0;
      }
    },

    clear() {
      this.children.splice(0);
      this.elements.splice(1);
      this.current_target = 0;
      this.connections.splice(0);
      this.tokens.splice(0);
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
        console.log('Zły plik');
      }
    }
  }
});
</script>
