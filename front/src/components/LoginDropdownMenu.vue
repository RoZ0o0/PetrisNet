<template>
  <div class="flex flex-wrap items-center">
    <el-dropdown class="border-0">
      <el-button type="primary">
        Witaj, {{ this.result }}<el-icon class="el-icon--right"></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu class='dropdown'>
          <router-link to="/profile">
            <el-dropdown-item>Twój profil</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click='logout()'>Wyloguj</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
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
        padding: 1.3rem 1.5rem 1.3rem 2rem;
        margin: 0px;
        width: 100%;
        height: 100%;
    }

    .el-dropdown-menu {
      width: 9rem;
    }
</style>
