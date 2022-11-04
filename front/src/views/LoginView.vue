<template>
  <div class="w-full max-h-max max-w-sm m-auto">
    <div
      class="bg-red-500 border border-red-600 shadow-md rounded px-8 pt-6 pb-8 mb-4"
      style="margin-bottom: auto"
    >
      <div class="mb-4">
        <label class="block text-black  text-base font-bold mb-2" for="email">
          Adres email
        </label>

        <input
          class="shadow appearance-none border-2 border-red-600  rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="email"
          type="text"
          v-model="this.result.email"
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
            border-red-600
            leading-tight
            focus:outline-none focus:shadow-outline
          "
          id="password"
          type="password"
          placeholder="********"
          v-model="this.result.password"
        />
      </div>

      <div class="grid place-items-center md:divide-y-8">
        <button
          @click="login()"
          class="bg-white hover:bg-red-300 text-black border-2 border-red-600 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline
          "
        >
          Zaloguj
        </button>

        <hr />
      </div>
      <p id="err" class="text-center hidden"></p>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';
import LoginServices, { ILogin } from '../services/LoginService';

export default defineComponent({
  data() {
    return {
      result: LoginServices.getBlankLoginTemplate()
    };
  },
  methods: {
    async login(): Promise<void> {
      try {
        if (await LoginServices.login(this.result)) {
          this.$router.push('users');
        }
      } catch (e) {
      }
      document.getElementById('err')?.classList.remove('hidden');
      document.getElementById('err')?.replaceChildren(document.createTextNode('Niepoprawne dane logowania'));
      this.result.email = '';
      this.result.password = '';
    }
  }
});
</script>
