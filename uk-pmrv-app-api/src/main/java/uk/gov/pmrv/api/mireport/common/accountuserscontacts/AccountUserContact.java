package uk.gov.pmrv.api.mireport.common.accountuserscontacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class AccountUserContact {
    private String userId;

    @JsonProperty(value = "Account type")
    private String accountType;

    @JsonProperty(value = "Account ID")
    private String accountId;

    @JsonProperty(value = "Account name")
    private String accountName;

    @JsonProperty(value = "Account status")
    private String accountStatus;

    @JsonProperty(value = "Permit ID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonProperty(value = "Permit type/Account category")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Is User Primary contact?")
    private Boolean primaryContact;

    @JsonProperty(value = "Is User Secondary contact?")
    private Boolean secondaryContact;

    @JsonProperty(value = "Is User Financial contact?")
    private Boolean financialContact;

    @JsonProperty(value = "Is User Service contact?")
    private Boolean serviceContact;

    @JsonProperty(value = "User status")
    private String authorityStatus;

    @JsonProperty(value = "Name")
    @JsonInclude(Include.NON_NULL)
    private String name;

    @JsonProperty(value = "Telephone")
    @JsonInclude(Include.NON_NULL)
    private String telephone;

    @JsonProperty(value = "Last logon")
    @JsonInclude(Include.NON_NULL)
    private String lastLogon;

    @JsonProperty(value = "Email")
    @JsonInclude(Include.NON_NULL)
    private String email;

    @JsonProperty(value = "User role")
    private String role;

    public static final List<String> getColumnNames() {
        return List.of("Account type", "Account ID", "Account name", "Account status", "Permit ID", "Permit type/Account category",
                "Legal Entity name", "Is User Primary contact?", "Is User Secondary contact?", "Is User Financial contact?", "Is User Service contact?",
                "User status", "Name", "Telephone", "Last logon", "Email", "User role");
    }
}
