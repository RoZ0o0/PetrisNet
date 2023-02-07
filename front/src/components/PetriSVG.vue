<!-- eslint-disable vue/no-v-text-v-html-on-component -->
<template>
  <div class="flex w-full h-16 items-center">
    <span v-if='checkIfCreateExample()' class='inline-block absolute ml-8'>Tworzenie przykładowej sieci</span>
    <span v-if='checkIfEdited()' class='inline-block absolute ml-8'>Edytowanie sieci: {{ this.saveResult.saveName }}</span>
    <span v-if='checkIfExampleEdited()' class='inline-block absolute ml-8'>Edytowanie przykładowej sieci: {{ this.exampleEditResult.netName }}</span>
    <div class="flex w-full h-16 items-center justify-center">
      <div class="items-center petri-nav w-1/12">
        <button class="border-2 border-black rounded-bl-xl border-r-0 rounded-tl-xl p-1 items-center" v-on:click="addToken">
          <PlusIcon class="inline-block align-middle" />
        </button>
        <input class="border-2 border-black items-center text-center w-1/3" :value="this.elements[this.current_target].name" disabled>
        <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-1 items-center" v-on:click="substractToken">
          <MinusIcon class="inline-block align-middle" />
        </button>
      </div>
      <div class="ml-4 items-center petri-nav">
        <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center" @click='run(); this.running = true;'>
          <RunIcon class="inline-block align-middle" />
          <span class="inline-block align-middle">Run</span>
        </button>
        <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-2 items-center" @click='stop()'>
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
    <svg ref="box" class="bg-gray-300 rounded-xl box" height="100%" width="100%" xmlns="http://www.w3.org/2000/svg" @mouseup='endDrag()' @mouseenter="endDrag()">
      <defs>
        <marker id="mkrArrow" markerWidth="10" markerHeight="7" refX="0" refY="3.5" orient="auto">
          <polygon points="0 0, 10 3.5, 0 7" />
        </marker>
      </defs>
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
          @click='selectConnection(index+1)'
          :x1="this.elements[findFirstConnection(this.elements[index+1].name)].x + offset(this.elements[findFirstConnection(this.elements[index+1].name)].name, this.elements[findSecondConnection(this.elements[index+1].name)].x, this.elements[findFirstConnection(this.elements[index+1].name)].x, 'x')"
          :y1="this.elements[findFirstConnection(this.elements[index+1].name)].y + offset(this.elements[findFirstConnection(this.elements[index+1].name)].name, this.elements[findSecondConnection(this.elements[index+1].name)].y, this.elements[findFirstConnection(this.elements[index+1].name)].y, 'y')"
          :x2="this.elements[findSecondConnection(this.elements[index+1].name)].x - offsetX(this.elements[findSecondConnection(this.elements[index+1].name)].x, this.elements[findFirstConnection(this.elements[index+1].name)].x, this.elements[findSecondConnection(this.elements[index+1].name)].name)"
          :y2="this.elements[findSecondConnection(this.elements[index+1].name)].y - offsetY(this.elements[findSecondConnection(this.elements[index+1].name)].y, this.elements[findFirstConnection(this.elements[index+1].name)].y, this.elements[findSecondConnection(this.elements[index+1].name)].name)">
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
      <button v-if="checkIfLogged()" @click="saveModal()" class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" data-bs-toggle="modal" data-bs-target="#saveNetModal">
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
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import Swal from 'sweetalert2';
import { IUser } from '@/services/UserService';
import axios, { AxiosError } from 'axios';
import LoginServices, { ILogin } from '@/services/LoginService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import SimulationServices, { ISimulation } from '@/services/SimulationService';

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
    <line class="element" stroke="rgb(0,0,0)" stroke-width="2" marker-end="url(#mkrArrow)" fill="rgb(0,0,255)"/>
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
      selectedFile: null,
      saveResult: SaveNetServices.getBlankSaveNetTemplate(),
      exampleEditResult: ExampleNetServices.getBlankExampleNetTemplate(),
      exampleNetResult: ExampleNetServices.getBlankExampleNetTemplate(),
      loginResult: LoginServices.getBlankLoginTemplate(),
      resultRef: SaveNetServices.getBlankSaveNetTemplate(),
      resultSimulation: SimulationServices.getBlankSimulationTemplate(),
      running: false
    };
  },
  watch: {
    resultRef() {
      if (this.resultRef.id !== 0) {
        this.redirectNetExport(this.resultRef.netExport);
      }
    }
  },
  mounted() {
    if (history.state.redirectExport) {
      this.redirectNetExport(history.state.redirectExport);
    }

    if (this.$route.query.ref) {
      const ref = this.$route.query.ref as string;
      this.getByRef(ref).then((data) => (this.resultRef = data));
    }

    if (history.state.editUserSave) {
      this.getUserNet().then((data) => (this.saveResult = data));
      this.redirectNetExport(history.state.editUserSave);
    }

    if (history.state.editExampleNet) {
      this.getExampleNet().then((data) => (this.exampleEditResult = data));
      this.redirectNetExport(history.state.editExampleNet);
    }

    window.addEventListener('keydown', (e) => {
      if (e.ctrlKey) {
        this.ctrl_pressed = true;
      }
    });
    window.addEventListener('keyup', (e) => {
      this.ctrl_pressed = false;
    });

    if (localStorage.getItem('token') != null) {
      (this.getUser().then((data) => (this.loginResult = data))).catch((error) => {
        if (error?.response.status === 401) {
          localStorage.removeItem('token');
        }
      });
    }
  },

  methods: {
    async getUser(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    async getUserNet(): Promise<ISaveNet> {
      return await SaveNetServices.find(history.state.editId);
    },

    async getExampleNet(): Promise<IExampleNet> {
      return await ExampleNetServices.fetchById(history.state.editId);
    },

    async findByUserAndSaveName(saveName: string): Promise<number> {
      return await SaveNetServices.findBySaveNameAndId(saveName);
    },

    async findByNetName(netName: string): Promise<boolean> {
      return await ExampleNetServices.findByNetName(netName);
    },

    async create(): Promise<any> {
      try {
        await SaveNetServices.create(this.saveResult);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 0;
    },

    async createExampleNet(): Promise<any> {
      try {
        await ExampleNetServices.create(this.exampleNetResult);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }

      return 0;
    },

    async update(id: number): Promise<ISaveNet> {
      return await SaveNetServices.update(this.saveResult, id);
    },

    async updateExampleNet(id: number): Promise<IExampleNet> {
      return await ExampleNetServices.update(this.exampleEditResult, id);
    },

    async getByRef(ref: string): Promise<ISaveNet> {
      return await SaveNetServices.getByRef(ref);
    },

    async simulation(result: ISimulation): Promise<ISimulation> {
      return await SimulationServices.simulation(result);
    },

    async run() {
      this.resultSimulation.elements = this.elements.slice(1);
      this.resultSimulation.connections = this.connections;
      this.resultSimulation.tokens = this.tokens;

      await this.simulation(this.resultSimulation).then((data) => (this.resultSimulation = data));

      console.log(this.resultSimulation);

      await this.netChangeSimulation();

      await this.checkIfTokenEmpty();
      await this.customTimeout(2000);
      if (this.running) {
        await this.run();
      }
    },

    customTimeout(ms: number) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },

    stop() {
      this.running = false;
    },

    checkIfLogged() {
      if (localStorage.getItem('token') != null) {
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

    selectConnection(index: number) {
      this.current_target = index;
    },

    offset(type: string, offset1: number, offset2: number, position: string) {
      if (type.substring(1, 2) === 'T') {
        if (position === 'x') {
          if (offset1 < offset2) {
            return 0;
          } else {
            return 30;
          }
        } else {
          if (offset1 < offset2) {
            return 0;
          } else {
            return 60;
          }
        }
      } else {
        return 0;
      }
    },

    offsetX(offset1: number, offset2: number, type: string) {
      const roundOffset = Math.min(offset1, offset2) + (Math.max(offset1, offset2) - Math.min(offset1, offset2)) / 2;

      let finalOffset = (roundOffset - offset2) - 7;

      if (finalOffset > 20) {
        finalOffset = 20;
      }
      if (finalOffset < -50) {
        finalOffset = -50;
      }
      if (type.substring(1, 2) === 'T') {
        return finalOffset;
      } else if (type.substring(1, 2) === 'A') {
        return 0;
      } else {
        if (offset1 < offset2) {
          let circleOffset = -(roundOffset - offset1 - 40);

          if (circleOffset < -40) {
            circleOffset = -40;
          }
          return circleOffset;
        } else {
          let circleOffset = -(roundOffset - offset1 + 40);

          if (circleOffset > 40) {
            circleOffset = 40;
          }
          return circleOffset;
        }
      }
    },

    offsetY(offset1: number, offset2: number, type: string) {
      const roundOffset = Math.min(offset1, offset2) + (Math.max(offset1, offset2) - Math.min(offset1, offset2)) / 2;
      let finalOffset = (roundOffset - offset2) - 15;

      if (finalOffset > 10) {
        finalOffset = 10;
      }
      if (finalOffset < -70) {
        finalOffset = -70;
      }

      if (type.substring(1, 2) === 'T') {
        return finalOffset;
      } else if (type.substring(1, 2) === 'A') {
        return 0;
      } else {
        if (offset1 < offset2) {
          let circleOffset = -(roundOffset - offset1 - 40);

          if (circleOffset < -40) {
            circleOffset = -40;
          }
          return circleOffset;
        } else {
          let circleOffset = -(roundOffset - offset1 + 40);

          if (circleOffset > 40) {
            circleOffset = 40;
          }
          return circleOffset;
        }
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
          this.connections[this.connections.length - 1].st = this.elements[this.hovered_target].name;
          document.getElementById(this.current_connection)?.removeEventListener('mousemove', this.connection_drag);

          if ((this.connections[this.connections.length - 1].ft.substring(1, 2) === 'C' &&
          this.connections[this.connections.length - 1].st.substring(1, 2) === 'C') ||
          (this.connections[this.connections.length - 1].ft.substring(1, 2) === 'T' &&
          this.connections[this.connections.length - 1].st.substring(1, 2) === 'T')) {
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
      this.connections.push({ name: 'EA' + this.counter, ft: this.elements[target].name, st: '' });
      this.children.push(Connection);
    },

    addToken() {
      if (this.current_target > 0) {
        if (this.elements[this.current_target].name.substring(1, 2) === 'C') {
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
            this.tokens.push({ name: 'EE' + this.counter, object_name: 'EE' + (this.elements.length - 1), label_name: 'EL' + this.elements.length, circle: this.elements[this.current_target].name, token_amount: 1 });
            this.children.push(SmallCircle);
            this.counter++;
            this.elements.push({ name: 'EL' + this.counter, x: this.elements[this.current_target].x, y: this.elements[this.current_target].y, x2: 0, y2: 0 });
            this.children.push(TokenText);
          }
        }
      }
    },

    substractToken() {
      if (this.current_target > 0) {
        let findCircle = false;
        for (let i = 0; i < this.tokens.length; i++) {
          if (this.tokens[i].circle === this.elements[this.current_target].name) {
            findCircle = true;
            this.tokens[i].token_amount--;
            if (this.tokens[i].token_amount === 0) {
              for (let j = 0; j < this.elements.length; j++) {
                if (this.elements[j].name === this.tokens[i].object_name) {
                  this.elements.splice(j, 1);
                  this.children.splice(j - 1, 1);
                }

                if (this.elements[j].name === this.tokens[i].label_name) {
                  this.elements.splice(j, 1);
                  this.children.splice(j - 1, 1);
                }
              }
              this.tokens.splice(i, 1);
            }
          }
        }
      }
    },

    checkIfTokenEmpty() {
      let deleteCounter = 0;
      this.resultSimulation.tokens.forEach((data, i) => {
        if (data.token_amount === 0) {
          for (let j = 0; j < this.elements.length; j++) {
            if (this.elements[j].name === this.tokens[i - deleteCounter].object_name) {
              this.elements.splice(j, 1);
              this.children.splice(j - 1, 1);
            }

            if (this.elements[j].name === this.tokens[i - deleteCounter].label_name) {
              this.elements.splice(j, 1);
              this.children.splice(j - 1, 1);
            }
          }
          this.tokens.splice(i - deleteCounter, 1);
          deleteCounter++;
        }
      });
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
            if (this.elements[j].name === this.connections[i].ft) {
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
          if (this.connections[i].st === 0) {
            secondElement = (this.elements.length - 1);
          } else {
            for (let j = 0; j < this.elements.length; j++) {
              if (this.elements[j].name === this.connections[i].st) {
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
          if (element.ft === this.elements[this.current_target].name || element.st === this.elements[this.current_target].name) {
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
        if (this.elements[this.current_target].name.substring(1, 2) === 'A') {
          for (let i = 0; i < this.connections.length; i++) {
            if (this.connections[i].name === this.elements[this.current_target].name) {
              this.connections.splice(i, 1);
            }
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
      if (this.children.length > 0) {
        Swal.fire({
          title: 'Wybierz format',
          showDenyButton: true,
          confirmButtonText: 'Standardowy',
          denyButtonText: 'Tina',
          denyButtonColor: '#7066e0'
        }).then((result) => {
          if (result.isConfirmed) {
            const elements = JSON.stringify(this.elements.slice(1));
            const connections = JSON.stringify(this.connections);
            const tokens = JSON.stringify(this.tokens);
            const data = '[' + elements + ',' + connections + ',' + tokens + ']';
            const blob = new Blob([data], { type: 'text/plain' });
            const anchor = document.createElement('a');
            anchor.download = 'PetriNet_import.txt';
            anchor.href = window.URL.createObjectURL(blob);
            anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');
            anchor.click();
          } else if (result.isDenied) {
            let data = '';
            this.elements.slice(1).forEach((element) => {
              if (element.name.substring(1, 2) === 'C') {
                data = data + 'p ' + element.x + '.0 ' + element.y + '.0 ' + element.name + ' ';

                if (this.tokens.length === 0) {
                  data = data + '0';
                } else {
                  let found = false;
                  for (let i = 0; i < this.tokens.length; i++) {
                    if (this.tokens[i].circle === element.name) {
                      data = data + this.tokens[i].token_amount;
                      found = true;
                    }
                  }
                  if (!found) {
                    data = data + '0';
                  }
                }

                data = data + ' n\n';
              }

              if (element.name.substring(1, 2) === 'T') {
                data = data + 't ' + element.x + '.0 ' + element.y + '.0 ' + element.name + ' 0 w n\n';
              }
            });

            this.connections.forEach((connection: { name: string, ft: string, st: string }) => {
              data = data + 'e ' + connection.ft + ' ' + connection.st + ' 1 n\n';
            });

            data = data + 'h PetriNetExport';

            const blob = new Blob([data], { type: 'text/plain' });
            const anchor = document.createElement('a');
            anchor.download = 'PetriNetExport.ndr';
            anchor.href = window.URL.createObjectURL(blob);
            anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');
            anchor.click();
          }
        });
      } else {
        Swal.fire(
          'Model jest pusty'
        );
      }
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
            for (let i = 0; i < JSON.parse(this.dest)[1].length; i++) {
              this.connections.push(JSON.parse(this.dest)[1][i]);
            }
            for (let i = 0; i < JSON.parse(this.dest)[2].length; i++) {
              this.tokens.push(JSON.parse(this.dest)[2][i]);
            }
            for (let i = 0; i < JSON.parse(this.dest)[0].length; i++) {
              const objectType = JSON.parse(this.dest)[0][i].name.substring(1, 2);
              if (objectType === 'C') {
                this.counter++;
                this.children.push(Circle);
              } else if (objectType === 'T') {
                this.counter++;
                this.children.push(Square);
              } else if (objectType === 'A') {
                this.counter++;
                this.children.push(Connection);
              } else if (objectType === 'E') {
                this.counter++;
                this.children.push(SmallCircle);
              } else if (objectType === 'L') {
                this.counter++;
                this.children.push(TokenText);
              } else {
                console.log('Zły plik');
                this.clear();
                break;
              }
              this.elements.push(JSON.parse(this.dest)[0][i]);
            }
          } catch (e) {
            this.clear();
            console.log('Zły plik');
          }
        };
        if (this.selectedFile != null) {
          reader.readAsText(this.selectedFile);
        }
      } else {
        console.log('Zły plik');
      }
    },

    redirectNetExport(netExport: string) {
      try {
        for (let i = 0; i < JSON.parse(netExport)[1].length; i++) {
          this.connections.push(JSON.parse(netExport)[1][i]);
        }
        for (let i = 0; i < JSON.parse(netExport)[2].length; i++) {
          this.tokens.push(JSON.parse(netExport)[2][i]);
        }
        for (let i = 0; i < JSON.parse(netExport)[0].length; i++) {
          const objectType = JSON.parse(netExport)[0][i].name.substring(1, 2);
          if (objectType === 'C') {
            this.counter++;
            this.children.push(Circle);
          } else if (objectType === 'T') {
            this.counter++;
            this.children.push(Square);
          } else if (objectType === 'A') {
            this.counter++;
            this.children.push(Connection);
          } else if (objectType === 'E') {
            this.counter++;
            this.children.push(SmallCircle);
          } else if (objectType === 'L') {
            this.counter++;
            this.children.push(TokenText);
          } else {
            this.clear();
            break;
          }
          this.elements.push(JSON.parse(netExport)[0][i]);
        }
      } catch (e) {
        this.clear();
      }
    },

    netChangeSimulation() {
      this.clear();
      try {
        for (let i = 0; i < this.resultSimulation.connections.length; i++) {
          this.connections.push(this.resultSimulation.connections[i]);
        }

        for (let i = 0; i < this.resultSimulation.tokens.length; i++) {
          this.tokens.push(this.resultSimulation.tokens[i]);
        }
        for (let i = 0; i < this.resultSimulation.elements.length; i++) {
          const objectType = this.resultSimulation.elements[i].name.substring(1, 2);
          if (objectType === 'C') {
            this.counter++;
            this.children.push(Circle);
          } else if (objectType === 'T') {
            this.counter++;
            this.children.push(Square);
          } else if (objectType === 'A') {
            this.counter++;
            this.children.push(Connection);
          } else if (objectType === 'E') {
            this.counter++;
            this.children.push(SmallCircle);
          } else if (objectType === 'L') {
            this.counter++;
            this.children.push(TokenText);
          } else {
            this.clear();
            break;
          }
          this.elements.push(this.resultSimulation.elements[i]);
        }
      } catch (e) {
        this.clear();
      }
    },

    saveModal() {
      if (history.state.editUserSave || history.state.editExampleNet) {
        Swal.fire({
          icon: 'warning',
          title: 'Czy napewno chcesz edytować tą sieć?',
          text: 'Nie będziesz mógł tego cofnąć!',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Tak, edytuj!',
          cancelButtonText: 'Anuluj'
        }).then((result) => {
          if (result.isConfirmed) {
            if (history.state.editUserSave) {
              this.saveResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + ']';
              this.update(history.state.editId);
            } else {
              this.exampleEditResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + ']';
              this.updateExampleNet(history.state.editId);
            }
            Swal.fire(
              'Zapisane!',
              'Twój model został zedytowany!',
              'success'
            );
          }
        });
      } else {
        Swal.fire({
          title: 'Podaj nazwę!',
          input: 'text',
          cancelButtonText: 'Anuluj',
          showCancelButton: true,
          inputPlaceholder: 'Wpisz nazwę!'
        }).then((result) => {
          if (result.value === '') {
            Swal.fire({
              title: 'Nie podałeś nazwy!'
            });
          } else if ((result.value).length < 3) {
            Swal.fire({
              title: 'Nazwa jest za krótka!'
            });
          } else if ((result.value).length > 16) {
            Swal.fire({
              title: 'Nazwa jest za długa!'
            });
          } else {
            if (this.children.length === 0) {
              Swal.fire({
                icon: 'error',
                title: 'Błąd!',
                text: 'Nie możesz zapisać pustego modelu!'
              });
            } else {
              if (!this.checkIfCreateExample()) {
                this.saveResult.userId = this.loginResult.id;
                this.saveResult.saveName = result.value;
                this.saveResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + ']';
                this.findByUserAndSaveName(result.value).then((id) => {
                  if (id !== 0) {
                    Swal.fire({
                      icon: 'warning',
                      title: 'Posiadasz już model z taką nazwą!',
                      text: 'Czy napewno chcesz nadpisać model? Nie będziesz mógł tego cofnąć!',
                      showCancelButton: true,
                      confirmButtonColor: '#d33',
                      cancelButtonColor: '#3085d6',
                      confirmButtonText: 'Tak, zapisz!',
                      cancelButtonText: 'Anuluj'
                    }).then((result) => {
                      if (result.isConfirmed) {
                        this.update(id);
                        Swal.fire(
                          'Zapisane!',
                          'Twój model został zapisany.',
                          'success'
                        );
                      }
                    });
                  } else {
                    this.create().then((result) => {
                      if (result !== 0) {
                        Swal.fire({
                          icon: 'error',
                          title: 'Błąd!',
                          text: 'Wystąpił błąd!'
                        });
                      } else {
                        Swal.fire(
                          'Zapisane!',
                          'Twój model został zapisany.',
                          'success'
                        );
                      }
                    });
                  }
                });
              } else {
                this.exampleNetResult.netName = result.value;
                this.exampleNetResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + ']';
                this.findByNetName(result.value).then((exist) => {
                  if (exist) {
                    Swal.fire({
                      icon: 'warning',
                      title: 'Istnieje już model z taką nazwą!'
                    });
                  } else {
                    this.createExampleNet().then((result) => {
                      if (result !== 0) {
                        Swal.fire({
                          icon: 'error',
                          title: 'Błąd!',
                          text: 'Wystąpił błąd!'
                        });
                      } else {
                        Swal.fire(
                          'Zapisane!',
                          'Model przykładowy został zapisany.',
                          'success'
                        );
                      }
                    });
                  }
                });
              }
            }
          }
        });
      }
    },

    fetchData: function() {
      return this.children.length;
    },

    checkIfCreateExample() {
      if (history.state.createExample) {
        return true;
      }

      return false;
    },

    checkIfEdited() {
      if (history.state.editUserSave) {
        return true;
      }

      return false;
    },

    checkIfExampleEdited() {
      if (history.state.editExampleNet) {
        return true;
      }

      return false;
    }
  }
});
</script>
