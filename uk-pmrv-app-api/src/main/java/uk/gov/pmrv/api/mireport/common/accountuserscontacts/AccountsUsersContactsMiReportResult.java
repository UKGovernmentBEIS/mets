package uk.gov.pmrv.api.mireport.common.accountuserscontacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountsUsersContactsMiReportResult extends MiReportResult {

    private List<AccountUserContact> results;
}
