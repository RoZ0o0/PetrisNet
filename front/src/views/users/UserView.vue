<template>
  <div class="px-6 pb-4 pt-7 w-4/5">
    <div class="overflow-auto rounded-xl">
      <table class="min-w-full">
        <thead class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-1/3">
              Imię
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-1/3">
              Nazwisko
            </th>
            <th class="text-left py-3 px-4 uppercase font-semibold text-sm w-1/3">
              E-mail
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
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import UserServices, { IUser } from '../../services/UserService';

export default defineComponent({
  data() {
    return {
      result: Array<IUser>()
    };
  },

  mounted() {
    this.getData().then((data) => (this.result = data));
  },

  methods: {
    async getData(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    }
  }
});
</script>
