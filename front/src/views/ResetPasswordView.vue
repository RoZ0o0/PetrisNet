<template>
  <div class="w-full max-h-max max-w-sm m-auto">
    <div
      class="color-F6C453 border border-orange-100 shadow-md rounded px-8 pt-6 pb-8 mb-4"
      style="margin-bottom: auto"
    >
      <div class="mb-4">
        <label class="block text-black  text-base font-bold mb-2" for="email">
          Nowe hasło
        </label>

        <input
          class="shadow appearance-none border-2 border-orange-100  rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="password"
          placeholder="********"
          type="password"
          v-model="this.passwd"
          autocomplete="off"
        />
      </div>

      <div class="mb-6">
        <label
          class="block text-black text-base font-bold mb-2"
          for="password"
        >
          Powtórz nowe hasło
        </label>

        <input
          class="
            shadow
            appearance-none
            border-2
            rounded
            w-full
            py-2
            px-3
            text-gray-700
            border-orange-100
            leading-tight
            focus:outline-none focus:shadow-outline
          "
          id="chck_password"
          type="password"
          placeholder="********"
          v-model="this.chck_passwd"
          autocomplete="off"
        />
      </div>

      <div class="grid place-items-center md:divide-y-8">
        <button
          class="bg-white hover:bg-red-300 text-black border-2 border-orange-100 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline
          "
          @click="resetPasswd()"
        >
          Zmień
        </button>

        <hr />
      </div>
      <p id="err" class="text-center hidden mt-2"></p>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import LoginServices, { ILogin } from '../services/LoginService';
import UserServices from '@/services/UserService';
import Swal from 'sweetalert2';

export default defineComponent({
  data() {
    return {
      passwd: '',
      chck_passwd: ''
    };
  },
  mounted() {
    if (this.$route.query.code) {
      if (localStorage.getItem('token')) {
        this.$router.push('/');
      }
    } else {
      this.$router.push('/');
    }
  },
  methods: {
    async reset(code: string, password: string) {
      return await UserServices.resetPassword(code, password);
    },

    resetPasswd() {
      if (this.passwd.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Za krótkie hasło.'));
        return;
      }
      if (!this.regexPassword(this.passwd)) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Hasło nie spełnia wymagań!'));
        return;
      }
      if (this.passwd !== this.chck_passwd) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Hasła się nie zgadzają!'));
        return;
      }

      if (this.$route.query.code) {
        const code = this.$route.query.code as string;
        this.reset(code, this.passwd).then((data) => {
          if (!data) {
            Swal.fire(
              'Błąd!',
              'Wystąpił błąd!',
              'error'
            );
            setTimeout(() => !data, 2000);
            this.$router.push('/login');
          } else {
            Swal.fire(
              'Gotowe!',
              'Hasło zostało zresetowane!',
              'success'
            );
            setTimeout(() => data, 2000);
            this.$router.push('/login');
          }
        });
      }
    },

    regexPassword(password: string) {
      const regex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,16}$/;
      const matches = regex.exec(password);
      return matches;
    }

  }
});
</script>
