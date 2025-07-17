package uk.gov.pmrv.api.competentauthority;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PmrvCompetentAuthorityServiceTest {

	@InjectMocks
    private PmrvCompetentAuthorityService competentAuthorityService;

	@Test
	void getCompetentAuthority() {
		CompetentAuthorityEnum competentAuthorityEnum = CompetentAuthorityEnum.ENGLAND;

		assertThrows(UnsupportedOperationException.class, () ->
           competentAuthorityService.getCompetentAuthorityDTO(competentAuthorityEnum));

	}
}
