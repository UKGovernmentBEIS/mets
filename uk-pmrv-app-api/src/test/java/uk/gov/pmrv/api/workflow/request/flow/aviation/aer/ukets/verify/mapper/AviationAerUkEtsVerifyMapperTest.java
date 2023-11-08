package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerVerifierContact;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AviationAerUkEtsVerifyMapperTest {

    private final AviationAerUkEtsVerifyMapper mapper = Mappers.getMapper(AviationAerUkEtsVerifyMapper.class);

    @Test
    void toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload() {
        RequestActionPayloadType requestActionPayloadType = RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD;
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
                .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").build())
                .build())
            .build();
        Year reportingYear = Year.of(2022);
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(23670.80);

        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.builder()
                .reportingRequired(Boolean.TRUE)
                .reportingYear(reportingYear)
                .verificationReport(verificationReport)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .totalEmissionsProvided(totalEmissionsProvided)
                .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email")
            .build();
        String crcoCodeNew = "crcoCodeNew";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(crcoCodeNew)
            .build();

        AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload requestActionPayload =
            mapper.toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(verificationSubmitRequestTaskPayload, accountInfo, requestActionPayloadType);

        assertEquals(requestActionPayloadType, requestActionPayload.getPayloadType());
        assertTrue(requestActionPayload.getReportingRequired());
        assertNull(requestActionPayload.getReportingObligationDetails());
        assertEquals(reportingYear, requestActionPayload.getReportingYear());
        assertEquals(verificationReport, requestActionPayload.getVerificationReport());
        assertEquals(serviceContactDetails, requestActionPayload.getServiceContactDetails());
        assertThat(requestActionPayload.getAerMonitoringPlanVersions()).isEmpty();
        assertEquals(totalEmissionsProvided, requestActionPayload.getTotalEmissionsProvided());
        assertNull(requestActionPayload.getNotCoveredChangesProvided());
        assertThat(requestActionPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);

        AviationAerUkEts requestActionPayloadAer = requestActionPayload.getAer();
        assertNotNull(requestActionPayloadAer);
        assertEquals(crcoCodeNew, requestActionPayloadAer.getOperatorDetails().getCrcoCode());

    }
}