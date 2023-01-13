<template>
  <div class="flex flex-wrap items-center">
    <el-dropdown class="border-0">
      <el-button type="primary">
        Witaj, {{ this.result }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>Twój profil</el-dropdown-item>
          <el-dropdown-item @click='logout()'>Wyloguj</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import UserServices, { IUser } from '@/services/UserService';
import axios from 'axios';
import { defineComponent } from 'vue';
export default defineComponent({
  name: 'LoginDropdownMenu',
  data() {
    return {
      result: localStorage.getItem('firstName')
    };
  },

  methods: {
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

<style scoped>
    .el-button {
        border: 0px;
        padding: 1.3rem 2rem 1.3rem 2rem;
        margin: 0px;
        width: 100%;
        height: 100%;
    }
</style>
