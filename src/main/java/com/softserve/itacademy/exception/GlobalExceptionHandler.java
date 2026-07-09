package com.softserve.itacademy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // 404 - Entity Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFound(EntityNotFoundException exception) {
        log.error("Entity not found: {}", exception.getMessage());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("message", exception.getMessage());
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    // 404 - No Handler Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound(NoHandlerFoundException exception) {
        log.error("Page not found: {}", exception.getRequestURL());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("message", "The page you requested was not found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    // 400 - Bad Request
    @ExceptionHandler(NullEntityReferenceException.class)
    public ModelAndView handleNullEntityReference(NullEntityReferenceException exception) {
        log.warn("Null entity reference: {}", exception.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", exception.getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception exception) {
        log.error("Unexpected error occurred: ", exception);
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }
}