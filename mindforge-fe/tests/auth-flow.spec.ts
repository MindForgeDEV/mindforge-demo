import { test, expect } from '@playwright/test';

test.describe('MindForge E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Set longer timeout for E2E tests
    test.setTimeout(60000);
  });

  test('should load the application', async ({ page }) => {
    await page.goto('http://localhost:3000');
    await expect(page).toHaveTitle(/MindForge/);
  });

  test('should navigate to auth page', async ({ page }) => {
    await page.goto('http://localhost:3000');
    await page.waitForURL('**/auth');
    await expect(page.locator('h1')).toContainText('Login');
  });

  test('should register a new user', async ({ page }) => {
    await page.goto('http://localhost:3000/auth');

    // Switch to register view
    await page.click('text=Register');

    // Fill registration form
    await page.fill('input[placeholder="Username"]', 'e2etestuser');
    await page.fill('input[placeholder="Password"]', 'testpass123');
    await page.fill('input[placeholder="Email"]', 'e2e@test.com');
    await page.fill('input[placeholder="First Name"]', 'E2E');
    await page.fill('input[placeholder="Last Name"]', 'Test');

    // Submit registration
    await page.click('button[type="submit"]');

    // Should navigate to dashboard or show success
    await page.waitForURL('**/dashboard');
    await expect(page.locator('h1')).toContainText('MindForge Dashboard');
  });

  test('should login with existing user', async ({ page }) => {
    await page.goto('http://localhost:3000/auth');

    // Fill login form
    await page.fill('input[placeholder="Username"]', 'testuser1');
    await page.fill('input[placeholder="Password"]', 'mindforge123');

    // Submit login
    await page.click('button[type="submit"]');

    // Should navigate to dashboard
    await page.waitForURL('**/dashboard');
    await expect(page.locator('h1')).toContainText('MindForge Dashboard');
  });

  test('should update user profile', async ({ page }) => {
    // First login
    await page.goto('http://localhost:3000/auth');
    await page.fill('input[placeholder="Username"]', 'testuser1');
    await page.fill('input[placeholder="Password"]', 'mindforge123');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/dashboard');

    // Update profile
    await page.fill('input[id="firstName"]', 'UpdatedFirst');
    await page.fill('input[id="lastName"]', 'UpdatedLast');
    await page.click('button:has-text("Update Profile")');

    // Should show success message
    await expect(page.locator('body')).toContainText('Profile updated successfully');
  });

  test('should logout', async ({ page }) => {
    // Login first
    await page.goto('http://localhost:3000/auth');
    await page.fill('input[placeholder="Username"]', 'testuser1');
    await page.fill('input[placeholder="Password"]', 'mindforge123');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/dashboard');

    // Logout
    await page.click('button:has-text("Logout")');

    // Should navigate back to auth
    await page.waitForURL('**/auth');
    await expect(page.locator('h1')).toContainText('Login');
  });
});