package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationTeamLeader;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerifierDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaVerifyMapperTest {

    private final AviationAerCorsiaVerifyMapper mapper = Mappers.getMapper(AviationAerCorsiaVerifyMapper.class);

    @Test
    void toAviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload() {

        Long vdId = 1L;
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
                .name("service contact name")
                .email("service contact email")
                .roleCode("service contact role code")
                .build();
        final Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");

        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
                .aer(AviationAerCorsia.builder()
                        .build())
                .aerAttachments(aerAttachments)
                .build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationBodyId(vdId)
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .name("VB name")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("country")
                                .postcode("postcode")
                                .build())
                        .build())
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .build())
                .build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
                .operatorName("operator name")
                .crcoCode("crco code")
                .serviceContactDetails(serviceContactDetails)
                .build();
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(100);
        BigDecimal totalOffsetEmissionsProvided = BigDecimal.valueOf(50);
        Year reportingYear = Year.of(2023);
        RequestTaskPayloadType payloadType = RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD;

        final AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload actualRequestTaskPayload =
                mapper.toAviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload(requestPayload,
                        verificationReport, aviationAccountInfo, totalEmissionsProvided, totalOffsetEmissionsProvided, reportingYear, payloadType);

        assertEquals(payloadType, actualRequestTaskPayload.getPayloadType());
        assertNull(actualRequestTaskPayload.getReportingObligationDetails());
        assertEquals(reportingYear, actualRequestTaskPayload.getReportingYear());
        assertEquals(verificationReport, actualRequestTaskPayload.getVerificationReport());
        assertEquals(serviceContactDetails, actualRequestTaskPayload.getServiceContactDetails());
        assertThat(actualRequestTaskPayload.getAerMonitoringPlanVersions()).isEmpty();
        assertThat(actualRequestTaskPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);

        AviationAerCorsia requestActionPayloadAer = actualRequestTaskPayload.getAer();
        assertNotNull(requestActionPayloadAer);
    }

    @Test
    void toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload() {
        RequestActionPayloadType requestActionPayloadType = RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD;
        AviationAerCorsia aer = AviationAerCorsia.builder()
                .operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("operatorName")
                        .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                        .build())
                .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .verifierDetails(AviationAerCorsiaVerifierDetails.builder().verificationTeamLeader(
                                        AviationAerCorsiaVerificationTeamLeader.builder()
                                                .email("email")
                                                .build())
                                .build()
                        )
                        .build())
                .build();
        Year reportingYear = Year.of(2022);
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload =
                AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.builder()
                        .reportingRequired(Boolean.TRUE)
                        .reportingYear(reportingYear)
                        .verificationReport(verificationReport)
                        .aer(aer)
                        .aerAttachments(aerAttachments)
                        .totalEmissionsProvided(BigDecimal.TEN)
                        .totalOffsetEmissionsProvided(BigDecimal.ONE)
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

        AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload requestActionPayload =
                mapper.toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload(verificationSubmitRequestTaskPayload, accountInfo, requestActionPayloadType);

        assertEquals(requestActionPayloadType, requestActionPayload.getPayloadType());
        assertTrue(requestActionPayload.getReportingRequired());
        assertNull(requestActionPayload.getReportingObligationDetails());
        assertEquals(reportingYear, requestActionPayload.getReportingYear());
        assertEquals(verificationReport, requestActionPayload.getVerificationReport());
        assertEquals(serviceContactDetails, requestActionPayload.getServiceContactDetails());
        assertThat(requestActionPayload.getAerMonitoringPlanVersions()).isEmpty();
        assertThat(requestActionPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(requestActionPayload.getTotalEmissionsProvided()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(requestActionPayload.getTotalOffsetEmissionsProvided()).isEqualByComparingTo(BigDecimal.ONE);

        AviationAerCorsia requestActionPayloadAer = requestActionPayload.getAer();
        assertNotNull(requestActionPayloadAer);
    }
}
