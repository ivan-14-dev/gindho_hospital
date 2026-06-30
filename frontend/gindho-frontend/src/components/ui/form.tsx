import * as React from "react";
import { cn } from "@/lib/utils";
import { Controller } from "react-hook-form";
import { Label } from "@/components/ui/label";

const Form = React.forwardRef<HTMLFormElement, React.FormHTMLAttributes<HTMLFormElement>>(
  ({ className, ...props }, ref) => (
    <form ref={ref} className={cn("space-y-6", className)} {...props} />
  )
);
Form.displayName = "Form";

const FormItem = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn("space-y-2", className)} {...props} />
  )
);
FormItem.displayName = "FormItem";

const FormLabel = Label;

const FormControl = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn("relative", className)} {...props} />
  )
);
FormControl.displayName = "FormControl";

const FormDescription = React.forwardRef<HTMLParagraphElement, React.HTMLAttributes<HTMLParagraphElement>>(
  ({ className, ...props }, ref) => (
    <p ref={ref} className={cn("text-sm text-muted-foreground", className)} {...props} />
  )
);
FormDescription.displayName = "FormDescription";

const FormMessage = React.forwardRef<HTMLParagraphElement, React.HTMLAttributes<HTMLParagraphElement>>(
  ({ className, ...props }, ref) => (
    <p ref={ref} className={cn("text-sm font-medium text-destructive", className)} {...props} />
  )
);
FormMessage.displayName = "FormMessage";

interface FormFieldProps<T = any> {
  control?: any;
  name: string;
  render: (props: { field: any }) => React.ReactNode;
  defaultValue?: any;
  rules?: any;
  shouldUnregister?: boolean;
}

const FormField = React.forwardRef<React.ElementRef<any>, FormFieldProps>(
  ({ control, name, render, defaultValue, rules, shouldUnregister, ...props }, ref) => {
    if (!control) {
      console.warn(`FormField: control prop is missing for field "${name}"`);
      return null;
    }
    return (
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue}
        rules={rules}
        shouldUnregister={shouldUnregister}
        render={({ field }) => render({ field })}
        {...props}
      />
    );
  }
);
FormField.displayName = "FormField";

export {
  Form,
  FormItem,
  FormLabel,
  FormControl,
  FormDescription,
  FormMessage,
  FormField,
};