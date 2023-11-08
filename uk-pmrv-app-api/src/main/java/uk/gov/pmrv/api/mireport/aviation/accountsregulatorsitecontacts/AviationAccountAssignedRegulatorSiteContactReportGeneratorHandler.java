package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactReportGenerator;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
public class AviationAccountAssignedRegulatorSiteContactReportGeneratorHandler
    extends AccountAssignedRegulatorSiteContactReportGenerator
    implements AviationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final AviationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository;

    public AviationAccountAssignedRegulatorSiteContactReportGeneratorHandler(
        AviationAccountAssignedRegulatorSiteContactsRepository regulatorSiteContactsRepository,
        UserAuthService userAuthService) {
        super(userAuthService);
        this.regulatorSiteContactsRepository = regulatorSiteContactsRepository;
    }

    @Override
    public List<AccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return regulatorSiteContactsRepository.findAccountAssignedRegulatorSiteContacts(entityManager);
    }
}
