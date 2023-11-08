package uk.gov.pmrv.api.mireport.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.customreport.CustomMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reportType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmptyMiReportParams.class, name = "LIST_OF_ACCOUNTS_USERS_CONTACTS"),
    @JsonSubTypes.Type(value = ExecutedRequestActionsMiReportParams.class, name = "COMPLETED_WORK"),
    @JsonSubTypes.Type(value = OutstandingRegulatorRequestTasksMiReportParams.class, name = "REGULATOR_OUTSTANDING_REQUEST_TASKS"),
    @JsonSubTypes.Type(value = EmptyMiReportParams.class, name = "LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS"),
    @JsonSubTypes.Type(value = EmptyMiReportParams.class, name = "LIST_OF_VERIFICATION_BODY_USERS"),
    @JsonSubTypes.Type(value = CustomMiReportParams.class, name = "CUSTOM")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MiReportParams {

    private MiReportType reportType;
}
