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
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-5/12">
              Nazwa
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-5/12">
              Export
            </th>
            <th class="text-center align-middle py-3 px-4 uppercase font-semibold text-sm w-2/12">
              <span class='inline-block align-middle'>Akcja</span>
              <PlusIcon @click='this.$router.push({ name:"creator", state: {createExample: true} })' class='inline-block float-right bg-green-600 rounded-lg' />
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
              <PencilIcon class="inline-block align-middle"/>
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
              <PencilIcon class="inline-block align-middle" @click='editExampleNetAlert(exampleNets, exampleNets.id)'/>
              <DeleteIcon class="inline-block align-middle" @click='deleteExampleNetAlert(exampleNets.id)'/>
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
import LoginServices from '@/services/LoginService';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import Swal from 'sweetalert2';

import DeleteIcon from 'vue-material-design-icons/Delete.vue';
import PlusIcon from 'vue-material-design-icons/Plus.vue';
import PencilIcon from 'vue-material-design-icons/Pencil.vue';
import { AxiosError } from 'axios';

export default defineComponent({
  components: {
    DeleteIcon,
    PlusIcon,
    PencilIcon
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

    async findNetName(netName: string): Promise<boolean> {
      return await ExampleNetServices.findByNetName(netName);
    },

    async editExampleNets(exampleNet: IExampleNet, id: number): Promise<any> {
      try {
        await ExampleNetServices.update(exampleNet, id);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 200;
    },

    async deleteExampleNet(exampleNetId: number): Promise<any> {
      try {
        await ExampleNetServices.delete(exampleNetId);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 200;
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
    },

    editExampleNetAlert(net: IExampleNet, id: number) {
      Swal.fire({
        title: 'Edytujesz nazwę sieci: ' + net.netName,
        html: '<input type="text" id="edit" class="swal2-input">',
        cancelButtonText: 'Anuluj',
        showCancelButton: true,
        preConfirm: () => {
          const editValue = (document.getElementById('edit') as HTMLInputElement).value;
          if (!editValue) {
            Swal.showValidationMessage('Pole jest puste!');
          }
          if (editValue.length < 3) {
            Swal.showValidationMessage('Podana nazwa jest za krótka');
          }
          if (editValue.length > 16) {
            Swal.showValidationMessage('Podana nazwa jest za długa');
          }

          return { editValue };
        }
      }).then((result) => {
        if (result.value) {
          net.netName = result.value.editValue;
          this.findNetName(result.value.editValue).then((exist) => {
            if (exist) {
              Swal.fire({
                icon: 'error',
                title: 'Błąd!',
                text: 'Nazwa już istnieje!'
              });
              this.getExampleNets().then((data) => (this.resultExampleNets = data));
            } else {
              this.editExampleNets(net, id).then((error) => {
                if (error !== 200) {
                  Swal.fire({
                    icon: 'error',
                    title: 'Błąd!',
                    text: 'Wystąpił błąd!'
                  });
                } else {
                  Swal.fire({
                    title: 'Gotowe!',
                    text: 'Nazwa przykładowej sieci została edytowana!',
                    icon: 'success'
                  });
                }
                this.getExampleNets().then((data) => (this.resultExampleNets = data));
              });
            }
          });
        }
      });
    },

    deleteExampleNetAlert(exampleNetId: number) {
      Swal.fire({
        title: 'Jesteś pewien?',
        text: 'Nie będziesz w stanie tego cofnąć!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Tak, usuń!',
        cancelButtonText: 'Anuluj'
      }).then((result) => {
        if (result.isConfirmed) {
          this.deleteExampleNet(exampleNetId).then((response) => {
            if (response !== 200) {
              Swal.fire(
                'Błąd!',
                'Nie można usunąć sieci.',
                'error'
              );
              this.getExampleNets().then((data) => (this.resultExampleNets = data));
            } else {
              Swal.fire(
                'Gotowe!',
                'Sieć została usunięta.',
                'success'
              );
              this.getExampleNets().then((data) => (this.resultExampleNets = data));
            }
          });
        }
      });
    }
  }
});
</script>
