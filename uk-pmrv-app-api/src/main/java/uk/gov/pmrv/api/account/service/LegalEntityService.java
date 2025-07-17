package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityMapper legalEntityMapper;
    private final LegalEntityValidationService legalEntityValidationService;

    /**
     * Returns the related legal entities for the authenticated user.
     *
     * @param appUser {@link AppUser}
     * @return List of {@link LegalEntityInfoDTO}
     */
    public List<LegalEntityInfoDTO> getUserLegalEntities(AppUser appUser) {
        final List<LegalEntity> legalEntities;
        switch (appUser.getRoleType()) {
            case RoleTypeConstants.REGULATOR:
                legalEntities = legalEntityRepository.findAllByStatusOrderByName(LegalEntityStatus.ACTIVE);
                break;
            case RoleTypeConstants.OPERATOR:
                legalEntities = legalEntityRepository.findActiveLegalEntitiesByAccountsOrderByName(appUser.getAccounts());
                break;
            default:
                legalEntities = Collections.emptyList();
        }

        return legalEntityMapper.toLegalEntityInfoDTOs(legalEntities);
    }

    /**
     * Returns legal entity with the provided id
     *
     * @param id Legal entity id
     * @return {@link LegalEntity}
     */
    public LegalEntity getLegalEntityById(Long id) {
        return legalEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    LegalEntity getUserLegalEntityById(Long legalEntityId, AppUser appUser) {
        LegalEntity legalEntity = getLegalEntityById(legalEntityId);

        switch (appUser.getRoleType()) {
            case RoleTypeConstants.REGULATOR:
                break;
            case RoleTypeConstants.OPERATOR:
                if(!legalEntityRepository.existsLegalEntityInAnyOfAccounts(legalEntity.getId(), appUser.getAccounts())) {
                    throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
                }
                break;
            default:
                throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
        }

        return legalEntity;
    }

    /**
     * Returns the requested legal entity associated with the user account.
     *
     * @param legalEntityId Legal entity id
     * @param appUser {@link AppUser}
     * @return {@link LegalEntityDTO}
     */
    public LegalEntityDTO getUserLegalEntityDTOById(Long legalEntityId, AppUser appUser) {
        LegalEntity legalEntity = getUserLegalEntityById(legalEntityId, appUser);
        return legalEntityMapper.toLegalEntityDTO(legalEntity);
    }

    public void deleteLegalEntity(LegalEntity legalEntity) {
        legalEntityRepository.delete(legalEntity);
    }

    public void handleLegalEntityDenied(LegalEntity legalEntity) {
        if(legalEntity.getStatus().equals(LegalEntityStatus.PENDING)){
            legalEntity.setStatus(LegalEntityStatus.DENIED);
            legalEntityRepository.save(legalEntity);
        }
    }

    public LegalEntity createLegalEntity(LegalEntityDTO legalEntityDTO, LegalEntityStatus status) {
        LegalEntity le = legalEntityMapper.toLegalEntity(legalEntityDTO);
        le.setStatus(status);
        return legalEntityRepository.save(le);
    }

    public LegalEntity resolveLegalEntity(LegalEntityDTO legalEntityDTO, AppUser authUser) {
        if(legalEntityDTO.getId() != null) {
            return getUserLegalEntityById(legalEntityDTO.getId(), authUser);
        } else {
            if (legalEntityValidationService.isExistingActiveLegalEntityName(legalEntityDTO.getName(), authUser)) {
                throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
            }
            return legalEntityRepository.findByNameAndStatus(legalEntityDTO.getName(), LegalEntityStatus.PENDING)
                    .orElseGet(() -> createLegalEntity(legalEntityDTO, LegalEntityStatus.PENDING));
        }
    }

    public LegalEntity resolveAmendedLegalEntity(LegalEntityDTO newLegalEntityDTO, LegalEntity currentLegalEntity, AppUser authUser) {
        if (newLegalEntityDTO.getId() != null ||
                !Objects.equals(newLegalEntityDTO.getName(), currentLegalEntity.getName())) {
            return resolveLegalEntity(newLegalEntityDTO, authUser);
        } else {
            return currentLegalEntity;
        }
    }

    public LegalEntity activateLegalEntity(LegalEntityDTO latestLegalEntityDTO) {
        if(latestLegalEntityDTO.getId() != null) {
            // already activated. just return it
            return getLegalEntityById(latestLegalEntityDTO.getId());
        }

        if (legalEntityValidationService.isExistingActiveLegalEntityName(latestLegalEntityDTO.getName())) {
            throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        }

        // If LE status is changed from another installation account create a new one
        LegalEntity legalEntity = legalEntityRepository
                .findByNameAndStatus(latestLegalEntityDTO.getName(), LegalEntityStatus.PENDING)
                .orElseGet(() -> createLegalEntity(latestLegalEntityDTO, LegalEntityStatus.PENDING));

        LegalEntity latestLegalEntity = legalEntityMapper.toLegalEntity(latestLegalEntityDTO);

        latestLegalEntity.setId(legalEntity.getId());
        latestLegalEntity.getLocation().setId(legalEntity.getLocation().getId());
        latestLegalEntity.setStatus(LegalEntityStatus.ACTIVE);
        return legalEntityRepository.save(latestLegalEntity);
    }

}
