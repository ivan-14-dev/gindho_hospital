package com.gindho.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect qui vérifie que les endpoints retournant des listes
 * utilisent la pagination. Log un avertissement si ce n'est pas le cas.
 */
@Aspect
@Component
@Slf4j
public class PageableValidationAspect {

    @Before("execution(* com.gindho.*.controller.*Controller.find*(..)) && args(..)")
    public void checkPageable() {
        // Vérification à implémenter — pour l'instant simple log
        log.debug("List endpoint called — ensure pagination is used");
    }
}
