package tn.esprit.autoloc.web.controller;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

/**
 * Gestionnaire centralisé des exceptions pour l'ensemble des contrôleurs REST.
 * Intercepte notamment les erreurs de validation Bean Validation (MethodArgumentNotValidException),
 * les erreurs 404 (ResourceNotFoundException) et les erreurs d'arguments invalides.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.BAD_REQUEST.value());
        body.put("erreur", "Erreur de validation des données");
        body.put("horodatage", LocalDateTime.now());

        Map<String, String> erreursValidation = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erreursValidation.put(error.getField(), error.getDefaultMessage());
        }
        body.put("erreurs", erreursValidation);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.BAD_REQUEST.value());
        body.put("erreur", "Violation de contrainte de validation");
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.NOT_FOUND.value());
        body.put("erreur", "Ressource introuvable");
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.BAD_REQUEST.value());
        body.put("erreur", "Argument invalide");
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statut", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("erreur", "Erreur interne du serveur");
        body.put("message", ex.getMessage());
        body.put("horodatage", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
