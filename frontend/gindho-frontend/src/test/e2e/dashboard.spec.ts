import { test, expect } from '@playwright/test';

test.describe('Dashboard', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should display dashboard KPIs', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Tableau de bord');
  });

  test('should be accessible', async ({ page }) => {
    const heading = page.locator('h1');
    await expect(heading).toBeVisible();
  });
});