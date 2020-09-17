package net.stawrul.services.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wyjątek sygnalizujący niedostępność towaru.
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class MoviesAndCDsException extends RuntimeException {
    public MoviesAndCDsException() {
        super("You can't order movies and CDs together");
    }
}
