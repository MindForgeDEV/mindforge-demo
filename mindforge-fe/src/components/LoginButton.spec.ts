import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createApp } from 'vue'
import LoginButton from '../components/LoginButton.vue'

// Mock axios
vi.mock('axios', () => ({
  default: {
    post: vi.fn(() => Promise.resolve({ data: { username: 'testuser', token: 'jwt-token' } }))
  }
}))

describe('LoginButton', () => {
  it('renders login form', () => {
    const wrapper = mount(LoginButton)
    expect(wrapper.text()).toContain('Username:')
    expect(wrapper.text()).toContain('Password:')
    expect(wrapper.find('button').text()).toBe('Login')
  })

  it('updates username and password on input', async () => {
    const wrapper = mount(LoginButton)

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')

    await usernameInput.setValue('testuser')
    await passwordInput.setValue('testpass')

    expect(usernameInput.element.value).toBe('testuser')
    expect(passwordInput.element.value).toBe('testpass')
  })

  it('shows success message after login', async () => {
    const wrapper = mount(LoginButton)

    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    const loginButton = wrapper.find('button')

    await usernameInput.setValue('testuser')
    await passwordInput.setValue('testpass')
    await loginButton.trigger('click')

    // Wait for the async operation
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.text()).toContain('Logged in as: testuser')
  })
})