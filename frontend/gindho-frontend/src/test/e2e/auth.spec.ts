import { test, expect } from '@playwright/test';

test.describe('Authentication flows', () => {
  test('should redirect to login when not authenticated', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveURL(/\/login/);
  });

  test('should show login page', async ({ page }) => {
    await page.goto('/login');
    await expect(page.locator('h1')).toContainText('Connexion');
  });

  test('should show register page', async ({ page }) => {
    await page.goto('/register');
    await expect(page.locator('h1')).toContainText('Inscription');
  });
});

test.describe('404 Not Found', () => {
  test('should show 404 page for unknown routes', async ({ page }) => {
    await page.goto('/this-route-does-not-exist');
    await expect(page.locator('h1')).toContainText('404');
    await expect(page.locator('text=Page non trouvée')).toBeVisible();
  });
});

test.describe('Accessibility', () => {
  test('should have accessible login form', async ({ page }) => {
    await page.goto('/login');
    const emailInput = page.locator('input[type="email"]');
    const passwordInput = page.locator('input[type="password"]');
    await expect(emailInput).toBeVisible();
    await expect(passwordInput).toBeVisible();
  });
});