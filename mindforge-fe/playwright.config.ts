import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/visual',
  outputDir: './test-results',
  snapshotDir: './snapshots',

  use: {
    baseURL: 'http://localhost:3000',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },

  projects: [
    {
      name: 'e2e-tests',
      testDir: './tests',
      testMatch: '**/*.spec.ts',
      use: {
        ...devices['Desktop Chrome'],
        headless: true, // Run in headless mode
      },
    },
    {
      name: 'visual-regression',
      testMatch: '**/*.visual.spec.ts',
      use: {
        ...devices['Desktop Chrome'],
        screenshot: 'on',
      },
    },
  ],
});