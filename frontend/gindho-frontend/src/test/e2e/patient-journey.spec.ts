import { test, expect } from '@playwright/test';

test.describe('Patient Journey', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/login');
    await page.fill('input[type="email"]', 'test@gindho.com');
    await page.fill('input[type="password"]', 'password123');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/');
  });

  test('should navigate to patients page', async ({ page }) => {
    await page.click('text=Patients');
    await expect(page).toHaveURL('/patients');
    await expect(page.locator('h1')).toContainText('Patients');
  });

  test('should search for a patient', async ({ page }) => {
    await page.click('text=Patients');
    await page.fill('input[placeholder*="Rechercher"]', 'Dupont');
    await page.waitForResponse('/api/patients/**');
    await expect(page.locator('text=Dupont')).toBeVisible();
  });

  test('should open patient details', async ({ page }) => {
    await page.click('text=Patients');
    await page.locator('button:has-text("Eye")').first().click();
    await expect(page.locator('text=Détails')).toBeVisible();
  });

  test('should navigate to appointments', async ({ page }) => {
    await page.click('text=Rendez-vous');
    await expect(page).toHaveURL('/appointments');
    await expect(page.locator('h1')).toContainText('Rendez-vous');
  });

  test('should create a new appointment', async ({ page }) => {
    await page.click('text=Rendez-vous');
    await page.click('text=Nouveau rendez-vous');
    await expect(page.locator('h2')).toContainText('Nouveau rendez-vous');
    
    await page.fill('input[id="patientId"]', 'patient-123');
    await page.fill('input[id="medecinId"]', 'medecin-123');
    await page.fill('input[id="dateDebut"]', '2024-12-01T10:00');
    await page.fill('input[id="dateFin"]', '2024-12-01T11:00');
    await page.click('button[type="submit"]');
    
    await expect(page.locator('text=Rendez-vous créé')).toBeVisible();
  });

  test('should navigate to medical records', async ({ page }) => {
    await page.click('text=Dossier Médical');
    await expect(page).toHaveURL('/medical-records');
  });

  test('should navigate to laboratory', async ({ page }) => {
    await page.click('text=Laboratoire');
    await expect(page).toHaveURL('/lab');
  });

  test('should navigate to pharmacy', async ({ page }) => {
    await page.click('text=Pharmacie');
    await expect(page).toHaveURL('/medications');
  });

  test('should navigate to payments', async ({ page }) => {
    await page.click('text=Facturation');
    await expect(page).toHaveURL('/payments');
  });

  test('should navigate to dashboard', async ({ page }) => {
    await page.click('text=Tableau de bord');
    await expect(page).toHaveURL('/');
    await expect(page.locator('h1')).toContainText('Dashboard');
  });
});