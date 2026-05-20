package com.proaula.aula.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    @ExceptionHandler({AulaException.class, org.springframework.web.bind.MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex, Model model, HttpServletRequest request) {
        logger.warn("BadRequestException: {}", ex.getMessage(), ex);

        if (ex instanceof AulaException) {
            AulaException aulaEx = (AulaException) ex;
            model.addAttribute("errorCode", aulaEx.getErrorCode());
            model.addAttribute("errorMessage", aulaEx.getMessage());
        } else {
            org.springframework.web.bind.MethodArgumentNotValidException validationEx =
                    (org.springframework.web.bind.MethodArgumentNotValidException) ex;
            Map<String, String> errors = new HashMap<>();
            validationEx.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            model.addAttribute("errorCode", "VALIDATION_ERROR");
            model.addAttribute("errorMessage", "Datos de entrada inválidos");
            model.addAttribute("fieldErrors", errors);
        }

        populateErrorModel(model, request);
        return "error/400";
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(Exception ex, Model model, HttpServletRequest request) {
        logger.warn("{}: {} {}", ex.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

        model.addAttribute("errorCode", "NOT_FOUND");
        model.addAttribute("errorMessage", "No se encontró la ruta solicitada");
        populateErrorModel(model, request);

        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, HttpServletRequest request) {
        logger.warn("AccessDeniedException: {}", ex.getMessage());

        model.addAttribute("errorCode", "ACCESS_DENIED");
        model.addAttribute("errorMessage", "No tienes permisos para acceder a este recurso");
        populateErrorModel(model, request);

        return "error/403";
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException ex, Model model, HttpServletRequest request) {
        logger.warn("AuthenticationException: {}", ex.getMessage());

        model.addAttribute("errorCode", "AUTHENTICATION_FAILED");
        model.addAttribute("errorMessage", "Error de autenticación");
        populateErrorModel(model, request);

        return "error/401";
    }

    @ExceptionHandler({org.springframework.dao.DataAccessException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError(Exception ex, Model model, HttpServletRequest request) {
        logger.error("ServerError: {}", ex.getMessage(), ex);

        if (ex instanceof org.springframework.dao.DataAccessException) {
            model.addAttribute("errorCode", "DATABASE_ERROR");
            model.addAttribute("errorMessage", "Error de conexión con la base de datos");
        } else {
            model.addAttribute("errorCode", "INTERNAL_ERROR");
            model.addAttribute("errorMessage", "Ha ocurrido un error interno en el servidor");
        }

        populateErrorModel(model, request);

        if (isDevelopment()) {
            model.addAttribute("errorDetails", ex.getMessage());
            model.addAttribute("stackTrace", getStackTraceAsString(ex));
        }

        return "error/500";
    }

    private void populateErrorModel(Model model, HttpServletRequest request) {
        model.addAttribute("error", true);
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
    }

    private boolean isDevelopment() {
        String profile = System.getProperty("spring.profiles.active", "");
        return "dev".equals(profile) || "development".equals(profile) || profile.isEmpty();
    }

    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}