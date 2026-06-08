package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NERApplicationRegulatorReviewSubmitInitializerTest {

    @InjectMocks
    private NERApplicationRegulatorReviewSubmitInitializer initializer;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Test
    void initializePayload_shouldCallVerificationServiceAndMap() {
        // given
        NERVerificationReport verificationReport = NERVerificationReport.builder().build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .verificationReport(verificationReport)
                .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .verificationBodyId(1L)
                .build();

        // when
        RequestTaskPayload result = initializer.initializePayload(request);

        // then
        verify(requestVerificationService).refreshVerificationReportVBDetails(
                verificationReport,
                1L
        );

        assertNotNull(result);
        assertInstanceOf(NERApplicationRegulatorReviewSubmitRequestTaskPayload.class, result);
    }

    @Test
    void getRequestTaskTypes_shouldReturnCorrectType() {
        // when
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();

        // then
        assertEquals(Set.of(RequestTaskType.NER_APPLICATION_REVIEW), result);
    }
}
