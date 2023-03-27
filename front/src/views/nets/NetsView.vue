<template>
  <div class='pt-7 pr-6 w-4/5 flex items-center justify-center'>
    <span />
    <input v-if="this.selectOption === 'users'" type="text" class="justify-end py-2 text-center rounded-md border-black border-2 ml-6" v-model="this.searchUserSaved" placeholder="Szukaj"/>
    <input v-if="this.selectOption === 'examples'" type="text" class="justify-end py-2 text-center rounded-md border-black border-2 ml-6" v-model="this.searchExample" placeholder="Szukaj"/>
    <PaginationBar
      ref='pagination'
      class='w-1/2 ml-auto'
      :size='this.size'
    />
    <select v-model="this.selectOption" @change="reloadData(this.selectOption)" class='px-2 py-3 border-black border-2 rounded-md appearance-none float-right text-center ml-auto bg-orange-100'>
      <option value='users' selected>Zapisy użytkowników</option>
      <option value='examples'>Zapisy przykładowe</option>
    </select>
  </div>
  <div class="px-6 pb-4 pt-7 w-4/5">
    <div class="overflow-auto rounded-xl block table-height overflow-y-scroll hide-scrollbar">
      <table class="min-w-full">
        <thead v-if="this.selectOption == 'users'" class="bg-gray-800 text-white table-style">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-5/12">
              Użytkownik
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-4/12">
              Nazwa
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-1/12">
              Publiczne
            </th>
            <th v-if="this.selectOption == 'examples'" class="text-center py-3 px-4 uppercase font-semibold text-sm w-2/12">
              Akcja

            </th>
          </tr>
        </thead>
        <thead v-if="this.selectOption == 'examples'" class="bg-gray-800 text-white table-style">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-10/12">
              Nazwa
            </th>
            <th class="text-center align-middle py-3 px-4 uppercase font-semibold text-sm w-2/12">
              <span class='inline-block align-middle'>Akcja</span>
              <PlusIcon @click='this.$router.push({ name:"creator", state: {createExample: true} })' class='inline-block float-right bg-green-600 rounded-lg' />
            </th>
          </tr>
        </thead>
        <tbody v-if="this.selectOption == 'users'" class="text-gray-700 wrap-last">
          <tr v-for="userNets in resultUserNets" :key="userNets" class="bg-orange-100">
            <td class="text-left py-2 px-4">
              {{ getUserInfo(userNets.userId) }}
            </td>
            <td class="text-left py-2 px-4">
              {{ userNets.saveName }}
            </td>
            <td class="text-left py-2 px-4">
              {{ returnPublic(userNets.public) }}
            </td>
          </tr>
        </tbody>
        <tbody v-if="this.selectOption == 'examples'" class="text-gray-700 wrap-last">
          <tr v-for="exampleNets in resultExampleNets" :key="exampleNets" class="bg-orange-100">
            <td class="text-left py-2 px-4">
              {{ exampleNets.netName }}
            </td>
            <td class="py-2 px-4 text-center">
              <TableEditIcon class="inline-block align-middle" @click='this.$router.push({ name:"creator", state: {editExampleNet: exampleNets.netExport, editId: exampleNets.id} })'/>
              <PencilIcon class="inline-block align-middle" @click='editExampleNetAlert(exampleNets, exampleNets.id)'/>
              <DeleteIcon class="inline-block align-middle" @click='deleteExampleNetAlert(exampleNets.id)'/>
            </td>
          </tr>
        </tbody>
      </table>
      <EditSavedNets
        :net='editedNet'
        v-show='isModalVisible'
        @close='closeEditModal()'
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { AxiosError } from 'axios';
import UserServices, { IUser } from '../../services/UserService';
import LoginServices from '@/services/LoginService';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import ExampleNetServices, { IExampleNet } from '@/services/ExampleNetService';
import Swal from 'sweetalert2';
import EditSavedNets from '../../components/EditSavedNets.vue';

import DeleteIcon from 'vue-material-design-icons/Delete.vue';
import PlusIcon from 'vue-material-design-icons/Plus.vue';
import PencilIcon from 'vue-material-design-icons/Pencil.vue';
import TableEditIcon from 'vue-material-design-icons/TableEdit.vue';

import PaginationBar from '../../components/PaginationBar.vue';

export default defineComponent({
  components: {
    DeleteIcon,
    PlusIcon,
    PencilIcon,
    EditSavedNets,
    TableEditIcon,
    PaginationBar
  },
  data() {
    return {
      isModalVisible: false,
      resultUserNets: [SaveNetServices.getBlankSaveNetTemplate()],
      resultExampleNets: [ExampleNetServices.getBlankExampleNetTemplate()],
      resultLoggedUser: LoginServices.getBlankLoginTemplate(),
      resultUsers: [UserServices.getBlankUserTemplate()],
      selectOption: 'users',
      searchExample: '',
      searchUserSaved: '',
      editedNet: 0,
      size: 0,
      selected: 0,
      pageSize: 0
    };
  },

  watch: {
    resultLoggedUser() {
      if (this.resultLoggedUser.role !== 'ROLE_ADMIN') {
        this.$router.push('/');
      }
    },

    searchUserSaved() {
      this.selected = 0;
      this.searchUserNets(this.selected, this.pageSize, this.searchUserSaved).then((data) => (this.resultUserNets = data));
      this.searchUserNets(this.selected, 100000, this.searchUserSaved).then((data) => (this.size = data.length));
    },

    searchExample() {
      this.selected = 0;
      this.searchExampleNets(this.selected, this.pageSize, this.searchExample).then((data) => (this.resultExampleNets = data));
      this.searchExampleNets(this.selected, 100000, this.searchExample).then((data) => (this.size = data.length));
    }
  },

  mounted() {
    if (localStorage.getItem('token')) {
      this.getLoggedUser().then((data) => (this.resultLoggedUser = data));
      this.pageSize = (this.$refs.pagination as any).pageSize;
      this.getUserNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultUserNets = data));
      this.getUserNets().then((data) => (this.size = data.length));
      this.getUsers().then((data) => (this.resultUsers = data));
      this.$watch(
        '$refs.pagination.selected',
        (newVal: any) => {
          this.selected = newVal;
          if (this.selectOption === 'users') {
            this.searchUserNets(this.selected, this.pageSize, this.searchUserSaved).then((data) => (this.resultUserNets = data));
          } else {
            this.searchExampleNets(this.selected, this.pageSize, this.searchExample).then((data) => (this.resultExampleNets = data));
          }
        }
      );
      this.$watch(
        '$refs.pagination.pageSize',
        (newVal: any) => {
          this.pageSize = newVal;
          if (this.selectOption === 'users') {
            this.searchUserNets(this.selected, this.pageSize, this.searchUserSaved).then((data) => (this.resultUserNets = data));
          } else {
            this.searchExampleNets(this.selected, this.pageSize, this.searchExample).then((data) => (this.resultExampleNets = data));
          }
        }
      );
    } else {
      this.$router.push('/');
    }
  },

  methods: {
    async getLoggedUser(): Promise<IUser> {
      return await LoginServices.fetch();
    },

    async getUserNetsPaginated(page: number, pageSize: number): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchPaginated(page, pageSize);
    },

    async getUserNets(): Promise<Array<ISaveNet>> {
      return await SaveNetServices.fetchAll();
    },

    async searchUserNets(page: number, pageSize: number, search: string): Promise<Array<ISaveNet>> {
      return await SaveNetServices.searchAllNetsPaginated(page, pageSize, search);
    },

    async searchExampleNets(page: number, pageSize: number, search: string): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.search(page, pageSize, search);
    },

    async getUsers(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    },

    async getExampleNets(): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchAll();
    },

    async getExampleNetsPaginated(page: number, pageSize: number): Promise<Array<IExampleNet>> {
      return await ExampleNetServices.fetchPaginated(page, pageSize);
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

    async deleteSaveNet(saveNetId: number): Promise<any> {
      try {
        await SaveNetServices.delete(saveNetId);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 200;
    },

    getUserInfo(userId: number) {
      return this.resultUsers.find(name => name.id === userId)?.firstName + ' ' + this.resultUsers.find(name => name.id === userId)?.lastName + ' | ' + this.resultUsers.find(name => name.id === userId)?.email;
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
        this.selected = 0;
        this.getUserNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultUserNets = data));
        this.getUserNets().then((data) => (this.size = data.length));
      } else {
        this.selected = 0;
        this.getExampleNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultExampleNets = data));
        this.getExampleNets().then((data) => (this.size = data.length));
      }
    },

    editExampleNetAlert(net: IExampleNet, id: number) {
      Swal.fire({
        title: 'Edytujesz nazwę sieci: ' + net.netName,
        html: '<input type="text" id="edit" class="swal2-input" autocomplete="off">',
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
          if (editValue.length > 20) {
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
              this.getExampleNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultExampleNets = data));
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
                this.getExampleNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultExampleNets = data));
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
              this.getExampleNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultExampleNets = data));
            } else {
              Swal.fire(
                'Gotowe!',
                'Sieć została usunięta.',
                'success'
              );
              this.getExampleNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultExampleNets = data));
              this.getExampleNets().then((data) => (this.size = data.length));
            }
          });
        }
      });
    },

    deleteUserNetAlert(userNetId: number) {
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
          this.deleteSaveNet(userNetId).then((response) => {
            if (response !== 200) {
              Swal.fire(
                'Błąd!',
                'Nie można usunąć sieci.',
                'error'
              );
              this.getUserNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultUserNets = data));
            } else {
              Swal.fire(
                'Gotowe!',
                'Sieć została usunięta.',
                'success'
              );
              this.getUserNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultUserNets = data));
              this.getUserNets().then((data) => (this.size = data.length));
            }
          });
        }
      });
    },

    showEditModal(save: ISaveNet) {
      this.getUsers().then((data) => (this.resultUsers = data));
      this.editedNet = save.id;
      this.isModalVisible = true;
    },

    closeEditModal() {
      this.isModalVisible = false;
      this.editedNet = 0;
      this.getUserNetsPaginated(this.selected, this.pageSize).then((data) => (this.resultUserNets = data));
    }
  }
});
</script>
