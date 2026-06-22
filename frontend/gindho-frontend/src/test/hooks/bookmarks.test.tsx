import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useBookmarks } from '@/hooks/use-bookmarks';

function wrapper({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('useBookmarks hook', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should return initial empty bookmarks', () => {
    const { result } = renderHook(() => useBookmarks(), { wrapper });
    expect(result.current.bookmarks).toEqual([]);
  });

  it('should add and remove bookmarks', () => {
    const { result } = renderHook(() => useBookmarks(), { wrapper });
    const bookmark = {
      id: 'patient-1',
      type: 'patient' as const,
      title: 'Test Patient',
      path: '/patients/1',
    };
    act(() => {
      result.current.addBookmark(bookmark);
    });
    expect(result.current.isBookmarked('patient-1')).toBe(true);
    act(() => {
      result.current.removeBookmark('patient-1');
    });
    expect(result.current.isBookmarked('patient-1')).toBe(false);
  });
});