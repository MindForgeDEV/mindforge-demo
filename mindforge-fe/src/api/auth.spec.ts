import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { authApi } from '../api/auth'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Clear localStorage
    localStorage.clear()
  })

  describe('register', () => {
    it('calls register endpoint with correct data', async () => {
      const mockResponse = { data: {} }
      mockedAxios.post.mockResolvedValueOnce(mockResponse)

      const userData = { username: 'testuser', password: 'testpass' }
      await authApi.register(userData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/auth/register', userData)
    })

    it('throws error on registration failure', async () => {
      const error = new Error('Registration failed')
      mockedAxios.post.mockRejectedValueOnce(error)

      const userData = { username: 'testuser', password: 'testpass' }
      await expect(authApi.register(userData)).rejects.toThrow('Registration failed')
    })
  })

  describe('login', () => {
    it('calls login endpoint and stores token in localStorage', async () => {
      const mockResponse = {
        data: { username: 'testuser', token: 'jwt-token-123' }
      }
      mockedAxios.post.mockResolvedValueOnce(mockResponse)

      const credentials = { username: 'testuser', password: 'testpass' }
      const result = await authApi.login(credentials)

      expect(mockedAxios.post).toHaveBeenCalledWith('/auth/login', credentials)
      expect(result).toEqual(mockResponse.data)
      expect(localStorage.getItem('jwt')).toBe('jwt-token-123')
    })
  })

  describe('me', () => {
    it('calls me endpoint with authorization header', async () => {
      localStorage.setItem('jwt', 'stored-jwt-token')
      const mockResponse = {
        data: { id: 1, username: 'testuser', role: 'USER' }
      }
      mockedAxios.get.mockResolvedValueOnce(mockResponse)

      const result = await authApi.me()

      expect(mockedAxios.get).toHaveBeenCalledWith('/auth/me', {
        headers: { Authorization: 'Bearer stored-jwt-token' }
      })
      expect(result).toEqual(mockResponse.data)
    })

    it('throws error when no JWT token in localStorage', async () => {
      const error = new Error('No JWT token')
      mockedAxios.get.mockRejectedValueOnce(error)

      await expect(authApi.me()).rejects.toThrow('No JWT token')
    })
  })
})