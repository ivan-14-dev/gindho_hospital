import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AIAssistantPage } from '@/pages/ai/AIAssistant';

function wrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('AI Assistant Page', () => {
  it('should render page title', () => {
    render(<AIAssistantPage />, { wrapper });
    expect(screen.getByText('Assistant IA Médical')).toBeInTheDocument();
  });

  it('should render chat input', () => {
    render(<AIAssistantPage />, { wrapper });
    expect(screen.getByPlaceholderText('Tapez votre message...')).toBeInTheDocument();
  });

  it('should render send button', () => {
    render(<AIAssistantPage />, { wrapper });
    expect(screen.getByRole('button')).toBeInTheDocument();
  });
});