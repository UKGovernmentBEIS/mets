package uk.gov.pmrv.api.account.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonSubTypes({
	@JsonSubTypes.Type(value = InstallationAccountDTO.class)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AccountDTO {
	
	private Long id;
	
	@NotNull
	private AccountType accountType;

	@NotBlank
	@Size(max = 255)
	private String name;
	
	@NotNull
    private EmissionTradingScheme emissionTradingScheme;

	@NotNull
	private CompetentAuthorityEnum competentAuthority;

	@NotNull
	private LocalDate commencementDate;

    @Valid
	private LegalEntityDTO legalEntity;

    @Valid
	private LocationDTO location;
	
	private LocalDateTime acceptedDate;

	private Long sopId;

	private Integer registryId;
}
