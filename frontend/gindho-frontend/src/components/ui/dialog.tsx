import * as React from 'react';
import { cn } from '@/lib/utils';

interface DialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children: React.ReactNode;
}

export function Dialog({ open, onOpenChange, children }: DialogProps) {
  React.useEffect(() => {
    if (open) {
      document.body.style.overflow = 'hidden';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [open]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center" onClick={() => onOpenChange(false)}>
      <div className="absolute inset-0 bg-background/80 backdrop-blur-sm" />
      <div onClick={(e) => e.stopPropagation()}>
        {children}
      </div>
    </div>
  );
}

export function DialogContent({ 
  className, 
  children, 
  ...props 
}: React.ComponentProps<'div'>) {
  return (
    <div
      className={cn('relative z-50 bg-background rounded-lg border p-6 shadow-lg w-full max-w-lg', className)}
      {...props}
    >
      {children}
    </div>
  );
}

export function DialogHeader({ 
  className, 
  ...props 
}: React.ComponentProps<'div'>) {
  return (
    <div className={cn('flex flex-col space-y-1.5', className)} {...props} />
  );
}

export function DialogTitle({ 
  className, 
  ...props 
}: React.ComponentProps<'h2'>) {
  return (
    <h2 className={cn('text-lg font-semibold', className)} {...props} />
  );
}