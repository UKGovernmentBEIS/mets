package uk.gov.pmrv.api.mireport.installation.accountuserscontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountUsersContactsReportGenerator;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class InstallationAccountUsersContactsReportGeneratorHandler
    extends AccountUsersContactsReportGenerator<InstallationAccountUserContact>
    implements InstallationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final InstallationAccountUsersContactsRepository accountUsersContactsRepository;

    public InstallationAccountUsersContactsReportGeneratorHandler(InstallationAccountUsersContactsRepository accountUsersContactsRepository,
                                                                  UserAuthService userAuthService) {
        super(userAuthService);
        this.accountUsersContactsRepository = accountUsersContactsRepository;
    }

    @Override
    public List<InstallationAccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return accountUsersContactsRepository.findAccountUserContacts(entityManager);
    }

    @Override
    public List<String> getColumnNames() {
        return InstallationAccountUserContact.getColumnNames();
    }
}
