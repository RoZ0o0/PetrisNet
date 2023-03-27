<template>
  <div class="w-full max-h-max max-w-sm m-auto">
    <div
      class="color-F6C453 border border-orange-100 shadow-md rounded px-8 pt-6 pb-8 mb-4"
      style="margin-bottom: auto"
    >
      <div class="mb-4">
        <label class="block text-black  text-base font-bold mb-2" for="email">
          Adres email
        </label>

        <input
          class="shadow appearance-none border-2 border-orange-100  rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="email"
          type="text"
          v-model="this.result.email"
          autocomplete="off"
        />
      </div>

      <div class="mb-6">
        <label
          class="block text-black text-base font-bold mb-2"
          for="password"
        >
          Hasło
        </label>

        <input
          @keyup.enter="login()"
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
          id="password"
          type="password"
          placeholder="********"
          v-model="this.result.password"
          autocomplete="off"
        />
      </div>

      <div class="grid place-items-center md:divide-y-8">
        <button
          @click="login()"
          class="bg-white hover:bg-red-300 text-black border-2 border-orange-100 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline
          "
        >
          Zaloguj
        </button>

        <hr />
      </div>
      <div class='text-center'>
        <p>Nie posiadasz konta? <router-link to='/register' class='text-red-600'>Zarejestruj się!</router-link></p>
        <p>Nie pamiętasz hasła? <a class='text-red-600' @click="resetSend()">Zresetuj!</a></p>
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
      result: LoginServices.getBlankLoginTemplate()
    };
  },
  mounted() {
    if (this.$route.query.verify) {
      const verify = this.$route.query.verify as string;
      this.verifyAccount(verify).then((data) => {
        if (!data) {
          Swal.fire(
            'Błąd!',
            'Wystąpił błąd!',
            'error'
          );
        } else {
          Swal.fire(
            'Gratulacje!',
            'Użytkownik został zweryfikowany',
            'success'
          );
        }
      });
    }
  },
  methods: {
    async login(): Promise<void> {
      await LoginServices.login(this.result).then((data) => {
        if (data.request.status === 200) {
          this.$router.push('/');
        }
      }).catch(() => {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Niepoprawne dane logowania'));
        this.result.email = '';
        this.result.password = '';
      });
    },

    async verifyAccount(code: string) {
      return await UserServices.verify(code);
    },

    async sendResetMail(email: string) {
      await UserServices.resetMail(email);
    },

    resetSend() {
      Swal.fire({
        title: 'Podaj email',
        html: '<input type="email" id="edit" class="swal2-input" autocomplete="off">',
        cancelButtonText: 'Anuluj',
        showCancelButton: true,
        preConfirm: () => {
          const editValue = (document.getElementById('edit') as HTMLInputElement).value;
          if (!editValue) {
            Swal.showValidationMessage('Pole jest puste!');
          }
          if (!this.regexEmail(editValue)) {
            Swal.showValidationMessage('Niepoprawny adres email!');
          }

          return { editValue };
        }
      }).then((result) => {
        if (result.value) {
          this.sendResetMail(result.value.editValue);
          Swal.fire(
            'Gotowe',
            'Jeśli adres istnieje w bazie, zostanie wysłany email!',
            'success'
          );
        }
      });
    },

    regexEmail(email: string) {
      const regex = /^[A-Za-z-\\.]+[A-Za-z0-9-\\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      const matches = regex.exec(email);
      return matches;
    }
  }
});
</script>
