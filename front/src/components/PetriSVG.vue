<!-- eslint-disable vue/no-v-text-v-html-on-component -->
<template>
  <div class="flex w-full h-16 items-center">
    <div class="flex flex-row w-full h-16 items-center justify-start">
      <div class="flex items-center petri-nav w-3/12 select-none ml-8 justify-center">
        <span v-if='checkIfCreateExample()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Tworzenie przykładowej sieci</span>
        <span v-if='checkIfEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie sieci: {{ this.saveResult.saveName }} <br> Użytkownika: {{ this.editedSaveUserEmail }} </span>
        <span v-if='checkIfExampleEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie przykładowej sieci: {{ this.exampleEditResult.netName }}</span>
      </div>
      <div class="flex items-center petri-nav w-2/12 select-none justify-center">
        <button class="border-2 border-black rounded-bl-xl border-r-0 rounded-tl-xl p-1 items-center" v-on:click="addToken(); addConnectionWeight()">
          <PlusIcon class="inline-block align-middle" />
        </button>
        <label class='select-none'>
          <input class="border-2 border-black items-center text-center pointer-events-none w-12" :value="this.elements[this.current_target].name" disabled>
        </label>
        <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-1 items-center" v-on:click="substractToken(); substractConnectionWeight()">
          <MinusIcon class="inline-block align-middle" />
        </button>
      </div>
      <div class="flex ml-4 items-center petri-nav">
        <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center" @click='run(); this.running = true;' :disabled='this.running'>
          <RunIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Run</span>
        </button>
        <button class="border-2 border-black border-l-0 rounded-br-xl rounded-tr-xl p-2 items-center" @click='stop()'>
          <StopIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Stop</span>
        </button>
      </div>
      <div class="flex ml-4 items-center petri-nav">
        <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center" v-on:click="switchPlace" :style='this.selectedElement === "place" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <CircleIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Place [1]</span>
        </button>
        <button class="border-2 border-black border-l-0 p-2 items-center" v-on:click="switchTransition" :style='this.selectedElement === "transition" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <SquareIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Transition [2]</span>
        </button>
        <button class="border-2 border-black border-l-0 border-r-0 p-2 items-center" v-on:click="switchDelete" :style='this.selectedElement === "delete" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <RemoveIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Remove [3]</span>
        </button>
        <button class="border-2 border-black rounded-br-xl rounded-tr-xl p-2 items-center" v-on:click="clear" :disabled="this.running">
          <ClearIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Clear</span>
        </button>
      </div>
    </div>
  </div>
  <div class="mx-8 my-4 border-2 border-black rounded-xl h-4/5 paper-container">
    <div ref="petriEditor" class='rounded-xl petriEditor'></div>
    <!-- <svg ref="box" class="bg-gray-300 rounded-xl box" height="100%" width="100%" xmlns="http://www.w3.org/2000/svg" @mouseup='endDrag()' @mouseenter="endDrag()">
      <defs>
        <marker id="mkrArrow" markerWidth="10" markerHeight="7" refX="0" refY="3.5" orient="auto">
          <polygon points="0 0, 10 3.5, 0 7" />
        </marker>
      </defs>
      <template v-for="(child, index) in children" :key="child.name">
        <component v-if='this.elements[index+1].name.substring(1,2) == "C"' :id='this.elements[index+1].name' :is="child"
          :stroke="selectedComponent(index+1)"
          :cx="this.elements[index+1].x" :cy="this.elements[index+1].y"
          @start-drag="startDrag(index+1)" @end-drag="endDrag" @mouseover="setHoveredID(index+1)" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "T"' :id='this.elements[index+1].name' :is="child"
          :stroke="selectedComponent(index+1)"
          :x="this.elements[index+1].x" :y="this.elements[index+1].y"
          :width="this.transition_width" :height="this.transition_height"
          @start-drag="startDrag(index+1)" @end-drag="endDrag()" @mouseover="setHoveredID(index+1)" @mouseleave="setHoveredID(0)">
        </component>

        <component v-if='this.elements[index+1].name.substring(1,2) == "A"' :id='this.elements[index+1].name' :is="child"
          @click='selectConnection(index+1)'
          :stroke="selectedComponent(index+1)"
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

        <component v-if='this.elements[index+1].name.substring(1,2) == "W"' :id='this.elements[index+1].name' :is="child"
          v-text="findConnectionText(this.elements[index+1].name)"
          :x="this.elements[findFirstConnection(findConnection(this.elements[index+1].name))].x - ((this.elements[findFirstConnection(findConnection(this.elements[index+1].name))].x - this.elements[findSecondConnection(findConnection(this.elements[index+1].name))].x) / 2)"
          :y="this.elements[findFirstConnection(findConnection(this.elements[index+1].name))].y - ((this.elements[findFirstConnection(findConnection(this.elements[index+1].name))].y - this.elements[findSecondConnection(findConnection(this.elements[index+1].name))].y) / 2)"
          @mouseover="setHoveredID(findConnection(this.elements[index+1].name))" @mouseleave="setHoveredID(0)">
        </component>
      </template>

      <template v-for="(animate) in animations" :key="animate">
        <component :is="animate" />
      </template>
    </svg> -->
  </div>
  <div class="flex w-full h-16 items-center justify-center">
    <div class="flex items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="checkNetAlert">
        <CheckNetIcon class="inline-block align-middle" />
        <span class="inline-block align-middle select-none">Check Net</span>
      </button>
      <button class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="importNet">
        <ImportIcon class="inline-block align-middle" />
        <span class="inline-block align-middle select-none">Import</span>
      </button>
      <input ref='import' type='file' class='hidden' @change='importNetChanged'>
      <button class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="exportNet">
        <ExportIcon class="inline-block align-middle" />
        <span class="inline-block align-middle select-none">Export</span>
      </button>
      <button v-if="checkIfLogged()" @click="saveModal()" class="ml-4 border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" data-bs-toggle="modal" data-bs-target="#saveNetModal">
        <SaveIcon class="inline-block align-middle" />
        <span class="inline-block align-middle select-none">Save</span>
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
import CheckNetIcon from 'vue-material-design-icons/CheckNetwork.vue';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import Swal from 'sweetalert2';
import UserServices, { IUser } from '@/services/UserService';
import axios, { AxiosError } from 'axios';
import LoginServices, { ILogin } from '@/services/LoginService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import SimulationServices, { ISimulation } from '@/services/SimulationService';
import * as joint from 'jointjs';

const Circle = markRaw({
  template: `
    <circle class="element" r="40" stroke-width="2" fill="#ffe6ee" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const SmallCircle = markRaw({
  template: `
    <circle class="element" r="10" fill="black" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Square = markRaw({
  template: `
    <rect class="element" stroke-width="2" fill="rgb(0,0,255)" @mousedown="$emit('start-drag')" @mouseup="$emit('end-drag')"/>
  `
});

const Connection = markRaw({
  template: `
    <line class="element" stroke-width="3" marker-end="url(#mkrArrow)" fill="rgb(0,0,255)"/>
  `
});

const TokenText = markRaw({
  template: `
    <text class="fill-black text-center select-none" disabled></text>
  `
});

const WeightText = markRaw({
  template: `
    <text class="text-center select-none" fill="rgb(255,87,51)" disabled></text>
  `
});

const placeSettings = {
  circle: {
    fill: '#ffffff',
    r: 20
  }
};

const transitionSettings = {
  rect: {
    fill: '#ffffff',
    width: 20,
    height: 60
  }
};

export default defineComponent({
  paper: null as joint.dia.Paper | null,
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
    MinusIcon,
    CheckNetIcon
  },
  data() {
    return {
      paper: null as any,
      graph: null as any,
      contextShow: false,
      pn: null,
      selectedElement: '',
      selectedLink: null,
      mode: 'pointer',
      commandKeyDown: false,
      placeCounter: 0,
      transitionCounter: 0,
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
      connection_weight: [] as any,
      connection_edit: false,
      current_connection: null as any,
      current_connections: [] as any,
      ctrl_pressed: false,
      current_target: 0,
      hovered_target: 0,
      transition_width: 30,
      transition_height: 60,
      children: [] as any,
      animations: [] as any,
      counter: 0,
      dest: null as any,
      selecting: false,
      selectedFile: null,
      saveResult: SaveNetServices.getBlankSaveNetTemplate(),
      exampleEditResult: ExampleNetServices.getBlankExampleNetTemplate(),
      editedSaveUserEmail: '',
      exampleNetResult: ExampleNetServices.getBlankExampleNetTemplate(),
      loginResult: LoginServices.getBlankLoginTemplate(),
      resultRef: SaveNetServices.getBlankSaveNetTemplate(),
      resultSimulation: SimulationServices.getBlankSimulationTemplate(),
      beforeSimulation: SimulationServices.getBlankSimulationTemplate(),
      simulationCounter: 0,
      running: false
    };
  },
  watch: {
    resultRef() {
      if (this.resultRef.id !== 0) {
        this.redirectNetExport(this.resultRef.netExport);
      }
    },

    async simulationCounter() {
      if (this.simulationCounter === 1) {
        this.beforeSimulation.elements.splice(0);
        this.beforeSimulation.connections.splice(0);
        this.beforeSimulation.tokens.splice(0);
        this.beforeSimulation.connectionWeights.splice(0);
        this.elements.slice(1).forEach((data) => {
          this.beforeSimulation.elements.push(data);
        });
        this.connections.forEach((data: any) => {
          this.beforeSimulation.connections.push(data);
        });
        this.tokens.forEach((data: any) => {
          this.beforeSimulation.tokens.push(data);
        });
        this.connection_weight.forEach((data: any) => {
          this.beforeSimulation.connectionWeights.push(data);
        });
      }
    }
  },

  mounted() {
    const container = this.$refs.petriEditor;

    this.graph = new joint.dia.Graph();

    let modelState: any;

    const paper = new joint.dia.Paper({
      el: container,
      model: this.graph,
      width: '100%',
      height: '100%',
      gridSize: 10,
      drawGrid: true,
      interactive: {
        linkMoveable: false
      },
      background: {
        color: 'white',
        gridWidth: 2,
        gridColor: '#000000'
      }
    } as joint.dia.Paper.Options);

    let startPoint: any;
    let endPoint: any;

    let currentScale = 1;

    paper.on('blank:mousewheel', function(event: MouseEvent, x: number, y: number, delta: number) {
      event.preventDefault();
      const direction = delta > 0 ? 1 : -1;
      const increment = 0.05;
      const zoomLevel = direction * increment;
      const maxZoom = 2;
      const minZoom = 0.2;
      const newScale = currentScale + zoomLevel;
      if (newScale >= minZoom && newScale <= maxZoom) {
        currentScale = newScale;
        paper.scale(newScale, newScale);
      }
    });

    let rect: any;

    let startX: any, startY: any;
    let moveX: any, moveY: any;

    paper.on('cell:pointerdown', (cellView: any, event, x, y) => {
      if (!this.ctrl_pressed) {
        if (cellView.model.attributes.type !== 'pn.Link') {
          startX = cellView.model.get('position').x;
          startY = cellView.model.get('position').y;
          const lastStep = this.graph.toJSON();
          modelState = lastStep;
        }

        if (!cellView.model.attributes.selected) {
          this.graph.getCells().forEach((element: any) => {
            element.attributes.selected = false;
            if (element.attributes.type === 'pn.Place') {
              element.attr({
                circle: {
                  stroke: 'black'
                }
              });
            } else if (element.attributes.type === 'pn.Transition') {
              element.attr({
                rect: {
                  stroke: 'black'
                }
              });
            } else if (element.attributes.type === 'pn.Link') {
              const link = this.graph.getCell(element.id);

              element.attr('.marker-arrowhead[end=target]', { style: { 'background-color': 'black' } });
            }
          });
          cellView.model.attributes.selected = true;
          if (cellView.model.attributes.type === 'pn.Place') {
            cellView.model.attr({
              circle: {
                stroke: 'red'
              }
            });
          } else if (cellView.model.attributes.type === 'pn.Transition') {
            cellView.model.attr({
              rect: {
                stroke: 'red'
              }
            });
          } else if (cellView.model.attributes.type === 'pn.Link') {
            cellView.model.attr('.marker-arrowhead[end=target]', { style: { 'background-color': 'red' } });
          }
        }
      } else {
        if (!cellView.model.attributes.selected) {
          const link = new joint.shapes.pn.Link({
            source: { id: cellView.model.id },
            target: { x: x, y: y },
            attrs: {
              '.marker-target': { display: 'none' },
              '.marker-source': { display: 'none' },
              '.link-tools': { display: 'none' },
              '.connection': { stroke: 'black', 'pointer-events': 'none' }
            },
            weight: 1,
            selected: false
          });

          const lastStep = this.graph.toJSON();
          modelState = lastStep;
          this.graph.addCell(link);

          link.appendLabel({
            attrs: {
              text: {
                text: '1',
                'pointer-events': 'none',
                'font-size': 20,
                'font-weight': 'bold',
                fill: 'black',
                stroke: 'white',
                'stroke-width': 1
              },
              rect: {
                fill: 'white',
                'fill-opacity': 0,
                stroke: 'none',
                'pointer-events': 'none'
              }
            },
            position: {
              distance: -22
            }
          });

          link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
          link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

          this.current_connection = link.id;
        } else {
          let checkIfPlacesSelected = true;
          let checkIfTransitionsSelected = true;
          this.graph.getCells().forEach((element: any) => {
            if (element.attributes.selected) {
              if (element.attributes.type !== 'pn.Place') {
                checkIfPlacesSelected = false;
              }
              if (element.attributes.type !== 'pn.Transition') {
                checkIfTransitionsSelected = false;
              }
            }
          });
          if (!checkIfPlacesSelected && !checkIfTransitionsSelected) {
            const link = new joint.shapes.pn.Link({
              source: { id: cellView.model.id },
              target: { x: x, y: y },
              attrs: {
                '.marker-target': { display: 'none' },
                '.marker-source': { display: 'none' },
                '.link-tools': { display: 'none' },
                '.connection': { stroke: 'black', 'pointer-events': 'none' }
              },
              weight: 1,
              selected: false
            });

            const lastStep = this.graph.toJSON();
            modelState = lastStep;
            this.graph.addCell(link);

            link.appendLabel({
              attrs: {
                text: {
                  text: '1',
                  'pointer-events': 'none',
                  'font-size': 20,
                  'font-weight': 'bold',
                  fill: 'black',
                  stroke: 'white',
                  'stroke-width': 1
                },
                rect: {
                  fill: 'white',
                  'fill-opacity': 0,
                  stroke: 'none',
                  'pointer-events': 'none'
                }
              },
              position: {
                distance: -22
              }
            });

            link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
            link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

            this.current_connection = link.id;
          } else {
            const lastStep = this.graph.toJSON();
            modelState = lastStep;
            this.graph.getCells().forEach((element: any) => {
              if ((element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition') && element.attributes.selected) {
                const link = new joint.shapes.pn.Link({
                  source: { id: element.id },
                  target: { x: x, y: y },
                  attrs: {
                    '.marker-target': { display: 'none' },
                    '.marker-source': { display: 'none' },
                    '.link-tools': { display: 'none' },
                    '.connection': { stroke: 'black', 'pointer-events': 'none' }
                  },
                  weight: 1,
                  selected: false
                });

                this.graph.addCell(link);

                link.appendLabel({
                  attrs: {
                    text: {
                      text: '1',
                      'pointer-events': 'none',
                      'font-size': 20,
                      'font-weight': 'bold',
                      fill: 'black',
                      stroke: 'white',
                      'stroke-width': 1
                    },
                    rect: {
                      fill: 'white',
                      'fill-opacity': 0,
                      stroke: 'none',
                      'pointer-events': 'none'
                    }
                  },
                  position: {
                    distance: -22
                  }
                });

                link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                this.current_connections.push(link.id);
              }
            });
          }
        }
      }
    });

    paper.on('blank:pointerdown', (evt: any, x: number, y: number) => {
      if (this.selectedElement === 'place') {
        const lastStep = this.graph.toJSON();
        modelState = lastStep;
        this.addPlace(x, y);
      } else if (this.selectedElement === 'transition') {
        const lastStep = this.graph.toJSON();
        modelState = lastStep;
        this.addTransition(x, y);
      } else if (this.selectedElement === '') {
        startPoint = { x, y };

        rect = new joint.shapes.basic.Rect({
          position: {
            x: Math.min(startPoint.x, startPoint.x),
            y: Math.min(startPoint.y, startPoint.y)
          },
          size: {
            width: Math.abs(startPoint.x - startPoint.x),
            height: Math.abs(startPoint.y - startPoint.y)
          },
          attrs: {
            rect: {
              stroke: 'black',
              'stroke-width': 1,
              fill: 'gray',
              opacity: 0.2
            }
          }
        });

        this.graph.addCell(rect);

        paper.on('blank:pointermove', (event, x, y) => {
          endPoint = { x, y };

          (rect as any).set('size', {
            width: Math.abs(startPoint.x - endPoint.x),
            height: Math.abs(startPoint.y - endPoint.y)
          });

          (rect as any).set('position', {
            x: Math.min(startPoint.x, endPoint.x),
            y: Math.min(startPoint.y, endPoint.y)
          });
        });
      }
    });

    paper.on('cell:pointermove', (cellView: any, event, x, y) => {
      if (cellView.model.attributes.type !== 'pn.Link') {
        if (!this.ctrl_pressed) {
          if (cellView.model.attributes.selected) {
            moveX = cellView.model.get('position').x - startX;
            moveY = cellView.model.get('position').y - startY;
            this.graph.getCells().forEach((element: any) => {
              if ((element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition') && element.attributes.selected && element.cid !== cellView.model.cid) {
                const position = element.get('position');
                element.set('position', { x: position.x + moveX, y: position.y + moveY });
              }
            });
            startX = startX + moveX;
            startY = startY + moveY;
          }
        } else {
          if (this.current_connections.length === 0) {
            const link = this.graph.getCell(this.current_connection);
            link.prop('target', { x: x, y: y });
          } else {
            this.current_connections.forEach((element: any) => {
              const link = this.graph.getCell(element);
              link.prop('target', { x: x, y: y });
            });
          }
        }
      } else {
        event.preventDefault();
      }
    });

    paper.on('cell:pointerup', (cellView: any, event, x, y) => {
      if (this.selectedElement === 'delete') {
        this.graph.getCells().forEach((element: any) => {
          if (cellView.model.id === element.attributes.place) {
            element.remove();
          }
        });
        cellView.model.remove();
      }
      if (this.current_connections.length === 0) {
        if (this.current_connection !== null) {
          const paperOffset = (paper as any).el.getBoundingClientRect();
          const link = this.graph.getCell(this.current_connection);
          let found = false;
          this.graph.getCells().forEach((element: any) => {
            const bbox = element.getBBox();
            if ((event.clientX - paperOffset.left) >= bbox.x && (event.clientX - paperOffset.left) <= bbox.x + bbox.width && (event.clientY - paperOffset.top) >= bbox.y && (event.clientY - paperOffset.top) <= bbox.y + bbox.height) {
              if (element instanceof joint.shapes.pn.Place || element instanceof joint.shapes.pn.Transition) {
                found = true;
                if (element.id === cellView.model.id) {
                  link.remove();
                  this.current_connection = null;
                } else if ((element as any).attributes.type === cellView.model.attributes.type) {
                  link.remove();
                  this.current_connection = null;
                } else {
                  this.graph.getCells().forEach((exist: any) => {
                    if (exist.attributes.type === 'pn.Link') {
                      if (exist.attributes.source.id === cellView.model.id && exist.attributes.target.id === element.id) {
                        link.remove();
                        this.current_connection = null;
                      }
                    }
                  });
                  link.prop('target', { id: element.id });
                  this.current_connection = null;
                }
              }
            }
          });
          if (!found) {
            link.remove();
            this.current_connection = null;
          }
        }
      } else if (this.current_connections.length > 0) {
        const paperOffset = (paper as any).el.getBoundingClientRect();
        this.current_connections.forEach((place: any) => {
          const link = this.graph.getCell(place);
          let found = false;
          this.graph.getCells().forEach((element: any) => {
            const bbox = element.getBBox();
            if ((event.clientX - paperOffset.left) >= bbox.x && (event.clientX - paperOffset.left) <= bbox.x + bbox.width && (event.clientY - paperOffset.top) >= bbox.y && (event.clientY - paperOffset.top) <= bbox.y + bbox.height) {
              if (element instanceof joint.shapes.pn.Place || element instanceof joint.shapes.pn.Transition) {
                found = true;
                if (element.id === cellView.model.id) {
                  link.remove();
                } else if ((element as any).attributes.type === cellView.model.attributes.type) {
                  link.remove();
                } else {
                  this.graph.getCells().forEach((exist: any) => {
                    if (exist.attributes.type === 'pn.Link') {
                      if (exist.attributes.source.id === place && exist.attributes.target.id === element.id) {
                        console.log('jd');
                        link.remove();
                      }
                    }
                  });
                  console.log(bbox);
                  link.prop('target', { id: element.id });
                }
              }
            }
          });
          if (!found) {
            link.remove();
          }
        });
        this.current_connections = [];
      }
    });

    paper.on('blank:pointerup', () => {
      if (this.selectedElement === '') {
        this.graph.getCells().forEach((element: any) => {
          element.attributes.selected = false;
          if (element.attributes.type === 'pn.Place') {
            element.attr({
              circle: {
                stroke: 'black'
              }
            });
          } else if (element.attributes.type === 'pn.Transition') {
            element.attr({
              rect: {
                stroke: 'black'
              }
            });
          }
        });
        this.graph.getCells().forEach((element: any) => {
          if (element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition') {
            const placeX = element.position().x;
            const placeY = element.position().y;
            const placeWidth = element.get('size').width;
            const placeHeight = element.get('size').height;

            const rectX = rect.position().x;
            const rectY = rect.position().y;
            const rectWidth = rect.get('size').width;
            const rectHeight = rect.get('size').height;

            const topLeft = placeX >= rectX && placeY >= rectY;
            const bottomRight = placeX + placeWidth <= rectX + rectWidth && placeY + placeHeight <= rectY + rectHeight;

            if (topLeft && bottomRight) {
              element.attributes.selected = true;
              if (element.attributes.type === 'pn.Place') {
                element.attr({
                  circle: {
                    stroke: 'red'
                  }
                });
              } else if (element.attributes.type === 'pn.Transition') {
                element.attr({
                  rect: {
                    stroke: 'red'
                  }
                });
              }
            }
          }
        });
        rect.remove();
      }
    });

    paper.on('cell:contextmenu', (cellView, evt) => {
      evt.preventDefault();
      this.contextShow = true;

      if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition' || (cellView as any).model.get('type') === 'pn.Link') {
        let nameDiv: any;
        let nameInput: any;
        let textName: any;

        let tokenDiv: any;
        let tokenInput: any;
        let textToken: any;

        let sizeDiv: any;
        let sizeInput: any;
        let textSize: any;

        let weightDiv: any;
        let weightInput: any;
        let textWeight: any;

        let widthDiv: any;
        let widthInput: any;
        let textWidth: any;

        let heightDiv: any;
        let heightInput: any;
        let textHeight: any;

        const editWindow = document.createElement('div');
        if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
          nameDiv = document.createElement('div');
        }
        if ((cellView as any).model.get('type') === 'pn.Place') {
          tokenDiv = document.createElement('div');
          sizeDiv = document.createElement('div');
        }
        if ((cellView as any).model.get('type') === 'pn.Transition') {
          widthDiv = document.createElement('div');
          heightDiv = document.createElement('div');
        }
        if ((cellView as any).model.get('type') === 'pn.Link') {
          weightDiv = document.createElement('div');
        }
        editWindow.classList.add('editElements');
        editWindow.style.position = 'absolute';
        editWindow.style.left = evt.pageX + 'px';
        editWindow.style.top = evt.pageY + 'px';
        editWindow.style.background = 'white';
        editWindow.style.border = '1px solid black';
        editWindow.style.borderRadius = '0.75rem';
        editWindow.style.padding = '10px';

        if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
          nameInput = document.createElement('input');
          nameInput.type = 'text';
          nameInput.value = (cellView as any).model.get('attrs')['.label'].text;

          textName = document.createElement('p');
          textName.innerHTML = 'Nazwa';
        }

        if ((cellView as any).model.get('type') === 'pn.Place') {
          tokenInput = document.createElement('input');
          tokenInput.type = 'number';
          tokenInput.setAttribute('min', '0');
          tokenInput.value = (cellView as any).model.get('tokens');

          tokenInput.addEventListener('input', () => {
            if (parseInt(tokenInput.value) < 0) {
              tokenInput.value = '0';
            }
          });

          textToken = document.createElement('p');
          textToken.innerHTML = 'Tokeny';

          sizeInput = document.createElement('input');
          sizeInput.type = 'number';
          sizeInput.setAttribute('min', '0');
          sizeInput.value = (cellView as any).model.get('attrs').circle.r;

          sizeInput.addEventListener('input', () => {
            if (parseInt(sizeInput.value) < 0) {
              sizeInput.value = '10';
            }
          });

          textSize = document.createElement('p');
          textSize.innerHTML = 'Rozmiar';
        }

        if ((cellView as any).model.get('type') === 'pn.Transition') {
          widthInput = document.createElement('input');
          widthInput.type = 'number';
          widthInput.setAttribute('min', '0');
          widthInput.value = (cellView as any).model.get('attrs').rect.width;

          widthInput.addEventListener('input', () => {
            if (parseInt(widthInput.value) < 0) {
              widthInput.value = '20';
            }
          });

          textWidth = document.createElement('p');
          textWidth.innerHTML = 'Szerokość';

          heightInput = document.createElement('input');
          heightInput.type = 'number';
          heightInput.setAttribute('min', '0');
          heightInput.value = (cellView as any).model.get('attrs').rect.height;

          heightInput.addEventListener('input', () => {
            if (parseInt(heightInput.value) < 0) {
              heightInput.value = '20';
            }
          });

          textHeight = document.createElement('p');
          textHeight.innerHTML = 'Wysokość';
        }

        if ((cellView as any).model.get('type') === 'pn.Link') {
          weightInput = document.createElement('input');
          weightInput.type = 'number';
          weightInput.setAttribute('min', '1');
          weightInput.value = (cellView as any).model.get('weight');

          weightInput.addEventListener('input', () => {
            if (parseInt(weightInput.value) < 1) {
              weightInput.value = '0';
            }
          });

          textWeight = document.createElement('p');
          textWeight.innerHTML = 'Waga';
        }

        const saveButton = document.createElement('button');
        saveButton.innerHTML = 'Save';
        saveButton.onclick = () => {
          const lastStep = this.graph.toJSON();
          modelState = lastStep;
          if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
            let nameExist = false;
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.type !== 'pn.Link' && element.attributes.type !== 'basic.Text') {
                if (!element.attributes.attrs['.label']) {
                  if ((cellView as any).model.id !== element.id && element.attributes.attrs['.label'].text === nameInput.value) {
                    nameExist = true;
                  }
                }
              }
            });
            if (!nameExist) {
              (cellView as any).model.attr('.label/text', nameInput.value);
            }
          }
          if ((cellView as any).model.get('type') === 'pn.Place') {
            if (tokenInput.value === '') {
              tokenInput.value = '0';
            }
            if (sizeInput.value === '') {
              sizeInput.value = '20';
            }
            if (sizeInput.value < 10) {
              sizeInput.value = '10';
            }
            (cellView as any).model.attr('circle/r', sizeInput.value);
            (cellView as any).model.set('tokens', tokenInput.value);
            if (parseInt(tokenInput.value) === 0) {
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.place === (cellView as any).model.get('id')) {
                  element.remove();
                }
              });
            }

            if (parseInt(tokenInput.value) > 0 && tokenInput.value.length <= 4) {
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.place === (cellView as any).model.get('id')) {
                  element.remove();
                }
              });
              const tokens = (cellView as any).model.get('tokens');

              let offset = 0;
              let tokenWidth = 9;

              for (let i = 1; i < tokens.toString().length; i++) {
                offset += 4;
                tokenWidth += 9;
              }

              const tokensText = new joint.shapes.basic.Text({
                position: { x: (cellView as any).model.position().x + 26 - offset, y: (cellView as any).model.position().y + 22 },
                size: { width: tokenWidth, height: 15 },
                attrs: {
                  text: {
                    text: (cellView as any).model.get('tokens').toString(),
                    'pointer-events': 'none',
                    'font-size': 18,
                    'text-anchor': 'middle',
                    'ref-x': '50%',
                    'ref-y': 0.5,
                    'y-alignment': 'middle',
                    'x-alignment': 'middle'
                  }
                },
                place: (cellView as any).model.get('id'),
                locked: true
              });

              this.graph.addCell(tokensText);
              (cellView as any).model.on('change:position', () => {
                tokensText.position(
                  (cellView as any).model.position().x + 26 - offset,
                  (cellView as any).model.position().y + 22
                );
              });

              // const circle = new joint.shapes.basic.Circle({
              //   position: { x: (cellView as any).model.position().x, y: (cellView as any).model.position().y },
              //   size: { width: 25, height: 25 },
              //   attrs: {
              //     circle: {
              //       fill: 'blue',
              //       'pointer-events': 'none',
              //       r: 20
              //     }
              //   }
              // });
              // this.graph.addCell(circle);
              // (cellView as any).model.on('change:position', () => {
              //   circle.position(
              //     (cellView as any).model.position().x,
              //     (cellView as any).model.position().y
              //   );
              // });
            }
          }
          if ((cellView as any).model.get('type') === 'pn.Transition') {
            if (widthInput.value === '') {
              widthInput.value = '20';
            }
            if (heightInput.value === '') {
              heightInput.value = '60';
            }
            if (widthInput.value < 10) {
              widthInput.value = '10';
            }
            if (heightInput.value < 10) {
              heightInput.value = '10';
            }
            (cellView as any).model.attr('rect/width', widthInput.value);
            (cellView as any).model.attr('rect/height', heightInput.value);
          }
          if ((cellView as any).model.get('type') === 'pn.Link') {
            const link = new joint.shapes.pn.Link({
              source: { id: (cellView as any).model.attributes.source.id },
              target: { id: (cellView as any).model.attributes.target.id },
              attrs: {
                '.marker-target': { display: 'none' },
                '.marker-source': { display: 'none' },
                '.link-tools': { display: 'none' },
                '.connection': { stroke: 'black', 'pointer-events': 'none' }
              },
              weight: weightInput.value,
              selected: false
            });

            this.graph.addCell(link);

            const distance = 18 + (4 * weightInput.value.toString().length);

            link.appendLabel({
              attrs: {
                text: {
                  text: weightInput.value,
                  'pointer-events': 'none',
                  'font-size': 20,
                  'font-weight': 'bold',
                  fill: 'black',
                  stroke: 'white',
                  'stroke-width': 1
                },
                rect: {
                  fill: 'white',
                  'fill-opacity': 0,
                  stroke: 'none',
                  'pointer-events': 'none'
                }
              },
              position: {
                distance: -distance
              }
            });

            link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
            link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
            (cellView as any).model.remove();
          }
          editWindow.remove();
          this.contextShow = false;
        };

        const cancelButton = document.createElement('button');
        cancelButton.innerHTML = 'Cancel';
        cancelButton.onclick = () => {
          editWindow.remove();
          this.contextShow = false;
        };

        paper.on('blank:pointerdown', (evt) => {
          editWindow.remove();
          this.contextShow = false;
        });

        paper.on('cell:pointerdown', (evt) => {
          editWindow.remove();
          this.contextShow = false;
        });

        paper.on('cell:contextmenu', (cellView, evt) => {
          editWindow.remove();
        });

        if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
          nameDiv.appendChild(textName);
          nameDiv.appendChild(nameInput);
          editWindow.appendChild(nameDiv);
        }

        if ((cellView as any).model.get('type') === 'pn.Place') {
          tokenDiv.appendChild(textToken);
          tokenDiv.appendChild(tokenInput);
          editWindow.appendChild(tokenDiv);

          sizeDiv.appendChild(textSize);
          sizeDiv.appendChild(sizeInput);
          editWindow.appendChild(sizeDiv);
        }

        if ((cellView as any).model.get('type') === 'pn.Transition') {
          widthDiv.appendChild(textWidth);
          widthDiv.appendChild(widthInput);
          editWindow.appendChild(widthDiv);

          heightDiv.appendChild(textHeight);
          heightDiv.appendChild(heightInput);
          editWindow.appendChild(heightDiv);
        }

        if ((cellView as any).model.get('type') === 'pn.Link') {
          weightDiv.appendChild(textWeight);
          weightDiv.appendChild(weightInput);
          editWindow.appendChild(weightDiv);
        }

        editWindow.appendChild(saveButton);
        editWindow.appendChild(cancelButton);

        (container as any).appendChild(editWindow);
      }
    });

    window.addEventListener('keydown', (evt: KeyboardEvent) => {
      if (!this.contextShow) {
        if (evt.key === '1') {
          this.switchPlace();
        }
        if (evt.key === '2') {
          this.switchTransition();
        }
        if (evt.key === '3') {
          this.switchDelete();
        }
        if (evt.key === 'Delete') {
          const lastStep = this.graph.toJSON();
          modelState = lastStep;
          this.graph.getCells().forEach((element: any) => {
            if (typeof element !== 'undefined') {
              if (element.attributes.selected) {
                this.graph.getCells().forEach((text: any) => {
                  if (element.id === text.attributes.place) {
                    text.remove();
                  }
                });
                element.remove();
              }
            }
          });
        }
        if (evt.key === 'z' && evt.ctrlKey) {
          if (this.graph.getCells().length > 0) {
            if (this.graph.toJSON().cells.length > modelState.cells.length) {
              if (this.graph.getLastCell().attributes.type === 'pn.Place') {
                this.placeCounter--;
              } else if (this.graph.getLastCell().attributes.type === 'pn.Transition') {
                this.transitionCounter--;
              }
            } else {
              if (this.graph.getLastCell().attributes.type === 'pn.Place') {
                this.placeCounter++;
              } else if (this.graph.getLastCell().attributes.type === 'pn.Transition') {
                this.transitionCounter++;
              }
            }
          }
          this.graph.clear();
          for (let i = 0; i < modelState.cells.length; i++) {
            if (modelState.cells[i].type === 'pn.Place') {
              const place = new joint.shapes.pn.Place({
                position: { x: modelState.cells[i].position.x, y: modelState.cells[i].position.y },
                attrs: {
                  '.label': { text: modelState.cells[i].attrs['.label'].text, 'ref-x': 0.5, 'ref-y': -15 },
                  circle: {
                    fill: modelState.cells[i].attrs.circle.fill,
                    r: 20
                  }
                },
                size: {
                  width: modelState.cells[i].size.width,
                  height: modelState.cells[i].size.height
                },
                selected: false,
                tokens: modelState.cells[i].tokens,
                id: modelState.cells[i].id
              });

              this.graph.addCell(place);
              place.attr('circle/r', parseInt(modelState.cells[i].attrs.circle.r));
            } else if (modelState.cells[i].type === 'basic.Text') {
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.id === modelState.cells[i].place) {
                  const tokens = element.attributes.tokens;

                  let offset = 0;
                  let tokenWidth = 9;

                  for (let i = 1; i < tokens.toString().length; i++) {
                    offset += 4;
                    tokenWidth += 9;
                  }

                  const tokensText = new joint.shapes.basic.Text({
                    position: { x: element.attributes.position.x + 26 - offset, y: element.attributes.position.y + 22 },
                    size: { width: tokenWidth, height: 15 },
                    attrs: {
                      text: {
                        text: element.get('tokens').toString(),
                        'font-size': 18,
                        'text-anchor': 'middle',
                        'ref-x': '50%',
                        'ref-y': 0.5,
                        'y-alignment': 'middle',
                        'x-alignment': 'middle'
                      }
                    },
                    place: element.attributes.id,
                    locked: true
                  });

                  this.graph.addCell(tokensText);
                  element.on('change:position', () => {
                    tokensText.position(
                      element.attributes.position.x + 26 - offset,
                      element.attributes.position.y + 22
                    );
                  });
                }
              });
            } else if (modelState.cells[i].type === 'pn.Transition') {
              this.transitionCounter++;
              const transition = new joint.shapes.pn.Transition({
                position: { x: modelState.cells[i].position.x, y: modelState.cells[i].position.y },
                attrs: {
                  '.label': { text: modelState.cells[i].attrs['.label'].text, 'ref-x': 0.5, 'ref-y': -15 },
                  rect: {
                    fill: '#ffffff',
                    width: 20,
                    height: 60
                  }
                },
                id: modelState.cells[i].id,
                selected: false
              });

              this.graph.addCell(transition);

              transition.attr('rect/width', parseInt(modelState.cells[i].attrs.rect.width));
              transition.attr('rect/height', parseInt(modelState.cells[i].attrs.rect.height));
            } else if (modelState.cells[i].type === 'pn.Link') {
              const link = new joint.shapes.pn.Link({
                source: { id: modelState.cells[i].source.id },
                target: { id: modelState.cells[i].target.id },
                id: modelState.cells[i].id,
                attrs: {
                  '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.link-tools': { display: 'none' },
                  '.connection': { stroke: 'black', 'pointer-events': 'none' }
                },
                weight: modelState.cells[i].weight,
                selected: false
              });
              this.graph.addCell(link);

              console.log();

              const distance = 18 + (4 * modelState.cells[i].labels[0].attrs.text.text.toString().length);

              link.appendLabel({
                attrs: {
                  text: {
                    text: modelState.cells[i].labels[0].attrs.text.text,
                    'pointer-events': 'none',
                    'font-size': 20,
                    'font-weight': 'bold',
                    fill: 'black',
                    stroke: 'white',
                    'stroke-width': 1
                  },
                  rect: {
                    fill: 'white',
                    'fill-opacity': 0,
                    stroke: 'none',
                    'pointer-events': 'none'
                  }
                },
                position: {
                  distance: -distance
                }
              });

              link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
              link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
            }
          }
        }
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Control') {
        paper.setInteractivity({ elementMove: false });
        this.ctrl_pressed = true;
      }
    });

    document.addEventListener('keyup', (event) => {
      if (event.key === 'Control') {
        paper.setInteractivity({ elementMove: true });
        this.ctrl_pressed = false;
      }
    });

    if (history.state.redirectExport) {
      console.log(history.state.redirectExport);
      this.redirectNetExport(history.state.redirectExport);
    }

    if (this.$route.query.ref) {
      const ref = this.$route.query.ref as string;
      this.getByRef(ref).then((data) => (this.resultRef = data));
    }

    if (history.state.editUserSave) {
      this.getUserNet().then((data) => (this.saveResult = data));
      this.findUser().then((data) => (this.editedSaveUserEmail = data.email));
      this.redirectNetExport(history.state.editUserSave);
    }

    if (history.state.editExampleNet) {
      this.getExampleNet().then((data) => (this.exampleEditResult = data));
      this.redirectNetExport(history.state.editExampleNet);
    }

    if (localStorage.getItem('token') != null) {
      (this.getUser().then((data) => (this.loginResult = data))).catch((error) => {
        if (error?.response.status === 401) {
          localStorage.removeItem('token');
        }
      });
    }
  },

  methods: {

    switchPlace() {
      if (this.selectedElement === 'place') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'place';
      }
    },

    switchTransition() {
      if (this.selectedElement === 'transition') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'transition';
      }
    },

    switchDelete() {
      if (this.selectedElement === 'delete') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'delete';
      }
    },

    async getUser(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    async getUserNet(): Promise<ISaveNet> {
      return await SaveNetServices.find(history.state.editId);
    },

    async findUser(): Promise<IUser> {
      return await UserServices.findById(history.state.userId);
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

    async checkNet(result: ISimulation): Promise<boolean> {
      return await SimulationServices.checkNet(result);
    },

    async simulation(result: ISimulation): Promise<ISimulation> {
      return await SimulationServices.simulation(result);
    },

    async run() {
      this.resultSimulation.elements = this.elements.slice(1);
      this.resultSimulation.connections = this.connections;
      this.resultSimulation.tokens = this.tokens;
      this.resultSimulation.connectionWeights = this.connection_weight;

      this.simulationCounter++;

      await this.simulation(this.resultSimulation).then((data) => (this.resultSimulation = data));

      console.log(this.resultSimulation);

      await this.netChangeSimulation(this.resultSimulation);

      await this.checkIfNewToken();

      let animationCounter = 0;

      await this.customTimeout(0.5);

      await this.resultSimulation.changes.forEach(async (data, i) => {
        animationCounter++;
        if (!data.includes('Added:')) {
          const arc = data.split(' ');
          let elementName = '';
          this.connections.forEach((conn: any) => {
            if (conn.ft === arc[0] && conn.st === arc[1]) {
              elementName = conn.name;
            }
          });
          this.elements.forEach((ele, i) => {
            if (ele.name === elementName) {
              const x1 = this.elements[this.findFirstConnection(this.elements[i].name)].x + this.offset(this.elements[this.findFirstConnection(this.elements[i].name)].name, this.elements[this.findSecondConnection(this.elements[i].name)].x, this.elements[this.findFirstConnection(this.elements[i].name)].x, 'x');
              const y1 = this.elements[this.findFirstConnection(this.elements[i].name)].y + this.offset(this.elements[this.findFirstConnection(this.elements[i].name)].name, this.elements[this.findSecondConnection(this.elements[i].name)].y, this.elements[this.findFirstConnection(this.elements[i].name)].y, 'y');
              const x2 = this.elements[this.findSecondConnection(this.elements[i].name)].x - this.offsetX(this.elements[this.findSecondConnection(this.elements[i].name)].x, this.elements[this.findFirstConnection(this.elements[i].name)].x, this.elements[this.findSecondConnection(this.elements[i].name)].name);
              const y2 = this.elements[this.findSecondConnection(this.elements[i].name)].y - this.offsetY(this.elements[this.findSecondConnection(this.elements[i].name)].y, this.elements[this.findFirstConnection(this.elements[i].name)].y, this.elements[this.findSecondConnection(this.elements[i].name)].name);
              this.addAnimation(animationCounter, x1, y1, x2, y2);
            }
          });
        }
      });

      await this.customTimeout(600);

      this.animations.splice(0);

      await this.customTimeout(400);

      if (this.running) {
        await this.run();
      }
    },

    async stop() {
      this.running = false;
      this.animations.splice(0);
      this.netChangeSimulation(this.beforeSimulation);
    },

    customTimeout(ms: number) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },

    async addAnimation(counter: number, x1: number, y1: number, x2: number, y2: number) {
      const animationId = 'animation' + counter;
      const circleId = 'circle' + counter;
      this.animations.push(
        markRaw({
          template: `
          <animateTransform
            id="` + animationId + `"
            begin="indefinite"
            xlink:href="#` + circleId + `"
            attributeName="transform"
            type="translate" from="` + x1 + ',' + y1 + '" to="' + x2 + ',' + y2 + `" dur="0.5s"
            additive="replace" fill="freeze"
          />
        `
        })
      );
      this.animations.push(
        markRaw({
          template: `
            <g id="` + circleId + `">
              <circle r="10" stroke="#000000" fill="#000000" />
            </g>
          `
        })
      );
      await this.customTimeout(1);
      (document.getElementById(animationId) as any).beginElement();
    },

    checkNetAlert() {
      this.resultSimulation.elements = this.elements.slice(1);
      this.resultSimulation.connections = this.connections;
      this.resultSimulation.tokens = this.tokens;
      this.resultSimulation.connectionWeights = this.connection_weight;

      this.checkNet(this.resultSimulation).then((data) => {
        if (data) {
          Swal.fire(
            'Sprawdzenie!',
            'Symulacja sieci zostanie wykonana!',
            'success'
          );
        } else {
          Swal.fire(
            'Sprawdzenie!',
            'Symulacja sieci nie wykona się!',
            'error'
          );
        }
      });
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
      if (!this.running) {
        this.current_target = index;
        if (this.ctrl_pressed) {
          this.addConnection();
          this.current_connection = this.elements[this.elements.length - 1].name;
          this.connection_edit = true;
          (this.$refs.box as any).addEventListener('mousemove', this.connection_drag);
        } else {
          (this.$refs.box as any).addEventListener('mousemove', this.drag);
        }
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

    selectedComponent(index: number) {
      if (this.current_target === index) {
        return '#FF5733';
      } else {
        return '#000000';
      }
    },

    connection_drag(event: any) {
      if (!this.running) {
        try {
          this.elements[this.elements.length - 1].x = event.offsetX - 5;
          this.elements[this.elements.length - 1].y = event.offsetY - 5;
        } catch (error) {
        }
      }
    },

    drag(event: any) {
      if (!this.running) {
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
      }
    },

    endDrag() {
      if (!this.running) {
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
      }
    },

    addPlace(x: number, y: number) {
      this.placeCounter++;
      const place = new joint.shapes.pn.Place({
        position: { x, y },
        attrs: {
          '.label': { text: 'P' + this.placeCounter, 'ref-x': 0.5, 'ref-y': -15 },
          circle: placeSettings.circle
        },
        selected: false,
        tokens: 0
      });

      this.graph.addCell(place);
    },

    addTransition(x: number, y: number) {
      this.transitionCounter++;
      const transition = new joint.shapes.pn.Transition({
        position: { x, y },
        attrs: {
          '.label': { text: 'T' + this.transitionCounter, 'ref-x': 0.5, 'ref-y': -15 },
          rect: transitionSettings.rect
        },
        selected: false
      });
      this.graph.addCell(transition);
    },

    addConnection() {
      if (!this.running) {
        const target = this.current_target;
        this.counter++;
        this.elements.push({ name: 'EA' + this.counter, x: this.elements[target].x, y: this.elements[target].y, x2: this.elements[target].x, y2: this.elements[target].y });
        this.connections.push({ name: 'EA' + this.counter, ft: this.elements[target].name, st: '' });
        this.children.push(Connection);
      }
    },

    addToken() {
      if (!this.running) {
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
      }
    },

    substractToken() {
      if (!this.running) {
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
      }
    },

    addConnectionWeight() {
      if (!this.running) {
        if (this.current_target > 0) {
          if (this.elements[this.current_target].name.substring(1, 2) === 'A') {
            let findConnection = false;
            for (let i = 0; i < this.connection_weight.length; i++) {
              if (this.connection_weight[i].name === this.elements[this.current_target].name) {
                findConnection = true;
                this.connection_weight[i].weight++;
              }
            }

            if (!findConnection) {
              this.counter++;
              const offsetX = Math.round((this.elements[this.current_target].x - this.elements[this.current_target].x2) / 2);
              const offsetY = Math.round((this.elements[this.current_target].y - this.elements[this.current_target].y2) / 2);
              this.elements.push({ name: 'EW' + this.counter, x: this.elements[this.current_target].x2, y: this.elements[this.current_target].y2, x2: 0, y2: 0 });
              this.connection_weight.push({ name: this.elements[this.current_target].name, element: 'EW' + this.counter, weight: 2 });
              this.children.push(WeightText);
            }
          }
        }
      }
    },

    substractConnectionWeight() {
      if (!this.running) {
        if (this.current_target > 0) {
          for (let i = 0; i < this.connection_weight.length; i++) {
            if (this.connection_weight[i].name === this.elements[this.current_target].name) {
              this.connection_weight[i].weight--;
              if (this.connection_weight[i].weight === 1) {
                for (let j = 0; j < this.elements.length; j++) {
                  if (this.elements[j].name === this.connection_weight[i].element) {
                    this.elements.splice(j, 1);
                    this.children.splice(j - 1, 1);
                  }
                }
                this.connection_weight.splice(i, 1);
              }
            }
          }
        }
      }
    },

    checkIfNewToken() {
      this.resultSimulation.changes.forEach((data) => {
        if (data.includes('Added:')) {
          const circle = data.split('Added:')[1];
          let circleIndex = 0;
          this.elements.forEach((data, i) => {
            if (data.name === circle) {
              circleIndex = i;
            }
          });
          if (circleIndex >= 0) {
            let name = '';
            let labelName = '';
            this.resultSimulation.tokens.forEach((data) => {
              if (data.circle === circle) {
                name = data.name;
                labelName = data.label_name;
              }
            });
            if (name !== '' || labelName !== '') {
              this.counter++;
              this.elements.push({ name: name, x: this.elements[circleIndex].x, y: this.elements[circleIndex].y, x2: 0, y2: 0 });
              this.children.push(SmallCircle);
              this.counter++;
              this.elements.push({ name: labelName, x: this.elements[circleIndex].x, y: this.elements[circleIndex].y, x2: 0, y2: 0 });
              this.children.push(TokenText);
            }
          }
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

    findConnection(index: string) {
      for (let i = 0; i < this.connection_weight.length; i++) {
        if (this.connection_weight[i].element === index) {
          for (let j = 0; j < this.elements.length; j++) {
            if (this.connection_weight[i].name === this.elements[j].name) {
              return this.elements[j].name;
            }
          }
        }
      }
    },

    findConnectionText(index: string) {
      for (let i = 0; i < this.connection_weight.length; i++) {
        if (this.connection_weight[i].element === index) {
          return this.connection_weight[i].weight;
        }
      }
    },

    findFirstConnection(index: string) {
      let firstElement = -1;
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
      if (!this.running) {
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
      }
    },

    clear() {
      this.graph.clear();
    },

    exportNet() {
      if (!this.running) {
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
              const connectionWeights = JSON.stringify(this.connection_weight);
              const data = '[' + elements + ',' + connections + ',' + tokens + ',' + connectionWeights + ']';
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
      }
    },

    importNet() {
      if (!this.running) {
        this.selecting = true;

        window.addEventListener('focus', () => {
          this.selecting = false;
        }, { once: true });

        (this.$refs.import as any).click();
      }
    },

    importNetChanged(e: any) {
      if (!this.running) {
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
              for (let i = 0; i < JSON.parse(this.dest)[3].length; i++) {
                this.connection_weight.push(JSON.parse(this.dest)[3][i]);
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
                } else if (objectType === 'W') {
                  this.counter++;
                  this.children.push(WeightText);
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
        for (let i = 0; i < JSON.parse(netExport)[3].length; i++) {
          this.connection_weight.push(JSON.parse(netExport)[3][i]);
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
          } else if (objectType === 'W') {
            this.counter++;
            this.children.push(WeightText);
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

    netChangeSimulation(result: ISimulation) {
      this.clear();
      try {
        for (let i = 0; i < result.connections.length; i++) {
          this.connections.push(result.connections[i]);
        }

        for (let i = 0; i < result.tokens.length; i++) {
          this.tokens.push(result.tokens[i]);
        }
        for (let i = 0; i < result.connectionWeights.length; i++) {
          this.connection_weight.push(result.connectionWeights[i]);
        }
        for (let i = 0; i < result.elements.length; i++) {
          const objectType = result.elements[i].name.substring(1, 2);
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
          } else if (objectType === 'W') {
            this.counter++;
            this.children.push(WeightText);
          } else {
            this.clear();
            break;
          }
          this.elements.push(result.elements[i]);
        }
      } catch (e) {
        this.clear();
      }
    },

    saveModal() {
      if (!this.running) {
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
                this.saveResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + JSON.stringify(this.connection_weight) + ']';
                this.update(history.state.editId);
              } else {
                this.exampleEditResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + JSON.stringify(this.connection_weight) + ']';
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
                  this.saveResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + ',' + JSON.stringify(this.connection_weight) + ']';
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
                  this.exampleNetResult.netExport = '[' + JSON.stringify(this.elements.slice(1)) + ',' + JSON.stringify(this.connections) + ',' + JSON.stringify(this.tokens) + JSON.stringify(this.connection_weight) + ']';
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
      }
    },

    fetchData() {
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
