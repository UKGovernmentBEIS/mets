package uk.gov.pmrv.api.account.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountDetailsHistoryCategory;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsHistoryDTO {

    private String changedBy;
    private LocalDateTime creationDate;
    private AccountDetailsHistoryCategory category;
    private JsonNode previousValue;
    private JsonNode newValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;

}
