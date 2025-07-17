package uk.gov.pmrv.api.mireport.aviation.accountuserscontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountUsersContactsReportGenerator;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class AviationAccountUsersContactsReportGeneratorHandler
    extends AccountUsersContactsReportGenerator<AviationAccountUserContact>
    implements AviationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final AviationAccountUsersContactsRepository accountUsersContactsRepository;

    public AviationAccountUsersContactsReportGeneratorHandler(AviationAccountUsersContactsRepository accountUsersContactsRepository,
                                                              UserAuthService userAuthService) {
        super(userAuthService);
        this.accountUsersContactsRepository = accountUsersContactsRepository;
    }

    @Override
    public List<AviationAccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return accountUsersContactsRepository.findAccountUserContacts(entityManager);
    }

    @Override
    public List<String> getColumnNames() {
        return AviationAccountUserContact.getColumnNames();
    }
}
