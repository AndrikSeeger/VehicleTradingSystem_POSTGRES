package Exceptions;

/**
 * Verbindung zur DB nicht möglich
 */

public class NoSuchDB extends Exception{
    public NoSuchDB() {
        super("Verbindung fehlgeschlagen!");
    }
}
