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

    <p v-if="authToken" style="color: green;">Logged in as: {{ authToken.username }}</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { authApi, type AuthToken } from '../api/auth'

const username = ref('')
const password = ref('')

const authToken = ref<AuthToken | null>(null)
const errorMessage = ref('')

const login = async () => {
  errorMessage.value = ''
  authToken.value = null

  try {
    const token = await authApi.login({
      username: username.value,
      password: password.value
    })

    authToken.value = token
    // Store JWT token in localStorage for persistence
    localStorage.setItem('jwt', token.token)
    console.log('Login successful:', token)

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
