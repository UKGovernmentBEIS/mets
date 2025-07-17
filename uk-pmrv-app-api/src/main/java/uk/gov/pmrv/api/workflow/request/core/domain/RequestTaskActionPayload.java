package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSubmitApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSubmitApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNotifyOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSkipReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveAuthorityResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpExtendDateRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferASaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningAmendApplicationRequestTaskActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationReviewRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSaveApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSaveCessationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpExtendDateRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveResponseRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationWithdrawRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSaveCessationRequestTaskActionPayload.class, name = "PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveApplicationReviewRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_VARIATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitTransferASaveApplicationRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_A_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBSaveApplicationRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = NonComplianceCloseApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_CLOSE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceSaveApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNotifyOperatorRequestTaskActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNotifyOperatorRequestTaskActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNotifyOperatorRequestTaskActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload.class, name = "NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION_PAYLOAD"),

    @JsonSubTypes.Type(value = NerSaveApplicationRequestTaskActionPayload.class, name = "NER_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSaveApplicationReviewRequestTaskActionPayload.class, name = "NER_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "NER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSaveReviewDeterminationRequestTaskActionPayload.class, name = "NER_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "NER_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "NER_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSaveApplicationAmendRequestTaskActionPayload.class, name = "NER_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSubmitApplicationAmendRequestTaskActionPayload.class, name = "NER_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "NER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NerSaveAuthorityResponseRequestTaskActionPayload.class, name = "NER_SAVE_AUTHORITY_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "NER_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = DoalSaveApplicationRequestTaskActionPayload.class, name = "DOAL_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "DOAL_SUBMIT_APPLICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "DOAL_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "DOAL_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalSaveAuthorityResponseTaskActionPayload.class, name = "DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = AerSaveApplicationRequestTaskActionPayload.class, name = "AER_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AER_REQUEST_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AER_REQUEST_AMENDS_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AerSaveApplicationVerificationRequestTaskActionPayload.class, name = "AER_SAVE_APPLICATION_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AerSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "AER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = RequestTaskActionEmptyPayload.class, name = "AER_COMPLETE_REVIEW"),
    @JsonSubTypes.Type(value = AerSaveApplicationAmendRequestTaskActionPayload.class, name = "AER_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AerSubmitApplicationAmendRequestTaskActionPayload.class, name = "AER_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationSkipReviewRequestTaskActionPayload.class, name = "AER_SKIP_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AerVerificationReturnToOperatorRequestTaskActionPayload.class, name = "AER_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD"),


    @JsonSubTypes.Type(value = DreSaveApplicationRequestTaskActionPayload.class, name = "DRE_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "DRE_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "DRE_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "DRE_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = VirSaveApplicationRequestTaskActionPayload.class, name = "VIR_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = VirSaveReviewRequestTaskActionPayload.class, name = "VIR_SAVE_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = VirSaveRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),
    @JsonSubTypes.Type(value = VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = AirSaveApplicationRequestTaskActionPayload.class, name = "AIR_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AirSaveReviewRequestTaskActionPayload.class, name = "AIR_SAVE_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = AirSaveRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),
    @JsonSubTypes.Type(value = AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = RfiSubmitRequestTaskActionPayload.class, name = "RFI_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskActionPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = RdeSubmitRequestTaskActionPayload.class, name = "RDE_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskActionPayload.class, name = "RDE_FORCE_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeResponseSubmitRequestTaskActionPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PaymentMarkAsReceivedRequestTaskActionPayload.class, name = "PAYMENT_MARK_AS_RECEIVED_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentCancelRequestTaskActionPayload.class, name = "PAYMENT_CANCEL_PAYLOAD"),

    @JsonSubTypes.Type(value = WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION_PAYLOAD"),

    @JsonSubTypes.Type(value = ReturnOfAllowancesSaveApplicationRequestTaskActionPayload.class, name = "RETURN_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload.class, name = "RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.class, name = "RETURN_OF_ALLOWANCES_RETURNED_SUBMIT_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "RETURN_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = InstallationAuditApplicationSaveRequestTaskActionPayload.class, name = "INSTALLATION_AUDIT_APPLICATION_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "INSTALLATION_AUDIT_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "INSTALLATION_AUDIT_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationInspectionOperatorRespondSaveRequestTaskActionPayload.class, name = "INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE_PAYLOAD"),

    @JsonSubTypes.Type(value = InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload.class, name = "INSTALLATION_ONSITE_INSPECTION_APPLICATION_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "INSTALLATION_ONSITE_INSPECTION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "INSTALLATION_ONSITE_INSPECTION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationInspectionOperatorRespondSaveRequestTaskActionPayload.class, name = "INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE_PAYLOAD"),

    @JsonSubTypes.Type(value = BDRApplicationSaveRequestTaskActionPayload.class, name = "BDR_APPLICATION_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationSubmitToVerifierRequestTaskActionPayload.class, name = "BDR_SUBMIT_TO_VERIFIER_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationVerificationSaveRequestTaskActionPayload.class, name = "BDR_APPLICATION_SAVE_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload.class, name = "BDR_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload.class, name = "BDR_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationAmendsSaveRequestTaskActionPayload.class, name = "BDR_APPLICATION_AMENDS_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload.class, name = "BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationAmendsSubmitRequestTaskActionPayload.class, name = "BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = BDRApplicationRegulatorReviewSaveTaskActionPayload.class, name = "BDR_REGULATOR_REVIEW_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "BDR_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "BDR_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = PermanentCessationSaveApplicationRequestTaskActionPayload.class, name = "PERMANENT_CESSATION_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "PERMANENT_CESSATION_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "PERMANENT_CESSATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = ALRApplicationSaveRequestTaskActionPayload.class, name = "ALR_APPLICATION_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = ALRApplicationSubmitToVerifierRequestTaskActionPayload.class, name = "ALR_SUBMIT_TO_VERIFIER_PAYLOAD"),
    @JsonSubTypes.Type(value = ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload.class, name = "ALR_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = ALRApplicationVerificationSaveRequestTaskActionPayload.class, name = "ALR_APPLICATION_SAVE_VERIFICATION_PAYLOAD"),



    @JsonSubTypes.Type(value = RequestTaskActionEmptyPayload.class, name = "EMPTY_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload.class, name = "EMP_ISSUANCE_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveApplicationRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload.class, name = "EMP_VARIATION_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAerUkEtsSaveApplicationRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsSubmitApplicationRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SUBMIT_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_REQUEST_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerSubmitApplicationAmendRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsVerificationReturnToOperatorRequestTaskActionPayload.class, name = "AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationDreUkEtsSaveApplicationRequestTaskActionPayload.class, name = "AVIATION_DRE_UKETS_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationVirSaveApplicationRequestTaskActionPayload.class, name = "AVIATION_VIR_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirSaveReviewRequestTaskActionPayload.class, name = "AVIATION_VIR_SAVE_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.class, name = "AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAccountClosureSaveRequestTaskActionPayload.class, name = "AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload.class, name = "EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveApplicationRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAerCorsiaSaveApplicationRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaSubmitApplicationRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SUBMIT_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_REQUEST_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerApplicationRequestVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerSubmitApplicationAmendRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaVerificationReturnToOperatorRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_PEER_REVIEW_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationDoECorsiaSubmitSaveRequestTaskActionPayload.class, name = "AVIATION_DOE_CORSIA_SUBMIT_SAVE_PAYLOAD"),
    @JsonSubTypes.Type(value = NotifyOperatorForDecisionRequestTaskActionPayload.class, name = "AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewRequestTaskActionPayload.class, name = "AVIATION_DOE_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PeerReviewDecisionRequestTaskActionPayload.class, name = "AVIATION_DOE_CORSIA_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD"),

})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskActionPayload {

    private RequestTaskActionPayloadType payloadType;
}
