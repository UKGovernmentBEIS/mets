package uk.gov.pmrv.api.mireport.installation.accountuserscontacts;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUsersContactsReportGenerator;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
public class InstallationAccountUsersContactsReportGeneratorHandler
    extends AccountUsersContactsReportGenerator
    implements InstallationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final InstallationAccountUsersContactsRepository accountUsersContactsRepository;

    public InstallationAccountUsersContactsReportGeneratorHandler(InstallationAccountUsersContactsRepository accountUsersContactsRepository,
                                                                  UserAuthService userAuthService) {
        super(userAuthService);
        this.accountUsersContactsRepository = accountUsersContactsRepository;
    }

    @Override
    public List<AccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return accountUsersContactsRepository.findAccountUserContacts(entityManager);
    }
}
