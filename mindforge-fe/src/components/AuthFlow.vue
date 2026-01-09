<template>
  <div class="auth-container">
    <!-- Background Elements -->
    <div class="background-elements">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- Header -->
    <header class="auth-header">
      <div class="brand">
        <svg class="brand-icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="20" cy="20" r="18" stroke="currentColor" stroke-width="2" fill="none"/>
          <circle cx="20" cy="20" r="12" stroke="currentColor" stroke-width="1" fill="none" opacity="0.6"/>
          <circle cx="20" cy="20" r="6" stroke="currentColor" stroke-width="1" fill="none" opacity="0.4"/>
        </svg>
        <span class="brand-text">MindForge</span>
      </div>
      <nav class="auth-nav">
        <button
          @click="setActiveView('register')"
          :class="['nav-btn', { active: activeView === 'register' }]"
        >
          Register
        </button>
        <button
          @click="setActiveView('login')"
          :class="['nav-btn', { active: activeView === 'login' }]"
        >
          Login
        </button>
        <button
          @click="setActiveView('profile')"
          :class="['nav-btn', { active: activeView === 'profile' }]"
          :disabled="!isLoggedIn"
        >
          Profile
        </button>
        <button
          @click="setActiveView('settings')"
          :class="['nav-btn', { active: activeView === 'settings' }]"
          :disabled="!isLoggedIn"
        >
          Settings
        </button>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="auth-main">
      <div class="content-wrapper">
        <!-- Step Indicators -->
        <div class="step-indicators" :class="'progress-' + (activeView === 'settings' ? 'profile' : activeView)">
          <div class="step-line"></div>
          <div class="steps">
            <div class="step" :class="{ active: activeView === 'register', completed: activeView === 'login' || activeView === 'profile' }">
              <div class="step-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M19 8v6m3-3h-6"/>
                </svg>
              </div>
              <span class="step-label">Register</span>
            </div>
            <div class="step" :class="{ active: activeView === 'login', completed: activeView === 'profile' }">
              <div class="step-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                  <polyline points="10,17 15,12 10,7"/>
                  <line x1="15" x2="3" y1="12" y2="12"/>
                </svg>
              </div>
              <span class="step-label">Login</span>
            </div>
             <div class="step" :class="{ active: activeView === 'profile', completed: activeView === 'settings' }">
              <div class="step-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <span class="step-label">Profile</span>
            </div>
            <div class="step" :class="{ active: activeView === 'settings' }">
              <div class="step-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 1v6m0 0l-4-4m4 4l4-4m0 16v6m0-6l4 4m-4-4l-4 4"/>
                  <circle cx="12" cy="9" r="3"/>
                  <path d="M12 15v3"/>
                </svg>
              </div>
              <span class="step-label">Settings</span>
             </div>
           </div>
         </div>

         <!-- Register Form -->
           <div v-if="activeView === 'register'" key="register" class="auth-card">
             <div class="card-header">
               <h2 class="card-title">Create Account</h2>
               <p class="card-subtitle">Join MindForge today</p>
             </div>

             <form @submit.prevent="handleRegister" class="auth-form">
               <div class="form-group">
                 <label class="form-label">Username</label>
                 <div class="input-wrapper">
                   <input
                     v-model="registerForm.username"
                     type="text"
                     class="form-input"
                     placeholder="Choose a username"
                     required
                     :disabled="isLoading"
                   />
                   <div class="input-icon">
                     <svg viewBox="0 0 20 20" fill="currentColor">
                       <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                     </svg>
                   </div>
                 </div>
               </div>

               <div class="form-group">
                 <label class="form-label">Password</label>
                 <div class="input-wrapper">
                   <input
                     v-model="registerForm.password"
                     type="password"
                     class="form-input"
                     placeholder="Create a password"
                     required
                     :disabled="isLoading"
                   />
                   <div class="input-icon">
                     <svg viewBox="0 0 20 20" fill="currentColor">
                       <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                     </svg>
                   </div>
                 </div>
               </div>

               <button type="submit" class="btn-primary" :disabled="isLoading">
                 <span v-if="isLoading" class="btn-spinner"></span>
                 <span v-else>Create Account</span>
               </button>
             </form>

             <!-- Success Message -->
             <transition name="bounce">
               <div v-if="registerSuccess" class="success-message">
                 <div class="success-icon">✓</div>
                 <p>Account created successfully!</p>
                 <button @click="setActiveView('login')" class="link-btn">
                   Proceed to login
                 </button>
               </div>
             </transition>

             <!-- Error Message -->
             <transition name="shake">
               <div v-if="registerError" class="error-message">
                 <div class="error-icon">⚠</div>
                 <p>{{ registerError }}</p>
               </div>
             </transition>
           </div>

           <!-- Login Form -->
           <div v-else-if="activeView === 'login'" key="login" class="auth-card">
            <div class="card-header">
              <h2 class="card-title">Welcome Back</h2>
              <p class="card-subtitle">Sign in to your account</p>
            </div>

            <form @submit.prevent="handleLogin" class="auth-form">
              <div class="form-group">
                <label class="form-label">Username</label>
                <div class="input-wrapper">
                  <input
                    v-model="loginForm.username"
                    type="text"
                    class="form-input"
                    placeholder="Enter your username"
                    required
                    :disabled="isLoading"
                  />
                  <div class="input-icon">
                    <svg viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                    </svg>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Password</label>
                <div class="input-wrapper">
                  <input
                    v-model="loginForm.password"
                    type="password"
                    class="form-input"
                    placeholder="Enter your password"
                    required
                    :disabled="isLoading"
                  />
                  <div class="input-icon">
                    <svg viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                    </svg>
                  </div>
                </div>
              </div>

              <!-- Remember Me Checkbox -->
              <div class="form-group checkbox-group">
                <label class="checkbox-label">
                  <input
                    v-model="loginForm.rememberMe"
                    type="checkbox"
                    class="checkbox-input"
                    :disabled="isLoading"
                  />
                  <span class="checkbox-mark"></span>
                  <span class="checkbox-text">Remember me for 30 days</span>
                </label>
              </div>

              <button type="submit" class="btn-primary" :disabled="isLoading">
                <span v-if="isLoading" class="btn-spinner"></span>
                <span v-else>Sign In</span>
              </button>
            </form>

            <!-- Success Message -->
            <transition name="bounce">
              <div v-if="loginSuccess" class="success-message">
                <div class="success-icon">✓</div>
                <p>Welcome back, {{ loginSuccess.username }}!</p>
                <button @click="setActiveView('profile')" class="link-btn">
                  View your profile
                </button>
              </div>
            </transition>

            <!-- Error Message -->
            <transition name="shake">
              <div v-if="loginError" class="error-message">
                <div class="error-icon">⚠</div>
                <p>{{ loginError }}</p>
              </div>
            </transition>
          </div>

          <!-- Profile View -->
          <div v-else-if="activeView === 'profile'" key="profile" class="auth-card">
            <div class="card-header">
              <h2 class="card-title">Your Profile</h2>
              <p class="card-subtitle">Account information</p>
            </div>

            <div v-if="!userInfo && !profileError" class="profile-loading">
              <button @click="fetchUserInfo" class="btn-secondary" :disabled="isLoading">
                <span v-if="isLoading" class="btn-spinner"></span>
                <span v-else>Load Profile</span>
              </button>
            </div>

            <!-- Loading State -->
            <div v-else-if="profileLoading" class="profile-loading">
              <div class="skeleton skeleton-avatar"></div>
              <div class="skeleton skeleton-text"></div>
              <div class="skeleton skeleton-text short"></div>
              <div class="skeleton skeleton-button"></div>
            </div>

            <!-- User Info -->
            <transition name="slide-up">
              <div v-if="userInfo" class="user-profile">
                <div class="profile-avatar">
                  <div class="avatar-circle">
                    {{ userInfo.username.charAt(0).toUpperCase() }}
                  </div>
                </div>

                <div class="profile-details">
                  <div class="detail-item">
                    <label class="detail-label">User ID</label>
                    <span class="detail-value">#{{ userInfo.id }}</span>
                  </div>

                  <div class="detail-item">
                    <label class="detail-label">Username</label>
                    <span class="detail-value">{{ userInfo.username }}</span>
                  </div>

                  <div class="detail-item">
                    <label class="detail-label">Role</label>
                    <span class="detail-value role-badge" :class="userInfo.role.toLowerCase()">
                      {{ userInfo.role }}
                    </span>
                  </div>
                </div>

                <div class="profile-actions">
                  <button @click="logout" class="btn-outline">
                    Sign Out
                  </button>
                </div>
              </div>
            </transition>

            <!-- Error Message -->
            <transition name="shake">
              <div v-if="profileError" class="error-message">
                <div class="error-icon">⚠</div>
                <p>{{ profileError }}</p>
                <button @click="fetchUserInfo" class="link-btn">
                  Try again
                </button>
              </div>
            </transition>
          </div>

          <!-- Settings View -->
          <div v-else-if="activeView === 'settings'" key="settings" class="auth-card">
            <div class="card-header">
              <h2 class="card-title">Account Settings</h2>
              <p class="card-subtitle">Manage your account preferences</p>
            </div>

            <div class="settings-actions">
              <button @click="setActiveView('profile')" class="btn-secondary">
                ← Back to Profile
              </button>
              <button @click="logout" class="btn-outline">
                Sign Out
              </button>
            </div>
           </div>
       </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, type AuthToken, type UserInfo, type UserRegister } from '../api/auth'

// Router
const router = useRouter()

// Reactive state
const activeView = ref<'register' | 'login' | 'profile' | 'settings'>('register')
const isLoading = ref(false)

// Forms
const registerForm = ref<UserRegister>({
  username: '',
  password: ''
})

const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false
})

// Messages
const registerSuccess = ref<AuthToken | null>(null)
const registerError = ref('')
const loginSuccess = ref<AuthToken | null>(null)
const loginError = ref('')
const profileError = ref('')
const userInfo = ref<UserInfo | null>(null)

// Loading states
const profileLoading = ref(false)





// Computed
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('jwt')
  if (!token) return false

  const expiresAt = localStorage.getItem('jwt_expires')
  if (expiresAt) {
    const expirationTime = parseInt(expiresAt)
    if (Date.now() > expirationTime) {
      // Token expired, remove it
      localStorage.removeItem('jwt')
      localStorage.removeItem('jwt_expires')
      return false
    }
  }

  return true
})





// Methods
const setActiveView = (view: 'register' | 'login' | 'profile' | 'settings') => {
  activeView.value = view
  // Clear messages when switching views
  registerSuccess.value = null
  registerError.value = ''
  loginSuccess.value = null
  loginError.value = ''
  profileError.value = ''
  userInfo.value = null
}

const handleRegister = async () => {
  isLoading.value = true
  registerError.value = ''
  registerSuccess.value = null

  try {
    await authApi.register(registerForm.value)
    registerSuccess.value = { username: registerForm.value.username, token: '' }

    // Reset form
    registerForm.value = { username: '', password: '' }

  } catch (error: any) {
    if (error.response?.data?.error) {
      registerError.value = error.response.data.error
    } else {
      registerError.value = 'Registration failed. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}

const handleLogin = async () => {
  isLoading.value = true
  loginError.value = ''
  loginSuccess.value = null

  try {
    const token = await authApi.login(loginForm.value)
    loginSuccess.value = token

    // Store JWT token with expiration if remember me is checked
    if (loginForm.value.rememberMe) {
      const expiration = Date.now() + (30 * 24 * 60 * 60 * 1000) // 30 days
      localStorage.setItem('jwt', token.token)
      localStorage.setItem('jwt_expires', expiration.toString())
    } else {
      localStorage.setItem('jwt', token.token)
      localStorage.removeItem('jwt_expires') // Remove expiration for session-only
    }

    // Navigate to dashboard
    router.push('/dashboard')

    // Reset form but keep remember me setting
    const rememberMe = loginForm.value.rememberMe
    loginForm.value = { username: '', password: '', rememberMe }

  } catch (error: any) {
    if (error.response?.status === 401) {
      loginError.value = 'Invalid username or password'
    } else {
      loginError.value = 'Login failed. Please try again.'
    }
  } finally {
    isLoading.value = false
  }
}

const fetchUserInfo = async () => {
  profileLoading.value = true
  profileError.value = ''
  userInfo.value = null

  try {
    const data = await authApi.me()
    userInfo.value = data
  } catch (error: any) {
    if (error.response?.status === 401) {
      profileError.value = 'Session expired. Please login again.'
      localStorage.removeItem('jwt')
    } else {
      profileError.value = 'Failed to load profile. Please try again.'
    }
  } finally {
    profileLoading.value = false
  }
}

const logout = () => {
  localStorage.removeItem('jwt')
  userInfo.value = null
  setActiveView('login')
}

// Check if user is already logged in on mount
onMounted(() => {
  if (isLoggedIn.value) {
    setActiveView('profile')
  }
})
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a2e 50%, #16213e 100%);
  position: relative;
  overflow: hidden;
}

/* Background Elements */
.background-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
  animation: float 20s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  animation-delay: 5s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-30px) rotate(120deg);
  }
  66% {
    transform: translateY(20px) rotate(240deg);
  }
}

/* Header */
.auth-header {
  position: relative;
  z-index: 10;
  padding: 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: white;
}

.brand-icon {
  width: 32px;
  height: 32px;
  filter: drop-shadow(0 0 5px rgba(0, 255, 255, 0.5));
}

.brand-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #00ffff;
  text-shadow: 0 0 15px rgba(0, 255, 255, 0.8);
}

.auth-nav {
  display: flex;
  gap: 0.5rem;
}

.nav-btn {
  padding: 0.75rem 1.5rem;
  background: rgba(0, 255, 255, 0.1);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 25px;
  color: #00ffff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.nav-btn:hover:not(:disabled) {
  background: rgba(0, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.nav-btn.active {
  background: rgba(0, 255, 255, 0.2);
  border-color: rgba(0, 255, 255, 0.6);
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.7);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Main Content */
.auth-main {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 120px);
  padding: 2rem;
}

.content-wrapper {
  width: 100%;
  max-width: 400px;
}

/* Auth Card */
.auth-card {
  background: rgba(10, 10, 10, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(0, 255, 255, 0.3);
  border: 1px solid rgba(0, 255, 255, 0.2);
  animation: slideIn 0.5s ease-out;
}

.card-header {
  text-align: center;
  margin-bottom: 2rem;
}

.card-title {
  font-size: 2rem;
  font-weight: 700;
  color: #00ffff;
  margin: 0 0 0.5rem 0;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.card-subtitle {
  color: #ff00ff;
  margin: 0;
  font-size: 1rem;
  text-shadow: 0 0 5px rgba(255, 0, 255, 0.5);
}

/* Form Styles */
.auth-form {
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

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 3rem;
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
  box-shadow: 0 0 0 3px rgba(0, 255, 255, 0.2), 0 0 10px rgba(0, 255, 255, 0.5);
}

.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-input.input-error {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.2);
}

.form-input.input-success {
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
}

.field-error {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  font-weight: 500;
}

.field-success {
  color: #10b981;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  font-weight: 500;
}

/* Password Strength Indicator */
.password-strength {
  margin-top: 0.5rem;
}

.strength-meter {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.strength-bar {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-0 { background: #991b1b; }
.strength-1 { background: #dc2626; }
.strength-2 { background: #d97706; }
.strength-3 { background: #059669; }
.strength-4 { background: #10b981; }

.strength-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Checkbox Styling */
.checkbox-group {
  margin-top: 1rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 0.875rem;
  color: #cccccc;
  user-select: none;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.checkbox-mark {
  position: relative;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(0, 255, 255, 0.3);
  border-radius: 4px;
  margin-right: 0.75rem;
  transition: all 0.3s ease;
  background: rgba(20, 20, 20, 0.8);
}

.checkbox-input:checked + .checkbox-mark {
  background: linear-gradient(135deg, #ff00ff 0%, #00ffff 100%);
  border-color: #00ffff;
}

.checkbox-input:checked + .checkbox-mark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #000000;
  font-size: 12px;
  font-weight: bold;
}

.checkbox-text {
  transition: color 0.3s ease;
}

.checkbox-input:checked ~ .checkbox-text {
  color: #00ffff;
}

.checkbox-input:disabled ~ .checkbox-mark,
.checkbox-input:disabled ~ .checkbox-text {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Skeleton Loading */
.skeleton {
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.1) 25%, rgba(255, 255, 255, 0.2) 50%, rgba(255, 255, 255, 0.1) 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 8px;
}

.skeleton-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 2rem auto;
}

.skeleton-text {
  height: 1rem;
  margin-bottom: 1rem;
  width: 100%;
}

.skeleton-text.short {
  width: 60%;
}

.skeleton-button {
  height: 3rem;
  width: 100%;
  margin-top: 2rem;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.input-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  width: 1.25rem;
  height: 1.25rem;
}

/* Buttons */
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

.btn-secondary {
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover:not(:disabled) {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.btn-outline {
  width: 100%;
  padding: 0.875rem 1.5rem;
  background: transparent;
  color: #dc2626;
  border: 2px solid #dc2626;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-outline:hover {
  background: #dc2626;
  color: white;
  transform: translateY(-2px);
}

.link-btn {
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: underline;
  transition: color 0.3s ease;
}

.link-btn:hover {
  color: #5a67d8;
}

.settings-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-secondary {
  padding: 0.875rem 1.5rem;
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

.btn-outline {
  padding: 0.875rem 1.5rem;
  background: transparent;
  color: #dc2626;
  border: 2px solid #dc2626;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-outline:hover {
  background: #dc2626;
  color: white;
}

/* Loading Spinner */
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

/* Messages */
.success-message, .error-message {
  text-align: center;
  padding: 1.5rem;
  border-radius: 12px;
  margin-top: 1.5rem;
}

.success-message {
  background: rgba(0, 255, 0, 0.1);
  border: 1px solid rgba(0, 255, 0, 0.3);
  color: #00ff00;
  box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
}

.error-message {
  background: rgba(255, 0, 0, 0.1);
  border: 1px solid rgba(255, 0, 0, 0.3);
  color: #ff0000;
  box-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
}

.success-icon, .error-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.success-message p, .error-message p {
  margin: 0 0 1rem 0;
  font-weight: 500;
}

/* Profile Styles */
.profile-loading {
  text-align: center;
  padding: 2rem;
}

.user-profile {
  text-align: center;
}

.profile-avatar {
  margin-bottom: 2rem;
}

.avatar-circle {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ff00ff 0%, #00ffff 100%);
  color: #000000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: 700;
  margin: 0 auto;
  box-shadow: 0 10px 25px rgba(255, 0, 255, 0.5), 0 0 15px rgba(0, 255, 255, 0.5);
}

.profile-details {
  margin-bottom: 2rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 600;
  color: #374151;
}

.detail-value {
  font-weight: 500;
  color: #1f2937;
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
  background: #dbeafe;
  color: #1e40af;
}

.profile-actions {
  margin-top: 2rem;
}

/* Transitions */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.4s ease;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-fade-enter-to,
.slide-fade-leave-from {
  opacity: 1;
  transform: translateX(0);
}

.bounce-enter-active {
  animation: bounce-in 0.6s ease;
}

.bounce-leave-active {
  animation: bounce-in 0.6s ease reverse;
}

@keyframes bounce-in {
  0% {
    opacity: 0;
    transform: scale(0.3);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.shake-enter-active {
  animation: shake 0.5s ease-in-out;
}

.shake-leave-active {
  animation: shake 0.5s ease-in-out reverse;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.5s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.slide-up-enter-to,
.slide-up-leave-from {
  opacity: 1;
  transform: translateY(0);
}

/* Animations */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Step Indicators */
.step-indicators {
  margin-bottom: 3rem;
  position: relative;
}

.step-line {
  position: absolute;
  top: 32px;
  left: 50%;
  right: 50%;
  height: 2px;
  background: rgba(0, 255, 255, 0.2);
  z-index: 1;
  transition: all 0.5s ease;
  box-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.steps {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 2;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  transition: all 0.3s ease;
}

.step-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 255, 255, 0.1);
  border: 2px solid rgba(0, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(0, 255, 255, 0.6);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.step-icon svg {
  width: 24px;
  height: 24px;
}

.step.active .step-icon {
  background: rgba(0, 255, 255, 0.2);
  border-color: rgba(0, 255, 255, 0.8);
  color: #00ffff;
  transform: scale(1.1);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.7);
}

.step.completed .step-icon {
  background: #ff00ff;
  border-color: #ff00ff;
  color: white;
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.7);
}

.step.completed .step-icon svg {
  animation: checkmark 0.6s ease;
}

@keyframes checkmark {
  0% { transform: scale(0); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.step-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: rgba(0, 255, 255, 0.7);
  transition: color 0.3s ease;
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.5);
}

.step.active .step-label {
  color: #00ffff;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.7);
}

.step.completed .step-label {
  color: #ff00ff;
  text-shadow: 0 0 10px rgba(255, 0, 255, 0.7);
}

/* Update step line progress */
.progress-login .step-line,
.progress-profile .step-line {
  left: 16.67%;
  right: 16.67%;
  background: linear-gradient(90deg, #ff00ff 0%, #ff00ff 50%, rgba(0, 255, 255, 0.2) 50%);
  animation: progress-fill 0.5s ease-out;
  box-shadow: 0 0 10px rgba(255, 0, 255, 0.5);
}

.progress-profile .step-line {
  background: #ff00ff;
  animation: progress-fill 0.5s ease-out;
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.7);
}

@keyframes progress-fill {
  from {
    background: rgba(0, 255, 255, 0.2);
  }
  to {
    background: linear-gradient(90deg, #ff00ff 0%, #ff00ff 50%, rgba(0, 255, 255, 0.2) 50%);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .auth-header {
    flex-direction: column;
    gap: 1.5rem;
    padding: 1.5rem;
  }

  .auth-nav {
    width: 100%;
    justify-content: center;
  }

  .nav-btn {
    flex: 1;
    padding: 0.625rem 1rem;
    font-size: 0.875rem;
  }

  .auth-main {
    padding: 1rem;
  }

  .auth-card {
    padding: 2rem;
  }

  .card-title {
    font-size: 1.75rem;
  }

  .step-indicators {
    margin-bottom: 2rem;
  }

  .steps {
    gap: 0.5rem;
  }

  .step-icon {
    width: 56px;
    height: 56px;
  }

  .step-icon svg {
    width: 20px;
    height: 20px;
  }

  .step-label {
    font-size: 0.75rem;
  }
}
</style>