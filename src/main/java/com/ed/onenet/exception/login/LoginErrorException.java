package com.ed.onenet.exception.login;

import com.ed.onenet.exception.common.SofiaException;

public class LoginErrorException extends SofiaException {

    public LoginErrorException(String message) {
        super(message);
    }
}
