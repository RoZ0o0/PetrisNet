<template>
  <div class="px-6 py-6 w-full h-full">
    <div class="overflow-auto rounded-xl w-full h-full">
      <div class=" bg-orange-100 h-full p-4">
        <PetriSVG ref='petriSVG'/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import PetriSVG from '../../components/PetriSVG.vue';

export default defineComponent({
  name: 'CreatorView',
  components: {
    PetriSVG
  },

  beforeRouteLeave(to: any, from: any, next: any) {
    if ((this.$refs.petriSVG as any).fetchData() > 0) {
      const answer = window.confirm('Napewno chcesz wyjść? Masz niezapisane zmiany!');
      if (answer) {
        next();
      } else {
        next(false);
      }
    } else {
      next();
    }
  }
});
</script>
