package com.gindho.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOriginsProperty;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // IMPORTANT (FR)
        // - Cette classe mappe les rôles -> endpoints REST existants.
        // - Si tu modifies une route côté ApiController, ajuste ici le matcher.
        //
        // Commandes utiles (FR) :
        // 1) Lancer le backend :
        //    mvn -f backend/pom.xml spring-boot:run -DskipTests
        // 2) Tester un accès “PATIENT” à une route réservée “ACCOUNTING” :
        //    curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/revenus?page=0&size=5
        // 3) Tester un accès “PHARMACIST” :
        //    curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/prescriptions?page=0&size=5

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints (doivent être accessibles sans token)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/*").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()

                // Spring Boot error endpoint must be accessible without auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/error/**").permitAll()

                // Admin backoffice
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // Dashboard
                .requestMatchers("/api/dashboard/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "UTILISATEUR_SECONDAIRE")
                .requestMatchers("/api/dashboard/medecin/**").hasAnyRole("MEDECIN")
                // Cas spécifique: /api/dashboard/medecin/{medecinId}/patients
                // IMPORTANT: éviter "**/patients" (pattern invalide avec PathPatternParser)
                .requestMatchers("/api/dashboard/medecin/*/patients").hasAnyRole("MEDECIN")
                .requestMatchers("/api/dashboard/patient/**").hasAnyRole("PATIENT")

                // Dossier médical complet (lecture)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patients/*/dossier-complet")
                .hasAnyRole("PATIENT", "MEDECIN", "NURSE", "PHARMACIST", "ADMIN", "SUPER_ADMIN", "UTILISATEUR_SECONDAIRE")

                // Patients
                // - POST /api/patients (création) : RECEPTION/ADMIN/SUPER_ADMIN uniquement
                // - GET /api/patients (lecture) : pour permettre les formulaires (ex: Analyses) chez MEDECIN/LABORATORY
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/patients")
                .hasAnyRole("RECEPTION", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patients")
                .hasAnyRole("RECEPTION", "ADMIN", "SUPER_ADMIN", "PATIENT", "UTILISATEUR_SECONDAIRE", "MEDECIN", "LABORATORY")
                .requestMatchers("/api/patients/**").hasAnyRole(
                        "RECEPTION", "ADMIN", "SUPER_ADMIN", "PATIENT", "UTILISATEUR_SECONDAIRE"
                )

                // Médecins (admin pour CRUD, médecin/labo pour lecture)
                // NOTE: PATIENT a besoin de la liste pour demander un RDV.
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medecins")
                .hasAnyRole("PATIENT", "RECEPTION", "ADMIN", "SUPER_ADMIN", "MEDECIN", "LABORATORY")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medecins/by-user/**").hasAnyRole("MEDECIN")
                .requestMatchers("/api/medecins/**").hasAnyRole("RECEPTION", "ADMIN", "SUPER_ADMIN", "MEDECIN")

                // Rendez-vous (création: réception/admin, lecture: patient/médecin, update/statut: admin/médecin)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/rendezvous").hasAnyRole("PATIENT", "MEDECIN", "RECEPTION", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/rendezvous").hasAnyRole("PATIENT", "RECEPTION", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/rendezvous/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/rendezvous/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN", "RECEPTION")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/rendezvous/**").hasAnyRole("RECEPTION", "MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/rendezvous/**").hasAnyRole("PATIENT", "MEDECIN", "RECEPTION", "ADMIN", "SUPER_ADMIN")

                // Analyses (médical / laboratoire / urgences / lecture patient)
                // IMPORTANT: URGENCY doit pouvoir créer/affecter une "urgence" via Analyse.urgent=true (MVP).
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/analyses").hasAnyRole("MEDECIN", "LABORATORY", "URGENCY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/analyses/**").hasAnyRole("MEDECIN", "LABORATORY", "URGENCY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/analyses/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/analyses").hasAnyRole(
                        "PATIENT", "MEDECIN", "NURSE", "LABORATORY", "URGENCY", "ADMIN", "SUPER_ADMIN", "UTILISATEUR_SECONDAIRE"
                )
                .requestMatchers("/api/analyses/**").hasAnyRole(
                        "PATIENT", "MEDECIN", "NURSE", "LABORATORY", "URGENCY", "ADMIN", "SUPER_ADMIN", "UTILISATEUR_SECONDAIRE"
                )

                // Prescriptions
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/prescriptions").hasAnyRole("PATIENT", "MEDECIN", "PHARMACIST", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/prescriptions").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/prescriptions/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/prescriptions/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/prescriptions/**").hasAnyRole("PATIENT", "MEDECIN", "PHARMACIST", "ADMIN", "SUPER_ADMIN")

                // Revenus
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/revenus").hasAnyRole("ACCOUNTING", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/revenus/patient/*").hasAnyRole("PATIENT")
                .requestMatchers("/api/revenus/**").hasAnyRole("ACCOUNTING", "ADMIN", "SUPER_ADMIN")

                // ===== FACTURES / PAIEMENTS (PATIENT) =====
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/factures/patient/*").hasAnyRole("PATIENT")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/paiements/patient/*").hasAnyRole("PATIENT")

                // ===== FACTURES / PAIEMENTS (ACCOUNTING) =====
                .requestMatchers("/api/factures/**").hasAnyRole("ACCOUNTING", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/paiements/**").hasAnyRole("ACCOUNTING", "ADMIN", "SUPER_ADMIN")

                // ===== ASSURANCES =====
                .requestMatchers("/api/assurances/**").hasAnyRole("RECEPTION", "ADMIN", "SUPER_ADMIN", "PATIENT", "ACCOUNTING")

                // ===== SOINS INFIRMIERS =====
                .requestMatchers("/api/signes-vitaux/**").hasAnyRole("NURSE", "MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/plans-soins/**").hasAnyRole("NURSE", "MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/administrations-medicaments/**").hasAnyRole("NURSE", "MEDECIN", "ADMIN", "SUPER_ADMIN")

                // ===== PHARMACIE =====
                .requestMatchers("/api/pharmacie/**").hasAnyRole("PHARMACIST", "ADMIN", "SUPER_ADMIN")

                // ===== GARDES (PLANNINGS) =====
                .requestMatchers("/api/gardes/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")

                // ===== STOCKS =====
                .requestMatchers("/api/stocks/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                
                // ===== RH =====
                .requestMatchers("/api/rh/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // ===== NOUVEAUX MODULES 7-13 =====
                .requestMatchers("/api/evenements/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/rondes/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/bloc/**").hasAnyRole("MEDECIN", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/qualite/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/incidents/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/equipements/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/ambulances/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // ===== IMAGERIE =====
                .requestMatchers("/api/imagerie/**").hasAnyRole("MEDECIN", "LABORATORY", "ADMIN", "SUPER_ADMIN")
                // ===== TÉLÉCONSULTATION =====
                .requestMatchers("/api/teleconsultations/**").hasAnyRole("MEDECIN", "PATIENT", "ADMIN", "SUPER_ADMIN")
                // ===== HOSPITALISATION (ADMIN / service hospitalisation) =====
                .requestMatchers("/api/chambres").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/chambres/**").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")

                .requestMatchers("/api/lits").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/lits/**").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/lits/chambre/*").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")

                .requestMatchers("/api/hospitalisations/**").hasAnyRole("HOSPITALIZATION_SERVICE", "ADMIN", "SUPER_ADMIN")

                // le reste requiert auth JWT
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Exemple attendu (dev):
        //   app.cors.allowed-origins=http://localhost:5173,http://localhost:5174
        // En prod: mets les origines exactes.
        var origins = Arrays.stream(allowedOriginsProperty.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        // IMPORTANT: en CORS, on ne peut pas utiliser allowedOrigins="*" avec allowCredentials=true.
        boolean allowCredentials = !(origins.size() == 1 && origins.get(0).equals("*"));

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
