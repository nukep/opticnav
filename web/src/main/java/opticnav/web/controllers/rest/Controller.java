package opticnav.web.controllers.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import opticnav.ardd.admin.ARDdAdminException;
import opticnav.web.controllers.rest.pojo.LocaleMessage;
import opticnav.web.controllers.rest.pojo.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Controller {
    @Autowired
    private MessageSource msg;
    
    public class MessageException extends Exception {
        private static final long serialVersionUID = 1L;
        
        private Message message;
        
        public MessageException(String key, Object...params) {
            this.message = new LocaleMessage(msg, key, params);
        }
        
        public Message getMessageObject() {
            return this.message;
        }
    }
    
    public class NotFound extends MessageException {
        private static final long serialVersionUID = 1L;
        public NotFound(String key, Object...params) {
            super(key, params);
        }
    }
    
    public class BadRequest extends MessageException {
        private static final long serialVersionUID = 1L;
        public BadRequest(String key, Object...params) {
            super(key, params);
        }
    }
    
    public class Conflict extends MessageException {
        private static final long serialVersionUID = 1L;
        public Conflict(String key, Object...params) {
            super(key, params);
        }
    }

    
    @ExceptionHandler(ARDdAdminException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Message handleAdminConnectionException(ARDdAdminException ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        return new LocaleMessage(msg, "adminconnection.broken");
    }
    
    // If an exception is thrown, override default error page with a POJO
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Message handleException(Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        return new Message(ex.getClass().getName() + ": " + ex.getMessage());
    }
    
    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Message handleNotFound(MessageException ex) {
        return ex.getMessageObject();
    }
    
    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleBadRequest(MessageException ex) {
        return ex.getMessageObject();
    }
    
    @ExceptionHandler(Conflict.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleConflict(MessageException ex) {
        return ex.getMessageObject();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        String message = "";
        for (FieldError fieldError: errors) {
            message += fieldError.getField() + ": " + fieldError.getDefaultMessage() + "\n";
        }
        return new Message(message);
    }

    public Message ok(String key, Object...params) {
        return new LocaleMessage(msg, key, params);
    }
}
