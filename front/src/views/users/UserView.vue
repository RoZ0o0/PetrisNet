<template>
  <div class="px-6 pb-4 pt-7 w-4/5">
    <div class="overflow-auto rounded-xl">
      <table class="min-w-full">
        <thead class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-3/12">
              Imię
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-4/12">
              Nazwisko
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-4/12">
              E-mail
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-1/12">
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
            <td class="py-2 px-4 text-center">
              <AccountEditIcon class="inline-block align-middle" @click='showEditModal(user.email)'/>
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
import AccountEditIcon from 'vue-material-design-icons/AccountEdit.vue';
import EditModal from '../../components/EditModal.vue';

export default defineComponent({
  components: {
    AccountEditIcon,
    EditModal
  },
  data() {
    return {
      isModalVisible: false,
      result: Array<IUser>(),
      resultEdit: UserServices.getBlankUserTemplate(),
      editedUser: ''
    };
  },

  mounted() {
    this.getData().then((data) => (this.result = data));
  },

  methods: {
    async getData(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    },

    showEditModal(email: string) {
      this.editedUser = email;
      this.isModalVisible = true;
    },

    closeEditModal() {
      this.isModalVisible = false;
    }
  }
});
</script>
