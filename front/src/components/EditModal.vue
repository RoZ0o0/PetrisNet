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
        </slot>
      </section>
    </div>
  </div>
</template>

<script lang="ts">
import UserServices, { IUser } from '@/services/UserService';
import { defineComponent } from 'vue';
import Close from 'vue-material-design-icons/Close.vue';

export default defineComponent({
  components: {
    Close
  },
  name: 'EditModal',
  props: ['user'],
  data() {
    return {
      result: UserServices.getBlankUserTemplate()
    };
  },
  watch: {
    user: function() {
      this.getUser().then((data) => (this.result = data));
    }
  },
  methods: {
    close() {
      this.$emit('close');
    },

    async getUser(): Promise<IUser> {
      return await UserServices.fetchByEmail(this.user);
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
