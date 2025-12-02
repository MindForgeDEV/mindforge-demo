<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AuthFlow from './components/AuthFlow.vue'

const showSplash = ref(true)

onMounted(() => {
  // Hide splash after animation
  setTimeout(() => {
    showSplash.value = false
  }, 2000)
})
</script>

<template>
  <div id="app">
    <!-- Splash Screen -->
    <div v-if="showSplash" class="splash-screen">
      <div class="splash-content">
        <div class="logo-container">
          <div class="logo-pulse">
            <svg class="mindforge-logo" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="50" cy="50" r="45" stroke="url(#gradient)" stroke-width="2" fill="none"/>
              <circle cx="50" cy="50" r="35" stroke="url(#gradient)" stroke-width="1" fill="none" opacity="0.6"/>
              <circle cx="50" cy="50" r="25" stroke="url(#gradient)" stroke-width="1" fill="none" opacity="0.4"/>
              <circle cx="50" cy="50" r="15" stroke="url(#gradient)" stroke-width="1" fill="none" opacity="0.3"/>
              <defs>
                <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 class="brand-name">MindForge</h1>
          <p class="tagline">AI-Powered Authentication</p>
        </div>
      </div>
    </div>

    <!-- Main Application -->
    <div v-else class="main-app">
      <AuthFlow />
    </div>
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* Splash Screen */
.splash-screen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.5s ease-out;
}

.splash-content {
  text-align: center;
  color: white;
}

.logo-container {
  animation: slideUp 1s ease-out 0.3s both;
}

.logo-pulse {
  margin-bottom: 2rem;
  animation: pulse 2s ease-in-out infinite;
}

.mindforge-logo {
  width: 120px;
  height: 120px;
  filter: drop-shadow(0 0 20px rgba(255, 255, 255, 0.3));
}

.brand-name {
  font-size: 3rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(45deg, #ffffff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: glow 2s ease-in-out infinite alternate;
}

.tagline {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0.5rem 0 0 0;
  letter-spacing: 0.1em;
}

/* Main App */
.main-app {
  min-height: 100vh;
  animation: fadeIn 0.8s ease-out;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

@keyframes glow {
  from {
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
  }
  to {
    text-shadow: 0 0 30px rgba(255, 255, 255, 0.8), 0 0 40px rgba(255, 255, 255, 0.6);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .brand-name {
    font-size: 2.5rem;
  }

  .mindforge-logo {
    width: 100px;
    height: 100px;
  }
}
</style>
