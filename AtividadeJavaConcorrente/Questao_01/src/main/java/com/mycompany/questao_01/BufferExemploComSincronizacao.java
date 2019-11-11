package com.mycompany.questao_01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class BufferExemploComSincronizacao implements Buffer {
    
    private Lock lockDeAcesso = new ReentrantLock();
    private Condition podeGravar = lockDeAcesso.newCondition();
    private Condition podeLer = lockDeAcesso.newCondition();
    private int buffer = -1;
    private boolean ocupado = false;
    
    public void set( int valor ) {
        lockDeAcesso.lock();
        try {
            while ( ocupado ) {
                System.out.println("Produtor tenta gravar.");
                mostrarEstado("Buffer Cheio. Produtor aguarda.");
                podeGravar.await();
            }
            buffer = valor;
            ocupado = true;
            mostrarEstado("Produtor grava: " + buffer );
            podeLer.signal();
        } catch ( InterruptedException exception ) {
            exception.printStackTrace();
        } finally {
            lockDeAcesso.unlock();
        }
    }
    
    public int get() {
        int valorLido = 0;
        lockDeAcesso.lock();
        try {
            while ( !ocupado ) {
                System.out.println("Consumidor tenta ler.");
                mostrarEstado("Buffer vazio. Consumidor aguarda.");
                podeLer.await();
            }
            ocupado = false;
            valorLido = buffer;
            mostrarEstado("Consumidor lê: " + valorLido );
            podeGravar.signal();
        } catch ( InterruptedException exception ) {
            exception.printStackTrace();
        } finally {
            lockDeAcesso.unlock();
        }
        return valorLido;
    }
    
    public void mostrarEstado( String operacao ) {
        System.out.printf("%-40s%d\t\t%b\n\n", operacao, buffer, ocupado );
    }
    
}