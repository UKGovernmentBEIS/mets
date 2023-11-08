package uk.gov.pmrv.api.mireport.installation.accountsregulatorsitecontacts;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactReportGenerator;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
public class InstallationAccountAssignedRegulatorSiteContactReportGeneratorHandler
    extends AccountAssignedRegulatorSiteContactReportGenerator
    implements InstallationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final InstallationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository;

    public InstallationAccountAssignedRegulatorSiteContactReportGeneratorHandler(
        InstallationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository,
        UserAuthService userAuthService) {
        super(userAuthService);
        this.regulatorSiteContactsRepository = regulatorSiteContactsRepository;
    }

    @Override
    public List<AccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return regulatorSiteContactsRepository.findAccountAssignedRegulatorSiteContacts(entityManager);
    }

}
