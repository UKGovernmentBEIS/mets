package uk.gov.pmrv.api.mireport.system.installation.accountsregulatorsitecontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.system.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactReportGenerator;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.pmrv.api.mireport.system.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class InstallationAccountAssignedRegulatorSiteContactReportGeneratorHandler
    extends AccountAssignedRegulatorSiteContactReportGenerator<InstallationAccountAssignedRegulatorSiteContact>
    implements InstallationMiReportGeneratorHandler<EmptyMiReportSystemParams> {

    private final InstallationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository;

    public InstallationAccountAssignedRegulatorSiteContactReportGeneratorHandler(
        InstallationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository,
        UserAuthService userAuthService) {
        super(userAuthService);
        this.regulatorSiteContactsRepository = regulatorSiteContactsRepository;
    }

    @Override
    public List<InstallationAccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return regulatorSiteContactsRepository.findAccountAssignedRegulatorSiteContacts(entityManager);
    }

    @Override
    public List<String> getColumnNames() {
        return InstallationAccountAssignedRegulatorSiteContact.getColumnNames();
    }

}
