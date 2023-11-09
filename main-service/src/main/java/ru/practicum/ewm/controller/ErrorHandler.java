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
import ru.practicum.ewm.error.ApiError;
import ru.practicum.ewm.exception.*;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice("ru.practicum.ewm")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBodyValidationError(MethodArgumentNotValidException exception) {
        StringBuilder sb = new StringBuilder("Request body failed validation: ");
        exception.getBindingResult().getFieldErrors().stream()
                .map(error -> "Field: " + error.getField() + ". Error: " + error.getDefaultMessage() + "\n")
                .forEach(sb::append);
        log.warn("Bad request received. {}", sb);
        return new ApiError(getStackTrace(exception),
                sb.toString(),
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleVariablesValidationError(ConstraintViolationException exception) {
        StringBuilder sb = new StringBuilder("Request variables failed validation: ");
        exception.getConstraintViolations().stream()
                .map(error -> "Field: " + error.getPropertyPath().toString() + ". Error: " + error.getMessage() + "\n")
                .forEach(sb::append);
        log.warn("Bad request received. {}", sb);
        return new ApiError(getStackTrace(exception),
                sb.toString(),
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            NotValidRequestParametersException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestError(Throwable exception) {
        log.warn("Bad request received. {}", exception.getMessage());
        return new ApiError(getStackTrace(exception),
                exception.getMessage(),
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler({
            EmailIsAlreadyInUseException.class,
            ObjectAlreadyExistsException.class,
            CanNotCreateEventParticipationException.class,
            CanNotUpdatePublishedEventException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleSavingConflictingObjectError(RuntimeException exception) {
        log.warn("Request on saving object conflicting with already existing has been received\n{}", exception.getMessage());
        return new ApiError(getStackTrace(exception),
                exception.getMessage(),
                "Request on saving object conflicting with already existing",
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler({
            CanNotDeleteObjectException.class,
            CanNotUpdateObjectException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenOperationError(RuntimeException exception) {
        log.warn("Request on forbidden operation has been received.\n{}", exception.getMessage());
        return new ApiError(getStackTrace(exception),
                exception.getMessage(),
                "For the requested operation the conditions are not met",
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(ObjectNotFoundException exception) {
        log.warn("Requested object not found. {}", exception.getMessage());
        return new ApiError(getStackTrace(exception),
                exception.getMessage(),
                "Requested object not found",
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(Throwable exception) {
        log.error("Error occurred. {}", exception.getMessage());
        return new ApiError(getStackTrace(exception),
                exception.getMessage(),
                "Error on server occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now());
    }

    private String getStackTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
}