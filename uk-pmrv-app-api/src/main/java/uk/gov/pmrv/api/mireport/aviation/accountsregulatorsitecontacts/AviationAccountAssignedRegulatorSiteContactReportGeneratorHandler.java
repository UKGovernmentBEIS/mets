package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactReportGenerator;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class AviationAccountAssignedRegulatorSiteContactReportGeneratorHandler
    extends AccountAssignedRegulatorSiteContactReportGenerator<AviationAccountAssignedRegulatorSiteContact>
    implements AviationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final AviationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository;

    public AviationAccountAssignedRegulatorSiteContactReportGeneratorHandler(
        AviationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository,
        UserAuthService userAuthService) {
        super(userAuthService);
        this.regulatorSiteContactsRepository = regulatorSiteContactsRepository;
    }

    @Override
    public List<AviationAccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return regulatorSiteContactsRepository.findAccountAssignedRegulatorSiteContacts(entityManager);
    }

    @Override
    public List<String> getColumnNames() {
        return AviationAccountAssignedRegulatorSiteContact.getColumnNames();
    }
}
