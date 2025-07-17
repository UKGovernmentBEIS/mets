package uk.gov.pmrv.api.web.controller.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingResponseWrapper;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restlogging.MultiReadHttpServletRequestWrapper;
import uk.gov.netz.api.restlogging.RestLoggingService;
import uk.gov.netz.api.restlogging.RestLoggingUtils;
import uk.gov.pmrv.api.web.util.ErrorUtil;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Implements spring {@link ErrorController}.
 */
@RestController
@RequestMapping(path = "/error")
@RequiredArgsConstructor
@Hidden
public class AppErrorController implements ErrorController {
    private final RestLoggingService restLoggingService;
    /**
     * Returns the error code.
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ErrorResponse}
     */
    @GetMapping
    public ResponseEntity<ErrorResponse> handleUnidentifiedErrorGet(HttpServletRequest request, HttpServletResponse response) {
        return handleUnidentifiedError(request, response);
    }

    @PostMapping
    public ResponseEntity<ErrorResponse> handleUnidentifiedErrorPost(HttpServletRequest request, HttpServletResponse response) {
        return handleUnidentifiedError(request, response);
    }

    @PutMapping
    public ResponseEntity<ErrorResponse> handleUnidentifiedErrorPut(HttpServletRequest request, HttpServletResponse response) {
        return handleUnidentifiedError(request, response);
    }

    @PatchMapping
    public ResponseEntity<ErrorResponse> handleUnidentifiedErrorPatch(HttpServletRequest request, HttpServletResponse response) {
        return handleUnidentifiedError(request, response);
    }

    @DeleteMapping
    public ResponseEntity<ErrorResponse> handleUnidentifiedErrorDelete(HttpServletRequest request, HttpServletResponse response) {
        return handleUnidentifiedError(request, response);
    }

    private ResponseEntity<ErrorResponse> handleUnidentifiedError(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<ErrorResponse>  errorResponse = getResponse(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String originalRequestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
        HttpStatus originalHttpStatus = ObjectUtils.isEmpty(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)) ? null
                : HttpStatus.valueOf((int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        MultiReadHttpServletRequestWrapper wrappedRequest = new MultiReadHttpServletRequestWrapper(request);
        
        LocalDateTime requestTimestamp = LocalDateTime.now();

		restLoggingService.log(wrappedRequest, wrappedResponse, requestTimestamp,
				response.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER),
				response.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER), 
				originalRequestUri, originalHttpStatus);
        return errorResponse;
    }

    private ResponseEntity<ErrorResponse> getResponse(HttpServletRequest request) {
        Integer status = (Integer) Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .orElse(HttpStatus.NOT_FOUND.value());

        switch (status){
            case 401:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.UNAUTHORIZED);
            case 403:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.FORBIDDEN);
            case 404:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.RESOURCE_NOT_FOUND);
            case 405:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.METHOD_NOT_ALLOWED);
            case 406:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.NOT_ACCEPTABLE);
            case 415:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
            default:
                return ErrorUtil.getErrorResponse(new Object[]{}, ErrorCode.INTERNAL_SERVER);
        }
    }
}
