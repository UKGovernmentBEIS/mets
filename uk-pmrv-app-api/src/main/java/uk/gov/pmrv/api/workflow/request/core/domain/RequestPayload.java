package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningRequestPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitIssuanceRequestPayload.class, name = "PERMIT_ISSUANCE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitSurrenderRequestPayload.class, name = "PERMIT_SURRENDER_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitNotificationRequestPayload.class, name = "PERMIT_NOTIFICATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitRevocationRequestPayload.class, name = "PERMIT_REVOCATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitVariationRequestPayload.class, name = "PERMIT_VARIATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferARequestPayload.class, name = "PERMIT_TRANSFER_A_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = PermitTransferBRequestPayload.class, name = "PERMIT_TRANSFER_B_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = BatchReissueRequestPayload.class, name = "PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = NonComplianceRequestPayload.class, name = "NON_COMPLIANCE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = NerRequestPayload.class, name = "NER_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalRequestPayload.class, name = "DOAL_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AerRequestPayload.class, name = "AER_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = VirRequestPayload.class, name = "VIR_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = DreRequestPayload.class, name = "DRE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AirRequestPayload.class, name = "AIR_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = ReissueRequestPayload.class, name = "REISSUE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = SystemMessageNotificationRequestPayload.class, name = "SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = WithholdingOfAllowancesRequestPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = ReturnOfAllowancesRequestPayload.class, name = "RETURN_OF_ALLOWANCES_REQUEST_PAYLOAD"),

    // Aviation related request payloads
    @JsonSubTypes.Type(value = EmpIssuanceUkEtsRequestPayload.class, name = "EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationUkEtsRequestPayload.class, name = "EMP_VARIATION_UKETS_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerUkEtsRequestPayload.class, name = "AVIATION_AER_UKETS_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationDreUkEtsRequestPayload.class, name = "AVIATION_DRE_UKETS_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationVirRequestPayload.class, name = "AVIATION_VIR_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAccountClosureRequestPayload.class, name = "AVIATION_ACCOUNT_CLOSURE_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpIssuanceCorsiaRequestPayload.class, name = "EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = EmpVariationCorsiaRequestPayload.class, name = "EMP_VARIATION_CORSIA_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaRequestPayload.class, name = "AVIATION_AER_CORSIA_REQUEST_PAYLOAD"),
    @JsonSubTypes.Type(value = BatchReissueRequestPayload.class, name = "EMP_BATCH_REISSUE_REQUEST_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestPayload implements Payload {

    private RequestPayloadType payloadType;

    private String operatorAssignee;

    private String regulatorAssignee;
    
    private String verifierAssignee;

    private String regulatorReviewer;

    private String regulatorPeerReviewer;
    
    private Boolean paymentCompleted;
    
    private BigDecimal paymentAmount;
}
