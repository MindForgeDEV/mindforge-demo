<template>
  <form @submit.prevent="login">
    <div>
      <label for="username">Username:</label>
      <input id="username" v-model="username" type="text" required />
    </div>

    <div>
      <label for="password">Password:</label>
      <input id="password" v-model="password" type="password" required />
    </div>

    <button type="submit">Login</button>

    <p v-if="jwt" style="color: green;">JWT: {{ jwt }}</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const API_URL = import.meta.env.API_URL || 'http://localhost'
const BACKEND_PORT = import.meta.env.BACKEND_PORT || '8080'

const username = ref('')
const password = ref('')

const jwt = ref<string | null>(null)
const errorMessage = ref('')

const login = async () => {
  errorMessage.value = ''
  jwt.value = null

  try {
    const response = await axios.post(`${API_URL}:${BACKEND_PORT}/auth/login`, {
      username: username.value,
      password: password.value
    })

    // JWT aus Response speichern
    jwt.value = response.data.token // oder response.data.jwt je nach Backend
    console.log('JWT received:', jwt.value)

    // optional: Felder zurücksetzen
    username.value = ''
    password.value = ''

  } catch (error: any) {
    if (error.response && error.response.status === 401) {
      errorMessage.value = 'Ungültiger Benutzername oder Passwort'
    } else {
      errorMessage.value = 'Fehler beim Login'
    }
    console.error('Login Error:', error)
  }
}
</script>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  max-width: 300px;
}

div {
  margin-bottom: 1em;
}

button {
  padding: 0.5em;
  cursor: pointer;
}
</style>
