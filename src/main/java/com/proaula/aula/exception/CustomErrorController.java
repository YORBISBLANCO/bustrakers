package com.proaula.aula.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (statusCode != null) {
            response.setStatus(statusCode);
        }

        String errorMessage = "Ha ocurrido un error interno en el servidor";
        String errorCode = "INTERNAL_ERROR";
        String viewName = "error/500";

        if (statusCode != null) {
            switch (statusCode) {
                case 404:
                    errorCode = "NOT_FOUND";
                    errorMessage = "No se encontró la ruta solicitada";
                    viewName = "error/404";
                    break;
                case 401:
                    errorCode = "AUTHENTICATION_FAILED";
                    errorMessage = "Error de autenticación";
                    viewName = "error/401";
                    break;
                case 403:
                    errorCode = "ACCESS_DENIED";
                    errorMessage = "No tienes permisos para acceder a este recurso";
                    viewName = "error/403";
                    break;
                default:
                    errorCode = "INTERNAL_ERROR";
                    errorMessage = message != null && !message.isEmpty() ? message : errorMessage;
                    viewName = "error/500";
                    break;
            }
        }

        logger.warn("Handled error {} for path {} with view {}", statusCode, requestUri, viewName);

        model.addAttribute("error", true);
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", requestUri != null ? requestUri : request.getRequestURI());

        if (isDevelopment()) {
            if (exception != null) {
                model.addAttribute("errorDetails", exception.getMessage());
            }
            if (message != null) {
                model.addAttribute("errorDetails", message);
            }
        }

        return viewName;
    }

    @RequestMapping("/access-denied")
    public String accessDenied(HttpServletRequest request, Model model) {
        String requestUri = request.getRequestURI();
        model.addAttribute("error", true);
        model.addAttribute("errorCode", "ACCESS_DENIED");
        model.addAttribute("errorMessage", "No tienes permisos para acceder a este recurso");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", requestUri);
        return "error/403";
    }

    private boolean isDevelopment() {
        String profile = System.getProperty("spring.profiles.active", "");
        return "dev".equals(profile) || "development".equals(profile) || profile.isEmpty();
    }
}
