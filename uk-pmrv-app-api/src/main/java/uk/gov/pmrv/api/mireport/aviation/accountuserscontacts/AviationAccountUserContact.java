package uk.gov.pmrv.api.mireport.aviation.accountuserscontacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountUserContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"userId", "accountType", "accountId", "accountName", "accountStatus", "permitId", "permitType", "legalEntityName", "primaryContact", "secondaryContact", "financialContact", "serviceContact", "authorityStatus", "name", "telephone", "lastLogon", "email", "role", "crcoCode"})
public class AviationAccountUserContact extends AccountUserContact {
    @JsonProperty(value = "Account type")
    private String accountType;

    @JsonProperty(value = "Permit ID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonProperty(value = "Permit type/Account category")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Is User Secondary contact?")
    private Boolean secondaryContact;

    @JsonProperty(value = "Is User Financial contact?")
    private Boolean financialContact;

    @JsonProperty(value = "Is User Service contact?")
    private Boolean serviceContact;

    @JsonProperty(value = "CRCO code")
    private String crcoCode;

    public static List<String> getColumnNames() {
        return List.of("Account type", "Account ID", "Account name", "Account status", "Permit ID", "Permit type/Account category", "Legal Entity name", "Is User Primary contact?", "Is User Secondary contact?", "Is User Financial contact?", "Is User Service contact?", "User status", "Name", "Telephone", "Last logon", "Email", "User role", "CRCO code");
    }
}
