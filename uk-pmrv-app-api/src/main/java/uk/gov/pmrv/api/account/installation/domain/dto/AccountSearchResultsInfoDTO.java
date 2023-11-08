package uk.gov.pmrv.api.account.installation.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;

@Getter
@EqualsAndHashCode
public class AccountSearchResultsInfoDTO {
    
	private Long id;
	private String name;
	private String emitterId;
	private InstallationAccountStatus status;
	private String legalEntityName;
	
    public AccountSearchResultsInfoDTO(Long id, String name, String emitterId, String status, String legalEntityName) {
        this.id = id;
        this.name = name;
        this.emitterId = emitterId;
        this.status = InstallationAccountStatus.valueOf(status);
        this.legalEntityName = legalEntityName;
    }
	
}
