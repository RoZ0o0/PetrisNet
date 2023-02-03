<template>
  <div class="modal-backdrop">
    <div class="modal rounded-xl p-4 w-1/4">
      <div class="flex justify-end align-top">
        <Close @click='close()' />
      </div>
      <div class='flex flex-col relative'>
        <span class='text-xl'>Link do modelu!</span>
        <div class='my-6 flex flex-row align-middle justify-center rounded-xl'>
          <input type='test' class='bg-gray-300 text-xl p-2 pl-4 pr-0 rounded-l-xl w-full' :value='this.result' ref='reflink' disabled>
          <ContentCopyIcon class='self-center bg-gray-300 hover:text-red-300 pl-4 pr-2 py-3 rounded-r-xl' :size='this.iconSize' @click='copyContent()'/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import SaveNetServices from '@/services/SaveNetService';
import UserServices, { IUser } from '@/services/UserService';
import { defineComponent } from 'vue';

import Close from 'vue-material-design-icons/Close.vue';
import ContentCopyIcon from 'vue-material-design-icons/ContentCopy.vue';

export default defineComponent({
  components: {
    Close,
    ContentCopyIcon
  },
  name: 'RefLinkModal',
  props: ['net'],
  data() {
    return {
      result: '',
      iconSize: 30
    };
  },
  watch: {
    net() {
      if (this.net !== 0) {
        this.getRefLink().then((data) => (this.result = 'http://localhost:8080/creator?ref=' + data));
      }
    }
  },

  methods: {
    async getRefLink(): Promise<string> {
      return await SaveNetServices.createRefLink(this.net);
    },

    close() {
      this.$emit('close');
    },

    copyContent() {
      const copyText = (this.$refs.reflink as any).value;

      navigator.clipboard.writeText(copyText);
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
</style>
