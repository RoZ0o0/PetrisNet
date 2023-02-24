<!-- eslint-disable vue/no-v-text-v-html-on-component -->
<template>
  <div class="flex w-full h-16 items-center">
    <div class="flex flex-row w-full h-16 items-center justify-start">
      <div class="flex items-center petri-nav w-3/12 select-none ml-8 justify-center">
        <span v-if='checkIfCreateExample()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Tworzenie przykładowej sieci</span>
        <span v-if='checkIfEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie sieci: {{ this.saveResult.saveName }} <br> Użytkownika: {{ this.editedSaveUserEmail }} </span>
        <span v-if='checkIfExampleEdited()' class='flex flex-row text-center rounded-xl color-F6C453 p-2'>Edytowanie przykładowej sieci: {{ this.exampleEditResult.netName }}</span>
      </div>
      <div class="flex ml-4 items-center petri-nav">
        <button class="border-2 border-black rounded-bl-xl rounded-tl-xl p-2 items-center" @click='run(); this.running = true;' :disabled='this.running'>
          <RunIcon class="inline-block align-middle" :disabled='this.running' />
          <span class="inline-block align-middle select-none" :disabled='this.running'>Run</span>
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
import CheckNetIcon from 'vue-material-design-icons/CheckNetwork.vue';
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
    CheckNetIcon
  },
  data() {
    return {
      paper: null as any,
      graph: null as any,
      contextShow: false,
      selectedElement: '',
      placeCounter: 0,
      transitionCounter: 0,
      current_connection: null as any,
      current_connections: [] as any,
      ctrl_pressed: false,
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

    const modelStates = [] as any;

    this.paper = new joint.dia.Paper({
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

    (this.paper as any).on('blank:mousewheel', (event: MouseEvent, x: number, y: number, delta: number) => {
      event.preventDefault();
      const direction = delta > 0 ? 1 : -1;
      const increment = 0.05;
      const zoomLevel = direction * increment;
      const maxZoom = 2;
      const minZoom = 0.2;
      const newScale = currentScale + zoomLevel;
      if (newScale >= minZoom && newScale <= maxZoom) {
        currentScale = newScale;
        this.paper.scale(newScale, newScale);
      }
    });

    this.updatePaperInteractivity();

    let rect: any;

    let startX: any, startY: any;
    let moveX: any, moveY: any;

    (this.paper as any).on('cell:pointerdown', (cellView: any, event: any, x: any, y: any) => {
      if (!this.ctrl_pressed) {
        if (cellView.model.attributes.type !== 'pn.Link') {
          startX = cellView.model.get('position').x;
          startY = cellView.model.get('position').y;
          const lastStep = this.graph.toJSON();
          modelStates.push(lastStep);
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
          modelStates.push(lastStep);
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
            modelStates.push(lastStep);
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
          } else {
            const lastStep = this.graph.toJSON();
            modelStates.push(lastStep);
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
              }
            });
          }
        }
      }
    });

    (this.paper as any).on('blank:pointerdown', (evt: any, x: number, y: number) => {
      if (this.selectedElement === 'place') {
        const lastStep = this.graph.toJSON();
        modelStates.push(lastStep);
        this.addPlace(x, y);
      } else if (this.selectedElement === 'transition') {
        const lastStep = this.graph.toJSON();
        modelStates.push(lastStep);
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

        (this.paper as any).on('blank:pointermove', (event: any, x: any, y: any) => {
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

    (this.paper as any).on('cell:pointermove', (cellView: any, event: any, x: any, y: any) => {
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

    (this.paper as any).on('cell:pointerup', (cellView: any, event: any, x: any, y: any) => {
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
          const paperOffset = (this.paper as any).el.getBoundingClientRect();
          const link = this.graph.getCell(this.current_connection);
          let found = false;
          this.graph.getCells().forEach((element: any) => {
            const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
            const bbox = element.getBBox();
            if (point.x >= bbox.x && point.x <= bbox.x + bbox.width && point.y >= bbox.y && point.y <= bbox.y + bbox.height) {
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
        const paperOffset = (this.paper as any).el.getBoundingClientRect();
        this.current_connections.forEach((place: any) => {
          const link = this.graph.getCell(place);
          let found = false;
          this.graph.getCells().forEach((element: any) => {
            const point = this.paper.pageToLocalPoint({ x: event.clientX, y: event.clientY });
            const bbox = element.getBBox();
            if (point.x >= bbox.x && point.x <= bbox.x + bbox.width && point.y >= bbox.y && point.y <= bbox.y + bbox.height) {
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
                        link.remove();
                      }
                    }
                  });
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

    (this.paper as any).on('blank:pointerup', () => {
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

    (this.paper as any).on('cell:contextmenu', (cellView: any, evt: any) => {
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
          modelStates.push(lastStep);
          if ((cellView as any).model.get('type') === 'pn.Place' || (cellView as any).model.get('type') === 'pn.Transition') {
            let nameExist = false;
            this.graph.getCells().forEach((element: any) => {
              if (element.attributes.type !== 'pn.Link' && element.attributes.type !== 'basic.Circle') {
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

              let tokenText = (cellView as any).model.get('tokens').toString();

              if (parseInt((cellView as any).model.get('tokens')) === 1) {
                tokenText = '';
              }

              const circle = new joint.shapes.basic.Circle({
                position: { x: (cellView as any).model.position().x, y: (cellView as any).model.position().y },
                attrs: {
                  circle: {
                    attrs: {
                      r: 20
                    },
                    fill: 'black',
                    'pointer-events': 'none'
                  },
                  text: {
                    text: tokenText,
                    'font-size': 16,
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'pointer-events': 'none',
                    fill: 'white'
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
                  stroke: 'none'
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
        }

        editWindow.appendChild(saveButton);
        editWindow.appendChild(cancelButton);

        (container as any).appendChild(editWindow);
      }
    });

    window.addEventListener('keydown', (evt: KeyboardEvent) => {
      if (!this.running) {
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
            modelStates.push(lastStep);
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
            let modelState: any;
            if (this.graph.getCells().length > 0) {
              modelState = modelStates.pop();
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
                } else if (modelState.cells[i].type === 'basic.Circle') {
                  this.graph.getCells().forEach((element: any) => {
                    if (element.attributes.id === modelState.cells[i].place) {
                      const circle = new joint.shapes.basic.Circle({
                        position: { x: element.attributes.position.x, y: element.attributes.position.y },
                        attrs: {
                          circle: {
                            attrs: {
                              r: 20
                            },
                            fill: 'black',
                            'pointer-events': 'none'
                          },
                          text: {
                            text: element.get('tokens').toString(),
                            'font-size': 18,
                            'text-anchor': 'middle',
                            'y-alignment': 'middle',
                            'pointer-events': 'none',
                            fill: 'white'
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
                        stroke: 'none'
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
        this.paper.setInteractivity(true);
        this.ctrl_pressed = false;
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

    async checkNet(result: ISimulation): Promise<boolean> {
      return await SimulationServices.checkNet(result);
    },

    async simulation(result: ISimulation): Promise<ISimulation> {
      return await SimulationServices.simulation(result);
    },

    async run() {
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

      let pathData: any;

      this.resultSimulation.changes.forEach((ele: any) => {
        this.graph.getCells().forEach((element: any) => {
          if (element.attributes.type === 'pn.Link') {
            if (element.getSourceElement().attributes.attrs['.label'].text === ele.split(' ')[0] &&
                element.getTargetElement().attributes.attrs['.label'].text === ele.split(' ')[1]) {
              sourcePosition = element.getSourcePoint();
              targetPosition = element.getTargetPoint();

              const circle = new joint.shapes.basic.Circle({
                position: { x: sourcePosition.x - 10, y: sourcePosition.y - 20 },
                size: { width: 20, height: 20 },
                attrs: {
                  circle: { fill: 'black' }
                },
                simulation: true
              });

              this.graph.addCell(circle);

              circle.transition('position/y', targetPosition.y - 5, {
                duration: 500,
                timingFunction: function(t) { return t * t; },
                valueFunction: function(a, b) {
                  return function(t) {
                    return a + (b - a) * t;
                  };
                }
              });

              circle.transition('position/x', targetPosition.x - 5, {
                duration: 500,
                timingFunction: function(t) { return t * t; },
                valueFunction: function(a, b) {
                  return function(t) {
                    return a + (b - a) * t;
                  };
                }
              });
            }
          }
        });
      });

      await this.customTimeout(500);

      this.graph.getCells().forEach((element: any) => {
        if (element.attributes.type === 'basic.Circle' && element.attributes.simulation) {
          element.remove();
        }
      });

      await this.customTimeout(400);

      if (this.running) {
        await this.run();
      }
    },

    async stop() {
      this.running = false;
      this.netChangeSimulation(this.beforeSimulation);
      this.simulationCounter = 0;

      this.graph.getCells().forEach((element: any) => {
        if (element.attributes.type === 'basic.Circle' && element.attributes.simulation) {
          element.remove();
        }
      });
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
        if (element.attributes.type === 'pn.Link') {
          weight = parseInt(element.attributes.weight);
          source = element.attributes.source.id;
          target = element.attributes.target.id;
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
        if (element.attributes.type !== 'basic.Circle') {
          if (element.attributes.type !== 'pn.Link') {
            elements.push({ name: name, type: element.attributes.type, x: x, y: y, weight: weight, tokens: tokens, id: element.attributes.id, r: r, width: width, height: height });
          } else {
            connections.push({ source: source, target: target, weight: weight });
          }
        }
      });
    },

    // checkNetAlert() {
    //   this.resultSimulation.elements = this.elements.slice(1);
    //   this.resultSimulation.connections = this.connections;
    //   this.resultSimulation.tokens = this.tokens;
    //   this.resultSimulation.connectionWeights = this.connection_weight;

    //   this.checkNet(this.resultSimulation).then((data) => {
    //     if (data) {
    //       Swal.fire(
    //         'Sprawdzenie!',
    //         'Symulacja sieci zostanie wykonana!',
    //         'success'
    //       );
    //     } else {
    //       Swal.fire(
    //         'Sprawdzenie!',
    //         'Symulacja sieci nie wykona się!',
    //         'error'
    //       );
    //     }
    //   });
    // },

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

    clear() {
      this.graph.clear();
    },

    exportNet() {
      if (!this.running) {
        if (this.graph.getCells().length > 0) {
          Swal.fire({
            title: 'Wybierz format',
            showDenyButton: true,
            confirmButtonText: 'Standardowy',
            denyButtonText: 'Tina',
            denyButtonColor: '#7066e0'
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
                  data = data + 'p ' + element.attributes.position.x + '.0 ' + element.attributes.position.y + '.0 ' + element.attributes.attrs['.label'].text + ' ' + element.attributes.tokens;

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
                  let tokenText = element.tokens;

                  if (parseInt(element.tokens) === 1) {
                    tokenText = '';
                  }

                  const circle = new joint.shapes.basic.Circle({
                    position: { x: element.x, y: element.y },
                    attrs: {
                      circle: {
                        attrs: {
                          r: 20
                        },
                        fill: 'black',
                        'pointer-events': 'none'
                      },
                      text: {
                        text: tokenText,
                        'font-size': 16,
                        'text-anchor': 'middle',
                        'y-alignment': 'middle',
                        'pointer-events': 'none',
                        fill: 'white'
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
            });
            JSON.parse(this.dest)[1].forEach((element: any) => {
              const link = new joint.shapes.pn.Link({
                source: { id: element.source },
                target: { id: element.target },
                attrs: {
                  '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
                  '.link-tools': { display: 'none' },
                  '.connection': { stroke: 'black', 'pointer-events': 'none' }
                },
                weight: element.weight,
                selected: false
              });
              this.graph.addCell(link);

              const distance = 18 + (4 * element.weight.toString().length);

              link.appendLabel({
                attrs: {
                  text: {
                    text: element.weight.toString(),
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

              link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
              link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
            });
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
              let tokenText = element.tokens;

              if (parseInt(element.tokens) === 1) {
                tokenText = '';
              }

              const circle = new joint.shapes.basic.Circle({
                position: { x: element.x, y: element.y },
                attrs: {
                  circle: {
                    attrs: {
                      r: 20
                    },
                    fill: 'black',
                    'pointer-events': 'none'
                  },
                  text: {
                    text: tokenText,
                    'font-size': 16,
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'pointer-events': 'none',
                    fill: 'white'
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
        });
        JSON.parse(netExport)[1].forEach((element: any) => {
          const link = new joint.shapes.pn.Link({
            source: { id: element.source },
            target: { id: element.target },
            attrs: {
              '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
              '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
              '.link-tools': { display: 'none' },
              '.connection': { stroke: 'black', 'pointer-events': 'none' }
            },
            weight: element.weight,
            selected: false
          });
          this.graph.addCell(link);

          const distance = 18 + (4 * element.weight.toString().length);

          link.appendLabel({
            attrs: {
              text: {
                text: element.weight.toString(),
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

          link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
          link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
        });
      } catch (e) {
        this.clear();
      }
    },

    netChangeSimulation(result: ISimulation) {
      this.clear();
      try {
        result.elements.forEach((element: any) => {
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
              let tokenText = element.tokens;

              if (parseInt(element.tokens) === 1) {
                tokenText = '';
              }

              const circle = new joint.shapes.basic.Circle({
                position: { x: element.x, y: element.y },
                attrs: {
                  circle: {
                    attrs: {
                      r: 20
                    },
                    fill: 'black',
                    'pointer-events': 'none'
                  },
                  text: {
                    text: tokenText,
                    'font-size': 16,
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'pointer-events': 'none',
                    fill: 'white'
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
        });
        result.connections.forEach((element: any) => {
          const link = new joint.shapes.pn.Link({
            source: { id: element.source },
            target: { id: element.target },
            attrs: {
              '.marker-target': { display: 'none', style: { 'pointer-events': 'none' } },
              '.marker-source': { display: 'none', style: { 'pointer-events': 'none' } },
              '.link-tools': { display: 'none' },
              '.connection': { stroke: 'black', 'pointer-events': 'none' }
            },
            weight: element.weight,
            selected: false
          });
          this.graph.addCell(link);

          const distance = 18 + (4 * element.weight.toString().length);

          link.appendLabel({
            attrs: {
              text: {
                text: element.weight.toString(),
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

          link.attr('.marker-arrowhead[end=target]', { d: 'M 8 -14 L -13 0 L 8 14 L 0 0 Z', class: 'arrowhead' });
          link.attr('.marker-arrowhead[end=source]', { d: 'M -10 0 L -10 0 L -10 0 z', style: { 'pointer-events': 'none' } });
        });
      } catch (e) {
        this.clear();
      }
    },

    saveModal() {
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
            } else if ((result.value).length > 16) {
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
