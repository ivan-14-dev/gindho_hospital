import * as React from 'react';
import { cn } from '@/lib/utils';

const SelectContext = React.createContext<{
  value: string;
  onValueChange: (v: string) => void;
}>({ value: '', onValueChange: () => {} });

export function Select({ 
  value, 
  onValueChange, 
  defaultValue, 
  children 
}: { 
  value?: string; 
  onValueChange?: (v: string) => void; 
  defaultValue?: string;
  children: React.ReactNode;
}) {
  const [internalValue, setInternalValue] = React.useState(defaultValue || '');
  return (
    <SelectContext.Provider value={{ 
      value: value ?? internalValue, 
      onValueChange: onValueChange || setInternalValue 
    }}>
      {children}
    </SelectContext.Provider>
  );
}

export function SelectTrigger({ className, ...props }: React.ComponentProps<'button'>) {
  return (
    <button
      className={cn('flex h-9 w-full items-center justify-between rounded-md border bg-background px-3 py-2 text-sm', className)}
      {...props}
    />
  );
}

export function SelectValue() {
  const { value } = React.useContext(SelectContext);
  return <span>{value}</span>;
}

export function SelectContent({ 
  children, 
  className 
}: { 
  children: React.ReactNode; 
  className?: string;
}) {
  return <div className={cn('absolute z-50 mt-1 w-full bg-background border rounded-md shadow-lg', className)}>{children}</div>;
}

export function SelectItem({ 
  value, 
  children, 
  className 
}: { 
  value: string; 
  children: React.ReactNode;
  className?: string;
}) {
  const { onValueChange } = React.useContext(SelectContext);
  return (
    <div
      className={cn('px-3 py-2 text-sm hover:bg-accent cursor-pointer', className)}
      onClick={() => onValueChange(value)}
    >
      {children}
    </div>
  );
}