package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountTransferringArchiveRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = InstallationAccountOpeningApplicationRequestTaskPayload.class, value = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD"),
                @DiscriminatorMapping(schema = InstallationAccountTransferringArchiveRequestTaskPayload.class, value = "INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitIssuanceApplicationSubmitRequestTaskPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReviewRequestTaskPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReviewRequestTaskPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReviewRequestTaskPayload.class, value = "PERMIT_ISSUANCE_WAIT_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReviewRequestTaskPayload.class, value = "PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitSurrenderApplicationReviewRequestTaskPayload.class, value = "PERMIT_SURRENDER_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationReviewRequestTaskPayload.class, value = "PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationReviewRequestTaskPayload.class, value = "PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationSubmitRequestTaskPayload.class, value = "PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitCessationSubmitRequestTaskPayload.class, value = "PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitNotificationApplicationReviewRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationReviewRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationReviewRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationSubmitRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpApplicationReviewRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationWaitForFollowUpRequestTaskPayload.class, value = "PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitCessationSubmitRequestTaskPayload.class, value = "PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, value = "PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, value = "PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationApplicationSubmitRequestTaskPayload.class, value = "PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationWaitForAppealRequestTaskPayload.class, value = "PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationSubmitRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationReviewRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationReviewRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationReviewRequestTaskPayload.class, value = "PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationAmendsSubmitRequestTaskPayload.class, value = "PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationReviewRequestTaskPayload.class, value = "PERMIT_VARIATION_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitTransferAApplicationRequestTaskPayload.class, value = "PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationReviewRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationReviewRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationAmendsSubmitRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationReviewRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_WAIT_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationReviewRequestTaskPayload.class, value = "PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

                @DiscriminatorMapping(schema = NonComplianceApplicationSubmitRequestTaskPayload.class, value = "NON_COMPLIANCE_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, value = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, value = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, value = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceNoticeOfIntentRequestTaskPayload.class, value = "NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceNoticeOfIntentRequestTaskPayload.class, value = "NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceNoticeOfIntentRequestTaskPayload.class, value = "NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceCivilPenaltyRequestTaskPayload.class, value = "NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceCivilPenaltyRequestTaskPayload.class, value = "NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceCivilPenaltyRequestTaskPayload.class, value = "NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceFinalDeterminationRequestTaskPayload.class, value = "NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD"),

                @DiscriminatorMapping(schema = NerApplicationSubmitRequestTaskPayload.class, value = "NER_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationReviewRequestTaskPayload.class, value = "NER_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationReviewRequestTaskPayload.class, value = "NER_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationReviewRequestTaskPayload.class, value = "NER_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationAmendsSubmitRequestTaskPayload.class, value = "NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationReviewRequestTaskPayload.class, value = "NER_WAIT_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = NerAuthorityResponseRequestTaskPayload.class, value = "NER_AUTHORITY_RESPONSE_PAYLOAD"),

                @DiscriminatorMapping(schema = DoalApplicationSubmitRequestTaskPayload.class, value = "DOAL_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationSubmitRequestTaskPayload.class, value = "DOAL_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationSubmitRequestTaskPayload.class, value = "DOAL_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalAuthorityResponseRequestTaskPayload.class, value = "DOAL_AUTHORITY_RESPONSE_PAYLOAD"),

                @DiscriminatorMapping(schema = AerApplicationSubmitRequestTaskPayload.class, value = "AER_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationVerificationSubmitRequestTaskPayload.class, value = "AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationReviewRequestTaskPayload.class, value = "AER_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationAmendsSubmitRequestTaskPayload.class, value = "AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationReviewRequestTaskPayload.class, value = "AER_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = VirApplicationSubmitRequestTaskPayload.class, value = "VIR_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = VirApplicationReviewRequestTaskPayload.class, value = "VIR_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = VirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, value = "VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

                @DiscriminatorMapping(schema = AirApplicationSubmitRequestTaskPayload.class, value = "AIR_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AirApplicationReviewRequestTaskPayload.class, value = "AIR_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, value = "AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

                @DiscriminatorMapping(schema = DreApplicationSubmitRequestTaskPayload.class, value = "DRE_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = DreApplicationSubmitRequestTaskPayload.class, value = "DRE_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = DreApplicationSubmitRequestTaskPayload.class, value = "DRE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

                @DiscriminatorMapping(schema = SystemMessageNotificationRequestTaskPayload.class, value = "SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD"),

                @DiscriminatorMapping(schema = RfiResponseSubmitRequestTaskPayload.class, value = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = RdeForceDecisionRequestTaskPayload.class, value = "RDE_WAIT_FOR_RESPONSE_PAYLOAD"),
                @DiscriminatorMapping(schema = RdeResponseRequestTaskPayload.class, value = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = PaymentMakeRequestTaskPayload.class, value = "PAYMENT_MAKE_PAYLOAD"),
                @DiscriminatorMapping(schema = PaymentTrackRequestTaskPayload.class, value = "PAYMENT_TRACK_PAYLOAD"),
                @DiscriminatorMapping(schema = PaymentConfirmRequestTaskPayload.class, value = "PAYMENT_CONFIRM_PAYLOAD"),

                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD"),

                @DiscriminatorMapping(schema = WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = ReturnOfAllowancesApplicationSubmitRequestTaskPayload.class, value = "RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.class, value = "RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload.class, value = "RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload.class, value = "RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationSubmitRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationSubmitRequestTaskPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationReviewRequestTaskPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationReviewRequestTaskPayload.class, value = "AVIATION_AER_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, value = "AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, value = "AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, value = "AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationVirApplicationSubmitRequestTaskPayload.class, value = "AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationVirApplicationReviewRequestTaskPayload.class, value = "AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, value = "AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAccountClosureSubmitRequestTaskPayload.class, value = "AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationSubmitRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, value = "EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationSubmitRequestTaskPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationReviewRequestTaskPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationReviewRequestTaskPayload.class, value = "AVIATION_AER_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),


        },
        discriminatorProperty = "payloadType"
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningApplicationRequestTaskPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationAccountTransferringArchiveRequestTaskPayload.class, name = "INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitIssuanceApplicationSubmitRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationReviewRequestTaskPayload.class, name = "PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationReviewRequestTaskPayload.class, name = "PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderApplicationSubmitRequestTaskPayload.class, name = "PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitCessationSubmitRequestTaskPayload.class, name = "PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationReviewRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationWaitForFollowUpRequestTaskPayload.class, name = "PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitCessationSubmitRequestTaskPayload.class, name = "PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, name = "PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationPeerReviewRequestTaskPayload.class, name = "PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationWaitForAppealRequestTaskPayload.class, name = "PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationReviewRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationReviewRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationReviewRequestTaskPayload.class, name = "PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationApplicationReviewRequestTaskPayload.class, name = "PERMIT_VARIATION_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = PermitTransferAApplicationRequestTaskPayload.class, name = "PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationReviewRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationReviewRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationAmendsSubmitRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationReviewRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBApplicationReviewRequestTaskPayload.class, name = "PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = NonComplianceApplicationSubmitRequestTaskPayload.class, name = "NON_COMPLIANCE_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceDailyPenaltyNoticeRequestTaskPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNoticeOfIntentRequestTaskPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNoticeOfIntentRequestTaskPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceNoticeOfIntentRequestTaskPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceCivilPenaltyRequestTaskPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceCivilPenaltyRequestTaskPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceCivilPenaltyRequestTaskPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceFinalDeterminationRequestTaskPayload.class, name = "NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD"),

    @JsonSubTypes.Type(value = NerApplicationSubmitRequestTaskPayload.class, name = "NER_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = NerApplicationReviewRequestTaskPayload.class, name = "NER_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NerApplicationReviewRequestTaskPayload.class, name = "NER_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NerApplicationReviewRequestTaskPayload.class, name = "NER_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = NerApplicationAmendsSubmitRequestTaskPayload.class, name = "NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = NerApplicationReviewRequestTaskPayload.class, name = "NER_WAIT_FOR_AMENDS_PAYLOAD"),
    @JsonSubTypes.Type(value = NerAuthorityResponseRequestTaskPayload.class, name = "NER_AUTHORITY_RESPONSE_PAYLOAD"),

    @JsonSubTypes.Type(value = DoalApplicationSubmitRequestTaskPayload.class, name = "DOAL_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalApplicationSubmitRequestTaskPayload.class, name = "DOAL_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalApplicationSubmitRequestTaskPayload.class, name = "DOAL_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalAuthorityResponseRequestTaskPayload.class, name = "DOAL_AUTHORITY_RESPONSE_PAYLOAD"),

    @JsonSubTypes.Type(value = AerApplicationSubmitRequestTaskPayload.class, name = "AER_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationVerificationSubmitRequestTaskPayload.class, name = "AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationReviewRequestTaskPayload.class, name = "AER_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationAmendsSubmitRequestTaskPayload.class, name = "AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AerApplicationReviewRequestTaskPayload.class, name = "AER_WAIT_FOR_AMENDS_PAYLOAD"),


    @JsonSubTypes.Type(value = VirApplicationSubmitRequestTaskPayload.class, name = "VIR_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = VirApplicationReviewRequestTaskPayload.class, name = "VIR_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = VirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, name = "VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = AirApplicationSubmitRequestTaskPayload.class, name = "AIR_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AirApplicationReviewRequestTaskPayload.class, name = "AIR_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, name = "AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = DreApplicationSubmitRequestTaskPayload.class, name = "DRE_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = DreApplicationSubmitRequestTaskPayload.class, name = "DRE_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = DreApplicationSubmitRequestTaskPayload.class, name = "DRE_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = SystemMessageNotificationRequestTaskPayload.class, name = "SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD"),

    @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskPayload.class, name = "RDE_WAIT_FOR_RESPONSE_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeResponseRequestTaskPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = PaymentMakeRequestTaskPayload.class, name = "PAYMENT_MAKE_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentTrackRequestTaskPayload.class, name = "PAYMENT_TRACK_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentConfirmRequestTaskPayload.class, name = "PAYMENT_CONFIRM_PAYLOAD"),

    @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = ReturnOfAllowancesApplicationSubmitRequestTaskPayload.class, name = "RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.class, name = "RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload.class, name = "RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload.class, name = "RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationSubmitRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAerUkEtsApplicationSubmitRequestTaskPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsApplicationReviewRequestTaskPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsApplicationReviewRequestTaskPayload.class, name = "AVIATION_AER_UKETS_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, name = "AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, name = "AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationDreUkEtsApplicationSubmitRequestTaskPayload.class, name = "AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationVirApplicationSubmitRequestTaskPayload.class, name = "AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirApplicationReviewRequestTaskPayload.class, name = "AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.class, name = "AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAccountClosureSubmitRequestTaskPayload.class, name = "AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationSubmitRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationReviewRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),

    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.class, name = "EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW_REGULATOR_LED_PAYLOAD"),

    @JsonSubTypes.Type(value = AviationAerCorsiaApplicationSubmitRequestTaskPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaApplicationReviewRequestTaskPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaApplicationReviewRequestTaskPayload.class, name = "AVIATION_AER_CORSIA_WAIT_FOR_AMENDS_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskPayload {

    private RequestTaskPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public Set<UUID> getReferencedAttachmentIds() {
        return Collections.emptySet();
    }

    @JsonIgnore
    public void removeAttachments(final Collection<UUID> uuids) {

        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        this.getAttachments().keySet().removeIf(uuids::contains);
    }
}
