//package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.mapper;
//
//import org.junit.jupiter.api.Test;
//import org.mapstruct.factory.Mappers;
//import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
//import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
//import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
//import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
//import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
//
//import java.time.LocalDate;
//import java.time.Year;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class AerAviationCorsiaAnnualOffsettingMapperTest {
//
//    private final AerAviationCorsiaAnnualOffsettingMapper mapper = Mappers.getMapper(AerAviationCorsiaAnnualOffsettingMapper.class);
//
//    @Test
//    void toAviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload() {
//
//        final Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();
//        aviationAerCorsiaAnnualOffsettingSectionsCompleted.put("test",false);
//
//
//        AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = AviationAerCorsiaAnnualOffsettingRequestPayload.builder()
//                .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting.builder()
//                        .schemeYear(Year.of(LocalDate.now().plusDays(1).getYear()))
//                        .calculatedAnnualOffsetting(23)
//                        .totalChapter(2)
//                        .sectorGrowth(1.2)
//                        .build())
//                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(aviationAerCorsiaAnnualOffsettingSectionsCompleted)
//                .decisionNotification(DecisionNotification.builder()
//                        .signatory("signatory")
//                        .build())
//                .build();
//
//        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload actionPayload =
//                mapper.toAviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload(requestPayload,
//                        RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD);
//
//        assertThat(actionPayload.getPayloadType())
//                .isEqualTo(RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD);
//        assertThat(actionPayload.getDecisionNotification()).isEqualTo(requestPayload.getDecisionNotification());
//        assertThat(actionPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted()).isEqualTo(requestPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
//    }
//}