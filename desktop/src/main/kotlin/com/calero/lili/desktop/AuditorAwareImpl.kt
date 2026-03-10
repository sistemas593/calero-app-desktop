package com.calero.lili.desktop

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional

@Component("auditorProvider")
class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("desktop-user")
    }
}
