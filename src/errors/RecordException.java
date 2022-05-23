/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;

/**
 *
 * @author Admin
 */
public class RecordException extends Exception {

    public RecordException() {
    }

    public RecordException(String msg) {
        super(msg);
    }
}
