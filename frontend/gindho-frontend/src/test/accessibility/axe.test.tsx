import { describe, it } from 'vitest';
import { render } from '@testing-library/react';
import AxeCore from 'axe-core';

interface AxeViolation {
  id: string;
  description: string;
  nodes: AxeCore.NodeResult[];
}

async function checkA11y(container: Element) {
  const results = await AxeCore.run(container);
  return {
    ...results,
    toHaveNoViolations() {
      if (this.violations.length > 0) {
        const violationData = (this.violations as AxeViolation[])
          .map((v) => `${v.id}: ${v.description}`)
          .join('\n');
        throw new Error(`Accessibility violations:\n${violationData}`);
      }
    },
  };
}

describe('Accessibility Tests with axe-core', () => {
  describe('UI Components', () => {
    it('Button component should have no accessibility violations', async () => {
      const { container } = render(
        <button type="button">Click me</button>
      );
      const results = await checkA11y(container);
      results.toHaveNoViolations();
    });

    it('Input component should have no accessibility violations', async () => {
      const { container } = render(
        <div>
          <label htmlFor="email">Email</label>
          <input id="email" type="email" aria-describedby="email-help" />
          <span id="email-help">Enter your email address</span>
        </div>
      );
      const results = await checkA11y(container);
      results.toHaveNoViolations();
    });
  });

  describe('Navigation', () => {
    it('Navigation nav should have proper landmark role', async () => {
      const { container } = render(
        <nav role="navigation" aria-label="Main navigation">
          <a href="/">Home</a>
          <a href="/patients">Patients</a>
        </nav>
      );
      const results = await checkA11y(container);
      results.toHaveNoViolations();
    });
  });
});