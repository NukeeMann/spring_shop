package net.stawrul.services.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wyjątek sygnalizujący niedostępność towaru.
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class ReduplicationException extends RuntimeException {
    public ReduplicationException() {
        super("You can't order the same thing more than once in one order");
    }
}
