<template>
  <div class="w-screen max-h-full rounded-b-xl">
    <div class="rounded-b-xl">
      <ul class="bg-red-500 rounded-b-xl navbar">
        <router-link to ="/">
          <li>
            <span>Strona główna</span>
          </li>
        </router-link>
        <router-link to="/users">
          <li v-if="this.result.role == 'Admin'">
            <span>Użytkownicy</span>
          </li>
        </router-link>
        <router-link to="/creator">
          <li>
            <span>Kreator</span>
          </li>
        </router-link>
        <router-link to="/login">
          <li v-if="!this.result.role" class="float-right">
            <span>Zaloguj</span>
          </li>
        </router-link>
        <li v-if="this.result.role" v-on:click="logout()" class="float-right">
          <span>Wyloguj</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import LoginServices, { ILogin } from '../services/LoginService';
import UserServices, { IUser } from '@/services/UserService';
export default defineComponent({
  data() {
    return {
      result: UserServices.getBlankUserTemplate()
    };
  },
  mounted() {
    this.getData().then((data) => (this.result = data));
    this.$forceUpdate();
  },
  name: 'NavigationBar',
  methods: {
    async getData(): Promise<IUser> {
      if (localStorage.getItem('mail') != null) {
        return await LoginServices.fetch();
      } else {
        return this.result;
      }
    },

    logout(): void {
      localStorage.removeItem('mail');
      if (this.$route.name === 'home') {
        window.location.reload();
      } else {
        this.$router.push('/');
      }
    }
  }
});
</script>
