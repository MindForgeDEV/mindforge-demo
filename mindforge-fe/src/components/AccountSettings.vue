<template>
  <div class="settings-container">
    <div class="settings-header">
      <h1 class="settings-title">Account Settings</h1>
      <p class="settings-subtitle">Manage your account preferences and security</p>
    </div>

    <div class="settings-content">
      <!-- Profile Information -->
      <div class="settings-section">
        <h2 class="section-title">Profile Information</h2>
        <div class="profile-info">
          <div class="info-item">
            <label class="info-label">Username</label>
            <span class="info-value">{{ userInfo?.username }}</span>
          </div>
          <div class="info-item">
            <label class="info-label">Role</label>
            <span class="info-value role-badge" :class="userInfo?.role.toLowerCase()">
              {{ userInfo?.role }}
            </span>
          </div>
          <div class="info-item">
            <label class="info-label">User ID</label>
            <span class="info-value">#{{ userInfo?.id }}</span>
          </div>
        </div>
      </div>

      <!-- Change Username -->
      <div class="settings-section">
        <h2 class="section-title">Change Username</h2>
        <form @submit.prevent="changeUsername" class="settings-form">
          <div class="form-group">
            <label class="form-label">New Username</label>
            <input
              v-model="usernameForm.newUsername"
              type="text"
              class="form-input"
              placeholder="Enter new username"
              :disabled="isLoading"
              required
              minlength="3"
              maxlength="50"
            />
          </div>
          <button type="submit" class="btn-primary" :disabled="isLoading || !usernameForm.newUsername.trim()">
            <span v-if="isLoading" class="btn-spinner"></span>
            <span v-else>Change Username</span>
          </button>
        </form>

        <!-- Success/Error Messages -->
        <div v-if="usernameSuccess" class="success-message">
          Username changed successfully!
        </div>
        <div v-if="usernameError" class="error-message">
          {{ usernameError }}
        </div>
      </div>

      <!-- Change Password -->
      <div class="settings-section">
        <h2 class="section-title">Change Password</h2>
        <form @submit.prevent="changePassword" class="settings-form">
          <div class="form-group">
            <label class="form-label">Current Password</label>
            <input
              v-model="passwordForm.currentPassword"
              type="password"
              class="form-input"
              placeholder="Enter current password"
              :disabled="isLoading"
              required
            />
          </div>
          <div class="form-group">
            <label class="form-label">New Password</label>
            <input
              v-model="passwordForm.newPassword"
              type="password"
              class="form-input"
              placeholder="Enter new password"
              :disabled="isLoading"
              required
              minlength="6"
            />
          </div>
          <div class="form-group">
            <label class="form-label">Confirm New Password</label>
            <input
              v-model="passwordForm.confirmPassword"
              type="password"
              class="form-input"
              placeholder="Confirm new password"
              :disabled="isLoading"
              required
              minlength="6"
            />
          </div>
          <button type="submit" class="btn-primary" :disabled="isLoading || !isPasswordFormValid">
            <span v-if="isLoading" class="btn-spinner"></span>
            <span v-else>Change Password</span>
          </button>
        </form>

        <!-- Success/Error Messages -->
        <div v-if="passwordSuccess" class="success-message">
          Password changed successfully!
        </div>
        <div v-if="passwordError" class="error-message">
          {{ passwordError }}
        </div>
      </div>

      <!-- Account Actions -->
      <div class="settings-section danger-zone">
        <h2 class="section-title">Danger Zone</h2>
        <div class="danger-actions">
          <button @click="confirmDeleteAccount" class="btn-danger">
            Delete Account
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Account Modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">Delete Account</h3>
        <p class="modal-text">
          Are you sure you want to delete your account? This action cannot be undone.
          All your data will be permanently removed.
        </p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="btn-secondary">
            Cancel
          </button>
          <button @click="deleteAccount" class="btn-danger" :disabled="isLoading">
            <span v-if="isLoading" class="btn-spinner"></span>
            <span v-else>Delete Account</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, type UserInfo } from '../api/auth'

// Reactive state
const router = useRouter()
const isLoading = ref(false)
const userInfo = ref<UserInfo | null>(null)
const showDeleteModal = ref(false)

// Username change form
const usernameForm = ref({
  newUsername: ''
})
const usernameSuccess = ref('')
const usernameError = ref('')

// Password change form
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordSuccess = ref('')
const passwordError = ref('')

// Computed
const isPasswordFormValid = computed(() => {
  return passwordForm.value.currentPassword.trim() &&
         passwordForm.value.newPassword.length >= 6 &&
         passwordForm.value.confirmPassword.length >= 6 &&
         passwordForm.value.newPassword === passwordForm.value.confirmPassword
})

// Methods
const loadUserInfo = async () => {
  try {
    userInfo.value = await authApi.me()
  } catch (error: any) {
    if (error.response?.status === 401) {
      router.push('/login')
    }
  }
}

const changeUsername = async () => {
  if (!usernameForm.value.newUsername.trim()) return

  isLoading.value = true
  usernameError.value = ''
  usernameSuccess.value = ''

  try {
    await authApi.updateProfile({
      username: usernameForm.value.newUsername,
      password: '' // Empty password means don't change it
    })

    usernameSuccess.value = 'Username changed successfully!'
    usernameForm.value.newUsername = ''
    await loadUserInfo() // Refresh user info
  } catch (error: any) {
    if (error.response?.data?.error) {
      usernameError.value = error.response.data.error
    } else {
      usernameError.value = 'Failed to change username. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}

const changePassword = async () => {
  if (!isPasswordFormValid.value) return

  isLoading.value = true
  passwordError.value = ''
  passwordSuccess.value = ''

  try {
    await authApi.updateProfile({
      username: userInfo.value?.username || '',
      password: passwordForm.value.newPassword
    })

    passwordSuccess.value = 'Password changed successfully!'
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error: any) {
    if (error.response?.data?.error) {
      passwordError.value = error.response.data.error
    } else {
      passwordError.value = 'Failed to change password. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}

const confirmDeleteAccount = () => {
  showDeleteModal.value = true
}

const deleteAccount = async () => {
  isLoading.value = true

  try {
    await authApi.deleteUser(userInfo.value?.username || '')
    // Clear JWT token and redirect to login
    localStorage.removeItem('jwt')
    router.push('/login')
  } catch (error: any) {
    showDeleteModal.value = false
    // Handle error - could show a toast notification here
    console.error('Failed to delete account:', error)
  } finally {
    isLoading.value = false
  }
}

// Lifecycle
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.settings-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.settings-header {
  text-align: center;
  margin-bottom: 3rem;
}

.settings-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.5rem 0;
  text-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
}

.settings-subtitle {
  font-size: 1.1rem;
  color: #cccccc;
  margin: 0;
}

.settings-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.settings-section {
  background: rgba(10, 10, 10, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(0, 255, 255, 0.3);
  border: 1px solid rgba(0, 255, 255, 0.2);
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #00ffff;
  margin: 0 0 1.5rem 0;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: rgba(20, 20, 20, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 255, 255, 0.1);
}

.info-label {
  font-weight: 600;
  color: #00ffff;
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.info-value {
  font-weight: 500;
  color: #ffffff;
}

.role-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.role-badge.user {
  background: rgba(0, 255, 0, 0.2);
  color: #00ff00;
  border: 1px solid rgba(0, 255, 0, 0.3);
}

.role-badge.admin {
  background: rgba(255, 0, 255, 0.2);
  color: #ff00ff;
  border: 1px solid rgba(255, 0, 255, 0.3);
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-weight: 600;
  color: #00ffff;
  font-size: 0.875rem;
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid rgba(0, 255, 255, 0.3);
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(20, 20, 20, 0.8);
  color: #ffffff;
}

.form-input:focus {
  outline: none;
  border-color: #00ffff;
  background: rgba(30, 30, 30, 0.9);
  box-shadow: 0 0 0 3px rgba(0, 255, 255, 0.2);
}

.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #ff00ff 0%, #00ffff 100%);
  color: #000000;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 0, 255, 0.5);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 0 20px rgba(255, 0, 255, 0.7), 0 10px 25px rgba(0, 255, 255, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-danger {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #ff0000 0%, #ff4444 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.7);
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background: rgba(100, 100, 100, 0.8);
  color: #ffffff;
  border: 2px solid rgba(150, 150, 150, 0.5);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: rgba(120, 120, 120, 0.9);
  border-color: rgba(180, 180, 180, 0.7);
}

.btn-spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.success-message, .error-message {
  padding: 1rem;
  border-radius: 12px;
  margin-top: 1rem;
  font-weight: 500;
}

.success-message {
  background: rgba(0, 255, 0, 0.1);
  border: 1px solid rgba(0, 255, 0, 0.3);
  color: #00ff00;
}

.error-message {
  background: rgba(255, 0, 0, 0.1);
  border: 1px solid rgba(255, 0, 0, 0.3);
  color: #ff0000;
}

.danger-zone {
  border: 2px solid rgba(255, 0, 0, 0.3);
  background: rgba(20, 0, 0, 0.95);
}

.danger-actions {
  display: flex;
  justify-content: flex-end;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: rgba(10, 10, 10, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 0, 0, 0.3);
  border: 1px solid rgba(255, 0, 0, 0.2);
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ff4444;
  margin: 0 0 1rem 0;
  text-shadow: 0 0 10px rgba(255, 68, 68, 0.5);
}

.modal-text {
  color: #cccccc;
  margin: 0 0 2rem 0;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

/* Responsive */
@media (max-width: 768px) {
  .settings-container {
    padding: 1rem;
  }

  .settings-title {
    font-size: 2rem;
  }

  .settings-section {
    padding: 1.5rem;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .modal-content {
    padding: 1.5rem;
  }

  .modal-actions {
    flex-direction: column;
  }

  .btn-primary, .btn-danger, .btn-secondary {
    width: 100%;
  }
}
</style>