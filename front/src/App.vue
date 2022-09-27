<template>
  <navigation-bar/>
  <router-view/>
</template>

<script lang="ts">
import NavigationBar from './components/NavigationBar.vue';

export default {
  name: 'App',
  components: {
    NavigationBar
  }
};

</script>

<style>
@import url("../public/css/tailwind.css");
@import url("../public/css/style.css");

#app {

}
</style>
