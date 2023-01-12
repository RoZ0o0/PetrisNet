<template>
  <div class="w-screen max-h-full rounded-b-xl">
    <div class="rounded-b-xl">
      <ul class="bg-red-500 rounded-b-xl navbar">
        <router-link to ="/">
          <li>
            <span>Strona główna</span>
          </li>
        </router-link>
        <router-link to="/creator">
          <li>
            <span>Kreator</span>
          </li>
        </router-link>
        <router-link to="/users">
          <li v-if="checkRole() == 'Admin'">
            <span>Użytkownicy</span>
          </li>
        </router-link>
        <router-link to="/login">
          <li v-if="checkRole() == null" class="float-right">
            <span>Zaloguj</span>
          </li>
        </router-link>
        <li v-if="checkRole() != null" v-on:click="logout()" class="float-right">
          <span>Wyloguj</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
export default defineComponent({

  name: 'NavigationBar',
  methods: {
    checkRole() {
      if (localStorage.getItem('role') !== undefined) {
        return localStorage.getItem('role');
      } else {
        return null;
      }
    },

    logout(): void {
      localStorage.removeItem('role');
      if (this.$route.name === 'home') {
        window.location.reload();
      } else {
        this.$router.push('/');
      }
    }
  }
});
</script>
