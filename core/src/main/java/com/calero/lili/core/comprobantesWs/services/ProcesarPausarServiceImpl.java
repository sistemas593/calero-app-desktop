package com.calero.lili.core.comprobantesWs.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ProcesarPausarServiceImpl {
    public void pausar() {

        System.out.println("Proceso detenido");

        try {
            // Pausa de 5 segundos (5000 milisegundos)
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // buena práctica
            //System.out.println("El hilo fue interrumpido");
        }

        System.out.println("Proceso reanudado después de la pausa");


    }

    public void pausarProcesoAutorizacion() {

        System.out.println("Proceso Autorizacion detenido");

        try {
            // Pausa de 10 segundos (10000 milisegundos)
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // buena práctica
            //System.out.println("El hilo fue interrumpido");
        }

        System.out.println("Proceso reanudado después de la pausa");


    }
}
