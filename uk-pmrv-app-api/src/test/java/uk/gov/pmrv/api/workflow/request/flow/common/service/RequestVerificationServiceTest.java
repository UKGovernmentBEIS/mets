package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestVerificationServiceTest {

    @InjectMocks
    private RequestVerificationService<AerVerificationReport> service;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void clearVerificationReport() {
        AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .verificationReport(AerVerificationReport.builder()
                        .verificationBodyId(1L)
                        .verificationBodyDetails(VerificationBodyDetails.builder().name("name").build()).build())
                .build();

        final Request request = Request.builder()
                .payload(requestPayload)
                .verificationBodyId(2L)
                .build();

        service.clearVerificationReport(requestPayload, request.getVerificationBodyId());

        Assertions.assertNull(requestPayload.getVerificationReport());
    }

    @Test
    void clearVerificationReport_same_vb() {
        AerVerificationReport verificationReport = AerVerificationReport.builder()
                .verificationBodyId(1L)
                .verificationBodyDetails(VerificationBodyDetails.builder().name("name").build()).build();
        AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .verificationReport(verificationReport)
                .build();

        final Request request = Request.builder()
                .payload(requestPayload)
                .verificationBodyId(1L)
                .build();

        service.clearVerificationReport(requestPayload, request.getVerificationBodyId());

        Assertions.assertEquals(verificationReport, requestPayload.getVerificationReport());
    }

    @Test
    void setVerificationBodyAndVerifierDetails() {
        final Long vbId = 1L;
        AerVerificationReport verificationReport = AerVerificationReport.builder()
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .name("name1")
                        .accreditationReferenceNumber("accRefNum1")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city1")
                                .country("GB")
                                .postcode("postcode1")
                                .build())
                        .emissionTradingSchemes(Set.of(EmissionTradingScheme.CORSIA))
                        .build())
                .build();
        final VerificationBodyDetails verificationBodyDetailsNew = VerificationBodyDetails.builder()
                .name("name2")
                .accreditationReferenceNumber("accRefNum2")
                .address(AddressDTO.builder()
                        .line1("line2")
                        .city("city2")
                        .country("GR")
                        .postcode("postcode2")
                        .build())
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
                .build();

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(vbId))
                .thenReturn(verificationBodyDetailsNew);

        // Invoke
        VerificationBodyDetails actual = service.getVerificationBodyDetails(verificationReport, vbId);

        // Verify
        verify(verificationBodyDetailsQueryService, times(1))
                .getVerificationBodyDetails(vbId);
        Assertions.assertEquals(verificationBodyDetailsNew, actual);
    }

    @Test
    void setVerificationBodyAndVerifierDetails_cleared_details() {
        final Long vbId = 1L;
        AerVerificationReport verificationReport = AerVerificationReport.builder().build();
        final VerificationBodyDetails verificationBodyDetailsNew = VerificationBodyDetails.builder()
                .name("name2")
                .accreditationReferenceNumber("accRefNum2")
                .address(AddressDTO.builder()
                        .line1("line2")
                        .city("city2")
                        .country("GR")
                        .postcode("postcode2")
                        .build())
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
                .build();

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(vbId))
                .thenReturn(verificationBodyDetailsNew);

        // Invoke
        VerificationBodyDetails actual = service.getVerificationBodyDetails(verificationReport, vbId);

        // Verify
        verify(verificationBodyDetailsQueryService, times(1))
                .getVerificationBodyDetails(vbId);
        Assertions.assertEquals(verificationBodyDetailsNew, actual);
    }
}
