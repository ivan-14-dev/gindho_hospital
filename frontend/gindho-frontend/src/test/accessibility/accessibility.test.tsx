import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { checkColorContrast } from '@/lib/accessibility';

describe('Accessibility Tests', () => {
  describe('Color Contrast', () => {
    it('checks contrast ratio for primary colors', () => {
      // Test WCAG AA compliance (4.5:1 for normal text)
      expect(checkColorContrast('rgb(0, 0, 0)', 'rgb(255, 255, 255)')).toBe(true); // Black on white
      expect(checkColorContrast('rgb(255, 255, 255)', 'rgb(0, 0, 0)')).toBe(true); // White on black
    });

    it('rejects low contrast combinations', () => {
      expect(checkColorContrast('rgb(204, 204, 204)', 'rgb(255, 255, 255)')).toBe(false); // Light gray on white
    });
  });

  describe('Button Accessibility', () => {
    it('renders button with proper attributes', () => {
      render(<Button>Click me</Button>);
      const button = screen.getByRole('button', { name: /click me/i });
      expect(button).toBeInTheDocument();
      expect(button.tagName).toBe('BUTTON');
    });

    it('supports aria-label', () => {
      render(<Button aria-label="Fermer la fenêtre">X</Button>);
      const button = screen.getByRole('button', { name: /fermer la fenêtre/i });
      expect(button).toBeInTheDocument();
    });

    it('supports disabled state', () => {
      render(<Button disabled>Disabled</Button>);
      const button = screen.getByRole('button');
      expect(button).toBeDisabled();
    });
  });

  describe('Input Accessibility', () => {
    it('renders input with label association', () => {
      render(
        <div>
          <label htmlFor="email">Email</label>
          <Input id="email" type="email" />
        </div>
      );
      const input = screen.getByLabelText('Email');
      expect(input).toBeInTheDocument();
    });

    it('supports aria-describedby for error messages', () => {
      render(
        <div>
          <Input aria-describedby="email-error" aria-invalid="true" />
          <span id="email-error">Email invalide</span>
        </div>
      );
      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('aria-describedby', 'email-error');
      expect(input).toHaveAttribute('aria-invalid', 'true');
    });

    it('supports placeholder as fallback', () => {
      render(<Input placeholder="Entrez votre email" />);
      const input = screen.getByPlaceholderText('Entrez votre email');
      expect(input).toBeInTheDocument();
    });
  });

  describe('Keyboard Navigation', () => {
    it('buttons are focusable', () => {
      render(<Button>Focusable</Button>);
      const button = screen.getByRole('button');
      button.focus();
      expect(document.activeElement).toBe(button);
    });

    it('inputs are focusable', () => {
      render(<Input />);
      const input = screen.getByRole('textbox');
      input.focus();
      expect(document.activeElement).toBe(input);
    });
  });

  describe('Semantic HTML', () => {
    it('uses semantic heading hierarchy', () => {
      render(
        <div>
          <h1>Main Title</h1>
          <h2>Section Title</h2>
          <h3>Subsection</h3>
        </div>
      );
      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Main Title');
      expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent('Section Title');
      expect(screen.getByRole('heading', { level: 3 })).toHaveTextContent('Subsection');
    });

    it('uses landmark roles', () => {
      render(
        <div>
          <header role="banner">Header</header>
          <nav role="navigation">Nav</nav>
          <main role="main">Main</main>
          <footer role="contentinfo">Footer</footer>
        </div>
      );
      expect(screen.getByRole('banner')).toBeInTheDocument();
      expect(screen.getByRole('navigation')).toBeInTheDocument();
      expect(screen.getByRole('main')).toBeInTheDocument();
      expect(screen.getByRole('contentinfo')).toBeInTheDocument();
    });
  });
});