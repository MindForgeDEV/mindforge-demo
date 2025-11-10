<template>
  <div>
    <button @click="login">Get JWT</button>
    <p v-if="jwt">JWT: {{ jwt }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const jwt = ref(null)

const login = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: 'testuser',
      password: 'testpass'
    })
    
    jwt.value = response.data.token  // je nach Backend: response.data.jwt oder response.data.token
    console.log('JWT received:', jwt.value)
  } catch (error) {
    console.error('Error fetching JWT:', error)
  }
}
</script>
