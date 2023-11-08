package uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAssignedRegulatorSiteContact {

    @JsonProperty(value = "Account ID")
    private String accountId;

    @JsonProperty(value = "Account type")
    private String accountType;

    @JsonProperty(value = "Account name")
    private String accountName;

    @JsonProperty(value = "Account status")
    private String accountStatus;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "User status")
    private String authorityStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userId;

    @JsonProperty(value = "Assigned regulator")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String assignedRegulatorName;

    public static final List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status",
                "Legal Entity name", "User status", "Assigned regulator");
    }
}
