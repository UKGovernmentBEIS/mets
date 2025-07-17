package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestCreateActionPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InstallationAccountOpeningSubmitApplicationCreateActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD"),
    @JsonSubTypes.Type(value = BatchReissueRequestCreateActionPayload.class, name = "PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = BatchReissueRequestCreateActionPayload.class, name = "EMP_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = ReportRelatedRequestCreateActionPayload.class, name = "REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = DoalRequestCreateActionPayload.class, name = "DOAL_REQUEST_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = InstallationAuditRequestCreateActionPayload.class, name = "INSTALLATION_AUDIT_REQUEST_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsiaAnnualOffsettingCreateActionPayload.class, name = "AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = AviationAerCorsia3YearPeriodCreateActionPayload.class, name = "AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CREATE_ACTION_PAYLOAD"),
    @JsonSubTypes.Type(value = RequestCreateActionEmptyPayload.class, name = "EMPTY_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestCreateActionPayload {

    private RequestCreateActionPayloadType payloadType;
}
