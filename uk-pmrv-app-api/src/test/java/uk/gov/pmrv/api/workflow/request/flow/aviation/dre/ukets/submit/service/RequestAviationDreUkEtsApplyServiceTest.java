package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreUkEtsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationDreUkEtsApplyServiceTest {

    @Mock
    private AviationDreUkEtsValidatorService aviationDreUkEtsValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    private RequestAviationDreUkEtsApplyService service;

    @BeforeEach
    public void setup() {
        service = new RequestAviationDreUkEtsApplyService(aviationDreUkEtsValidatorService,
            decisionNotificationUsersValidator, aviationAccountQueryService);
    }

    @Test
    void applySaveAction() {
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .type(AviationDreDeterminationReasonType.VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER)
                .build())
            .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .totalReportableEmissions(BigDecimal.valueOf(1250.9))
            .fee(AviationDreFee.builder().chargeOperator(false).build())
            .build();
        AviationDreUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationDreUkEtsSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_DRE_UKETS_SAVE_APPLICATION_PAYLOAD)
                .dre(dre)
                .sectionCompleted(true)
                .build();

        AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
            .dre(AviationDre.builder()
                .determinationReason(AviationDreDeterminationReason.builder()
                    .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                    .build())
                .build())
            .sectionCompleted(false)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        //invoke
        service.applySaveAction(taskActionPayload, requestTask);

        AviationDreUkEtsApplicationSubmitRequestTaskPayload payloadSaved =
            (AviationDreUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD, payloadSaved.getPayloadType());
        assertEquals(dre, payloadSaved.getDre());
        Assertions.assertTrue(payloadSaved.getSectionCompleted());
    }

    @Test
    void applySubmitNotify_location_exists() {
        UUID att1 = UUID.randomUUID();
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .type(AviationDreDeterminationReasonType.VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER)
                .build())
            .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .totalReportableEmissions(BigDecimal.valueOf(1250.9))
            .fee(AviationDreFee.builder().chargeOperator(false).build())
            .build();

        AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
            .dre(dre)
            .dreAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        AviationDreUkEtsRequestPayload aviationDreRequestPayload = AviationDreUkEtsRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .accountId(1L)
                .payload(aviationDreRequestPayload)
                .build())
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory("signatory")
            .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        AviationAccountInfoDTO aviationAccountDTO = AviationAccountInfoDTO.builder()
            .location(LocationDTO.builder()
                .type(LocationType.ONSHORE)
                .build())
            .accountType(AccountType.AVIATION)
            .build();

        doNothing().when(aviationDreUkEtsValidatorService).validateAviationDre(any(AviationDre.class));
        when(decisionNotificationUsersValidator.areUsersValid(any(RequestTask.class), any(DecisionNotification.class), any(AppUser.class))).thenReturn(true);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(1L)).thenReturn(aviationAccountDTO);

        // Invoke
        service.applySubmitNotify(requestTask, decisionNotification, appUser);

        assertThat(aviationDreRequestPayload.getDre()).isEqualTo(dre);
        assertThat(aviationDreRequestPayload.getSectionCompleted()).isTrue();
        assertThat(aviationDreRequestPayload.getDreAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
    }

    @Test
    void applySubmitNotify_location_missing() {
        UUID att1 = UUID.randomUUID();
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .type(AviationDreDeterminationReasonType.VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER)
                .build())
            .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .totalReportableEmissions(BigDecimal.valueOf(1250.9))
            .fee(AviationDreFee.builder().chargeOperator(false).build())
            .build();

        AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
            .dre(dre)
            .dreAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        AviationDreUkEtsRequestPayload aviationDreRequestPayload = AviationDreUkEtsRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .accountId(1L)
                .payload(aviationDreRequestPayload)
                .build())
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory("signatory")
            .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        AviationAccountInfoDTO aviationAccountDTO = AviationAccountInfoDTO.builder()
            .accountType(AccountType.AVIATION)
            .build();

        doNothing().when(aviationDreUkEtsValidatorService).validateAviationDre(any(AviationDre.class));
        when(decisionNotificationUsersValidator.areUsersValid(any(RequestTask.class), any(DecisionNotification.class), any(AppUser.class))).thenReturn(true);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(1L)).thenReturn(aviationAccountDTO);

        assertThrows(
            BusinessException.class, () -> service.applySubmitNotify(requestTask, decisionNotification, appUser));
    }

    @Test
    void requestPeerReview() {
        UUID att1 = UUID.randomUUID();
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .type(AviationDreDeterminationReasonType.VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER)
                .build())
            .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .totalReportableEmissions(BigDecimal.valueOf(1250.9))
            .fee(AviationDreFee.builder().chargeOperator(false).build())
            .build();

        AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.DRE_APPLICATION_PEER_REVIEW_PAYLOAD)
            .dre(dre)
            .dreAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(false)
            .build();

        AviationDreUkEtsRequestPayload aviationDreRequestPayload = AviationDreUkEtsRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .payload(aviationDreRequestPayload)
                .build())
            .payload(requestTaskPayload).build();

        String peerReviewer = "peerReviewer";

        // Invoke
        service.requestPeerReview(requestTask, peerReviewer);

        assertThat(aviationDreRequestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(aviationDreRequestPayload.getDre()).isEqualTo(dre);
        assertThat(aviationDreRequestPayload.getSectionCompleted()).isFalse();
        assertThat(aviationDreRequestPayload.getDreAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
        assertThat(aviationDreRequestPayload.getPaymentAmount()).isNull();
    }
}