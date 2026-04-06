package uk.gov.pmrv.api.mireport.system.installation.accountuserscontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.system.accountuserscontacts.AccountUsersContactsReportGenerator;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.pmrv.api.mireport.system.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class InstallationAccountUsersContactsReportGeneratorHandler
    extends AccountUsersContactsReportGenerator<InstallationAccountUserContact>
    implements InstallationMiReportGeneratorHandler<EmptyMiReportSystemParams> {

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
