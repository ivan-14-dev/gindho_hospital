package com.gindho.security;

import java.lang.annotation.*;

/**
 * Annotation pour vérifier les permissions côté BACKEND.
 * Utiliser sur les méthodes des contrôleurs :
 * {@code @RequirePermission("PATIENT:READ")}
 * <p>
 * Le frontend ne décide JAMAIS des permissions — c'est le backend qui valide.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String value();
    String message() default "Accès non autorisé";
}
