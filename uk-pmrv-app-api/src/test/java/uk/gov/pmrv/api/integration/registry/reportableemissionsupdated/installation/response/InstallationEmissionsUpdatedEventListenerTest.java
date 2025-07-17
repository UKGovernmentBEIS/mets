package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

import java.time.Year;
import java.util.List;

class InstallationEmissionsUpdatedEventListenerTest {


    private static final String TEST_CORRELATION_ID = "test-correlation-123";

    @Mock
    private InstallationEmissionsUpdatedResponseHandler handler;

    @InjectMocks
    private InstallationEmissionsUpdatedEventListener listener;

    private AccountEmissionsUpdatedResponseEvent mockEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockEvent = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder()
                        .registryId(123)
                        .reportableEmissions("10")
                        .reportingYear(Year.of(2002))
                        .build())
                .outcome(RegistryResponseStatus.ERROR)
                .errors(List.of(RegistryResponseErrorCode.ERROR_0803.getCode(), RegistryResponseErrorCode.ERROR_0805.getCode()))
                .build();
    }

    @Test
    void testHandleEvent_Success() {
        listener.handle(mockEvent, TEST_CORRELATION_ID);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, TEST_CORRELATION_ID);
    }

    @Test
    void testHandleEvent_NullCorrelationId() {
        listener.handle(mockEvent, null);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, null);
    }
}