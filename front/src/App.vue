<template>
  <div id="app" class="flex flex-col h-screen items-center">
    <navigation-bar :key="$route.fullPath"/>
    <router-view/>
  </div>
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
</style>
