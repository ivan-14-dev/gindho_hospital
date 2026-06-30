import * as React from "react";
import { cn } from "@/lib/utils";

const DropdownContext = React.createContext<{ open: boolean; setOpen: (open: boolean) => void }>({ open: false, setOpen: () => {} });

export function DropdownMenu({ children }: { children: React.ReactNode }) {
  const [open, setOpen] = React.useState(false);
  return <DropdownContext.Provider value={{ open, setOpen }}><div className="relative">{children}</div></DropdownContext.Provider>;
}

export function DropdownMenuTrigger({ className, children, ...props }: React.ComponentProps<"button"> & { asChild?: boolean }) {
  const ctx = React.useContext(DropdownContext);
  return <button className={cn("outline-none", className)} onClick={() => ctx.setOpen(!ctx.open)} {...props}>{children}</button>;
}

export function DropdownMenuContent({ className, children, ...props }: React.ComponentProps<"div">) {
  const ctx = React.useContext(DropdownContext);
  if (!ctx.open) return null;
  return (
    <>
      <div className="fixed inset-0 z-40" onClick={() => ctx.setOpen(false)} />
      <div className={cn("absolute z-50 mt-1 min-w-[8rem] overflow-hidden rounded-md border bg-popover p-1 text-popover-foreground shadow-md", className)} {...props}>{children}</div>
    </>
  );
}

export function DropdownMenuItem({ className, children, ...props }: React.ComponentProps<"div">) {
  return <div className={cn("relative flex cursor-default select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50", className)} {...props}>{children}</div>;
}