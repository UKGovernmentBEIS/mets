package uk.gov.pmrv.api.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import uk.gov.netz.api.common.exception.NetzErrorCode;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * ErrorUtil for error manipulation.
 */
@UtilityClass
public class ErrorUtil {

    /**
     * Constructs the {@link ErrorResponse}.
     *
     * @param data Error data populated
     * @param errorCode {@link NetzErrorCode}
     * @return {@link ErrorResponse}
     */
    public ResponseEntity<ErrorResponse> getErrorResponse(Object[] data, NetzErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .security(errorCode.isSecurity())
                .data(data)
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
