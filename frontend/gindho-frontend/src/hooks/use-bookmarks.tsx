import { useState, useEffect } from 'react';
import { Heart } from 'lucide-react';
import { Button } from '@/components/ui/button';

const BOOKMARKS_KEY = 'gindho-bookmarks';

export interface Bookmark {
  id: string;
  type: 'patient' | 'appointment';
  title: string;
  path: string;
}

export function useBookmarks() {
  const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);

  useEffect(() => {
    const stored = localStorage.getItem(BOOKMARKS_KEY);
    if (stored) {
      try {
        setBookmarks(JSON.parse(stored));
      } catch {
        setBookmarks([]);
      }
    }
  }, []);

  const addBookmark = (bookmark: Bookmark) => {
    const newBookmarks = [...bookmarks, bookmark];
    setBookmarks(newBookmarks);
    localStorage.setItem(BOOKMARKS_KEY, JSON.stringify(newBookmarks));
  };

  const removeBookmark = (id: string) => {
    const newBookmarks = bookmarks.filter((b) => b.id !== id);
    setBookmarks(newBookmarks);
    localStorage.setItem(BOOKMARKS_KEY, JSON.stringify(newBookmarks));
  };

  const isBookmarked = (id: string) => bookmarks.some((b) => b.id === id);

  const toggleBookmark = (bookmark: Bookmark) => {
    if (isBookmarked(bookmark.id)) {
      removeBookmark(bookmark.id);
    } else {
      addBookmark(bookmark);
    }
  };

  return { bookmarks, addBookmark, removeBookmark, isBookmarked, toggleBookmark };
}

interface BookmarkButtonProps {
  id: string;
  type: 'patient' | 'appointment';
  title: string;
  path: string;
}

export function BookmarkButton({ id, type, title, path }: BookmarkButtonProps) {
  const { isBookmarked, toggleBookmark } = useBookmarks();

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={() => toggleBookmark({ id, type, title, path })}
      className={isBookmarked(id) ? 'text-red-500' : ''}
    >
      <Heart className="h-4 w-4" />
    </Button>
  );
}