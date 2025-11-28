import { test, expect } from '@playwright/test';

test.describe('StorageMan App', () => {
  test('should display the app title', async ({ page }) => {
    await page.goto('/');

    // Check if the title is visible
    const title = page.getByRole('heading', { name: /StorageMan/i });
    await expect(title).toBeVisible();
  });

  test('should display the app description', async ({ page }) => {
    await page.goto('/');

    // Check if the description is visible
    const description = page.getByText(/Cloud storage management application/i);
    await expect(description).toBeVisible();
  });

  test('should have a counter button that increments', async ({ page }) => {
    await page.goto('/');

    // Find the counter button
    const button = page.getByRole('button', { name: /count is 0/i });
    await expect(button).toBeVisible();

    // Click the button
    await button.click();

    // Check if the count increased
    await expect(page.getByRole('button', { name: /count is 1/i })).toBeVisible();

    // Click again
    await button.click();
    await expect(page.getByRole('button', { name: /count is 2/i })).toBeVisible();
  });

  test('should have correct page title in document', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/StorageMan/);
  });

  test('should apply correct styling', async ({ page }) => {
    await page.goto('/');

    // Check if dark theme is applied
    const container = page.locator('div').first();
    await expect(container).toHaveClass(/bg-gray-950/);
  });
});
