<template>
  <div class="admin-dashboard">
    <header class="dashboard-header">
      <h1>Admin Dashboard</h1>
      <div v-if="user" class="user-info">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="avatar" />
        <div class="user-details">
          <p>{{ user.firstName || user.username }} (Admin)</p>
          <p>Role: {{ user.role }}</p>
        </div>
        <button @click="goToUserDashboard" class="dashboard-btn">User Dashboard</button>
        <button @click="logout" class="logout-btn">Logout</button>
      </div>
    </header>

    <main class="dashboard-content">
      <section class="users-section">
        <h2>User Management</h2>
        <div class="search-bar">
          <input
            v-model="searchTerm"
            type="text"
            placeholder="Search users by username..."
            class="search-input"
            @input="filterUsers"
          />
        </div>

        <div v-if="loading" class="loading">Loading users...</div>

        <div v-else-if="filteredUsers.length === 0" class="no-users">
          No users found
        </div>

        <div v-else class="users-table-container">
          <table class="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.id" :class="{ 'locked-user': user.accountLocked }">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.email || '-' }}</td>
                <td>
                  <select
                    :value="user.role"
                    @change="updateUserRole(user.id, $event.target.value)"
                    class="role-select"
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td>
                  <span :class="user.accountLocked ? 'status-locked' : 'status-active'">
                    {{ user.accountLocked ? 'Locked' : 'Active' }}
                  </span>
                  <span v-if="user.failedLoginAttempts > 0" class="attempts">
                    ({{ user.failedLoginAttempts }} attempts)
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td class="actions">
                  <button
                    v-if="user.accountLocked"
                    @click="unlockUser(user.id)"
                    class="unlock-btn"
                    title="Unlock account"
                  >
                    Unlock
                  </button>
                  <button
                    v-else
                    @click="lockUser(user.id)"
                    class="lock-btn"
                    title="Lock account"
                  >
                    Lock
                  </button>
                  <button
                    v-if="user.role !== 'ADMIN'"
                    @click="deleteUser(user.id)"
                    class="delete-btn"
                    title="Delete user"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, type UserInfo, type AdminUserInfo } from '@/api/auth'

const router = useRouter()
const user = ref<UserInfo | null>(null)
const users = ref<AdminUserInfo[]>([])
const searchTerm = ref('')
const loading = ref(false)

const filteredUsers = computed(() => {
  if (!searchTerm.value) return users.value
  return users.value.filter(u =>
    u.username.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
})

const loadUser = async () => {
  try {
    user.value = await authApi.me()
    if (user.value.role !== 'ADMIN') {
      router.push('/dashboard')
      return
    }
  } catch (error) {
    console.error('Failed to load user:', error)
    router.push('/login')
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    users.value = await authApi.getAllUsers()
  } catch (error) {
    console.error('Failed to load users:', error)
    alert('Failed to load users')
  } finally {
    loading.value = false
  }
}

const updateUserRole = async (userId: number, newRole: string) => {
  try {
    const updatedUser = await authApi.updateUserRole(userId, newRole)
    const index = users.value.findIndex(u => u.id === userId)
    if (index !== -1) {
      users.value[index] = updatedUser
    }
    alert('User role updated successfully')
  } catch (error) {
    console.error('Failed to update user role:', error)
    alert('Failed to update user role')
  }
}

const lockUser = async (userId: number) => {
  try {
    const updatedUser = await authApi.lockUserAccount(userId)
    const index = users.value.findIndex(u => u.id === userId)
    if (index !== -1) {
      users.value[index] = updatedUser
    }
    alert('User account locked')
  } catch (error) {
    console.error('Failed to lock user account:', error)
    alert('Failed to lock user account')
  }
}

const unlockUser = async (userId: number) => {
  try {
    const updatedUser = await authApi.unlockUserAccount(userId)
    const index = users.value.findIndex(u => u.id === userId)
    if (index !== -1) {
      users.value[index] = updatedUser
    }
    alert('User account unlocked')
  } catch (error) {
    console.error('Failed to unlock user account:', error)
    alert('Failed to unlock user account')
  }
}

const deleteUser = async (userId: number) => {
  if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
    return
  }

  try {
    await authApi.adminDeleteUser(userId)
    users.value = users.value.filter(u => u.id !== userId)
    alert('User deleted successfully')
  } catch (error) {
    console.error('Failed to delete user:', error)
    alert('Failed to delete user')
  }
}

const filterUsers = () => {
  // Computed property will handle filtering
}

const formatDate = (dateString?: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString()
}

const goToUserDashboard = () => {
  router.push('/dashboard')
}

const logout = () => {
  localStorage.removeItem('jwt')
  localStorage.removeItem('refreshToken')
  router.push('/login')
}

onMounted(async () => {
  await loadUser()
  await loadUsers()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.dashboard-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1.5rem 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-header h1 {
  margin: 0;
  color: #333;
  font-size: 2rem;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: 2px solid #667eea;
}

.user-details p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.dashboard-btn, .logout-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.dashboard-btn {
  background: #667eea;
  color: white;
}

.dashboard-btn:hover {
  background: #5a6fd8;
}

.logout-btn {
  background: #dc3545;
  color: white;
}

.logout-btn:hover {
  background: #c82333;
}

.dashboard-content {
  max-width: 1400px;
  margin: 0 auto;
}

.users-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.users-section h2 {
  margin-top: 0;
  color: #333;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.search-bar {
  margin-bottom: 1.5rem;
}

.search-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.loading, .no-users {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-size: 1.1rem;
}

.users-table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.users-table th,
.users-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e1e5e9;
}

.users-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.users-table tr:hover {
  background: #f8f9fa;
}

.locked-user {
  background: #fff5f5 !important;
}

.role-select {
  padding: 0.25rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  font-size: 0.9rem;
}

.status-active {
  color: #28a745;
  font-weight: 500;
}

.status-locked {
  color: #dc3545;
  font-weight: 500;
}

.attempts {
  font-size: 0.8rem;
  color: #666;
  margin-left: 0.5rem;
}

.actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.unlock-btn, .lock-btn, .delete-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.unlock-btn {
  background: #28a745;
  color: white;
}

.unlock-btn:hover {
  background: #218838;
}

.lock-btn {
  background: #ffc107;
  color: #212529;
}

.lock-btn:hover {
  background: #e0a800;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.delete-btn:hover {
  background: #c82333;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .users-table {
    font-size: 0.9rem;
  }

  .users-table th,
  .users-table td {
    padding: 0.5rem;
  }

  .actions {
    flex-direction: column;
    gap: 0.25rem;
  }
}
</style>