<template>
  <div class="px-6 pb-4 pt-7 w-4/5">
    <PaginationBar ref='pagination'
      class='w-1/2 m-auto'
      :size='this.size'
    />
    <div class="overflow-auto rounded-xl">
      <table class="min-w-full">
        <thead class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Imię
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Nazwisko
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              E-mail
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-2/12">
              Rola
            </th>
            <th class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/12">
              Akcja
            </th>
          </tr>
        </thead>
        <tbody class="text-gray-700">
          <tr v-for="user in result" :key="user" class="bg-white">
            <td class="text-left py-2 px-4">
              {{ user.firstName }}
            </td>
            <td class="text-left py-2 px-4">
              {{ user.lastName }}
            </td>
            <td class="text-left py-2 px-4">
              {{ user.email }}
            </td>
            <td class="text-left py-2 px-4">
              {{ showRole(user.role) }}
            </td>
            <td class="py-2 px-4 text-center">
              <AccountEditIcon v-if="user.id != this.resultLoggedUser.id" class="inline-block align-middle" @click='showEditModal(user.email)'/>
              <DeleteIcon v-if="user.id != this.resultLoggedUser.id" class="inline-block align-middle" @click='deleteAlert(user.id)'/>
            </td>
          </tr>
        </tbody>
      </table>
      <EditModal
        :user='editedUser'
        v-show='isModalVisible'
        @close='closeEditModal()'
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import UserServices, { IUser } from '../../services/UserService';
import Swal from 'sweetalert2';
import { AxiosError } from 'axios';
import { onBeforeRouteLeave } from 'vue-router';
import LoginServices from '@/services/LoginService';

import EditModal from '../../components/EditModal.vue';
import PaginationBar from '../../components/PaginationBar.vue';

import AccountEditIcon from 'vue-material-design-icons/AccountEdit.vue';
import DeleteIcon from 'vue-material-design-icons/Delete.vue';

export default defineComponent({
  components: {
    AccountEditIcon,
    DeleteIcon,
    EditModal,
    PaginationBar
  },
  data() {
    return {
      isModalVisible: false,
      result: Array<IUser>(),
      resultLoggedUser: UserServices.getBlankUserTemplate(),
      resultEdit: UserServices.getBlankUserTemplate(),
      editedUser: '',
      closed: false,
      deleted: false,
      size: 0,
      selected: 0,
      pageSize: 0
    };
  },

  watch: {
    closed() {
      if (this.closed) {
        this.getData(this.selected, this.pageSize).then((data) => (this.result = data));
        this.getUsers().then((data) => (this.size = data.length));
      }
    },
    deleted() {
      if (this.deleted) {
        this.getData(this.selected, this.pageSize).then((data) => (this.result = data));
        this.getUsers().then((data) => (this.size = data.length));
      }
    }
  },

  mounted() {
    this.pageSize = (this.$refs.pagination as any).pageSize;
    this.getData(this.selected, this.pageSize).then((data) => (this.result = data));
    this.getUser().then((data) => (this.resultLoggedUser = data));
    this.getUsers().then((data) => (this.size = data.length));
    this.$watch(
      '$refs.pagination.selected',
      (newVal: any) => {
        this.selected = newVal;
        this.getData(this.selected, this.pageSize).then((data) => (this.result = data));
      }
    );
    this.$watch(
      '$refs.pagination.pageSize',
      (newVal: any) => {
        this.pageSize = newVal;
        this.getData(0, this.pageSize).then((data) => (this.result = data));
      }
    );
  },

  methods: {
    async getUser(): Promise<IUser> {
      return await LoginServices.fetch();
    },

    async getData(page: number, pageSize: number): Promise<Array<IUser>> {
      return await UserServices.fetchPaginated(page, pageSize);
    },

    async getUsers(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    },

    async deleteUser(userId: number): Promise<any> {
      try {
        await UserServices.delete(userId);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      this.deleted = true;
      return 0;
    },

    deleteAlert(userId: number) {
      this.deleted = false;
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
          this.deleteUser(userId).then(function(result) {
            if (result > 0) {
              Swal.fire(
                'Błąd!',
                'Nie można usunąć użytkownika.',
                'error'
              );
            } else {
              Swal.fire(
                'Gotowe!',
                'Użytkownik został usunięty.',
                'success'
              );
            }
          });
        }
      });
    },

    showEditModal(email: string) {
      this.editedUser = email;
      this.isModalVisible = true;
      this.closed = false;
    },

    closeEditModal() {
      this.isModalVisible = false;
      this.editedUser = '';
      this.closed = true;
    },

    showRole(role: string) {
      if (role === 'ROLE_ADMIN') {
        return 'Admin';
      } else {
        return 'User';
      }
    }
  }
});
</script>
