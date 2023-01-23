<template>
  <div class="px-6 py-6 w-full h-full">
    <div class="overflow-auto rounded-xl w-full h-full">
      <div class="grid grid-cols-4 gap-10 bg-gray-200 h-full p-4">
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6">
          <span class="border-b-2">Sieci innych użytkowników</span>
          <ul v-for="save in result" :key="save" class="mt-2">
            <li>{{ save.saveName }}</li>
          </ul>
        </div>
        <div class="text-center border-2 rounded-xl bg-gray-400 p-6">
          <span class="border-b-2">Coś innego</span>
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
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'HomeView',
  data() {
    return {
      result: [SaveNetServices.getBlankSaveNetTemplate()]
    };
  },

  mounted() {
    (this.getData().then((data) => (this.result = data))).catch((error) => {
      if (error?.response.status === 401) {
        localStorage.removeItem('token');
      }
    });
  },

  methods: {
    async getData(): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchPublicNets();
    }
  }
});
</script>
