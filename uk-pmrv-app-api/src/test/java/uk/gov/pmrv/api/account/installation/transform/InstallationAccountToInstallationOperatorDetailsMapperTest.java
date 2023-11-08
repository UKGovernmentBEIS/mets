package uk.gov.pmrv.api.account.installation.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOffShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

import static org.assertj.core.api.Assertions.assertThat;

class InstallationAccountToInstallationOperatorDetailsMapperTest {

	private final InstallationAccountToInstallationOperatorDetailsMapper mapper = Mappers.getMapper(InstallationAccountToInstallationOperatorDetailsMapper.class);
	
	@Test
	void toPermitInstallationOperatorDetails_onshore() {
		LocationDTO location = LocationOnShoreDTO.builder()
				.type(LocationType.ONSHORE)
                .gridReference("gridReference")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GB")
                        .postcode("postcode")
                        .build())
                .build();

        InstallationAccountWithoutLeHoldingCompanyDTO accountDetailsDTO = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name("Account name")
                .siteName("siteName")
                .location(location)
                .emitterType(EmitterType.GHGE)
                .installationCategory(InstallationCategory.A)
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                		.name("leName")
                		.type(LegalEntityType.PARTNERSHIP)
                		.referenceNumber("refNum")
                		.address(AddressDTO.builder()
	                        .line1("line1")
	                        .city("city")
	                        .country("GB")
	                        .postcode("postcode")
	                        .build())
                		.build())
                .build();
        
        InstallationOperatorDetails result = mapper.toPermitInstallationOperatorDetails(accountDetailsDTO);
        
        assertThat(result.getInstallationLocation()).isEqualTo(accountDetailsDTO.getLocation());
        assertThat(result.getInstallationName()).isEqualTo(accountDetailsDTO.getName());
        assertThat(result.getOperator()).isEqualTo(accountDetailsDTO.getLegalEntity().getName());
        assertThat(result.getOperatorDetailsAddress().getLine1()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getLine1());
        assertThat(result.getOperatorDetailsAddress().getLine2()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getLine2());
        assertThat(result.getOperatorDetailsAddress().getCity()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getCity());
        assertThat(result.getOperatorDetailsAddress().getPostcode()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getPostcode());
	}
	
	@Test
	void toPermitInstallationOperatorDetails_offshore() {
		LocationDTO location = LocationOffShoreDTO.builder()
				.type(LocationType.OFFSHORE)
				.latitude(CoordinatesDTO.builder().cardinalDirection(CardinalDirection.EAST).build())
				.longitude(CoordinatesDTO.builder().cardinalDirection(CardinalDirection.NORTH).build())
                .build();

        InstallationAccountWithoutLeHoldingCompanyDTO accountDetailsDTO = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name("Account name")
                .siteName("siteName")
                .location(location)
                .emitterType(EmitterType.GHGE)
                .installationCategory(InstallationCategory.A)
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                		.name("leName")
                		.type(LegalEntityType.PARTNERSHIP)
                		.referenceNumber("refNum")
                		.address(AddressDTO.builder()
	                        .line1("line1")
	                        .city("city")
	                        .country("GB")
	                        .postcode("postcode")
	                        .build())
                		.build())
                .build();
        
        InstallationOperatorDetails result = mapper.toPermitInstallationOperatorDetails(accountDetailsDTO);
        
        assertThat(result.getInstallationLocation()).isEqualTo(accountDetailsDTO.getLocation());
        assertThat(result.getInstallationName()).isEqualTo(accountDetailsDTO.getName());
        assertThat(result.getOperator()).isEqualTo(accountDetailsDTO.getLegalEntity().getName());
        assertThat(result.getOperatorDetailsAddress().getLine1()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getLine1());
        assertThat(result.getOperatorDetailsAddress().getLine2()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getLine2());
        assertThat(result.getOperatorDetailsAddress().getCity()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getCity());
        assertThat(result.getOperatorDetailsAddress().getPostcode()).isEqualTo(accountDetailsDTO.getLegalEntity().getAddress().getPostcode());
	}
}
