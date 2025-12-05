import { test, expect } from '@playwright/test';

test.describe('Auth Flow Visual Regression', () => {
  test('login form should match visual baseline', async ({ page }) => {
    await page.goto('/');

    // Wait for the auth flow to load
    await page.waitForSelector('.auth-card');

    // Take a screenshot of the login form
    await expect(page.locator('.auth-card')).toHaveScreenshot('login-form.png');
  });

  test('register form should match visual baseline', async ({ page }) => {
    await page.goto('/');

    // Click register button
    await page.click('text=Register');

    // Wait for register form
    await page.waitForSelector('.auth-card');

    // Take a screenshot of the register form
    await expect(page.locator('.auth-card')).toHaveScreenshot('register-form.png');
  });

  test('successful registration should match visual baseline', async ({ page }) => {
    await page.goto('/');

    // Click register button
    await page.click('text=Register');

    // Fill registration form
    await page.fill('input[placeholder="Choose a username"]', 'visualtestuser');
    await page.fill('input[placeholder="Create a password"]', 'testpass123');

    // Submit form
    await page.click('button:has-text("Create Account")');

    // Wait for success message
    await page.waitForSelector('.success-message');

    // Take screenshot of success state
    await expect(page.locator('.auth-card')).toHaveScreenshot('registration-success.png');
  });
});