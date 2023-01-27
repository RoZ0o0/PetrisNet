<template>
  <div class='mt-4 text-left'>
    <select v-model="this.selectOption" @change="reloadData(this.selectOption)">
      <option value='users' selected>Zapisy użytkowników</option>
      <option value='examples'>Zapisy przykładowe</option>
    </select>
  </div>
  <div class="px-6 pb-4 pt-7 w-4/5">
    <div class="overflow-auto rounded-xl">
      <table class="min-w-full">
        <thead v-if="this.selectOption == 'users'" class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Użytkownik
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Nazwa
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-2/12">
              Publiczne
            </th>
            <th class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/12">
              Akcja
            </th>
          </tr>
        </thead>
        <thead v-if="this.selectOption == 'examples'" class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Nazwa
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-2/12">
              Export
            </th>
            <th class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/12">
              Akcja
            </th>
          </tr>
        </thead>
        <tbody v-if="this.selectOption == 'users'" class="text-gray-700">
          <tr v-for="userNets in resultUserNets" :key="userNets" class="bg-white">
            <td class="text-left py-2 px-4">
              {{ getUserInfo(userNets.userId) }}
            </td>
            <td class="text-left py-2 px-4">
              {{ userNets.saveName }}
            </td>
            <td class="text-left py-2 px-4">
              {{ returnPublic(userNets.public) }}
            </td>
            <td class="py-2 px-4 text-center">
              <AccountEditIcon class="inline-block align-middle"/>
              <DeleteIcon class="inline-block align-middle"/>
            </td>
          </tr>
        </tbody>
        <tbody v-if="this.selectOption == 'examples'" class="text-gray-700">
          <tr v-for="exampleNets in resultExampleNets" :key="exampleNets" class="bg-white">
            <td class="text-left py-2 px-4">
              {{ exampleNets.netName }}
            </td>
            <td class="text-left py-2 px-4">
              {{ exampleNets.netExport }}
            </td>
            <td class="py-2 px-4 text-center">
              <AccountEditIcon class="inline-block align-middle"/>
              <DeleteIcon class="inline-block align-middle"/>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import UserServices, { IUser } from '../../services/UserService';
import AccountEditIcon from 'vue-material-design-icons/AccountEdit.vue';
import DeleteIcon from 'vue-material-design-icons/Delete.vue';
import LoginServices from '@/services/LoginService';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';

export default defineComponent({
  components: {
    AccountEditIcon,
    DeleteIcon
  },
  data() {
    return {
      isModalVisible: false,
      resultUserNets: [SaveNetServices.getBlankSaveNetTemplate()],
      resultExampleNets: [ExampleNetServices.getBlankExampleNetTemplate()],
      resultLoggedUser: UserServices.getBlankUserTemplate(),
      resultUsers: [UserServices.getBlankUserTemplate()],
      selectOption: 'users'
    };
  },
  mounted() {
    this.getLoggedUser().then((data) => (this.resultLoggedUser = data));
    this.getUserNets().then((data) => (this.resultUserNets = data));
    this.getUsers().then((data) => (this.resultUsers = data));
  },

  methods: {
    async getLoggedUser(): Promise<IUser> {
      return await LoginServices.fetch();
    },

    async getUserNets(): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchAll();
    },

    async getUsers(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    },

    async getExampleNets(): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchAll();
    },

    getUserInfo(userId: number) {
      return this.resultUsers.find(name => name.id === userId)?.firstName + ' ' + this.resultUsers.find(name => name.id === userId)?.lastName;
    },

    returnPublic(isPublic: boolean) {
      if (isPublic) {
        return 'Tak';
      } else {
        return 'Nie';
      }
    },

    reloadData(option: string) {
      if (option === 'users') {
        this.getUserNets().then((data) => (this.resultUserNets = data));
      } else {
        this.getExampleNets().then((data) => (this.resultExampleNets = data));
      }
    }
  }
});
</script>
