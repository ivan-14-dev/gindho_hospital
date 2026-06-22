import React from 'react';

export function useVirtualScroll<T>(
  items: T[],
  itemHeight: number,
  containerHeight: number
) {
  const [scrollTop, setScrollTop] = React.useState(0);

  const visibleStart = Math.max(0, Math.floor(scrollTop / itemHeight) - 3);
  const visibleEnd = Math.min(
    items.length - 1,
    Math.ceil((scrollTop + containerHeight) / itemHeight) + 3
  );

  return {
    visibleItems: items.slice(visibleStart, visibleEnd + 1),
    visibleStart,
    setScrollTop,
    totalHeight: items.length * itemHeight,
    offsetY: visibleStart * itemHeight,
  };
}