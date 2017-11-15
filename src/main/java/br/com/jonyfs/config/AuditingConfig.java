/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.jonyfs.config;

import br.com.jonyfs.user.User;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jony
 */
@EnableJpaAuditing
@Configuration
public class AuditingConfig {

    @Bean
    public AuditorAware<User> createAuditorProvider() {
        return new SecurityAuditor();
    }

    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }

    public static class SecurityAuditor implements AuditorAware<User> {

        @Override
        public Optional<User> getCurrentAuditor() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                if (auth.getDetails() instanceof User) {
                    return Optional.ofNullable((User) auth.getDetails());
                }
            }
            return Optional.empty();
        }
    }

}
