<template>
  <div class="px-6 py-6 w-full h-full">
    <div class="overflow-auto rounded-xl w-full h-full">
      <div class="grid grid-cols-4 gap-10 bg-gray-200 h-full p-4">
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6 flex flex-col">
          <span class="border-b-2">Sieci innych użytkowników</span>
          <ul v-for="save in resultPublicNets" :key="save" class="mt-2">
            <li @click='this.$router.push({ name:"creator", state: {redirectExport: save.netExport} })'>{{ save.saveName }}</li>
          </ul>
          <HomePaginationBar
            ref='userNetPagination'
            class='mt-auto'
            :size='this.userNetSize'
            :type='this.type'
          />
        </div>
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6 flex flex-col">
          <span class="border-b-2">Przykładowe sieci</span>
          <ul v-for="exampleNets in resultExampleNets" :key="exampleNets" class="mt-2">
            <li @click='this.$router.push({ name:"creator", state: {redirectExport: exampleNets.netExport} })'>{{ exampleNets.netName }}</li>
          </ul>
          <HomePaginationBar
            ref='exampleNetPagination'
            class='mt-auto'
            :size='this.exampleNetSize'
            :type='this.type'
          />
        </div>
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6">
          <span class="border-b-2">Coś innego</span>
        </div>
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6">
          <span class="border-b-2">Coś innego</span>
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
      pageSize: 20,
      type: 'home'
    };
  },

  mounted() {
    this.getPublicNets().then((data) => (this.userNetSize = data.length));
    this.getExampleNets().then((data) => (this.exampleNetSize = data.length));

    this.getPublicNetsPaginated(this.userNetSelected, this.pageSize).then((data) => (this.resultPublicNets = data));
    this.getExampleNetsPaginated(this.exampleNetSelected, this.pageSize).then((data) => (this.resultExampleNets = data));

    this.$watch(
      '$refs.userNetPagination.selected',
      (newVal: any) => {
        this.userNetSelected = newVal;
        this.getPublicNetsPaginated(this.userNetSelected, this.pageSize).then((data) => (this.resultPublicNets = data));
      }
    );

    this.$watch(
      '$refs.exampleNetPagination.selected',
      (newVal: any) => {
        this.exampleNetSelected = newVal;
        this.getExampleNetsPaginated(this.exampleNetSelected, this.pageSize).then((data) => (this.resultExampleNets = data));
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

    async getExampleNets(): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchAll();
    },

    async getExampleNetsPaginated(page: number, pageSize: number): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchPaginated(page, pageSize);
    }
  }
});
</script>
