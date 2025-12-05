<template>
  <div>
    <button @click="fetchMe">Load My Info</button>

    <div v-if="user">
      <p>ID: {{ user.id }}</p>
      <p>Username: {{ user.username }}</p>
      <p>Role: {{ user.role }}</p>
    </div>

    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { authApi, type UserInfo } from '../api/auth'

const user = ref<UserInfo | null>(null)
const errorMessage = ref('')

const fetchMe = async () => {
  const jwt = localStorage.getItem('jwt')
  if (!jwt) {
    errorMessage.value = 'Kein JWT vorhanden. Bitte zuerst einloggen.'
    return
  }

  errorMessage.value = ''
  user.value = null

  try {
    const userData = await authApi.me()
    user.value = userData
    console.log('User data:', user.value)

  } catch (error: any) {
    if (error.response && error.response.status === 401) {
      errorMessage.value = 'Nicht autorisiert. Bitte einloggen.'
    } else if (error.response && error.response.status === 403) {
      errorMessage.value = 'Zugriff verweigert.'
    } else if (error.response && error.response.status === 404) {
      errorMessage.value = 'Benutzer nicht gefunden.'
    } else {
      errorMessage.value = 'Fehler beim Abrufen der Daten.'
    }
    console.error('Fetch Me Error:', error)
  }
}
</script>

<style scoped>
button {
  padding: 0.5em;
  cursor: pointer;
  margin-bottom: 1em;
}
</style>
