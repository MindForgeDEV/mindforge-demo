<template>
  <form @submit.prevent="register">
    <div>
      <label for="username">Username:</label>
      <input id="username" v-model="username" type="text" required minlength="3" maxlength="50" />
    </div>

    <div>
      <label for="password">Password:</label>
      <input id="password" v-model="password" type="password" required minlength="6" />
    </div>

    <button type="submit">Register</button>

    <p v-if="successMessage" style="color: green;">{{ successMessage }}</p>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { authApi, type UserRegister } from '../api/auth'

const username = ref('')
const password = ref('')

const errorMessage = ref('')
const successMessage = ref('')

const register = async () => {
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const userData: UserRegister = {
      username: username.value,
      password: password.value
    }

    await authApi.register(userData)

    successMessage.value = 'User erfolgreich erstellt!'
    console.log('Registration successful')

    // optional: Felder zurücksetzen
    username.value = ''
    password.value = ''

  } catch (error: any) {
    if (error.response && error.response.data && error.response.data.error) {
      errorMessage.value = error.response.data.error
    } else {
      errorMessage.value = 'Fehler beim Registrieren'
    }
    console.error('Register Error:', error)
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
