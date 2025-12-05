<template>
  <div role="img" :aria-label="`3D visualization of user ${user?.username || 'profile'}`">
    <TresCanvas shadows alpha>
    <TresPerspectiveCamera :position="[5, 5, 5]" />
    <OrbitControls />
    <TresDirectionalLight :position="[0, 10, 0]" :intensity="1" cast-shadow />
    <TresAmbientLight :intensity="0.5" />

    <!-- User profile sphere with LOD -->
    <TresLOD>
      <TresMesh
        :position="[0, 0, 0]"
        :rotation="[sphereRotation, sphereRotation * 0.5, 0]"
        :scale="sphereHover ? [1.2, 1.2, 1.2] : [1, 1, 1]"
        cast-shadow
        receive-shadow
        @pointer-enter="onSphereEnter"
        @pointer-leave="onSphereLeave"
      >
        <TresSphereGeometry :args="[userSphereSize, 32, 32]" />
        <TresMeshStandardMaterial :color="sphereHover ? 'yellow' : userColor" />
      </TresMesh>
      <TresMesh
        :position="[0, 0, 0]"
        :rotation="[sphereRotation, sphereRotation * 0.5, 0]"
        :scale="sphereHover ? [1.2, 1.2, 1.2] : [1, 1, 1]"
        cast-shadow
        receive-shadow
        @pointer-enter="onSphereEnter"
        @pointer-leave="onSphereLeave"
        :distance="10"
      >
        <TresSphereGeometry :args="[userSphereSize, 16, 16]" />
        <TresMeshStandardMaterial :color="sphereHover ? 'yellow' : userColor" />
      </TresMesh>
      <TresMesh
        :position="[0, 0, 0]"
        :rotation="[sphereRotation, sphereRotation * 0.5, 0]"
        :scale="sphereHover ? [1.2, 1.2, 1.2] : [1, 1, 1]"
        cast-shadow
        receive-shadow
        @pointer-enter="onSphereEnter"
        @pointer-leave="onSphereLeave"
        :distance="20"
      >
        <TresSphereGeometry :args="[userSphereSize, 8, 8]" />
        <TresMeshStandardMaterial :color="sphereHover ? 'yellow' : userColor" />
      </TresMesh>
    </TresLOD>

    <!-- Username text placeholder (basic cube for now) -->
    <TresMesh
      :position="[-2, 0, 0]"
      :rotation="[0, cubeRotation, 0]"
      :scale="cubeHover ? [1.1, 1.1, 1.1] : [1, 1, 1]"
      cast-shadow
      receive-shadow
      @pointer-enter="onCubeEnter"
      @pointer-leave="onCubeLeave"
    >
      <TresBoxGeometry :args="[usernameLength * 0.1, 0.2, 0.1]" />
      <TresMeshStandardMaterial :color="cubeHover ? 'lightblue' : 'white'" />
    </TresMesh>

    <!-- Role indicator -->
    <TresMesh
      :position="[2, 0, 0]"
      :scale="[roleScale * (roleHover ? 1.3 : 1), roleScale * (roleHover ? 1.3 : 1), roleScale * (roleHover ? 1.3 : 1)]"
      cast-shadow
      receive-shadow
      @pointer-enter="onRoleEnter"
      @pointer-leave="onRoleLeave"
    >
      <TresBoxGeometry :args="[0.5, roleHeight, 0.5]" />
      <TresMeshStandardMaterial :color="roleHover ? 'lightgreen' : 'green'" />
      </TresMesh>
    </TresLOD>
  </TresCanvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRenderLoop } from '@tresjs/core'
import { TresCanvas, TresPerspectiveCamera, TresDirectionalLight, TresAmbientLight, TresMesh, TresBoxGeometry, TresMeshStandardMaterial, TresSphereGeometry, TresLOD } from '@tresjs/core'
import { OrbitControls } from '@tresjs/cientos'
import { authApi, type UserInfo } from '@/api/auth'

const user = ref<UserInfo | null>(null)
const userSphereSize = ref(0.5)
const userColor = ref('blue')
const usernameLength = ref(5)
const roleHeight = ref(1)

const sphereRotation = ref(0)
const cubeRotation = ref(0)
const roleScale = ref(1)

const sphereHover = ref(false)
const cubeHover = ref(false)
const roleHover = ref(false)

const prefersReducedMotion = ref(false)

const { onLoop } = useRenderLoop()

// Animation loop
onLoop(() => {
  if (!prefersReducedMotion.value) {
    sphereRotation.value += 0.01 * (user.value?.username.length || 1) * 0.1
    cubeRotation.value += 0.005
    roleScale.value = 1 + Math.sin(Date.now() * 0.001) * 0.1
  }
})

const onSphereEnter = () => { sphereHover.value = true }
const onSphereLeave = () => { sphereHover.value = false }
const onCubeEnter = () => { cubeHover.value = true }
const onCubeLeave = () => { cubeHover.value = false }
const onRoleEnter = () => { roleHover.value = true }
const onRoleLeave = () => { roleHover.value = false }

const loadUser = async () => {
  try {
    user.value = await authApi.me()
    if (user.value) {
      // Map user data to 3D properties
      usernameLength.value = user.value.username.length
      userSphereSize.value = Math.max(0.3, Math.min(1.5, user.value.username.length * 0.1))
      userColor.value = user.value.role === 'ADMIN' ? 'red' : user.value.role === 'USER' ? 'blue' : 'green'
      roleHeight.value = user.value.role === 'ADMIN' ? 2 : 1
    }
  } catch (error) {
    console.error('Failed to load user for 3D scene:', error)
  }
}

onMounted(() => {
  loadUser()
  prefersReducedMotion.value = window.matchMedia('(prefers-reduced-motion: reduce)').matches
})
</script>

<style scoped>
canvas {
  width: 100%;
  height: 100vh;
}
</style>