package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityUpdateService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.transform.AddressMapper;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBodyEmissionScheme;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.event.AccreditationEmissionTradingSchemeNotAvailableEvent;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationBodyUpdateService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final AccreditationRefNumValidationService accreditationRefNumValidationService;
    private final ApplicationEventPublisher eventPublisher;
    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    private final VerifierAuthorityUpdateService verifierAuthorityUpdateService;

    @Transactional
    public void updateVerificationBody(VerificationBodyUpdateDTO verificationBodyUpdateDTO) {
        Long verificationBodyId = verificationBodyUpdateDTO.getId();
        VerificationBody verificationBody =
                verificationBodyRepository.findVerificationBodyWithVerBodyEmissionSchemes(verificationBodyId)
                        .orElseThrow(() -> {
                            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
                        });
        VerificationBodyEditDTO vbUpdate = verificationBodyUpdateDTO.getVerificationBody();

        Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemeDTOS = verificationBodyUpdateDTO.getVerificationBody().getVerificationBodyEmissionSchemes();
        if (verificationBodyEmissionSchemeDTOS != null) {
            verificationBodyEmissionSchemeDTOS.forEach(verBodyEmissionSchemeDTO -> {
                String accreditationReferenceNumber = verBodyEmissionSchemeDTO.getAccreditationReferenceNumber();
                if (accreditationReferenceNumber != null) {
                    accreditationRefNumValidationService.validateUpdate(accreditationReferenceNumber, verificationBodyId);
                }
            });

            Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemeDTOSet = new HashSet<>(verificationBodyEmissionSchemeDTOS);
            Set<EmissionTradingScheme> removedEmissionTradingSchemes = retrieveRemovedEmissionTradingSchemes(verificationBody, verificationBodyEmissionSchemeDTOSet);

            //do update verification body
            updateVerificationBodyProperties(verificationBody, vbUpdate);

            //publish event for not available accr ref number emission trading schemes
            if(!removedEmissionTradingSchemes.isEmpty()) {
                eventPublisher.publishEvent(
                        new AccreditationEmissionTradingSchemeNotAvailableEvent(verificationBodyId, removedEmissionTradingSchemes)
                );
            }
        }
    }

    @Transactional
    public void updateVerificationBodiesStatus(List<VerificationBodyUpdateStatusDTO> verificationBodyUpdateStatusList) {
        Map<VerificationBodyStatus, Set<Long>> updateStatus = verificationBodyUpdateStatusList.stream()
                .collect(Collectors.groupingBy(VerificationBodyUpdateStatusDTO::getStatus,
                        Collectors.mapping(VerificationBodyUpdateStatusDTO::getId, Collectors.toSet())));

        if(updateStatus.containsKey(VerificationBodyStatus.ACTIVE)
                && !updateStatus.get(VerificationBodyStatus.ACTIVE).isEmpty()){
            updateStatusToActive(updateStatus.get(VerificationBodyStatus.ACTIVE));
        }

        if(updateStatus.containsKey(VerificationBodyStatus.DISABLED)
                && !updateStatus.get(VerificationBodyStatus.DISABLED).isEmpty()){
            updateStatusToDisabled(updateStatus.get(VerificationBodyStatus.DISABLED));
        }
    }

    private void updateStatusToActive(Set<Long> verificationBodyIds) {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAllByIdIn(verificationBodyIds);

        if (verificationBodies.size() != verificationBodyIds.size()) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST);
        }

        Set<Long> idsUpdated = verificationBodies.stream()
                .filter(vb -> VerificationBodyStatus.ACTIVE != vb.getStatus())
                .map(vb -> {
                    vb.setStatus(VerificationBodyStatus.ACTIVE);
                    return vb.getId();
                })
                .collect(Collectors.toSet());

        // Event could be used for updating authorities
        // but direct service call was preferred to avoid introducing dependency from authorization to verification body domain (for the event).
        verifierAuthorityUpdateService.updateAuthoritiesOnVbActivation(idsUpdated);
    }

    private void updateStatusToDisabled(Set<Long> verificationBodyIds) {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAllByIdIn(verificationBodyIds);

        if (verificationBodies.size() != verificationBodyIds.size()) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST);
        }

        Set<Long> idsUpdated = verificationBodies.stream()
                .filter(vb -> VerificationBodyStatus.DISABLED != vb.getStatus())
                .map(vb -> {
                    vb.setStatus(VerificationBodyStatus.DISABLED);
                    return vb.getId();
                })
                .collect(Collectors.toSet());

        // VerificationBodyStatusDisabledEvent could be used for deleting authorities
        // but direct service call was preferred to avoid introducing dependency from authorization to verification body domain (for the VerificationBodyStatusDisabledEvent).
        // On the other hand, event was preferred for notifying the account domain in order to avoid introducing dependency from verification body to account domain.
        verifierAuthorityUpdateService.updateAuthoritiesOnVbDeactivation(idsUpdated);
        eventPublisher.publishEvent(new VerificationBodyStatusDisabledEvent(idsUpdated));
    }

    private void updateVerificationBodyProperties(VerificationBody vb, VerificationBodyEditDTO vbUpdate) {
        //update name
        vb.setName(vbUpdate.getName());

        //update address fields
        vb.setAddress(addressMapper.toAddress(vbUpdate.getAddress()));

        Set<VerificationBodyEmissionSchemeDTO> newVerificationBodyEmissionSchemes = vbUpdate.getVerificationBodyEmissionSchemes();

        if (newVerificationBodyEmissionSchemes == null) {
            vb.getEmissionSchemes().clear();
            return;
        }

        //map existing by emission trading scheme
        Map<EmissionTradingScheme, VerificationBodyEmissionScheme> existingMap = vb.getEmissionSchemes().stream()
                        .collect(Collectors.toMap(VerificationBodyEmissionScheme::getEmissionTradingScheme, Function.identity()));
        //map new by emission trading scheme
        Map<EmissionTradingScheme, VerificationBodyEmissionSchemeDTO> newMap = newVerificationBodyEmissionSchemes.stream()
                        .collect(Collectors.toMap(VerificationBodyEmissionSchemeDTO::getEmissionTradingScheme, Function.identity()));

        //remove if this emission trading scheme does not exist
        Set<VerificationBodyEmissionScheme> toRemove = vb.getEmissionSchemes().stream()
                .filter(type -> !newMap.containsKey(type.getEmissionTradingScheme()))
                .collect(Collectors.toSet());
        toRemove.forEach(vb::removeEmissionScheme);

        //update or add
        for (VerificationBodyEmissionSchemeDTO dto : newMap.values()) {
            VerificationBodyEmissionScheme existing = existingMap.get(dto.getEmissionTradingScheme());

            if (existing != null) {
                existing.setAccreditationReferenceNumber(dto.getAccreditationReferenceNumber());
                existing.setAccreditationName(dto.getAccreditationName());
            } else {
                VerificationBodyEmissionScheme newEmissionScheme = VerificationBodyEmissionScheme.builder()
                        .emissionTradingScheme(dto.getEmissionTradingScheme())
                        .accreditationReferenceNumber(dto.getAccreditationReferenceNumber())
                        .accreditationName(dto.getAccreditationName())
                        .build();

                vb.addEmissionScheme(newEmissionScheme);
            }
        }
    }

    private Set<EmissionTradingScheme> retrieveRemovedEmissionTradingSchemes(VerificationBody vb, Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemeDTOS) {

        // existing (DB)
        Set<EmissionTradingScheme> existingSchemes = vb.getEmissionSchemes().stream()
                .map(VerificationBodyEmissionScheme::getEmissionTradingScheme)
                .collect(Collectors.toSet());

        // new
        Set<EmissionTradingScheme> newSchemes = verificationBodyEmissionSchemeDTOS.stream()
                .map(VerificationBodyEmissionSchemeDTO::getEmissionTradingScheme)
                .collect(Collectors.toSet());

        // diff = existing - new
        Set<EmissionTradingScheme> removed = new HashSet<>(existingSchemes);
        removed.removeAll(newSchemes);

        return removed;
    }
}
