<template>
  <div>
    <button @click="fetchMe">Load My Info</button>

    <div v-if="user">
      <p>Username: {{ user.username }}</p>
      <p>Role: {{ user.role }}</p>
    </div>

    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

// Backend-URL aus Vite ENV
const API_URL = import.meta.env.API_URL || 'http://localhost'
const BACKEND_PORT = import.meta.env.BACKEND_PORT || '8080'

const user = ref<{ username: string; role: string } | null>(null)
const errorMessage = ref('')

const fetchMe = async () => {
  if (!jwt.value) {
    errorMessage.value = 'Kein JWT vorhanden. Bitte zuerst einloggen.'
    return
  }

  errorMessage.value = ''
  user.value = null

  try {
    const response = await axios.get('${API_URL}:${BACKEND_PORT}/api/auth/me', {
      headers: {
        Authorization: `Bearer ${jwt.value}`
      }
    })

    user.value = response.data
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
