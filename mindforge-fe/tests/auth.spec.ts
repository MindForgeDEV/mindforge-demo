import { describe, it, expect, vi, beforeEach } from 'vitest'
import { authApi } from '@/api/auth'

// Mock axios at module level
vi.mock('axios', async () => {
  const mockAxios = {
    post: vi.fn(),
    get: vi.fn(),
    create: vi.fn(() => mockAxios),
    interceptors: {
      request: { use: vi.fn() }
    }
  }
  return {
    default: mockAxios
  }
})

// Import after mocking
import axios from 'axios'

const mockAxios = vi.mocked(axios)

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Clear localStorage
    localStorage.clear()
  })

  describe('register', () => {
    it('calls register endpoint with correct data', async () => {
      const mockResponse = { data: {} }
      mockAxios.post.mockResolvedValueOnce(mockResponse)

      const userData = { username: 'testuser', password: 'testpass' }
      await authApi.register(userData)

      expect(mockAxios.post).toHaveBeenCalledWith('/auth/register', userData)
    })

    it('throws error on registration failure', async () => {
      const error = new Error('Registration failed')
      mockAxios.post.mockRejectedValueOnce(error)

      const userData = { username: 'testuser', password: 'testpass' }
      await expect(authApi.register(userData)).rejects.toThrow('Registration failed')
    })
  })

  describe('login', () => {
    it('calls login endpoint with correct data', async () => {
      const mockResponse = {
        data: { username: 'testuser', token: 'jwt-token-123' }
      }
      mockAxios.post.mockResolvedValueOnce(mockResponse)

      const credentials = { username: 'testuser', password: 'testpass' }
      const result = await authApi.login(credentials)

      expect(mockAxios.post).toHaveBeenCalledWith('/auth/login', credentials)
      expect(result).toEqual(mockResponse.data)
    })
  })


})