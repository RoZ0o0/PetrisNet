<template>
  <div class="w-screen max-h-full rounded-b-xl">
    <div class="rounded-b-xl">
      <ul class="rounded-b-xl navbar">
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
          <li v-if="this.result.role == 'ROLE_ADMIN'">
            <span>Użytkownicy</span>
          </li>
        </router-link>
        <router-link to="/nets">
          <li v-if="this.result.role == 'ROLE_ADMIN'">
            <span>Sieci</span>
          </li>
        </router-link>
        <router-link to="/login">
          <li v-if="this.result.role == ''" class="float-right">
            <span>Zaloguj</span>
          </li>
        </router-link>
        <li v-if="this.result.role != ''" class="float-right" style='padding: 0px'>
          <dropdown />
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import LoginServices, { ILogin } from '@/services/LoginService';
import { defineComponent } from 'vue';
import dropdown from '../components/LoginDropdownMenu.vue';
export default defineComponent({
  components: {
    dropdown
  },
  data() {
    return {
      result: LoginServices.getBlankLoginTemplate()
    };
  },
  mounted() {
    if (localStorage.getItem('token') != null) {
      this.getData().then((data) => (this.result = data)).catch((error) => {
        if (error.response.status === 401) {
          this.logout();
        }
      });
    }
  },

  name: 'NavigationBar',
  methods: {
    async getData(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    logout(): void {
      localStorage.removeItem('token');
      if (this.$route.name === 'home') {
        window.location.reload();
      } else {
        this.$router.push('/');
      }
    }
  }
});
</script>
