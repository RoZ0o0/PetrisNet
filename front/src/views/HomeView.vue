<template>
  <div class="px-6 py-6 w-full h-full">
    <div class="overflow-auto rounded-xl w-3/5 h-full mx-auto">
      <div class="h-1/6 flex justify-center items-center flex-col color-F6C453 border-1 border-black rounded-t-xl">
        <h1 class="text-xl border-b-2 border-b-black">Edytor Sieci Petriego Online</h1>
        <p class='mt-4'>Aby tworzyć swoją sieć udaj się na zakładkę <b>"Kreator"</b>.</p>
        <p>Jeśli chcesz sprawdzić sieci innych użytkowników, bądź przykładowe sieci, kliknij na wybraną nazwę poniżej!</p>
        <p><b>Zaloguj się</b> aby mieć dodatkowe możliwości!</p>
      </div>
      <div class="grid grid-cols-2 gap-10 bg-orange-100 h-5/6 p-4">
        <div class="text-center border-2 rounded-xl color-F6C453 p-6 flex flex-col">
          <span class="border-b-2">Sieci innych użytkowników</span>
          <input class="mt-2 mb-2 rounded-md bg-gray-200 text-center p-1 w-1/2 mx-auto" v-model="this.searchUserSave" placeholder="Szukaj"/>
          <ul v-for="save in resultPublicNets" :key="save" class="mt-2 font-mono">
            <li @click='this.$router.push({ name:"creator", state: {redirectExport: save.netExport} })'>{{ save.saveName }}</li>
          </ul>
          <HomePaginationBar
            ref='userNetPagination'
            class='mt-auto'
            :size='this.userNetSize'
            :type='this.type'
          />
        </div>
        <div class="text-center border-2 rounded-xl color-F6C453 p-6 flex flex-col">
          <span class="border-b-2">Przykładowe sieci</span>
          <input class="mt-2 mb-2 rounded-md bg-gray-200 text-center p-1 w-1/2 mx-auto" v-model="this.searchExampleName" placeholder="Szukaj"/>
          <ul v-for="exampleNets in resultExampleNets" :key="exampleNets" class="mt-2 font-mono">
            <li @click='this.$router.push({ name:"creator", state: {redirectExport: exampleNets.netExport} })'>{{ exampleNets.netName }}</li>
          </ul>
          <HomePaginationBar
            ref='exampleNetPagination'
            class='mt-auto'
            :size='this.exampleNetSize'
            :type='this.type'
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import { defineComponent } from 'vue';

import HomePaginationBar from '../components/HomePaginationBar.vue';

export default defineComponent({
  components: {
    HomePaginationBar
  },
  name: 'HomeView',
  data() {
    return {
      resultPublicNets: [SaveNetServices.getBlankSaveNetTemplate()],
      resultExampleNets: [ExampleNetServices.getBlankExampleNetTemplate()],
      userNetSize: 0,
      exampleNetSize: 0,
      userNetSelected: 0,
      exampleNetSelected: 0,
      searchUserSave: '',
      searchExampleName: '',
      pageSize: 14,
      type: 'home'
    };
  },

  watch: {
    searchUserSave() {
      this.userNetSelected = 0;
      this.searchPublicNetsPaginated(this.userNetSelected, this.pageSize, this.searchUserSave).then((data) => (this.resultPublicNets = data));
      this.searchPublicNetsPaginated(this.userNetSelected, 100000, this.searchUserSave).then((data) => (this.userNetSize = data.length));
    },

    searchExampleName() {
      this.exampleNetSelected = 0;
      this.searchExampleNetsPaginated(this.userNetSelected, this.pageSize, this.searchExampleName).then((data) => (this.resultExampleNets = data));
      this.searchExampleNetsPaginated(this.userNetSelected, 100000, this.searchExampleName).then((data) => (this.exampleNetSize = data.length));
    }
  },

  mounted() {
    this.getPublicNets().then((data) => (this.userNetSize = data.length));
    this.getExampleNets().then((data) => (this.exampleNetSize = data.length));

    this.searchPublicNetsPaginated(this.userNetSelected, this.pageSize, this.searchUserSave).then((data) => (this.resultPublicNets = data));
    this.searchExampleNetsPaginated(this.exampleNetSelected, this.pageSize, this.searchExampleName).then((data) => (this.resultExampleNets = data));

    this.$watch(
      '$refs.userNetPagination.selected',
      (newVal: any) => {
        this.userNetSelected = newVal;
        this.searchPublicNetsPaginated(this.userNetSelected, this.pageSize, this.searchUserSave).then((data) => (this.resultPublicNets = data));
      }
    );

    this.$watch(
      '$refs.exampleNetPagination.selected',
      (newVal: any) => {
        this.exampleNetSelected = newVal;
        this.searchExampleNetsPaginated(this.exampleNetSelected, this.pageSize, this.searchExampleName).then((data) => (this.resultExampleNets = data));
      }
    );
  },

  methods: {
    async getPublicNets(): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchPublicNets();
    },

    async getPublicNetsPaginated(page: number, pageSize: number): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchPublicNetsPaginated(page, pageSize);
    },

    async searchPublicNetsPaginated(page: number, pageSize: number, search: string): Promise<Array<ISaveNet>> {
      return await SaveNetServices.searchPublicNetsPaginated(page, pageSize, search);
    },

    async getExampleNets(): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchAll();
    },

    async getExampleNetsPaginated(page: number, pageSize: number): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchPaginated(page, pageSize);
    },

    async searchExampleNetsPaginated(page: number, pageSize: number, search: string): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.search(page, pageSize, search);
    }

  }
});
</script>
