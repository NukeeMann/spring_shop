package net.stawrul.services.exceptions;

/**
 * Wyjątek sygnalizujący niedostępność towaru.
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class EmptyOrderException extends RuntimeException {
    public EmptyOrderException() {
        super("You didn't order anything");
    }
}
