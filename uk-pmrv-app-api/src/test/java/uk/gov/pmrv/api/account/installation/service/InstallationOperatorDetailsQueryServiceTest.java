package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountToInstallationOperatorDetailsMapper;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationOperatorDetailsQueryServiceTest {

	@InjectMocks
    private InstallationOperatorDetailsQueryService service;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;
    
    @Test
    void getInstallationOperatorDetails() {
    	Long accountId = 1L;
    	
    	LocationDTO location = LocationOnShoreDTO.builder()
                .gridReference("gridReference")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GB")
                        .postcode("postcode")
                        .build())
                .build();
        location.setType(LocationType.ONSHORE);

        InstallationAccountWithoutLeHoldingCompanyDTO accountDetailsDTO = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name("Account name")
                .siteName("siteName")
                .location(location)
                .emitterType(EmitterType.GHGE)
                .installationCategory(InstallationCategory.A)
                .emitterId("emitter id")
                .build();
        
        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(accountId)).thenReturn(accountDetailsDTO);
        
        InstallationOperatorDetails result = service.getInstallationOperatorDetails(accountId);
        
        assertThat(result).isEqualTo(Mappers
				.getMapper(InstallationAccountToInstallationOperatorDetailsMapper.class).toPermitInstallationOperatorDetails(accountDetailsDTO));
        verify(installationAccountQueryService, times(1)).getAccountWithoutLeHoldingCompanyDTOById(accountId);
    }

    @Test
    void getInstallationCategory() {
        Long accountId = 1L;
        InstallationCategory installationCategory = InstallationCategory.N_A;

        when(installationAccountQueryService.getAccountInstallationCategoryById(accountId))
                .thenReturn(installationCategory);

        InstallationCategory result = service.getInstallationCategory(accountId);

        assertThat(result).isEqualTo(installationCategory);
    }
}
