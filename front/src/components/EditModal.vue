<template>
  <div class="modal-backdrop">
    <div class="modal rounded-xl p-4">
      <header class="modal-header">
        <slot name="header">
          Edytuj użytkownika {{ this.result.firstName }}
        </slot>
        <Close @click='close()' />
      </header>
      <section class="modal-body">
        <slot name="body">
          <div class="mb-4 text-right">
            <label class="inline text-black text-base font-bold mb-2" for="firstName">
              Imię:
            </label>

            <input
              class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 pb-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="firstname"
              type="text"
              autocomplete="off"
              v-model='this.result.firstName'
              required
            />
          </div>

          <div class="mb-4 text-right">
            <label class="inline text-black text-base font-bold mb-2" for="lastName">
              Nazwisko:
            </label>

            <input
              class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="lastname"
              type="text"
              autocomplete="off"
              v-model='this.result.lastName'
              required
            />
          </div>

          <div class="mb-4 text-right">
            <label class="inline text-black  text-base font-bold mb-2" for="email">
              Adres email:
            </label>

            <input
              class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="email"
              type="text"
              autocomplete="off"
              v-model='this.result.email'
              required
            />
          </div>

          <div class="mb-8 text-right">
            <label class="inline text-black  text-base font-bold mb-2" for="email">
              Rola:
            </label>

            <select v-model='this.result.role' class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
              <option v-for='item in roles' v-bind:value='item.role' v-bind:key='item.role'>
                {{ item.name }}
              </option>
            </select>
          </div>

          <div class="text-center">
            <button class="shadow appearance-none border-2 border-red-600 bg-red-400  rounded w-3/5 py-2 px-3 text-black leading-tight focus:outline-none focus:shadow-outline" @click='validateEdit()'>
              <span class="inline-block align-middle">Edytuj użytkownika</span>
            </button>
          </div>
        </slot>
      </section>
      <p id="err" class="text-center hidden"></p>
    </div>
  </div>
</template>

<script lang="ts">
import UserServices, { IUser } from '@/services/UserService';
import { defineComponent } from 'vue';
import Close from 'vue-material-design-icons/Close.vue';
import Swal from 'sweetalert2';

export default defineComponent({
  components: {
    Close
  },
  name: 'EditModal',
  props: ['user'],
  data() {
    return {
      result: UserServices.getBlankUserTemplate(),
      roles: [
        { name: 'User', role: 'ROLE_USER' },
        { name: 'Admin', role: 'ROLE_ADMIN' }
      ]
    };
  },
  watch: {
    user() {
      if (this.user !== '') {
        this.getUser().then((data) => (this.result = data));
      }
    }
  },
  methods: {
    close() {
      this.$emit('close');
    },

    async getUser(): Promise<IUser> {
      return await UserServices.fetchByEmail(this.user);
    },

    regexEmail(email: string) {
      const regex = /^[A-Za-z-\\.]+[A-Za-z0-9-\\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      const matches = regex.exec(email);
      return matches;
    },

    async validateEdit(e: Event) {
      if (this.result.firstName.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Imie jest niepoprawne!'));
        return;
      }

      if (this.result.lastName.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Nazwisko jest niepoprawne!'));
        return;
      }

      if (!this.regexEmail(this.result.email)) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Niepoprawny email!'));
        return;
      }

      try {
        await UserServices.update(this.result.id, this.result);
      } catch (e) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Email już istnieje!'));
        return;
      }
      Swal.fire(
        'Gotowe!',
        'Użytkownik został edytowany!',
        'success'
      );
      this.$emit('close');
    }
  }
});
</script>

<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal {
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: auto;
    display: flex;
    flex-direction: column;
  }

  .modal-header {
    padding: 15px;
    display: flex;
  }

  .modal-header {
    position: relative;
    border-bottom: 1px solid #eeeeee;
    color: #ee2d2d;
    justify-content: space-between;
  }

    .modal-body {
    position: relative;
    padding: 20px 10px;
  }

  .btn-close {
    position: absolute;
    top: 0;
    right: 0;
    border: none;
    font-size: 20px;
    padding: 10px;
    cursor: pointer;
    font-weight: bold;
    color: #4AAE9B;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #4AAE9B;
    border: 1px solid #4AAE9B;
    border-radius: 2px;
  }
</style>
