<script setup lang="ts">
import LoginButton from './components/LoginButton.vue'
import RegisterButton from './components/RegisterButton.vue'
import MeButton from './components/MeButton.vue'
</script>

<template>
  <div class='flex'>
    <RegisterButton/>
    <LoginButton/>
    <MeButton/>
  </div>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
