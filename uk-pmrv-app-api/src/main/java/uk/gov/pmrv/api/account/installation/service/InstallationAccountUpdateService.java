package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateFaStatusDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.account.service.LegalEntityValidationService;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.account.transform.HoldingCompanyMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.transform.AddressMapper;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstallationAccountUpdateService {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final InstallationAccountRepository installationAccountRepository;
    private final InstallationAccountStatusService installationAccountStatusService;
    private final LegalEntityValidationService legalEntityValidationService;
    private static final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);
    private static final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    private static final HoldingCompanyMapper holdingCompanyMapper = Mappers.getMapper(HoldingCompanyMapper.class);

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountSiteName(Long accountId, String newSiteName) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setSiteName(newSiteName);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountRegistryId(Long accountId, Integer registryId) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setRegistryId(registryId);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountSopId(Long accountId, Long sopId) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setSopId(sopId);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountAddress(Long accountId, LocationDTO address) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        Location location = locationMapper.toLocation(address);
        account.setLocation(location);
    }

    @Transactional
    public void updateAccountUponPermitGranted(Long accountId, EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setEmitterType(emitterType);
        account.setInstallationCategory(InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions));

        installationAccountStatusService.handlePermitGranted(accountId);
    }

    @Transactional
    public void updateAccountUponPermitVariationGranted(Long accountId, EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setInstallationCategory(InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions));
    }

    @Transactional
    public void updateAccountUponPermitVariationRegulatorLedSubmit(Long accountId, EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setEmitterType(emitterType);
        account.setInstallationCategory(InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions));
    }

    @Transactional
    public void updateAccountUponTransferBGranted(Long accountId, EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setEmitterType(emitterType);
        account.setInstallationCategory(InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions));

        installationAccountStatusService.handleTransferBGranted(accountId);
    }

    @Transactional
    public void updateAccountUponTransferAGranted(Long accountId) {
        installationAccountStatusService.handleTransferAGranted(accountId);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountLegalEntity(Long accountId, LegalEntityDTO legalEntityDTO) {
        var account = installationAccountQueryService.getAccountWithLocAndLeWithLocById(accountId);
        var legalEntity = account.getLegalEntity();

        legalEntityValidationService.validateNameExistenceInOtherActiveLegalEntities(legalEntityDTO.getName(), legalEntity.getId());

        legalEntity.setName(legalEntityDTO.getName());
        legalEntity.getLocation().setAddress(addressMapper.toAddress(legalEntityDTO.getAddress()));
        legalEntity.setReferenceNumber(legalEntityDTO.getReferenceNumber());
        legalEntity.setNoReferenceNumberReason(legalEntityDTO.getNoReferenceNumberReason());
        legalEntity.setType(legalEntityDTO.getType());

        if (legalEntityDTO.getHoldingCompany() == null) {
            legalEntity.setHoldingCompany(null);
        } else {
            var holdingCompany = Optional.ofNullable(holdingCompanyMapper.toHoldingCompany(legalEntityDTO.getHoldingCompany()))
                .orElseGet(HoldingCompany::new);
            legalEntity.setHoldingCompany(holdingCompany);
        }
    }

    @Transactional
    public void disableTransferCodeStatus(final String transferCode) {

        final InstallationAccount account = installationAccountRepository
            .findAccountByTransferCodeAndTransferCodeStatus(transferCode, TransferCodeStatus.ACTIVE)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        account.setTransferCodeStatus(TransferCodeStatus.DISABLED);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateInstallationName(Long accountId, String newInstallationName) {
        // Validate if installation name exists
        installationAccountQueryService.validateAccountNameExistence(newInstallationName);

        // Update installation name
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setName(newInstallationName);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateFaStatus(
        Long accountId,
        AccountUpdateFaStatusDTO accountUpdateFaStatusDTO) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);
        account.setFaStatus(accountUpdateFaStatusDTO.getFaStatus());
    }
}
