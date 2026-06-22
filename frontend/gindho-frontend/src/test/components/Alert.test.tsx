import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';

describe('Alert Component', () => {
  it('renders alert correctly', () => {
    render(
      <Alert>
        <AlertTitle>Test Alert</AlertTitle>
        <AlertDescription>Test description</AlertDescription>
      </Alert>
    );
    expect(screen.getByText('Test Alert')).toBeInTheDocument();
    expect(screen.getByText('Test description')).toBeInTheDocument();
  });

  it('applies variant classes', () => {
    const { container } = render(<Alert variant="destructive">Error</Alert>);
    expect(container.firstChild).toHaveClass('border-destructive/50');
  });
});