import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('Accessibility Tests', () => {
  test('auth flow should be accessible', async ({ page }) => {
    await page.goto('/');

    // Wait for the page to load
    await page.waitForSelector('.auth-card');

    // Run accessibility scan
    const accessibilityScanResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();

    // Check for critical accessibility violations
    const criticalViolations = accessibilityScanResults.violations.filter(
      violation => violation.impact === 'critical' || violation.impact === 'serious'
    );

    // Log violations for debugging
    if (criticalViolations.length > 0) {
      console.log('Accessibility violations found:');
      criticalViolations.forEach(violation => {
        console.log(`- ${violation.id}: ${violation.description}`);
        console.log(`  Impact: ${violation.impact}`);
        console.log(`  Help: ${violation.helpUrl}`);
      });
    }

    // Assert no critical accessibility violations
    expect(criticalViolations).toHaveLength(0);
  });

  test('register form should be accessible', async ({ page }) => {
    await page.goto('/');

    // Click register button
    await page.click('text=Register');

    // Wait for register form
    await page.waitForSelector('.auth-card');

    // Run accessibility scan on the form
    const accessibilityScanResults = await new AxeBuilder({ page })
      .include('.auth-card')
      .withTags(['wcag2a', 'wcag2aa'])
      .analyze();

    // Check for violations
    const violations = accessibilityScanResults.violations.filter(
      violation => violation.impact === 'critical' || violation.impact === 'serious'
    );

    expect(violations).toHaveLength(0);
  });

  test('login form should be accessible', async ({ page }) => {
    await page.goto('/');

    // Run accessibility scan on the login form
    const accessibilityScanResults = await new AxeBuilder({ page })
      .include('.auth-card')
      .withTags(['wcag2a', 'wcag2aa'])
      .analyze();

    // Check for violations
    const violations = accessibilityScanResults.violations.filter(
      violation => violation.impact === 'critical' || violation.impact === 'serious'
    );

    expect(violations).toHaveLength(0);
  });

  test('keyboard navigation should work', async ({ page }) => {
    await page.goto('/');

    // Test tab navigation through form elements
    await page.keyboard.press('Tab');
    await page.keyboard.press('Tab');

    // Check if focus is on the first input field
    const activeElement = await page.evaluate(() => document.activeElement?.tagName);
    expect(activeElement).toBe('INPUT');
  });
});