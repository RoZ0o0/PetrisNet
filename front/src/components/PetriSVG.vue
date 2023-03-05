<!-- eslint-disable vue/no-v-text-v-html-on-component -->
<template>
  <div class="flex w-full h-16 items-center">
    <div class="flex flex-row w-full h-16 items-center justify-center">
      <div v-if='checkIfCreateExample() || checkIfEdited() || checkIfExampleEdited()' class="flex items-center petri-nav w-2/12 select-none ml-8 justify-center">
        <span v-if='checkIfCreateExample()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Tworzenie przykładowej sieci</span>
        <span v-if='checkIfEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie sieci: {{ this.saveResult.saveName }} <br> Użytkownika: {{ this.editedSaveUserEmail }} </span>
        <span v-if='checkIfExampleEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie przykładowej sieci: {{ this.exampleEditResult.netName }}</span>
      </div>
      <div class="flex ml-4 items-center petri-nav">
        <button class="border-2 border-black rounded-xl p-2 items-center" @click='run(); this.running = true;' :disabled='this.running && !this.wait' :style='this.running ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <RunIcon class="inline-block align-middle" :disabled='this.running && !this.wait' />
          <span class="inline-block align-middle select-none" :disabled='this.running && !this.wait'>Start</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" @click='stop()'>
          <StopIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Stop</span>
        </button>
      </div>
      <div class="flex ml-6 items-center petri-nav">
        <button class="border-2 border-black rounded-xl p-2 items-center" v-on:click="switchPlace" :style='this.selectedElement === "place" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <CircleIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Miejsce [1]</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" v-on:click="switchTransition" :style='this.selectedElement === "transition" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <SquareIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Przejście [2]</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" v-on:click="switchFastAdd" :style='this.selectedElement === "fastadd" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <CircleIcon class="inline-block align-middle" v-if='!this.tab_clicked' />
          <SquareIcon class="inline-block align-middle" v-if='this.tab_clicked' />
          <span class="inline-block align-middle select-none">Szybkie dodawanie [3]</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" v-on:click="switchLabel" :style='this.selectedElement === "label" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <TextIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Etykieta [4]</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" v-on:click="switchDelete" :style='this.selectedElement === "delete" ? {"background-color":"#fada8f"} : {"background-color":"#F6C453"}'>
          <RemoveIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Usuń [5]</span>
        </button>
        <button class="border-2 border-black rounded-xl p-2 items-center ml-2" v-on:click="clear" :disabled="this.running">
          <ClearIcon class="inline-block align-middle" />
          <span class="inline-block align-middle select-none">Wyczyść</span>
        </button>
      </div>
    </div>
  </div>
  <div class="mx-8 my-4 border-2 border-black rounded-xl h-4/5 paper-container">
    <div ref="petriEditor" class='rounded-xl petriEditor'></div>
  </div>
  <div class="flex w-full h-16 items-center justify-center">
    <div class="flex items-center petri-nav">
      <button class="border-2 border-black rounded-bl-xl rounded-tr-xl px-2 py-1 items-center" v-on:click="checkNetAlert">
        <CheckNetIcon class="inline-block align-middle" />
        <span class="inline-block align-middle select-none">Sprawdź sieć</span>
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
        <span class="inline-block align-middle select-none">Zapisz</span>
      </button>
      <div class="flex flex-wrap items-center ml-4 border-2 color-F6C453 border-black rounded-bl-xl rounded-tr-xl px-2 py-1">
        <el-dropdown class="border-0">
          <HelpIcon class="inline-block align-middle" />
          <template #dropdown>
            <p class="text-center font-bold px-1 text-base">Aby wybrać jedną z opcji tworzenia sieci, można użyć klawiszy (1, 2, 3, 4).</p>
            <p class="text-center font-bold px-1 text-base">Aby stworzyć połączenie, należy przytrzymać klawisz CTRL i przeciągnąć z elementu.</p>
            <p class="text-center font-bold px-1 text-base">Po wybraniu opcji szybkiego dodawania, pojawia się zasięg w jakim zostanie dodane połączenie.</p>
            <p class="text-center font-bold px-1 text-base">Klawiszem TAB przełącza się pomiędzy stawianiem miejsca bądź przejścia.</p>
            <p class="text-center font-bold px-1 text-base">Aby dodać wielę połączeń, można zaznaczyć wielę miejsc bądź przejść, a następnie z klawiszem CTRL przeciągnać do innego elementu.</p>
            <p class="text-center font-bold px-1 text-base">Aby dodać wiele tokenów bądź wag połączeń, wystarczy zaznaczyć odpowiednie elementy, po czym wcisnąć prawy przycisk myszy i wprowadzić wartość.</p>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { defineComponent, markRaw } from 'vue';
import CircleIcon from 'vue-material-design-icons/CircleOutline.vue';
import SquareIcon from 'vue-material-design-icons/SquareOutline.vue';
import RemoveIcon from 'vue-material-design-icons/Close.vue';
import ClearIcon from 'vue-material-design-icons/CloseCircleOutline.vue';
import RunIcon from 'vue-material-design-icons/Play.vue';
import StopIcon from 'vue-material-design-icons/Stop.vue';
import ImportIcon from 'vue-material-design-icons/ArrowBottomRight.vue';
import ExportIcon from 'vue-material-design-icons/ArrowTopRight.vue';
import SaveIcon from 'vue-material-design-icons/ContentSaveAll.vue';
import HelpIcon from 'vue-material-design-icons/Help.vue';
import CheckNetIcon from 'vue-material-design-icons/CheckNetwork.vue';
import TextIcon from 'vue-material-design-icons/FormatText.vue';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import Swal from 'sweetalert2';
import UserServices, { IUser } from '@/services/UserService';
import axios, { AxiosError } from 'axios';
import LoginServices, { ILogin } from '@/services/LoginService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import SimulationServices, { ISimulation } from '@/services/SimulationService';
import * as joint from 'jointjs';
import _ from 'lodash';

const placeSettings = {
  circle: {
    fill: '#ffffff',
    r: 20,
    stroke: 'black',
    'stroke-width': 1
  }
};

const transitionSettings = {
  rect: {
    fill: '#ffffff',
    width: 20,
    height: 60
  }
};

const svgPath = 'M -100 -100 L 100 -100 L 100 100 L -100 100 L -100 -100 Z';

let currentScale = 1;

const cursorWidth = 100 * currentScale;
const cursorHeight = 100 * currentScale;

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
    TextIcon,
    SaveIcon,
    CheckNetIcon,
    HelpIcon
  },
  data() {
    return {
      paper: null as any,
      graph: null as any,
      contextShow: false,
      selectedElement: '',
      selectRect: null as any,
      rectRangeAdd: null as any,
      placeCounter: 0,
      transitionCounter: 0,
      counterPoints: 0,
      current_connection: null as any,
      current_connections: [] as any,
      current_x: null as any,
      current_y: null as any,
      modelStates: [] as any,
      ctrl_pressed: false,
      shift_pressed: false,
      alt_pressed: false,
      tab_clicked: false,
      altOnce: false,
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
      running: false,
      wait: false
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
        const elements = [] as any;
        const connections = [] as any;
        this.getGraphData(elements, connections);

        this.beforeSimulation.elements = elements;
        this.beforeSimulation.connections = connections;
      }
    },

    running() {
      this.updatePaperInteractivity();
    }
  },

  mounted() {
    const container = this.$refs.petriEditor;

    this.graph = new joint.dia.Graph();

    this.paper = new joint.dia.Paper({
      el: container,
      model: this.graph,
      width: '100%',
      height: '100%',
      gridSize: 5,
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

    let path: any;
    let isDrawing: any;

    let pathData: any;

    (this.paper as any).on('blank:mousewheel', (event: MouseEvent, x: number, y: number, delta: number) => {
      event.preventDefault();
      if (!this.running) {
        const direction = delta > 0 ? 1 : -1;
        const increment = 0.05;
        const zoomLevel = direction * increment;
        const maxZoom = 2;
        const minZoom = 0.2;
        const newScale = currentScale + zoomLevel;
        if (newScale >= minZoom && newScale <= maxZoom) {
          currentScale = newScale;
          this.paper.scale(newScale, newScale);
          if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
            if (this.rectRangeAdd.attr().rect.display !== 'none') {
              this.rectRangeAdd.attr('rect/display', 'none');
            }
          }
        }
      }
    });

    this.updatePaperInteractivity();

    let startX: any, startY: any;
    let moveX: any, moveY: any;

    let currentMousePos: any;

    (this.paper as any).on('cell:pointerdown', (cellView: any, event: any, x: any, y: any) => {
      if (!this.running) {
        if (this.selectedElement === '') {
          if (!this.ctrl_pressed) {
            if (cellView.model.attributes.type !== 'pn.Link') {
              startX = cellView.model.get('position').x;
              startY = cellView.model.get('position').y;
            }

            if (!cellView.model.attributes.selected) {
              if (!this.shift_pressed) {
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

                    element.attr('.marker-vertices/display', 'none');
                    element.attr('.connection/stroke', 'black');
                  } else if (element.attributes.type === 'basic.Text') {
                    element.attr('text/fill', 'black');
                  }
                });
              }
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
                const link = this.graph.getCell(cellView.model.attributes.id);
                link.attr('.marker-vertices', { display: '', fill: 'transparent', stroke: 'black', 'stroke-width': 2 });
                link.attr('.marker-vertex', { circle: { r: 5 } });
                link.attr('.marker-vertex-remove-area', { fill: 'transparent', stroke: 'transparent' });
                cellView.model.attr('.connection/stroke', 'red');
              } else if (cellView.model.attributes.type === 'basic.Text') {
                cellView.model.attr('text/fill', 'red');
              }
            } else {
              if (this.shift_pressed) {
                cellView.model.attributes.selected = false;
                if (cellView.model.attributes.type === 'pn.Place') {
                  cellView.model.attr({
                    circle: {
                      stroke: 'black'
                    }
                  });
                } else if (cellView.model.attributes.type === 'pn.Transition') {
                  cellView.model.attr({
                    rect: {
                      stroke: 'black'
                    }
                  });
                } else if (cellView.model.attributes.type === 'pn.Link') {
                  const link = this.graph.getCell(cellView.model.attributes.id);
                  link.attr('.marker-vertices/display', 'none');
                  link.attr('.connection/stroke', 'black');
                } else if (cellView.model.attributes.type === 'basic.Text') {
                  cellView.model.attr('text/fill', 'black');
                }
              }
            }
          } else {
            if (!cellView.model.attributes.selected) {
              if (cellView.model.attributes.type === 'pn.Place' || cellView.model.attributes.type === 'pn.Transition') {
                const link = new joint.shapes.pn.Link({
                  source: { id: cellView.model.id },
                  target: { x: x, y: y },
                  attrs: {
                    '.marker-target': { display: 'none' },
                    '.marker-source': { display: 'none' },
                    '.link-tools': { display: 'none' },
                    '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                    '.connection-wrap': { display: 'none' },
                    '.marker-vertices': { display: 'none' },
                    '.marker-vertex-remove-area': { fill: 'transparent' },
                    '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                  },
                  connector: {
                    name: 'normal'
                  },
                  weight: 1,
                  selected: false
                });

                this.graph.addCell(link);

                link.appendLabel({
                  attrs: {
                    text: {
                      text: '',
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
                      stroke: 'none'
                    }
                  },
                  position: {
                    distance: -22
                  }
                });

                link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                const lastStep = [] as any;
                lastStep.push(link);
                this.modelStates.push({ action: 'added', lastStep: lastStep });

                this.current_connection = link.id;
              }
            } else {
              if (cellView.model.attributes.type === 'pn.Place' || cellView.model.attributes.type === 'pn.Transition') {
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
                      '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                      '.connection-wrap': { display: 'none' },
                      '.marker-vertices': { display: 'none' },
                      '.marker-vertex-remove-area': { fill: 'transparent' },
                      '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                    },
                    connector: {
                      name: 'normal'
                    },
                    weight: 1,
                    selected: false
                  });

                  this.graph.addCell(link);

                  link.appendLabel({
                    attrs: {
                      text: {
                        text: '',
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
                        stroke: 'none'
                      }
                    },
                    position: {
                      distance: -22
                    }
                  });

                  link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                  link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                  this.current_connection = link.id;

                  const lastStep = [] as any;
                  lastStep.push(link);
                  this.modelStates.push({ action: 'added', lastStep: lastStep });
                } else {
                  const lastStep = [] as any;
                  this.graph.getCells().forEach((element: any) => {
                    if ((element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition') && element.attributes.selected) {
                      const link = new joint.shapes.pn.Link({
                        source: { id: element.id },
                        target: { x: x, y: y },
                        attrs: {
                          '.marker-target': { display: 'none' },
                          '.marker-source': { display: 'none' },
                          '.link-tools': { display: 'none' },
                          '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                          '.connection-wrap': { display: 'none' },
                          '.marker-vertices': { display: 'none' },
                          '.marker-vertex-remove-area': { fill: 'transparent' },
                          '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                        },
                        connector: {
                          name: 'normal'
                        },
                        weight: 1,
                        selected: false
                      });

                      this.graph.addCell(link);

                      link.appendLabel({
                        attrs: {
                          text: {
                            text: '',
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
                            stroke: 'none'
                          }
                        },
                        position: {
                          distance: -22
                        }
                      });

                      link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                      link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                      this.current_connections.push(link.id);
                      lastStep.push(link);
                    }
                  });
                  this.modelStates.push({ action: 'added', lastStep: lastStep });
                }
              }
            }
          }
        }
      }
    });

    (this.paper as any).on('blank:pointerdown', (evt: any, x: number, y: number) => {
      currentMousePos = { x: evt.pageX, y: evt.pageY };
      if (!this.running) {
        if (this.selectedElement === 'place') {
          const place = this.addPlace(x - 30, y - 30);
          const lastStep = [] as any;
          lastStep.push(place);
          this.modelStates.push({ action: 'added', lastStep: lastStep });
        } else if (this.selectedElement === 'transition') {
          const transition = this.addTransition(x, y - 20);
          const lastStep = [] as any;
          lastStep.push(transition);
          this.modelStates.push({ action: 'added', lastStep: lastStep });
        } else if (this.selectedElement === 'fastadd') {
          if (this.tab_clicked) {
            const lastStep = [] as any;

            const rectWidth = 200;
            const rectHeight = 200;
            const rect = {
              x: x - rectWidth / 2,
              y: y - rectHeight / 2,
              width: rectWidth,
              height: rectHeight
            };

            const elementsInArea = this.paper.findViewsInArea(rect);

            this.transitionCounter++;
            const transition = new joint.shapes.pn.Transition({
              position: { x: x, y: y - 20 },
              attrs: {
                '.label': { text: 'T' + this.transitionCounter, 'ref-x': 0.5, 'ref-y': -15 },
                rect: transitionSettings.rect
              },
              selected: false
            });

            this.graph.addCell(transition);

            lastStep.push(transition);

            elementsInArea.forEach((element: any) => {
              if (element.model.attributes.type === 'pn.Place') {
                const link = new joint.shapes.pn.Link({
                  source: { id: element.model.attributes.id },
                  target: { id: transition.id },
                  attrs: {
                    '.marker-target': { display: 'none' },
                    '.marker-source': { display: 'none' },
                    '.link-tools': { display: 'none' },
                    '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                    '.connection-wrap': { display: 'none' },
                    '.marker-vertices': { display: 'none' },
                    '.marker-vertex-remove-area': { fill: 'transparent' },
                    '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                  },
                  connector: {
                    name: 'normal'
                  },
                  weight: 1,
                  selected: false
                });

                this.graph.addCell(link);

                link.appendLabel({
                  attrs: {
                    text: {
                      text: '',
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
                      stroke: 'none'
                    }
                  },
                  position: {
                    distance: -22
                  }
                });

                link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                lastStep.push(link);
              }
            });
            this.modelStates.push({ action: 'added', lastStep: lastStep });
          } else {
            const lastStep = [] as any;

            const rectWidth = 200;
            const rectHeight = 200;
            const rect = {
              x: x - rectWidth / 2,
              y: y - rectHeight / 2,
              width: rectWidth,
              height: rectHeight
            };

            const elementsInArea = this.paper.findViewsInArea(rect);

            this.placeCounter++;
            const place = new joint.shapes.pn.Place({
              position: { x: x - 30, y: y - 30 },
              attrs: {
                '.label': { text: 'P' + this.placeCounter, 'ref-x': 0.5, 'ref-y': -15 },
                circle: placeSettings.circle
              },
              selected: false,
              tokens: 0
            });

            this.graph.addCell(place);

            lastStep.push(place);

            elementsInArea.forEach((element: any) => {
              if (element.model.attributes.type === 'pn.Transition') {
                const link = new joint.shapes.pn.Link({
                  source: { id: element.model.attributes.id },
                  target: { id: place.id },
                  attrs: {
                    '.marker-target': { display: 'none' },
                    '.marker-source': { display: 'none' },
                    '.link-tools': { display: 'none' },
                    '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                    '.connection-wrap': { display: 'none' },
                    '.marker-vertices': { display: 'none' },
                    '.marker-vertex-remove-area': { fill: 'transparent' },
                    '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                  },
                  connector: {
                    name: 'normal'
                  },
                  weight: 1,
                  selected: false
                });

                this.graph.addCell(link);

                link.appendLabel({
                  attrs: {
                    text: {
                      text: '',
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
                      stroke: 'none'
                    }
                  },
                  position: {
                    distance: -22
                  }
                });

                link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                lastStep.push(link);
              }
            });

            this.modelStates.push({ action: 'added', lastStep: lastStep });
          }
        } else if (this.selectedElement === 'label') {
          const label = this.addLabel(x, y);
          const lastStep = [] as any;
          lastStep.push(label);
          this.modelStates.push({ action: 'added', lastStep: lastStep });
        } else if (this.selectedElement === '') {
          if (!this.ctrl_pressed) {
            startPoint = { x, y };

            this.selectRect = new joint.shapes.basic.Rect({
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

            this.graph.addCell(this.selectRect);

            (this.paper as any).on('blank:pointermove', (event: any, x: any, y: any) => {
              endPoint = { x, y };

              (this.selectRect as any).set('size', {
                width: Math.abs(startPoint.x - endPoint.x),
                height: Math.abs(startPoint.y - endPoint.y)
              });

              (this.selectRect as any).set('position', {
                x: Math.min(startPoint.x, endPoint.x),
                y: Math.min(startPoint.y, endPoint.y)
              });
            });
          }
        }
      }
    });

    (this.paper as any).on('cell:pointermove', (cellView: any, event: any, x: any, y: any) => {
      if (!this.running) {
        if (this.selectedElement === '') {
          if (cellView.model.attributes.type !== 'pn.Link') {
            if (!this.ctrl_pressed) {
              if (cellView.model.attributes.selected) {
                moveX = cellView.model.get('position').x - startX;
                moveY = cellView.model.get('position').y - startY;
                this.graph.getCells().forEach((element: any) => {
                  if ((element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition' || element.attributes.type === 'basic.Text') && element.attributes.selected && element.cid !== cellView.model.cid) {
                    const position = element.get('position');
                    element.set('position', { x: position.x + moveX, y: position.y + moveY });
                  }
                });
                this.graph.getCells().forEach((element: any) => {
                  if (element.attributes.type === 'pn.Link') {
                    const link = this.graph.getCell(element.attributes.id);
                    const source = link.getSourceElement();
                    const target = link.getTargetElement();
                    if (source.attributes.selected && target.attributes.selected) {
                      const vertices = [] as any;
                      if (link.vertices().length > 0) {
                        for (let i = 0; i < element.vertices().length; i++) {
                          vertices.push({ x: element.vertices()[i].x + moveX, y: element.vertices()[i].y + moveY });
                        }
                        link.vertices(vertices);
                      }
                    }
                  }
                });
                startX = startX + moveX;
                startY = startY + moveY;
              }
            } else {
              if (this.current_connections.length === 0) {
                const link = this.graph.getCell(this.current_connection);
                link.prop('target', { x: x, y: y });
                let found = false;
                this.graph.getCells().forEach((element: any) => {
                  const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
                  const bbox = element.getBBox();
                  if (point.x >= bbox.x && point.x <= bbox.x + bbox.width && point.y >= bbox.y && point.y <= bbox.y + bbox.height) {
                    if (element.attributes.type !== 'pn.Link') {
                      found = true;
                    }
                  }
                });

                if (!found) {
                  this.current_x = x;
                  this.current_y = y;
                }
              } else {
                this.current_connections.forEach((element: any) => {
                  const link = this.graph.getCell(element);
                  link.prop('target', { x: x, y: y });
                  if (this.current_connections.length === 1) {
                    let found = false;
                    this.graph.getCells().forEach((ele: any) => {
                      const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
                      const bbox = ele.getBBox();
                      if (point.x >= bbox.x && point.x <= bbox.x + bbox.width && point.y >= bbox.y && point.y <= bbox.y + bbox.height) {
                        if (ele.attributes.type !== 'pn.Link') {
                          found = true;
                        }
                      }
                    });

                    if (!found) {
                      this.current_x = x;
                      this.current_y = y;
                    }
                  }
                });
              }
            }
          } else {
            event.preventDefault();
          }
        }
      }
    });

    (this.paper as any).on('blank:pointermove', (event: any, x: any, y: any) => {
      if (this.ctrl_pressed) {
        const diff = {
          x: this.paper.translate().tx + (event.pageX - currentMousePos.x),
          y: this.paper.translate().ty + (event.pageY - currentMousePos.y)
        };

        this.paper.translate(diff.x, diff.y);
        currentMousePos = { x: event.pageX, y: event.pageY };
      }
    });

    (this.paper as any).on('cell:pointerup', (cellView: any, event: any, x: any, y: any) => {
      if (!this.running) {
        if (this.selectedElement === 'delete') {
          const lastStep = [] as any;
          this.graph.getCells().forEach((element: any) => {
            if (cellView.model.id === element.attributes.place) {
              element.remove();
            }
          });
          lastStep.push(cellView.model);
          this.modelStates.push({ action: 'removed', lastStep: lastStep });
          cellView.model.remove();
        }
        if (this.current_connections.length === 0) {
          if (this.current_connection !== null) {
            const paperOffset = (this.paper as any).el.getBoundingClientRect();
            const link = this.graph.getCell(this.current_connection);
            let found = false;
            this.graph.getCells().forEach((element: any) => {
              const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
              const bbox = element.getBBox();
              let newBBox;
              if (element.attributes.type === 'pn.Place') {
                newBBox = {
                  x: parseInt(element.get('position').x) - parseInt(element.attr('circle/r')),
                  y: parseInt(element.get('position').y) - parseInt(element.attr('circle/r')),
                  width: parseInt(element.attr('circle/r')) * 2 + 40,
                  height: parseInt(element.attr('circle/r')) * 2 + 40
                };
              } else if (element.attributes.type === 'pn.Transition') {
                newBBox = {
                  x: parseInt(element.get('position').x),
                  y: parseInt(element.get('position').y),
                  width: parseInt(element.attr('rect/width')),
                  height: parseInt(element.attr('rect/height'))
                };
              } else {
                newBBox = bbox;
              }
              if (point.x >= newBBox.x && point.x <= newBBox.x + newBBox.width && point.y >= newBBox.y && point.y <= newBBox.y + newBBox.height) {
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
                    const linkView = link;
                    link.remove();
                    this.graph.addCell(linkView);
                    linkView.set('connector', { name: 'normal' });
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
          const paperOffset = (this.paper as any).el.getBoundingClientRect();
          this.current_connections.forEach((place: any) => {
            const link = this.graph.getCell(place);
            let found = false;
            this.graph.getCells().forEach((element: any) => {
              const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
              const bbox = element.getBBox();
              let newBBox;
              if (element.attributes.type === 'pn.Place') {
                newBBox = {
                  x: parseInt(element.get('position').x) - parseInt(element.attr('circle/r')),
                  y: parseInt(element.get('position').y) - parseInt(element.attr('circle/r')),
                  width: parseInt(element.attr('circle/r')) * 2 + 40,
                  height: parseInt(element.attr('circle/r')) * 2 + 40
                };
              } else if (element.attributes.type === 'pn.Transition') {
                newBBox = {
                  x: parseInt(element.get('position').x),
                  y: parseInt(element.get('position').y),
                  width: parseInt(element.attr('rect/width')),
                  height: parseInt(element.attr('rect/height'))
                };
              } else {
                newBBox = bbox;
              }
              if (point.x >= newBBox.x && point.x <= newBBox.x + newBBox.width && point.y >= newBBox.y && point.y <= newBBox.y + newBBox.height) {
                if (element instanceof joint.shapes.pn.Place || element instanceof joint.shapes.pn.Transition) {
                  found = true;
                  if (element.id === cellView.model.id) {
                    link.remove();
                  } else if ((element as any).attributes.type === cellView.model.attributes.type) {
                    link.remove();
                  } else {
                    let exs = false;
                    this.graph.getCells().forEach((exist: any) => {
                      if (exist.attributes.type === 'pn.Link') {
                        if (exist.attributes.source.id === link.getSourceElement().attributes.id && exist.attributes.target.id === element.id) {
                          exs = true;
                        }
                      }
                    });
                    if (exs) {
                      link.remove();
                    }
                    link.prop('target', { id: element.id });
                    if (this.current_connections.length === 1) {
                      const linkView = link;
                      link.remove();
                      this.graph.addCell(linkView);
                      linkView.set('connector', { name: 'normal' });
                    }
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

        isDrawing = false;
      }
    });

    (this.paper as any).on('blank:pointerup', (event: any) => {
      if (!this.running) {
        if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
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
              } else if (element.attributes.type === 'pn.Link') {
                element.attr('.marker-vertices/display', 'none');
                element.attr('.connection/stroke', 'black');
              } else if (element.attributes.type === 'basic.Text') {
                element.attr('text/fill', 'black');
              }
            });
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition' || element.attributes.type === 'pn.Link' || element.attributes.type === 'basic.Text') {
                let placeX: any;
                let placeY: any;
                let placeWidth: any;
                let placeHeight: any;

                if (element.attributes.type === 'pn.Place' || element.attributes.type === 'pn.Transition') {
                  placeX = element.position().x;
                  placeY = element.position().y;
                  placeWidth = element.get('size').width;
                  placeHeight = element.get('size').height;
                } else {
                  placeX = element.getBBox().x;
                  placeY = element.getBBox().y;
                  placeWidth = element.getBBox().width;
                  placeHeight = element.getBBox().height;
                }

                const rectX = this.selectRect.position().x;
                const rectY = this.selectRect.position().y;
                const rectWidth = this.selectRect.get('size').width;
                const rectHeight = this.selectRect.get('size').height;

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
                  } else if (element.attributes.type === 'pn.Link') {
                    element.attr('.connection/stroke', 'red');
                  } else if (element.attributes.type === 'basic.Text') {
                    element.attr('text/fill', 'red');
                  }
                }
              }
            });
            this.selectRect.remove();
          }
        }
      }
    });

    (this.paper as any).on('cell:contextmenu', (cellView: any, evt: any) => {
      evt.preventDefault();
      if (!this.running) {
        this.contextShow = true;

        const lastStep = [] as any;

        let counter = 0;
        let counterLink = 0;
        this.graph.getCells().forEach((element: any) => {
          if (element.attributes.type === 'pn.Place' && element.attributes.selected) {
            counter++;
          }
          if (element.attributes.type === 'pn.Link' && element.attributes.selected) {
            counterLink++;
          }
        });

        if (counter > 1 && (cellView as any).model.get('type') === 'pn.Place') {
          let tokenDiv: any;
          let tokenInput: any;
          let textToken: any;

          const editWindow = document.createElement('div');
          if ((cellView as any).model.get('type') === 'pn.Place') {
            tokenDiv = document.createElement('div');
            editWindow.classList.add('editElements');
            editWindow.style.position = 'absolute';
            editWindow.style.left = evt.pageX + 'px';
            editWindow.style.top = evt.pageY + 'px';
            editWindow.style.background = 'white';
            editWindow.style.border = '1px solid black';
            editWindow.style.borderRadius = '0.75rem';
            editWindow.style.padding = '10px';

            tokenInput = document.createElement('input');
            tokenInput.style.border = 'thin solid black';
            tokenInput.style.borderRadius = '0.25rem';
            tokenInput.style.marginLeft = 'auto';
            tokenInput.style.paddingLeft = '2px';
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

            const saveButton = document.createElement('button');
            saveButton.innerHTML = 'Zapisz';
            saveButton.onclick = () => {
              if (tokenInput.value === '') {
                tokenInput.value = '0';
              }

              this.graph.getCells().forEach((element: any) => {
                let tokens: any;
                let r: any;
                if (element.attributes.type === 'pn.Place' && element.attributes.selected) {
                  const name = element.attr('.label/text');
                  tokens = parseInt(element.attributes.tokens);
                  r = element.attr('circle/r');
                  lastStep.push({ element: element, name: name, r: r, tokens: tokens });
                  element.attributes.tokens = parseInt(tokenInput.value);
                  if (parseInt(tokenInput.value) === 0) {
                    this.graph.getCells().forEach((ele: any) => {
                      if (ele.attributes.place === element.attributes.id) {
                        ele.remove();
                      }
                    });
                  }
                  if (parseInt(tokenInput.value) > 0 && tokenInput.value.length <= 4) {
                    this.graph.getCells().forEach((ele: any) => {
                      if (ele.attributes.place === element.attributes.id) {
                        ele.remove();
                      }
                    });

                    const circle = new joint.shapes.basic.Circle({
                      position: { x: element.position().x, y: element.position().y },
                      attrs: {
                        circle: {
                          attrs: {
                            r: 20
                          },
                          fill: 'transparent',
                          stroke: 'transparent',
                          'pointer-events': 'none'
                        },
                        text: {
                          text: element.attributes.tokens.toString(),
                          'font-size': 16,
                          'text-anchor': 'middle',
                          'y-alignment': 'middle',
                          'pointer-events': 'none',
                          fill: 'black'
                        }
                      },
                      'pointer-events': 'none',
                      place: element.attributes.id,
                      locked: true,
                      interactive: false
                    });
                    this.graph.addCell(circle);
                    circle.attr('circle/r', parseInt(element.attributes.attrs.circle.r) / 2);
                    element.on('change:position', () => {
                      circle.position(
                        element.position().x,
                        element.position().y
                      );
                    });
                  }
                }
              });
              this.modelStates.push({ action: 'modified', lastStep: lastStep });
              editWindow.remove();
              this.contextShow = false;
            };

            const cancelButton = document.createElement('button');
            cancelButton.innerHTML = 'Anuluj';
            cancelButton.onclick = () => {
              editWindow.remove();
              this.contextShow = false;
            };

            this.paper.on('blank:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:contextmenu', (cellView: any, evt: any) => {
              editWindow.remove();
            });

            tokenDiv.appendChild(textToken);
            tokenDiv.appendChild(tokenInput);
            editWindow.appendChild(tokenDiv);

            editWindow.appendChild(saveButton);
            editWindow.appendChild(cancelButton);

            (container as any).appendChild(editWindow);
          }
        } else if (counterLink > 1 && (cellView as any).model.get('type') === 'pn.Link') {
          let weightDiv: any;
          let weightInput: any;
          let textWeight: any;

          const editWindow = document.createElement('div');
          if ((cellView as any).model.get('type') === 'pn.Link') {
            weightDiv = document.createElement('div');
            editWindow.classList.add('editElements');
            editWindow.style.position = 'absolute';
            editWindow.style.left = evt.pageX + 'px';
            editWindow.style.top = evt.pageY + 'px';
            editWindow.style.background = 'white';
            editWindow.style.border = '1px solid black';
            editWindow.style.borderRadius = '0.75rem';
            editWindow.style.padding = '10px';

            weightInput = document.createElement('input');
            weightInput.style.border = 'thin solid black';
            weightInput.style.borderRadius = '0.25rem';
            weightInput.style.marginLeft = 'auto';
            weightInput.style.paddingLeft = '2px';
            weightInput.type = 'number';
            weightInput.setAttribute('min', '1');
            weightInput.value = (cellView as any).model.get('tokens');

            weightInput.addEventListener('input', () => {
              if (parseInt(weightInput.value) < 0) {
                weightInput.value = '0';
              }
            });

            textWeight = document.createElement('p');
            textWeight.innerHTML = 'Wagi';

            const saveButton = document.createElement('button');
            saveButton.innerHTML = 'Zapisz';
            saveButton.onclick = () => {
              if (weightInput.value === '') {
                weightInput.value = '0';
              }

              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Link' && element.attributes.selected) {
                  const weight = parseInt(element.attributes.weight);
                  element.attributes.weight = parseInt(weightInput.value);
                  if (parseInt(weightInput.value) > 0 && weightInput.value.length <= 4) {
                    const link = new joint.shapes.pn.Link({
                      source: { id: element.attributes.source.id },
                      target: { id: element.attributes.target.id },
                      attrs: {
                        '.marker-target': { display: 'none' },
                        '.marker-source': { display: 'none' },
                        '.link-tools': { display: 'none' },
                        '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                        '.connection-wrap': { display: 'none' },
                        '.marker-vertices': { display: 'none' },
                        '.marker-vertex-remove-area': { fill: 'transparent' },
                        '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                      },
                      connector: {
                        name: element.attributes.connector.name
                      },
                      weight: parseInt(weightInput.value),
                      selected: false
                    });

                    this.graph.addCell(link);

                    const distance = 18 + (4 * weightInput.value.toString().length);

                    let weight = '';

                    if (weightInput.value > 1) {
                      weight = weightInput.value;
                    }

                    link.appendLabel({
                      attrs: {
                        text: {
                          text: weight,
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
                          stroke: 'none'
                        }
                      },
                      position: {
                        distance: -distance
                      }
                    });

                    if (element.attributes.vertices) {
                      link.vertices(element.attributes.vertices);
                    }

                    link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                    link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                    lastStep.push({ element: link, weight: weight });

                    element.remove();
                  }
                }
              });
              this.modelStates.push({ action: 'modified', lastStep: lastStep });
              editWindow.remove();
              this.contextShow = false;
            };

            const cancelButton = document.createElement('button');
            cancelButton.innerHTML = 'Anuluj';
            cancelButton.onclick = () => {
              editWindow.remove();
              this.contextShow = false;
            };

            this.paper.on('blank:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:contextmenu', (cellView: any, evt: any) => {
              editWindow.remove();
            });

            weightDiv.appendChild(textWeight);
            weightDiv.appendChild(weightInput);
            editWindow.appendChild(weightDiv);

            editWindow.appendChild(saveButton);
            editWindow.appendChild(cancelButton);

            (container as any).appendChild(editWindow);
          }
        } else {
          if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition' || (cellView as any).model.get('type') === 'pn.Link' || (cellView as any).model.get('type') === 'basic.Text') {
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

            let fontNameDiv: any;
            let fontNameInput: any;
            let textFontName: any;

            let fontDiv: any;
            let fontInput: any;
            let textFont: any;

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
            if ((cellView as any).model.get('type') === 'basic.Text') {
              fontDiv = document.createElement('div');
              fontNameDiv = document.createElement('div');
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
              nameInput.style.border = 'thin solid black';
              nameInput.style.borderRadius = '0.25rem';
              nameInput.style.marginLeft = 'auto';
              nameInput.style.paddingLeft = '2px';
              nameInput.type = 'text';
              nameInput.value = (cellView as any).model.get('attrs')['.label'].text;

              textName = document.createElement('p');
              textName.innerHTML = 'Nazwa';
            }

            if ((cellView as any).model.get('type') === 'pn.Place') {
              tokenInput = document.createElement('input');
              tokenInput.style.border = 'thin solid black';
              tokenInput.style.borderRadius = '0.25rem';
              tokenInput.style.marginLeft = 'auto';
              tokenInput.style.paddingLeft = '2px';
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
              sizeInput.style.border = 'thin solid black';
              sizeInput.style.borderRadius = '0.25rem';
              sizeInput.style.marginLeft = 'auto';
              sizeInput.style.paddingLeft = '2px';
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
              widthInput.style.border = 'thin solid black';
              widthInput.style.borderRadius = '0.25rem';
              widthInput.style.marginLeft = 'auto';
              widthInput.style.paddingLeft = '2px';
              widthInput.style.marginLeft = 'auto';
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
              heightInput.style.border = 'thin solid black';
              heightInput.style.borderRadius = '0.25rem';
              heightInput.style.marginLeft = 'auto';
              heightInput.style.paddingLeft = '2px';
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
              weightInput.style.border = 'thin solid black';
              weightInput.style.borderRadius = '0.25rem';
              weightInput.style.marginLeft = 'auto';
              weightInput.style.paddingLeft = '2px';
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

            if ((cellView as any).model.get('type') === 'basic.Text') {
              fontInput = document.createElement('input');
              fontInput.style.border = 'thin solid black';
              fontInput.style.borderRadius = '0.25rem';
              fontInput.style.marginLeft = 'auto';
              fontInput.style.paddingLeft = '2px';
              fontInput.style.marginLeft = 'auto';
              fontInput.type = 'number';
              fontInput.setAttribute('min', '0');
              fontInput.value = (cellView as any).model.attr('text/font-size');

              textFont = document.createElement('p');
              textFont.innerHTML = 'Czcionka';

              fontNameInput = document.createElement('input');
              fontNameInput.style.border = 'thin solid black';
              fontNameInput.style.borderRadius = '0.25rem';
              fontNameInput.style.marginLeft = 'auto';
              fontNameInput.style.paddingLeft = '2px';
              fontNameInput.style.marginLeft = 'auto';
              fontNameInput.type = 'text';
              fontNameInput.value = (cellView as any).model.attr('text/text');

              textFontName = document.createElement('p');
              textFontName.innerHTML = 'Tekst';
            }

            const name = (cellView as any).model.attr('.label/text');
            let width: any;
            let height: any;
            let tokens: any;
            let r: any;
            if ((cellView as any).model.get('type') === 'pn.Transition') {
              width = (cellView as any).model.attr('rect/width');
              height = (cellView as any).model.attr('rect/height');
            }
            if ((cellView as any).model.get('type') === 'pn.Place') {
              tokens = (cellView as any).model.attributes.tokens;
              r = (cellView as any).model.attr('circle/r');
            }

            const saveButton = document.createElement('button');
            saveButton.innerHTML = 'Zapisz';
            saveButton.onclick = () => {
              if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
                if ((cellView as any).model.get('type') === 'pn.Place') {
                  lastStep.push({ element: (cellView as any).model, name: name, r: r, tokens: parseInt(tokens) });
                }
                if ((cellView as any).model.get('type') === 'pn.Transition') {
                  lastStep.push({ element: (cellView as any).model, name: name, width: width, height: height });
                }
                let nameExist = false;
                this.graph.getCells().forEach((element: any) => {
                  if (element.attributes.type !== 'pn.Link' && element.attributes.type !== 'basic.Circle') {
                    if (element.attributes.attrs['.label']) {
                      if ((cellView as any).model.attributes.id !== element.attributes.id && element.attributes.attrs['.label'].text === nameInput.value) {
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
                (cellView as any).model.set('tokens', parseInt(tokenInput.value));
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

                  const circle = new joint.shapes.basic.Circle({
                    position: { x: (cellView as any).model.position().x, y: (cellView as any).model.position().y },
                    attrs: {
                      circle: {
                        attrs: {
                          r: 20
                        },
                        fill: 'transparent',
                        stroke: 'transparent',
                        'pointer-events': 'none'
                      },
                      text: {
                        text: (cellView as any).model.get('tokens').toString(),
                        'font-size': 16,
                        'text-anchor': 'middle',
                        'y-alignment': 'middle',
                        'pointer-events': 'none',
                        fill: 'black'
                      }
                    },
                    'pointer-events': 'none',
                    place: (cellView as any).model.get('id'),
                    locked: true,
                    interactive: false
                  });
                  this.graph.addCell(circle);
                  circle.attr('circle/r', parseInt((cellView as any).model.attributes.attrs.circle.r) / 2);
                  (cellView as any).model.on('change:position', () => {
                    circle.position(
                      (cellView as any).model.position().x,
                      (cellView as any).model.position().y
                    );
                  });
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
                    '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                    '.connection-wrap': { display: 'none' },
                    '.marker-vertices': { display: 'none' },
                    '.marker-vertex-remove-area': { fill: 'transparent' },
                    '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                  },
                  connector: {
                    name: (cellView as any).model.attributes.connector.name
                  },
                  weight: parseInt(weightInput.value),
                  selected: false
                });

                this.graph.addCell(link);

                const distance = 18 + (4 * weightInput.value.toString().length);

                let weight = '';

                if (weightInput.value > 1) {
                  weight = weightInput.value;
                }

                link.appendLabel({
                  attrs: {
                    text: {
                      text: weight,
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
                      stroke: 'none'
                    }
                  },
                  position: {
                    distance: -distance
                  }
                });

                if ((cellView as any).model.attributes.vertices) {
                  link.vertices((cellView as any).model.attributes.vertices);
                }
                link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
                link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });

                lastStep.push({ element: link, weight: (cellView as any).model.attributes.weight });

                (cellView as any).model.remove();
              }
              if ((cellView as any).model.get('type') === 'basic.Text') {
                if (fontInput.value === '') {
                  fontInput.value = 20;
                }
                if (fontInput.value < 10) {
                  fontInput.value = 10;
                }

                if (fontNameInput.value === '') {
                  fontNameInput.value = (cellView as any).model.attr('text/text');
                }

                lastStep.push({ element: (cellView as any).model, font: (cellView as any).model.attr('text/font-size'), name: (cellView as any).model.attr('text/text') });

                (cellView as any).model.attr('text/font-size', fontInput.value);
                (cellView as any).model.attr('text/text', fontNameInput.value);
              }
              editWindow.remove();
              this.contextShow = false;
              this.modelStates.push({ action: 'modified', lastStep: lastStep });
            };

            const cancelButton = document.createElement('button');
            cancelButton.innerHTML = 'Anuluj';
            cancelButton.onclick = () => {
              editWindow.remove();
              this.contextShow = false;
            };

            const bezierButton = document.createElement('button');
            bezierButton.innerHTML = 'Zamień na beziera';
            bezierButton.onclick = () => {
              (cellView as any).model.set('connector', { name: 'smooth' });
              editWindow.remove();
              this.contextShow = false;
            };

            const standardLinkButton = document.createElement('button');
            standardLinkButton.innerHTML = 'Zamień na normalny';
            standardLinkButton.onclick = () => {
              (cellView as any).model.set('connector', { name: 'normal' });
              editWindow.remove();
              this.contextShow = false;
            };

            this.paper.on('blank:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:pointerdown', (evt: any) => {
              editWindow.remove();
              this.contextShow = false;
            });

            this.paper.on('cell:contextmenu', (cellView: any, evt: any) => {
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
              if ((cellView as any).model.get('connector').name === 'smooth') {
                editWindow.appendChild(standardLinkButton);
              } else {
                editWindow.appendChild(bezierButton);
              }
            }

            if ((cellView as any).model.get('type') === 'basic.Text') {
              fontDiv.appendChild(textFont);
              fontDiv.appendChild(fontInput);
              fontNameDiv.appendChild(textFontName);
              fontNameDiv.appendChild(fontNameInput);
              editWindow.appendChild(fontNameDiv);
              editWindow.appendChild(fontDiv);
            }

            editWindow.appendChild(saveButton);
            editWindow.appendChild(cancelButton);

            (container as any).appendChild(editWindow);
          }
        }
      }
    });

    window.addEventListener('keydown', (evt: KeyboardEvent) => {
      if (!this.contextShow) {
        if (evt.key === '1') {
          if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
            this.selectRect.remove();
          }
          if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
            this.rectRangeAdd.remove();
          }
          this.switchPlace();
        }
        if (evt.key === '2') {
          if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
            this.selectRect.remove();
          }
          if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
            this.rectRangeAdd.remove();
          }
          this.switchTransition();
        }
        if (evt.key === '3') {
          if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
            this.selectRect.remove();
          }

          this.switchFastAdd();
        }
        if (evt.key === '4') {
          if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
            this.selectRect.remove();
          }
          if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
            this.rectRangeAdd.remove();
          }
          this.switchLabel();
        }
        if (evt.key === '5') {
          if (this.selectRect && this.graph.getCell(this.selectRect.attributes.id)) {
            this.selectRect.remove();
          }
          if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
            this.rectRangeAdd.remove();
          }
          this.switchDelete();
        }
        if (evt.key === 'Delete') {
          const lastStep = [] as any;
          this.graph.getCells().forEach((element: any) => {
            if (typeof element !== 'undefined') {
              if (element.attributes.selected) {
                this.graph.getCells().forEach((text: any) => {
                  if (element.id === text.attributes.place) {
                    text.remove();
                  }
                });
                lastStep.push(element);
                element.remove();
              }
            }
          });
          this.modelStates.push({ action: 'removed', lastStep: lastStep });
        }
        if (evt.key === 'z' && evt.ctrlKey) {
          if (!this.running) {
            const modelState = this.modelStates.pop();
            if (modelState) {
              if (modelState.action === 'added') {
                modelState.lastStep.forEach((element: any) => {
                  if (element.attributes.type === 'pn.Link') {
                    this.graph.getCells().forEach((ele: any) => {
                      if (ele.attributes.type === 'pn.Link') {
                        if (ele.attributes.source.id === element.attributes.source.id && ele.attributes.target.id === element.attributes.target.id) {
                          ele.remove();
                        }
                      }
                    });
                  } else {
                    if (element.attributes.type === 'pn.Place') {
                      this.placeCounter--;
                    }
                    if (element.attributes.type === 'pn.Transition') {
                      this.transitionCounter--;
                    }
                    element.remove();
                  }
                });
              }
              if (modelState.action === 'removed') {
                modelState.lastStep.forEach((element: any) => {
                  this.graph.addCell(element);
                });
                this.graph.getCells().forEach((element: any) => {
                  if (element.attributes.type === 'pn.Place') {
                    if (element.attributes.tokens > 0) {
                      const circle = new joint.shapes.basic.Circle({
                        position: { x: element.position().x, y: element.position().y },
                        attrs: {
                          circle: {
                            attrs: {
                              r: 20
                            },
                            fill: 'transparent',
                            stroke: 'transparent',
                            'pointer-events': 'none'
                          },
                          text: {
                            text: element.attributes.tokens,
                            'font-size': 16,
                            'text-anchor': 'middle',
                            'y-alignment': 'middle',
                            'pointer-events': 'none',
                            fill: 'black'
                          }
                        },
                        'pointer-events': 'none',
                        place: element.attributes.id,
                        locked: true,
                        interactive: false
                      });
                      this.graph.addCell(circle);
                      circle.attr('circle/r', parseInt(element.attributes.attrs.circle.r) / 2);
                      element.on('change:position', () => {
                        circle.position(
                          element.position().x,
                          element.position().y
                        );
                      });
                    }
                  }
                });
              }
              if (modelState.action === 'modified') {
                modelState.lastStep.forEach((element: any) => {
                  if (element.element.attributes.type === 'pn.Link') {
                    const link = this.graph.getCell(element.element.attributes.id);

                    link.attributes.weight = element.weight.toString();
                    link.attr('text/text', element.weight.toString());
                  }
                  if (element.element.attributes.type === 'basic.Text') {
                    const label = this.graph.getCell(element.element.attributes.id);

                    label.attr('text/font-size', element.font);
                    label.attr('text/text', element.name);
                  }
                  if (element.element.attributes.type === 'pn.Place') {
                    const place = this.graph.getCell(element.element.attributes.id);
                    place.attr('.label/text', element.name);
                    place.attributes.tokens = element.tokens;
                    place.attr('circle/r', element.r);
                    let found = false;
                    this.graph.getCells().forEach((ele: any) => {
                      if (place.attributes.id === ele.attributes.place) {
                        found = true;
                        if (place.attributes.tokens > 0) {
                          ele.attr('text/text', place.attributes.tokens);
                          ele.attr('circle/r', place.attr('circle/r') / 2);
                        } else {
                          ele.remove();
                        }
                      }
                    });

                    if (!found) {
                      if (place.attributes.tokens > 0) {
                        const circle = new joint.shapes.basic.Circle({
                          position: { x: place.position().x, y: place.position().y },
                          attrs: {
                            circle: {
                              attrs: {
                                r: 20
                              },
                              fill: 'transparent',
                              stroke: 'transparent',
                              'pointer-events': 'none'
                            },
                            text: {
                              text: place.attributes.tokens,
                              'font-size': 16,
                              'text-anchor': 'middle',
                              'y-alignment': 'middle',
                              'pointer-events': 'none',
                              fill: 'black'
                            }
                          },
                          'pointer-events': 'none',
                          place: place.attributes.id,
                          locked: true,
                          interactive: false
                        });
                        this.graph.addCell(circle);
                        circle.attr('circle/r', parseInt(place.attributes.attrs.circle.r) / 2);
                        place.on('change:position', () => {
                          circle.position(
                            place.position().x,
                            place.position().y
                          );
                        });
                      }
                    }
                  }
                  if (element.element.attributes.type === 'pn.Transition') {
                    const transition = this.graph.getCell(element.element.attributes.id);
                    transition.attr('.label/text', element.name);
                    transition.attr('rect/width', element.width);
                    transition.attr('rect/height', element.height);
                  }
                });
              }

              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'basic.Circle') {
                  if (!this.graph.getCell(element.attributes.place)) {
                    element.remove();
                  }
                }
              });
            }
          }
        }
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Control') {
        this.paper.setInteractivity(false);
        this.ctrl_pressed = true;
      }
    });

    document.addEventListener('keyup', (event) => {
      if (event.key === 'Control') {
        if (this.current_connection !== null) {
          const link = this.graph.getCell(this.current_connection);
          link.remove();
          this.current_connection = null;
        }
        if (this.current_connections.length > 0) {
          this.current_connections.forEach((element: any) => {
            const link = this.graph.getCell(element);
            link.remove();
            this.current_connections.pop(element);
          });
        }
        this.paper.setInteractivity(true);
        this.ctrl_pressed = false;
        this.alt_pressed = false;
        this.altOnce = false;
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Shift') {
        this.paper.setInteractivity(false);
        this.shift_pressed = true;
      }
    });

    document.addEventListener('keyup', (event) => {
      if (event.key === 'Shift') {
        this.paper.setInteractivity(true);
        this.shift_pressed = false;
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Alt') {
        event.preventDefault();
        this.alt_pressed = true;
        if (!this.altOnce) {
          if (this.current_connection !== null || this.current_connections.length === 1) {
            if (this.current_x !== null && this.current_y !== null) {
              let link: any;
              if (this.current_connection !== null) {
                link = this.graph.getCell(this.current_connection);
              } else if (this.current_connections.length === 1) {
                link = this.graph.getCell(this.current_connections[0]);
              }
              let vertices = [] as any;
              if (link.vertices() <= 0) {
                const points = {
                  x: this.current_x,
                  y: this.current_y
                };
                vertices.push(points);
              } else {
                vertices = link.get('vertices');
                const points = {
                  x: this.current_x,
                  y: this.current_y
                };
                vertices.push(points);
              }
              link.vertices(vertices);
            }
          }
          this.altOnce = true;
          this.current_x = null;
          this.current_y = null;
        }
      }
    });

    document.addEventListener('keyup', (event) => {
      if (event.key === 'Alt') {
        this.alt_pressed = false;
        this.altOnce = false;
      }
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Tab') {
        event.preventDefault();
        if (this.selectedElement === 'fastadd') {
          if (this.tab_clicked) {
            this.tab_clicked = false;
          } else {
            this.tab_clicked = true;
          }
        }
      }
    });

    document.addEventListener('mousemove', (event) => {
      if (this.selectedElement === 'fastadd') {
        const mouseX = event.clientX;
        const mouseY = event.clientY;

        const paperPosition = this.paper.clientToLocalPoint({ x: mouseX, y: mouseY });

        this.rectRangeAdd.position(paperPosition.x, paperPosition.y);

        this.rectRangeAdd.translate(-100, -100);

        if (this.rectRangeAdd.attr().rect.display) {
          if (this.rectRangeAdd.attr().rect.display === 'none') {
            this.rectRangeAdd.attr('rect/display', '');
          }
        }
      }
    });

    if (history.state.redirectExport) {
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

    async checkNet(result: ISimulation): Promise<ISimulation> {
      return await SimulationServices.checkNet(result);
    },

    async simulation(result: ISimulation): Promise<ISimulation> {
      return await SimulationServices.simulation(result);
    },

    async run() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      this.selectedElement = '';
      try {
        this.running = true;

        while (this.running) {
          const elements = [] as any;
          const connections = [] as any;

          await this.simulationCounter++;

          this.getGraphData(elements, connections);
          this.resultSimulation.elements = elements;
          this.resultSimulation.connections = connections;

          await this.simulation(this.resultSimulation).then((data) => (this.resultSimulation = data));

          await this.netChangeSimulation(this.resultSimulation);

          let sourcePosition: any;
          let targetPosition: any;

          let sourceElement: any;
          let targetElement: any;

          this.resultSimulation.changes.forEach((ele: any) => {
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.type === 'pn.Link') {
                if (element.attributes.source.id === ele.split(' ')[0] &&
                    element.attributes.target.id === ele.split(' ')[1]) {
                  if (this.running) {
                    sourcePosition = element.getSourcePoint();
                    targetPosition = element.getTargetPoint();

                    sourceElement = element.getSourceElement();
                    targetElement = element.getTargetElement();

                    this.graph.getCells().forEach((cells: any) => {
                      if (cells.attributes.type === 'pn.Transition') {
                        cells.attr('rect/stroke', 'black');
                      }
                    });

                    if (sourceElement.attributes.type === 'pn.Transition') {
                      sourceElement.attr('rect/stroke', 'red');
                    }

                    if (sourceElement.attributes.type === 'pn.Transition') {
                      targetElement.attr('rect/stroke', 'red');
                    }

                    const connector = joint.connectors.normal;

                    let pathData: any;

                    if (element.get('vertices')) {
                      pathData = connector(sourcePosition, targetPosition, element.get('vertices'));
                    } else {
                      pathData = 'M ' + sourcePosition.x + ' ' + sourcePosition.y + ' L ' + targetPosition.x + ' ' + targetPosition.y;
                    }

                    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
                    path.setAttribute('d', pathData.toString());
                    const pathLength = path.getTotalLength();

                    const circle = new joint.shapes.basic.Circle({
                      position: { x: element.getSourcePoint().x, y: element.getSourcePoint().y - 5 },
                      size: { width: 20, height: 20 },
                      attrs: { circle: { fill: 'black' } },
                      simulation: true
                    });
                    this.graph.addCell(circle);

                    let distance = 0;
                    const step = (path.getTotalLength() / 60);

                    function animate() {
                      distance += step;

                      const point = path.getPointAtLength(distance);
                      const position = {
                        x: point.x - (circle as any).attributes.size.width / 2,
                        y: point.y - ((circle as any).attributes.size.height / 2) - 5
                      };

                      if (distance >= pathLength) {
                        circle.position(targetPosition.x - (circle as any).attributes.size.width / 3, targetPosition.y - (circle as any).attributes.size.height / 3);
                        circle.remove();
                        return;
                      }

                      circle.position(position.x, position.y);

                      requestAnimationFrame(animate);
                    }
                    animate();
                  }
                }
              }
            });
          });

          await this.customTimeout(1000);
        }
      } catch (e) {
      }
    },

    async stop() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      this.selectedElement = '';
      this.running = false;
      this.beforeSimulation.elements.forEach((element: any) => {
        if (element.type === 'pn.Place') {
          this.graph.getCells().forEach((ele: any) => {
            if (element.id === ele.attributes.id) {
              ele.attributes.tokens = element.tokens;
              if (parseInt(ele.attributes.tokens) === 0) {
                this.graph.getCells().forEach((circles: any) => {
                  if (circles.attributes.place === ele.attributes.id) {
                    circles.remove();
                  }
                });
              } else if (parseInt(ele.attributes.tokens) > 0) {
                let found = false;
                this.graph.getCells().forEach((circles: any) => {
                  if (circles.attributes.place === ele.attributes.id) {
                    found = true;
                    circles.attr('text/text', ele.attributes.tokens);
                  }
                });
                if (!found) {
                  const circle = new joint.shapes.basic.Circle({
                    position: { x: ele.position().x, y: ele.position().y },
                    attrs: {
                      circle: {
                        attrs: {
                          r: 20
                        },
                        fill: 'transparent',
                        stroke: 'transparent',
                        'pointer-events': 'none'
                      },
                      text: {
                        text: ele.attributes.tokens,
                        'font-size': 16,
                        'text-anchor': 'middle',
                        'y-alignment': 'middle',
                        'pointer-events': 'none',
                        fill: 'black'
                      }
                    },
                    'pointer-events': 'none',
                    place: ele.attributes.id,
                    locked: true,
                    interactive: false
                  });
                  this.graph.addCell(circle);
                  circle.attr('circle/r', parseInt(ele.attributes.attrs.circle.r) / 2);
                  ele.on('change:position', () => {
                    circle.position(
                      ele.position().x,
                      ele.position().y
                    );
                  });
                }
              }
            }
          });
        }
      });
      this.simulationCounter = 0;

      this.graph.getCells().forEach((element: any) => {
        if (element.attributes.type === 'basic.Circle' && element.attributes.simulation) {
          element.remove();
        }
      });
      this.graph.getCells().forEach((cells: any) => {
        if (cells.attributes.type === 'pn.Transition') {
          cells.attr('rect/stroke', 'black');
        }
      });
    },

    async customTimeout(ms: number) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },

    getGraphData(elements = [] as any, connections = [] as any) {
      this.graph.getCells().forEach((element: any) => {
        let weight = 0;
        let tokens = 0;
        let name = '';
        let x = 0;
        let y = 0;
        let width = 0;
        let height = 0;
        let r = 0;
        let source = '';
        let target = '';
        let vertices = [];
        let connector = '';
        if (element.attributes.type === 'pn.Link') {
          weight = parseInt(element.attributes.weight);
          source = element.attributes.source.id;
          target = element.attributes.target.id;
          connector = element.attributes.connector.name;
          if (element.attributes.vertices) {
            vertices = element.attributes.vertices;
          }
        }
        if (element.attributes.type === 'pn.Place') {
          tokens = parseInt(element.attributes.tokens);
          name = element.attributes.attrs['.label'].text;
          x = element.attributes.position.x;
          y = element.attributes.position.y;
          r = parseInt(element.attributes.attrs.circle.r);
        }
        if (element.attributes.type === 'pn.Transition') {
          name = element.attributes.attrs['.label'].text;
          x = element.attributes.position.x;
          y = element.attributes.position.y;
          width = parseInt(element.attributes.attrs.rect.width);
          height = parseInt(element.attributes.attrs.rect.height);
        }
        if (element.attributes.type === 'basic.Text') {
          name = element.attr('text/text');
          r = element.attr('text/font-size');
          x = element.attributes.position.x;
          y = element.attributes.position.y;
        }
        if (element.attributes.type !== 'basic.Circle') {
          if (element.attributes.type !== 'pn.Link') {
            elements.push({ name: name, type: element.attributes.type, x: x, y: y, tokens: tokens, id: element.attributes.id, r: r, width: width, height: height });
          } else {
            connections.push({ source: source, target: target, weight: weight, vertices: vertices, connector: connector });
          }
        }
      });
    },

    checkNetAlert() {
      const elements = [] as any;
      const connections = [] as any;

      this.getGraphData(elements, connections);
      this.resultSimulation.elements = elements;
      this.resultSimulation.connections = connections;

      this.checkNet(this.resultSimulation).then((data) => {
        if (data.changes.length === 0) {
          Swal.fire(
            'Sukces!',
            'Model jest poprawny!',
            'success'
          );
        } else {
          Swal.fire(
            'Błąd!',
            data.changes[0],
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

      return place;
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

      return transition;
    },

    addLabel(x: number, y: number) {
      const label = new joint.shapes.basic.Text({
        position: { x: x, y: y },
        size: { width: 50, height: 20 },
        attrs: {
          text: {
            text: 'Etykieta',
            'font-size': 14,
            'font-weight': 'bold',
            'text-anchor': 'middle',
            'ref-x': 0.5,
            'ref-y': 0.5,
            'y-alignment': 'middle',
            'x-alignment': 'middle'
          }
        }
      });

      this.graph.addCell(label);

      return label;
    },

    clear() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      this.selectedElement = '';
      this.graph.clear();
      this.placeCounter = 0;
      this.transitionCounter = 0;
    },

    exportNet() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      this.selectedElement = '';
      if (!this.running) {
        if (this.graph.getCells().length > 0) {
          Swal.fire({
            title: 'Wybierz format',
            showDenyButton: true,
            showCancelButton: true,
            confirmButtonText: 'Standardowy',
            denyButtonText: 'Tina',
            denyButtonColor: '#7066e0',
            cancelButtonText: 'PIPE',
            cancelButtonColor: '#7066e0'
          }).then((result) => {
            if (result.isConfirmed) {
              const elements = [] as any;
              const connections = [] as any;
              this.getGraphData(elements, connections);
              const data = '[' + JSON.stringify(elements) + ',' + JSON.stringify(connections) + ']';
              const blob = new Blob([data], { type: 'text/plain' });
              const anchor = document.createElement('a');
              anchor.download = 'PetriNet_import.txt';
              anchor.href = window.URL.createObjectURL(blob);
              anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');
              anchor.click();
            } else if (result.isDenied) {
              let data = '';
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Place') {
                  data = data + 'p ' + (element.attributes.position.x + 30) + '.0 ' + element.attributes.position.y + '.0 ' + element.attributes.attrs['.label'].text + ' ' + element.attributes.tokens;

                  data = data + ' n\n';
                }

                if (element.attributes.type === 'pn.Transition') {
                  data = data + 't ' + element.attributes.position.x + '.0 ' + element.attributes.position.y + '.0 ' + element.attributes.attrs['.label'].text + ' 0 w n\n';
                }
              });

              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Link') {
                  let source: any;
                  let target: any;
                  this.graph.getCells().forEach((elementName: any) => {
                    if (element.attributes.source.id === elementName.attributes.id) {
                      source = elementName.attributes.attrs['.label'].text;
                    }
                    if (element.attributes.target.id === elementName.attributes.id) {
                      target = elementName.attributes.attrs['.label'].text;
                    }
                  });
                  data = data + 'e ' + source + ' ' + target + ' ' + element.attributes.weight + ' n\n';
                }
              });

              data = data + 'h PetriNetExport';

              const blob = new Blob([data], { type: 'text/plain' });
              const anchor = document.createElement('a');
              anchor.download = 'PetriNetExport.ndr';
              anchor.href = window.URL.createObjectURL(blob);
              anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');
              anchor.click();
            } else if (result.dismiss === Swal.DismissReason.cancel) {
              let data = '<?xml version="1.0" encoding="ISO-8859-1"?><pnml>\n';
              data = data + '\t<net id="Net-One" type="P/T net">\n';
              data = data + '\t\t<token id="Default" enabled="true" red="0" green="0" blue="0"/>\n';
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Place') {
                  data = data + '\t\t<place id="' + element.attributes.attrs['.label'].text + '">\n';

                  data = data + '\t\t\t<graphics>\n';
                  data = data + '\t\t\t\t<position x="' + (element.attributes.position.x + 30) + '.0" y="' + element.attributes.position.y + '.0"/>\n';
                  data = data + '\t\t\t</graphics>\n';

                  data = data + '\t\t\t<name>\n';
                  data = data + '\t\t\t\t<value>' + element.attributes.attrs['.label'].text + '</value>\n';
                  data = data + '\t\t\t\t<graphics>\n';
                  data = data + '\t\t\t\t\t<offset x="0.0" y="0.0"/>\n';
                  data = data + '\t\t\t\t</graphics>\n';
                  data = data + '\t\t\t</name>\n';

                  data = data + '\t\t\t<initialMarking>\n';
                  data = data + '\t\t\t\t<value>Default,' + element.attributes.tokens + '</value>\n';
                  data = data + '\t\t\t\t<graphics>\n';
                  data = data + '\t\t\t\t\t<offset x="0.0" y="0.0"/>\n';
                  data = data + '\t\t\t\t</graphics>\n';
                  data = data + '\t\t\t</initialMarking>\n';

                  data = data + '\t\t\t<capacity>\n';
                  data = data + '\t\t\t\t<value>0</value>\n';
                  data = data + '\t\t\t</capacity>\n';

                  data = data + '\t\t</place>\n';
                }
              });
              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Transition') {
                  data = data + '\t\t<transition id="' + element.attributes.attrs['.label'].text + '">\n';

                  data = data + '\t\t\t<graphics>\n';
                  data = data + '\t\t\t\t<position x="' + element.attributes.position.x + '.0" y="' + element.attributes.position.y + '.0"/>\n';
                  data = data + '\t\t\t</graphics>\n';

                  data = data + '\t\t\t<name>\n';
                  data = data + '\t\t\t\t<value>' + element.attributes.attrs['.label'].text + '</value>\n';
                  data = data + '\t\t\t\t<graphics>\n';
                  data = data + '\t\t\t\t\t<offset x="-5.0" y="35.0"/>\n';
                  data = data + '\t\t\t\t</graphics>\n';
                  data = data + '\t\t\t</name>\n';

                  data = data + '\t\t\t<orientation>\n';
                  data = data + '\t\t\t\t<value>0</value>\n';
                  data = data + '\t\t\t</orientation>\n';

                  data = data + '\t\t\t<rate>\n';
                  data = data + '\t\t\t\t<value>1.0</value>\n';
                  data = data + '\t\t\t</rate>\n';

                  data = data + '\t\t\t<timed>\n';
                  data = data + '\t\t\t\t<value>false</value>\n';
                  data = data + '\t\t\t</timed>\n';

                  data = data + '\t\t\t<infiniteServer>\n';
                  data = data + '\t\t\t\t<value>false</value>\n';
                  data = data + '\t\t\t</infiniteServer>\n';

                  data = data + '\t\t\t<priority>\n';
                  data = data + '\t\t\t\t<value>1</value>\n';
                  data = data + '\t\t\t</priority>\n';

                  data = data + '\t\t</transition>\n';
                }
              });

              this.graph.getCells().forEach((element: any) => {
                if (element.attributes.type === 'pn.Link') {
                  data = data + '\t\t<arc id="' + element.getSourceElement().attributes.attrs['.label'].text + ' to ' + element.getTargetElement().attributes.attrs['.label'].text + '" ';
                  data = data + 'source="' + element.getSourceElement().attributes.attrs['.label'].text + '" target="' + element.getTargetElement().attributes.attrs['.label'].text + '">\n';

                  data = data + '\t\t\t<graphics/>\n';

                  data = data + '\t\t\t<inscription>\n';
                  data = data + '\t\t\t\t<value>Default,' + element.attributes.weight + '</value>\n';
                  data = data + '\t\t\t\t<graphics/>\n';
                  data = data + '\t\t\t</inscription>\n';

                  data = data + '\t\t\t<tagged>\n';
                  data = data + '\t\t\t\t<value>false</value>\n';
                  data = data + '\t\t\t</tagged>\n';

                  if (element.attributes.connector.name === 'smooth') {
                    let counter = 0;
                    this.graph.getCell(element.attributes.id).get('vertices').forEach((vertices: any) => {
                      let arcPathId = '';
                      for (let i = 0; i < (3 - counter.toString().length); i++) {
                        arcPathId += '0';
                      }
                      arcPathId += counter.toString();
                      data = data + '\t\t\t<arcpath id="' + arcPathId + '" x="' + vertices.x + '" y="' + vertices.y + '" curvePoint="true">\n';
                      counter++;
                    });
                  } else {
                    let counter = 0;
                    this.graph.getCell(element.attributes.id).get('vertices').forEach((vertices: any) => {
                      let arcPathId = '';
                      for (let i = 0; i < (3 - counter.toString().length); i++) {
                        arcPathId += '0';
                      }
                      arcPathId += counter.toString();
                      data = data + '\t\t\t<arcpath id="' + arcPathId + '" x="' + vertices.x + '" y="' + vertices.y + '" curvePoint="false">\n';
                      counter++;
                    });
                  }

                  data = data + '\t\t\t<type value="normal"/>\n';

                  data = data + '\t\t</arc>\n';
                }
              });

              data = data + '\t</net>\n';
              data = data + '</pnml>\n';

              const blob = new Blob([data], { type: 'text/plain' });
              const anchor = document.createElement('a');
              anchor.download = 'PetriNetExport.xml';
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
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      this.selectedElement = '';
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
            this.graph.clear();
            JSON.parse(this.dest)[0].forEach((element: any) => {
              if (element.type === 'pn.Place') {
                this.placeCounter++;
                const place = new joint.shapes.pn.Place({
                  position: { x: element.x, y: element.y },
                  id: element.id,
                  attrs: {
                    '.label': { text: element.name, 'ref-x': 0.5, 'ref-y': -15 },
                    circle: placeSettings.circle
                  },
                  selected: false,
                  tokens: element.tokens
                });

                this.graph.addCell(place);

                let r = element.r;

                if (r < 10) {
                  r = 10;
                }

                place.attr('circle/r', parseInt(r));

                if (element.tokens > 0) {
                  const circle = new joint.shapes.basic.Circle({
                    position: { x: element.x, y: element.y },
                    attrs: {
                      circle: {
                        attrs: {
                          r: 20
                        },
                        fill: 'transparent',
                        stroke: 'transparent',
                        'pointer-events': 'none'
                      },
                      text: {
                        text: element.tokens,
                        'font-size': 16,
                        'text-anchor': 'middle',
                        'y-alignment': 'middle',
                        'pointer-events': 'none',
                        fill: 'black'
                      }
                    },
                    'pointer-events': 'none',
                    place: element.id,
                    locked: true,
                    interactive: false
                  });
                  this.graph.addCell(circle);
                  circle.attr('circle/r', parseInt((place as any).attributes.attrs.circle.r) / 2);
                  (place as any).on('change:position', () => {
                    circle.position(
                      place.position().x,
                      place.position().y
                    );
                  });
                }
              }
              if (element.type === 'pn.Transition') {
                this.transitionCounter++;
                const transition = new joint.shapes.pn.Transition({
                  position: { x: element.x, y: element.y },
                  id: element.id,
                  attrs: {
                    '.label': { text: element.name, 'ref-x': 0.5, 'ref-y': -15 },
                    rect: transitionSettings.rect
                  },
                  selected: false
                });
                this.graph.addCell(transition);

                let width = element.width;
                let height = element.height;

                if (width < 20) {
                  width = 20;
                }

                if (height < 20) {
                  height = 20;
                }

                transition.attr('rect/width', parseInt(width));
                transition.attr('rect/height', parseInt(height));
              }
              if (element.type === 'basic.Text') {
                const label = this.addLabel(element.x, element.y);

                label.attr('text/text', element.name);
                label.attr('text/font-size', element.r);
              }
            });
            JSON.parse(this.dest)[1].forEach((element: any) => {
              const link = new joint.shapes.pn.Link({
                source: { id: element.source },
                target: { id: element.target },
                attrs: {
                  '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.link-tools': { display: 'none' },
                  '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
                  '.connection-wrap': { display: 'none' },
                  '.marker-vertices': { display: 'none' },
                  '.marker-vertex-remove-area': { fill: 'transparent' },
                  '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
                },
                connector: {
                  name: element.connector
                },
                weight: element.weight,
                selected: false
              });
              this.graph.addCell(link);

              const distance = 18 + (4 * element.weight.toString().length);

              let weight = '';

              if (element.weight > 1) {
                weight = element.weight.toString();
              }

              link.appendLabel({
                attrs: {
                  text: {
                    text: weight,
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
                    stroke: 'none'
                  }
                },
                position: {
                  distance: -distance
                }
              });

              if (element.vertices) {
                link.vertices(element.vertices);
              }

              link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
              link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
            });
          };
          if (this.selectedFile != null) {
            reader.readAsText(this.selectedFile);
          }
        } else {
          Swal.fire(
            'Błąd!',
            'Zły plik!',
            'error'
          );
        }
      }
    },

    redirectNetExport(netExport: string) {
      try {
        JSON.parse(netExport)[0].forEach((element: any) => {
          if (element.type === 'pn.Place') {
            this.placeCounter++;
            const place = new joint.shapes.pn.Place({
              position: { x: element.x, y: element.y },
              id: element.id,
              attrs: {
                '.label': { text: element.name, 'ref-x': 0.5, 'ref-y': -15 },
                circle: placeSettings.circle
              },
              selected: false,
              tokens: element.tokens
            });

            this.graph.addCell(place);

            let r = element.r;

            if (r < 10) {
              r = 10;
            }

            place.attr('circle/r', parseInt(r));

            if (element.tokens > 0) {
              const circle = new joint.shapes.basic.Circle({
                position: { x: element.x, y: element.y },
                attrs: {
                  circle: {
                    attrs: {
                      r: 20
                    },
                    fill: 'transparent',
                    stroke: 'transparent',
                    'pointer-events': 'none'
                  },
                  text: {
                    text: element.tokens,
                    'font-size': 16,
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'pointer-events': 'none',
                    fill: 'black'
                  }
                },
                'pointer-events': 'none',
                place: element.id,
                locked: true,
                interactive: false
              });
              this.graph.addCell(circle);
              circle.attr('circle/r', parseInt((place as any).attributes.attrs.circle.r) / 2);
              (place as any).on('change:position', () => {
                circle.position(
                  place.position().x,
                  place.position().y
                );
              });
            }
          }
          if (element.type === 'pn.Transition') {
            this.transitionCounter++;
            const transition = new joint.shapes.pn.Transition({
              position: { x: element.x, y: element.y },
              id: element.id,
              attrs: {
                '.label': { text: element.name, 'ref-x': 0.5, 'ref-y': -15 },
                rect: transitionSettings.rect
              },
              selected: false
            });
            this.graph.addCell(transition);

            let width = element.width;
            let height = element.height;

            if (width < 20) {
              width = 20;
            }

            if (height < 20) {
              height = 20;
            }

            transition.attr('rect/width', parseInt(width));
            transition.attr('rect/height', parseInt(height));
          }
          if (element.type === 'basic.Text') {
            const label = this.addLabel(element.x, element.y);

            label.attr('text/font-size', element.r);
            label.attr('text/text', element.name);
          }
        });
        JSON.parse(netExport)[1].forEach((element: any) => {
          const link = new joint.shapes.pn.Link({
            source: { id: element.source },
            target: { id: element.target },
            attrs: {
              '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
              '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
              '.link-tools': { display: 'none' },
              '.connection': { stroke: 'black', 'stroke-width': 2, fill: 'none' },
              '.connection-wrap': { display: 'none' },
              '.marker-vertices': { display: 'none' },
              '.marker-vertex-remove-area': { fill: 'transparent' },
              '.marker-vertex': { r: 7, stroke: 'black', fill: 'transparent', 'stroke-width': 1 }
            },
            connector: {
              name: element.connector
            },
            weight: element.weight,
            selected: false
          });
          this.graph.addCell(link);

          const distance = 18 + (4 * element.weight.toString().length);

          let weight = '';

          if (element.weight > 1) {
            weight = element.weight;
          }

          link.appendLabel({
            attrs: {
              text: {
                text: weight,
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
                stroke: 'none'
              }
            },
            position: {
              distance: -distance
            }
          });

          if (element.vertices) {
            link.vertices(element.vertices);
          }

          link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
          link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
        });
      } catch (e) {
        this.clear();
      }
    },

    async netChangeSimulation(result: ISimulation) {
      result.changes.forEach((changes) => {
        const firstElem = this.graph.getCell(changes.split(' ')[0]);
        const secondElem = this.graph.getCell(changes.split(' ')[1]);
        if (firstElem) {
          if (firstElem.attributes.type === 'pn.Place') {
            firstElem.attributes.tokens = parseInt(firstElem.attributes.tokens) - parseInt(changes.split(' ')[2]);
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.place === firstElem.attributes.id) {
                if (parseInt(firstElem.attributes.tokens) > 0) {
                  element.attr('text/text', firstElem.attributes.tokens);
                } else {
                  element.remove();
                }
              }
            });
          }
          if (firstElem.attributes.type === 'pn.Transition') {
            secondElem.attributes.tokens = parseInt(secondElem.attributes.tokens) + parseInt(changes.split(' ')[2]);
            let found = false;
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.place === secondElem.attributes.id) {
                element.attr('text/text', secondElem.attributes.tokens);
                found = true;
              }
            });

            if (!found) {
              const circle = new joint.shapes.basic.Circle({
                position: { x: secondElem.position().x, y: secondElem.position().y },
                attrs: {
                  circle: {
                    attrs: {
                      r: 20
                    },
                    fill: 'transparent',
                    stroke: 'transparent',
                    'pointer-events': 'none'
                  },
                  text: {
                    text: secondElem.attributes.tokens,
                    'font-size': 16,
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'pointer-events': 'none',
                    fill: 'black'
                  }
                },
                'pointer-events': 'none',
                place: secondElem.attributes.id,
                locked: true,
                interactive: false
              });
              this.graph.addCell(circle);
              circle.attr('circle/r', parseInt(secondElem.attributes.attrs.circle.r) / 2);
              secondElem.on('change:position', () => {
                circle.position(
                  secondElem.position().x,
                  secondElem.position().y
                );
              });
            }
          }
        }
      });
    },

    saveModal() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      if (this.graph.getCells().length > 0) {
        this.selectedElement = '';
        if (!this.running) {
          const elements = [] as any;
          const connections = [] as any;
          this.getGraphData(elements, connections);
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
                  this.saveResult.netExport = '[' + JSON.stringify(elements) + ',' + JSON.stringify(connections) + ']';
                  this.update(history.state.editId);
                } else {
                  this.exampleEditResult.netExport = '[' + JSON.stringify(elements) + ',' + JSON.stringify(connections) + ']';
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
              } else if ((result.value).length > 20) {
                Swal.fire({
                  title: 'Nazwa jest za długa!'
                });
              } else {
                if (this.graph.getCells().length === 0) {
                  Swal.fire({
                    icon: 'error',
                    title: 'Błąd!',
                    text: 'Nie możesz zapisać pustego modelu!'
                  });
                } else {
                  if (!this.checkIfCreateExample()) {
                    this.saveResult.userId = this.loginResult.id;
                    this.saveResult.saveName = result.value;
                    this.saveResult.netExport = '[' + JSON.stringify(elements) + ',' + JSON.stringify(connections) + ']';
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
                    this.exampleNetResult.netExport = '[' + JSON.stringify(elements) + ',' + JSON.stringify(connections) + ']';
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
      } else {
        Swal.fire(
          'Błąd!',
          'Nie możesz zapisać pustego modelu!',
          'error'
        );
      }
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

    fetchData() {
      return this.graph.getCells().length;
    },

    checkIfExampleEdited() {
      if (history.state.editExampleNet) {
        return true;
      }

      return false;
    },

    switchPlace() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      if (this.selectedElement === 'place') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'place';
      }
    },

    switchTransition() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      if (this.selectedElement === 'transition') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'transition';
      }
    },

    switchDelete() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      if (this.selectedElement === 'delete') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'delete';
      }
    },

    switchFastAdd() {
      if (this.selectedElement === 'fastadd') {
        this.selectedElement = '';
        this.rectRangeAdd.remove();
      } else {
        this.selectedElement = 'fastadd';
        this.rectRangeAdd = new joint.shapes.basic.Rect({
          position: {
            x: -199,
            y: -199
          },
          size: {
            width: 200,
            height: 200
          },
          attrs: {
            rect: {
              stroke: 'black',
              'stroke-width': 1,
              fill: 'gray',
              opacity: 0.2,
              'pointer-events': 'none'
            }
          }
        });

        this.graph.addCell(this.rectRangeAdd);
      }
    },

    switchLabel() {
      if (this.rectRangeAdd && this.graph.getCell(this.rectRangeAdd.attributes.id)) {
        this.rectRangeAdd.remove();
      }
      if (this.selectedElement === 'label') {
        this.selectedElement = '';
      } else {
        this.selectedElement = 'label';
      }
    },

    updatePaperInteractivity() {
      if (this.running) {
        this.paper.setInteractivity(false);
      } else {
        this.paper.setInteractivity(true);
      }
    }
  }
});
</script>
