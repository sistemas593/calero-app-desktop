package com.calero.lili.api.advice;


import com.calero.lili.core.errors.exceptions.ApiErrorResponse;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.errors.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        apiErrorResponse.setMessage(ex.getMessage());
        apiErrorResponse.setErrors(Collections.singletonList("Error"));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(NOT_FOUND.value());
        apiErrorResponse.setMessage(ex.getMessage());
        apiErrorResponse.setErrors(Collections.singletonList("Error"));
        return new ResponseEntity<>(apiErrorResponse, NOT_FOUND);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(GeneralException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        apiErrorResponse.setMessage(ex.getMessage());
        apiErrorResponse.setErrors(Collections.singletonList("Error"));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ListErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleListException(ListErrorException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        apiErrorResponse.setMessage(ex.getMessage());
        apiErrorResponse.setErrors(ex.getErrors());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    // TODOS LOS ERRORES NO CONTROLADOS INGRESAN POR AQUI Y DEVUELVE UN ERROR 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        //apiErrorResponse.setMessage(ex.getMessage());
        log.info("Error del servidor handleException", ex.getMessage());
        apiErrorResponse.setErrors(Collections.singletonList("Error interno del servidor"));
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleException(MethodArgumentNotValidException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();

        apiErrorResponse.setErrors(new ArrayList<>());

        for (ObjectError error : allErrors) {
            // EL ULTIMO MENSAJE EN SETMESSAJE
            apiErrorResponse.setMessage(error.getDefaultMessage());
            // ACUMULO LOS MENSAJES
            apiErrorResponse.getErrors().add(error.getDefaultMessage());
        }

        apiErrorResponse.setStatus(ex.getStatusCode().value());
        //apiErrorResponse.setMessage(ex.getBody().toString());
        log.info("Error del servidor handleException", ex.getBody());
        //apiErrorResponse.setErrors(Collections.singletonList("Error interno del servidor"));
        return new ResponseEntity<>(apiErrorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleException(HttpMessageNotReadableException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        apiErrorResponse.setMessage(ex.getMessage());
        log.info("Error del servidor handleException", ex.getMessage());
        ;
        apiErrorResponse.setErrors(List.of());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }


//    @ExceptionHandler({ChangeSetPersister.NotFoundException.class})
//    public ResponseEntity<?> handleNotFoundException(ChangeSetPersister.NotFoundException ex, HttpServletRequest request) {
//        ErrorDto error = ErrorDto
//                .builder()
//                .messages(Collections.singletonList(ex.getMessage()))
//                .resource(request.getRequestURI())
//                .build();
//        return buildResponseEntity(NOT_FOUND, error);
//    }
//
//    private ResponseEntity buildResponseEntity(HttpStatus httpStatus, ErrorDto error) {
//        return new ResponseEntity(error, httpStatus);
//    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        apiErrorResponse.setMessage("No tiene permisos para acceder a esta sección");
        apiErrorResponse.setErrors(Collections.singletonList("Acceso denegado"));

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.FORBIDDEN);
    }
}
