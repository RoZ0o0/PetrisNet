<template>
  <div class='border-2 border-slate-400 text-center mb-2 p-1 rounded-xl bg-slate-500' v-if='calculatePages(this.size) < 10'>
    <span class='bg-white p-1 rounded-md font-bold select-none' @click='goLeft()'> &lt; </span>
    <ul v-for='(n, i) in calculatePages(this.size)' :key='i' class='inline-block'>
      <li v-if='i == this.selected' class='text-lg mx-2 px-1 bg-slate-200 rounded-md text-red-600 select-none'> {{ n }} </li>
      <li v-else @click='this.selected = i' class='px-2 select-none'> {{ n }} </li>
    </ul>
    <span class='bg-white p-1 rounded-md font-bold select-none' @click='goRight()'> &gt; </span>

    <select @change="changePageSize($event)" v-model="this.pageSize" class='float-right'>
      <option value=5 selected>5</option>
      <option value=10>10</option>
      <option value=15>15</option>
    </select>
  </div>
  <div class='border-2 border-slate-400 text-center mb-2 p-1 rounded-xl bg-slate-500' v-else>
    <span class='bg-white p-1 rounded-md font-bold select-none' @click='goLeft()'> &lt; </span>
    <ul v-for='(n, i) in calculatePages(this.size)' :key='i' class='inline-block'>
      <li v-if='i == this.selected && (i == 0 || i == 1)' class='text-lg mx-2 px-1 bg-slate-200 rounded-md text-red-600 select-none'> {{ n }} </li>
      <li v-else-if='i == this.selected && (i != 0 && i != 1 && i != (calculatePages(this.size) - 1))' class='text-lg mx-2 px-1 bg-slate-200 rounded-md text-red-600 select-none'> {{ n }} </li>
      <li v-else-if='i == 0 || i == 1' @click='this.selected = i' class='px-2 select-none'> {{ n }} </li>
    </ul>
    <span v-if='this.selected == 0 || this.selected == 1 || this.selected == (calculatePages(this.size) - 1)' class='px-2 select-none'>...</span>
    <ul class='inline-block'>
      <li v-if='this.selected == calculatePages(this.size) - 1' class='text-lg mx-2 px-1 bg-slate-200 rounded-md text-red-600 select-none'> {{ calculatePages(this.size) }} </li>
      <li v-else class='px-2 select-none' @click='this.selected = calculatePages(this.size) - 1'> {{ calculatePages(this.size) }} </li>
    </ul>
    <span class='bg-white p-1 rounded-md font-bold select-none' @click='goRight()'> &gt; </span>

    <select @change="changePageSize($event)" v-model='this.pageSize' class='float-right'>
      <option value=5>5</option>
      <option value=10>10</option>
      <option value=15>15</option>
    </select>
  </div>
</template>

<script lang="ts">
import { Vue } from 'vue-class-component';
import { defineComponent } from 'vue';

export default defineComponent({
  data() {
    return {
      pageSize: 5,
      selected: 0
    };
  },

  watch: {
    size() {
      if (this.size <= this.selected * this.pageSize) {
        if (this.selected !== 0) {
          this.selected--;
        }
      }
    }
  },

  props: ['size'],
  methods: {
    calculatePages(size: number) {
      let pages = 0;
      while (size > 0) {
        size -= this.pageSize;
        pages++;
      }

      return pages;
    },

    changePageSize(event: any) {
      this.pageSize = event.target.value;
      this.selected = 0;
    },

    goLeft() {
      if (this.selected !== 0) {
        this.selected--;
      }
    },

    goRight() {
      if (this.selected !== this.calculatePages(this.size) - 1) {
        this.selected++;
      }
    }
  }
});
</script>
