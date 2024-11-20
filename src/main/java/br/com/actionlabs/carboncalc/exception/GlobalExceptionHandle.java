package br.com.actionlabs.carboncalc.exception;

import br.com.actionlabs.carboncalc.util.ResponseStandartDTO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(APISuccessException.class)
    public ResponseEntity<ResponseStandartDTO> ApiSuccessException(APISuccessException e, HttpServletRequest httpServletRequest) {
        ResponseStandartDTO success = ResponseStandartDTO.success(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(success);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ResponseStandartDTO> ApiException(APIException e, HttpServletRequest httpServletRequest) {
        ResponseStandartDTO error = ResponseStandartDTO.failed(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseStandartDTO> handleInvalidDataType(HttpMessageNotReadableException ex) {
        ResponseStandartDTO error = new ResponseStandartDTO("Data Type Error", "Provided data is invalid or in an incorrect format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseStandartDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("The parameter '%s' received the value '%s', which is of an invalid type. The expected type is '%s'.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        ResponseStandartDTO error = new ResponseStandartDTO("Argument Type Error", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String supportedMethods = String.join(", ", ex.getSupportedHttpMethods().toString());

        List<Map<String, String>> errors = new ArrayList<>();

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("method", ex.getMethod());
        errorDetails.put("message", "HTTP method not supported");
        errorDetails.put("supportedMethods", supportedMethods);

        errors.add(errorDetails);

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                Instant.now(),
                "Invalid HTTP Method",
                errors
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("field", error.getField());
                    errorDetails.put("message", error.getDefaultMessage());
                    return errorDetails;
                })
                .collect(Collectors.toList());

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                "Validation Error",
                errors
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
