<template>
  <div class="px-6 pb-40 pt-40 w-3/5 h-full">
    <div class="overflow-auto rounded-xl h-full">
      <table class="min-w-full h-full">
        <thead class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th colspan="2" class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/3">
              Twój profil
            </th>
          </tr>
        </thead>
        <tbody class="text-gray-700 h-full">
          <tr class="bg-white h-full">
            <td class="text-left pl-8 px-4 h-full w-1/2">
              <div>
                <span>Imię: </span>
                {{ this.result.firstName }}
              </div>
              <div class='mt-16'>
                <span>Nazwisko: </span>
                {{ this.result.lastName }}
              </div>
              <div class='mt-16'>
                <span>Email: </span>
                {{ this.result.email }}
              </div>
            </td>
            <td class="text-center px-4 pt-8 pb-4 h-full w-1/2 align-top">
              <span>Twoje zapisy</span> <br>
              <template v-for="save in resultSaves" :key="save">
                <span>{{ save.saveName }}</span><br>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import UserServices, { IUser } from '@/services/UserService';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import LoginServices, { ILogin } from '@/services/LoginService';

export default defineComponent({
  data() {
    return {
      result: LoginServices.getBlankLoginTemplate(),
      resultSaves: [SaveNetServices.getBlankSaveNetTemplate()]
    };
  },

  mounted() {
    this.getUser().then((data) => (this.result = data));
    this.getSaves().then((data) => (this.resultSaves = data));
  },

  methods: {
    async getUser(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    async getSaves(): Promise<Array<ISaveNet>> {
      return (await SaveNetServices.fetchSavedNets((await this.getUser()).email));
    }
  }
});
</script>
