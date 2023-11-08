package uk.gov.pmrv.api.mireport.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactsMiReportResult;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.common.customreport.CustomMiReportResult;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportResult;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksMiReportResult;
import uk.gov.pmrv.api.mireport.common.verificationbodyusers.VerificationBodyUsersMiReportResult;

import java.util.List;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = AccountsUsersContactsMiReportResult.class, value = "LIST_OF_ACCOUNTS_USERS_CONTACTS"),
                @DiscriminatorMapping(schema = ExecutedRequestActionsMiReportResult.class, value = "COMPLETED_WORK"),
                @DiscriminatorMapping(schema = OutstandingRequestTasksMiReportResult.class, value = "REGULATOR_OUTSTANDING_REQUEST_TASKS"),
                @DiscriminatorMapping(schema = AccountAssignedRegulatorSiteContactsMiReportResult.class, value = "LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS"),
                @DiscriminatorMapping(schema = VerificationBodyUsersMiReportResult.class, value = "LIST_OF_VERIFICATION_BODY_USERS"),
                @DiscriminatorMapping(schema = CustomMiReportResult.class, value = "CUSTOM")
        },
        discriminatorProperty = "reportType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reportType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AccountsUsersContactsMiReportResult.class, name = "LIST_OF_ACCOUNTS_USERS_CONTACTS"),
    @JsonSubTypes.Type(value = ExecutedRequestActionsMiReportResult.class, name = "COMPLETED_WORK"),
    @JsonSubTypes.Type(value = OutstandingRequestTasksMiReportResult.class, name = "REGULATOR_OUTSTANDING_REQUEST_TASKS"),
    @JsonSubTypes.Type(value = AccountAssignedRegulatorSiteContactsMiReportResult.class, name = "LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS"),
    @JsonSubTypes.Type(value = VerificationBodyUsersMiReportResult.class, name = "LIST_OF_VERIFICATION_BODY_USERS"),
    @JsonSubTypes.Type(value = CustomMiReportResult.class, name = "CUSTOM")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MiReportResult {

    @NotNull
    private MiReportType reportType;

    @NotNull
    private List<String> columnNames;
}
