package com.calero.lili.core.comprobantesWs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xades4j.SignXmlString;

/**
 * Registra los beans necesarios para el módulo de comprobantes electrónicos.
 */
@Configuration
public class ComprobantesWsConfig {

    /**
     * SignXmlString solo tiene métodos estáticos, pero ProcesarDocumentosServiceImpl
     * la recibe por constructor — Spring necesita una instancia registrada como bean.
     */
    @Bean
    public SignXmlString signXmlString() {
        return new SignXmlString();
    }
}
