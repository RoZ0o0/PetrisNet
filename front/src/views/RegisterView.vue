<template>
  <div class="w-full max-h-max max-w-md m-auto">
    <div
      class="color-F6C453 border border-orange-100 shadow-md rounded pl-4 pr-6 pt-8 pb-8 mb-4"
      style="margin-bottom: auto"
    >
      <form @submit.prevent="validateRegister">
        <div class="mb-4 text-center border-b-2 border-b-black pb-2">
          <label class="inline text-black text-base font-bold mb-2" for="firstName">
            Rejestracja
          </label>
        </div>

        <div class="mb-4 text-right">
          <label class="inline text-black text-base font-bold mb-2" for="firstName">
            Imię:
          </label>

          <input
            class="shadow appearance-none border-2 border-orange-100  rounded w-3/5 py-2 px-3 pb-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="firstname"
            type="text"
            v-model="this.result.firstName"
            autocomplete="off"
            required
          />
        </div>

        <div class="mb-4 text-right">
          <label class="inline text-black text-base font-bold mb-2" for="lastName">
            Nazwisko:
          </label>

          <input
            class="shadow appearance-none border-2 border-orange-100  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="lastname"
            type="text"
            v-model="this.result.lastName"
            autocomplete="off"
            required
          />
        </div>

        <div class="mb-4 text-right">
          <label class="inline text-black  text-base font-bold mb-2" for="email">
            Adres email:
          </label>

          <input
            class="shadow appearance-none border-2 border-orange-100  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="email"
            type="text"
            v-model="this.result.email"
            autocomplete="off"
            required
          />
        </div>

        <div class="mb-4 text-right">
          <label class="inline text-black  text-base font-bold mb-2" for="email">
            Powtórz adres email:
          </label>

          <input
            class="shadow appearance-none border-2 border-orange-100  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="chck_email"
            type="text"
            v-model="this.chck_email"
            autocomplete="off"
            required
          />
        </div>

        <div class="mb-6 text-right">
          <label
            class="inline text-black text-base font-bold mb-2"
            for="password"
          >
            Hasło:
          </label>

          <input
            class="
              shadow
              appearance-none
              border-2
              rounded
              w-3/5
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
            required
          />
        </div>

        <div class="mb-8 text-right">
          <label
            class="inline text-black text-base font-bold mb-2"
            for="chck_password"
          >
            Powtórz hasło:
          </label>

          <input
            class="
              shadow
              appearance-none
              border-2
              rounded
              w-3/5
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
            v-model="this.chck_password"
            autocomplete="off"
            required
          />
        </div>

        <div class="grid place-items-center md:divide-y-8">
          <button
            class="bg-white hover:bg-red-300 text-black border-2 border-orange-100 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline
            "
          >
            Zarejestruj
          </button>

          <hr />
        </div>
      </form>
      <p id="err" class="text-center hidden"></p>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import UserServices, { IUser } from '@/services/UserService';
import Swal from 'sweetalert2';

export default defineComponent({
  data() {
    return {
      result: UserServices.getBlankUserTemplate(),
      chck_email: '',
      chck_password: ''
    };
  },

  methods: {

    regexEmail(email: string) {
      const regex = /^[A-Za-z-\\.]+[A-Za-z0-9-\\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      const matches = regex.exec(email);
      return matches;
    },

    regexPassword(password: string) {
      const regex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,16}$/;
      const matches = regex.exec(password);
      return matches;
    },

    async validateRegister(e: Event) {
      if (this.result.email !== this.chck_email) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Adresy email się nie zgadzają!'));
        e.preventDefault();
        return;
      }

      if (this.result.password !== this.chck_password) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Hasła sie nie zgadzają!'));
        e.preventDefault();
        return;
      }

      if (this.result.firstName.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Imie jest niepoprawne!'));
        e.preventDefault();
        return;
      }

      if (this.result.lastName.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Nazwisko jest niepoprawne!'));
        e.preventDefault();
        return;
      }

      if (!this.regexEmail(this.result.email)) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Niepoprawny email!'));
        e.preventDefault();
        return;
      }

      if (!this.regexPassword(this.result.password)) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Niepoprawne hasło! \n Hasło musi mieć od 8 do 16 znaków oraz zawierać cyfrę i wielką literę!'));
        e.preventDefault();
        return;
      }

      try {
        await UserServices.create(this.result);
      } catch (e) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Email już istnieje!'));
        return;
      }

      this.result = UserServices.getBlankUserTemplate();
      this.chck_email = '';
      this.chck_password = '';

      Swal.fire(
        'Zarejestrowałeś się pomyślnie',
        'Sprawdź swój email aby zweryfikować konto.',
        'success'
      );

      this.$router.push('/login');
    }
  }
});
</script>
