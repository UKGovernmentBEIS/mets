package uk.gov.pmrv.api.mireport.aviation.accountuserscontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUsersContactsReportGenerator;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class AviationAccountUsersContactsReportGeneratorHandler
    extends AccountUsersContactsReportGenerator
    implements AviationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final AviationAccountUsersContactsRepository accountUsersContactsRepository;

    public AviationAccountUsersContactsReportGeneratorHandler(AviationAccountUsersContactsRepository accountUsersContactsRepository,
                                                              UserAuthService userAuthService) {
        super(userAuthService);
        this.accountUsersContactsRepository = accountUsersContactsRepository;
    }

    @Override
    public List<AccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return accountUsersContactsRepository.findAccountUserContacts(entityManager);
    }
}
