<template>
  <div class="modal-backdrop">
    <div class="modal rounded-xl p-4 w-3/12">
      <header class="modal-header">
        <slot name="header">
          Edytuj sieć
        </slot>
        <Close @click='close()' />
      </header>
      <section class="modal-body">
        <slot name="body">
          <div class="mb-4 text-right">
            <label class="inline text-black text-base font-bold mb-2" for="user">
              Użytkownik:
            </label>

            <select v-model='this.resultSaveNet.userId' class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
              <option v-for='user in resultUser' v-bind:value='user.id' v-bind:key='user.id'>
                {{ user.firstName }} {{ user.lastName }} {{ user.email }}
              </option>
            </select>
          </div>

          <div class="mb-4 text-right">
            <label class="inline text-black text-base font-bold mb-2" for="name">
              Nazwa:
            </label>

            <input
              class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="name"
              type="text"
              autocomplete="off"
              v-model='this.resultSaveNet.saveName'
              required
            />
          </div>

          <div class="mb-8 text-right">
            <label class="inline text-black  text-base font-bold mb-2" for="isPublic">
              Publiczne:
            </label>

            <select v-model='this.resultSaveNet.public' class="shadow appearance-none border-2 border-red-600  rounded w-3/5 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
              <option v-for='item in isPublic' v-bind:value='item.check' v-bind:key='item.check'>
                {{ item.name }}
              </option>
            </select>
          </div>

          <div class="text-center">
            <button class="shadow appearance-none border-2 border-red-600 bg-red-400  rounded w-3/5 py-2 px-3 text-black leading-tight focus:outline-none focus:shadow-outline" @click='validateEdit()'>
              <span class="inline-block align-middle">Edytuj sieć</span>
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
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';

export default defineComponent({
  components: {
    Close
  },
  name: 'EditSavedNets',
  props: ['net'],
  data() {
    return {
      resultUser: [UserServices.getBlankUserTemplate()],
      resultSaveNet: SaveNetServices.getBlankSaveNetTemplate(),
      isPublic: [
        { check: true, name: 'Tak' },
        { check: false, name: 'Nie' }
      ]
    };
  },
  mounted() {
    this.getUser().then((data) => (this.resultUser = data));
  },

  watch: {
    net() {
      if (this.net !== 0) {
        this.getSaveNet().then((data) => (this.resultSaveNet = data));
      }
    }
  },

  methods: {
    close() {
      document.getElementById('err')?.classList.add('hidden');
      this.$emit('close');
    },

    async getUser(): Promise<Array<IUser>> {
      return await UserServices.fetch();
    },

    async getSaveNet(): Promise<ISaveNet> {
      return await SaveNetServices.find(this.net);
    },

    async validateEdit(e: Event) {
      if (this.resultSaveNet.userId === 0) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Niewłaściwy użytkownik!'));
        return;
      }

      if (this.resultSaveNet.saveName.length < 3) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Podana nazwa jest za krótka'));
        return;
      }

      if (this.resultSaveNet.saveName.length > 20) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Podana nazwa jest za długa!'));
        return;
      }

      try {
        await SaveNetServices.update(this.resultSaveNet, this.resultSaveNet.id);
      } catch (e) {
        document.getElementById('err')?.classList.remove('hidden');
        document.getElementById('err')?.replaceChildren(document.createTextNode('Już istnieje taka nazwa sieci dla tego użytkownika!'));
        return;
      }
      Swal.fire(
        'Gotowe!',
        'Sieć została edytowana!',
        'success'
      );
      document.getElementById('err')?.classList.add('hidden');
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
