<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <h1>Mindforge Dashboard</h1>
      <div class="user-info">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="avatar" />
        <div class="user-details">
          <p>Welcome, {{ user.firstName || user.username }}!</p>
          <p>Role: {{ user.role }}</p>
        </div>
        <button @click="logout" class="logout-btn">Logout</button>
      </div>
    </header>

    <main class="dashboard-content">
      <section class="profile-section">
        <h2>User Profile</h2>
        <form @submit.prevent="updateProfile" class="profile-form">
          <div class="form-group">
            <label for="email">Email:</label>
            <input v-model="profileForm.email" type="email" id="email" />
          </div>
          <div class="form-group">
            <label for="firstName">First Name:</label>
            <input v-model="profileForm.firstName" type="text" id="firstName" />
          </div>
          <div class="form-group">
            <label for="lastName">Last Name:</label>
            <input v-model="profileForm.lastName" type="text" id="lastName" />
          </div>
          <div class="form-group">
            <label for="avatarUrl">Avatar URL:</label>
            <input v-model="profileForm.avatarUrl" type="url" id="avatarUrl" />
          </div>
          <button type="submit" :disabled="updating" class="update-btn">
            {{ updating ? 'Updating...' : 'Update Profile' }}
          </button>
        </form>
      </section>

       <section class="data-section">
         <h2>Data Overview</h2>
         <p>This is where you would display lists of projects, notes, or other data.</p>
         <button @click="view3DScene" class="scene-btn">View 3D Scene</button>
         <!-- Placeholder for future data lists -->
       </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, type UserInfo } from '@/api/auth'

const router = useRouter()
const user = ref<UserInfo | null>(null)
const profileForm = ref({
  email: '',
  firstName: '',
  lastName: '',
  avatarUrl: ''
})
const updating = ref(false)

const loadUser = async () => {
  try {
    user.value = await authApi.me()
    profileForm.value = {
      email: user.value.email || '',
      firstName: user.value.firstName || '',
      lastName: user.value.lastName || '',
      avatarUrl: user.value.avatarUrl || ''
    }
  } catch (error) {
    console.error('Failed to load user:', error)
    router.push('/login')
  }
}

const updateProfile = async () => {
  if (!user.value) return

  updating.value = true
  try {
    const updatedUser = await authApi.updateProfile({
      username: user.value.username,
      password: '', // Not updating password here
      email: profileForm.value.email,
      firstName: profileForm.value.firstName,
      lastName: profileForm.value.lastName,
      avatarUrl: profileForm.value.avatarUrl
    })
    user.value = updatedUser
    alert('Profile updated successfully!')
  } catch (error) {
    console.error('Failed to update profile:', error)
    alert('Failed to update profile')
  } finally {
    updating.value = false
  }
}

const view3DScene = () => {
  router.push('/scene')
}

const logout = () => {
  localStorage.removeItem('jwt')
  localStorage.removeItem('refreshToken')
  router.push('/login')
}

onMounted(() => {
  loadUser()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.dashboard-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.user-details p {
  margin: 0;
  font-size: 0.9rem;
}

.logout-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.dashboard-content {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-section, .data-section {
  background: white;
  padding: 2rem;
  margin-bottom: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.profile-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.update-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-top: 1rem;
}

.update-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.scene-btn {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-top: 1rem;
  transition: transform 0.2s;
}

.scene-btn:hover {
  transform: scale(1.05);
}
</style>