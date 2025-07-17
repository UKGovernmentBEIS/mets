package uk.gov.pmrv.api.user.core.domain.model.core;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUserTest {

    @Test
    void getAccounts() {
        Long accountId = 1L;
        AppUser appUser = createOperatorUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().accountId(accountId).build()
        );
        appUser.setAuthorities(authorities);

        Set<Long> accounts = appUser.getAccounts();

        assertThat(accounts).containsOnly(accountId);
    }

    @Test
    void getAccounts_no_authorities() {
        AppUser appUser = createOperatorUser("user");

        Set<Long> accounts = appUser.getAccounts();

        assertThat(accounts).isEmpty();
    }

    @Test
    void getAccounts_no_account_authorities() {
        AppUser appUser = createRegulatorUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
        );
        appUser.setAuthorities(authorities);

        Set<Long> accounts = appUser.getAccounts();

        assertThat(accounts).isEmpty();
    }

    @Test
    void getVerificationBodyId() {
        Long vbId = 1L;
        AppUser appUser = createVerifierUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().verificationBodyId(vbId).build()
        );
        appUser.setAuthorities(authorities);

        Long optionalVbId = appUser.getVerificationBodyId();

        assertThat(optionalVbId).isNotNull();
        assertEquals(vbId, optionalVbId);
    }

    @Test
    void getVerificationBodyId_no_authorities() {
        AppUser appUser = createVerifierUser("user");

        Long optionalVbId = appUser.getVerificationBodyId();

        assertThat(optionalVbId).isNull();
    }

    @Test
    void getVerificationBodyId_no__verifier_authorities() {
        AppUser appUser = createVerifierUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
        );
        appUser.setAuthorities(authorities);

        Long optionalVbId = appUser.getVerificationBodyId();

        assertThat(optionalVbId).isNull();
    }

    @Test
    void getCompetentAuthority() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AppUser appUser = createRegulatorUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().competentAuthority(competentAuthority).build()
        );
        appUser.setAuthorities(authorities);

        CompetentAuthorityEnum optionalCompetentAuthority = appUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNotNull();
        assertEquals(competentAuthority, optionalCompetentAuthority);
    }

    @Test
    void getCompetentAuthority_no_authorities() {
        AppUser appUser = createRegulatorUser("user");

        CompetentAuthorityEnum optionalCompetentAuthority = appUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNull();
    }

    @Test
    void getVerificationBodyId_no__regulator_authorities() {
        AppUser appUser = createVerifierUser("user");
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().accountId(1L).build()
        );
        appUser.setAuthorities(authorities);

        CompetentAuthorityEnum optionalCompetentAuthority = appUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNull();
    }

	private AppUser createRegulatorUser(String userId) {
    	return createUser(userId, RoleTypeConstants.REGULATOR);
    }
	
	private AppUser createOperatorUser(String userId) {
    	return createUser(userId, RoleTypeConstants.OPERATOR);
    }

    private AppUser createVerifierUser(String userId) {
        return createUser(userId, RoleTypeConstants.VERIFIER);
    }
	
	private AppUser createUser(String userId, String roleType) {
    	return AppUser.builder()
    				.userId(userId)
    				.email("email@email")
    				.firstName("fn")
    				.lastName("ln")
    				.roleType(roleType)
    				.build();
    }
	
}
