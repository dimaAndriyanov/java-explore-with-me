package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.model.ErrorResponse;
import ru.practicum.ewm.model.ValidationErrorResponse;
import ru.practicum.ewm.model.Violation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = StatController.class)
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleBodyValidationError(MethodArgumentNotValidException exception) {
        final List<Violation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (Violation violation : violations) {
            sb.append(violation.getFieldName())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("\n");
        }
        log.warn("Bad request received. Request body failed validation\n{}", sb);
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            EndBeforeStartException.class,
            InvalidTimeFormatException.class,
            NotIpAddressException.class,
            TimestampInFutureException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestError(Throwable exception) {
        log.warn("Bad request received.\n{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundDataError(NoStatsForSuchApplicationException exception) {
        log.warn("Requested object not found.\n{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(Throwable exception) {
        log.warn("Internal server error has occurred.\n{}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}