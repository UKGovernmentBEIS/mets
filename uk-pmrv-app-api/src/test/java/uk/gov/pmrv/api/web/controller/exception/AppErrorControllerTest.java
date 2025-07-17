package uk.gov.pmrv.api.web.controller.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.ContentCachingResponseWrapper;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restlogging.MultiReadHttpServletRequestWrapper;
import uk.gov.netz.api.restlogging.RestLoggingService;
import uk.gov.netz.api.restlogging.RestLoggingUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppErrorControllerTest {

    @InjectMocks
    private AppErrorController errorController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private RestLoggingService restLoggingService;


    @Test
    void handleUnidentifiedError_error_status_code_401() {
        final ErrorCode expectedErrorCode = ErrorCode.UNAUTHORIZED;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(401);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void handleUnidentifiedError_error_status_code_404() {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
        
        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.NOT_FOUND));
    }

    @Test
    void handleUnidentifiedError_error_status_code_405() {
        final ErrorCode expectedErrorCode = ErrorCode.METHOD_NOT_ALLOWED;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(405);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPut(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
        
        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.METHOD_NOT_ALLOWED));
    }

    @Test
    void handleUnidentifiedError_error_status_code_406() {
        final ErrorCode expectedErrorCode = ErrorCode.NOT_ACCEPTABLE;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(406);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPatch(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
        
        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.NOT_ACCEPTABLE));
    }

    @Test
    void handleUnidentifiedError_error_status_code_415() {
        final ErrorCode expectedErrorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(415);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorDelete(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
        
        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    void handleUnidentifiedError_error_status_code_500() {
        final ErrorCode expectedErrorCode = ErrorCode.INTERNAL_SERVER;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();
        
        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    void handleUnidentifiedError_error_status_code_null() {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        final String correlationId = "1234";
        final String correlationParentId = "5678";
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER)).thenReturn(correlationId);
        when(httpServletResponse.getHeader(RestLoggingUtils.CORRELATION_PARENT_ID_HEADER)).thenReturn(correlationParentId);

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        Mockito.verify(restLoggingService, Mockito.times(1)).log(
        		any(MultiReadHttpServletRequestWrapper.class),
        		any(ContentCachingResponseWrapper.class),
        		any(LocalDateTime.class),
                eq(correlationId), eq(correlationParentId), 
                eq("/error"), eq(null));
    }
}