package uk.gov.pmrv.api.permit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.PermitAuthorityInfoProvider;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.mapper.PermitEntityMapper;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermitQueryService implements PermitAuthorityInfoProvider {

    private static final PermitEntityMapper PERMIT_ENTITY_MAPPER = Mappers.getMapper(PermitEntityMapper.class);
    
    private final PermitRepository permitRepo;
    private final FileDocumentService fileDocumentService;

    public PermitContainer getPermitContainerById(String id) {
        return permitRepo.findById(id)
            .map(PermitEntity::getPermitContainer)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public PermitContainer getPermitContainerByAccountId(Long accountId) {
        return permitRepo.findByAccountId(accountId)
            .map(PermitEntity::getPermitContainer)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Long getPermitAccountById(String id) {
        return permitRepo.findPermitAccountById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
    
    public int getPermitConsolidationNumberByAccountId(Long accountId) {
    	return permitRepo.findByAccountId(accountId)
    			.map(PermitEntity::getConsolidationNumber)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public Optional<String> getPermitIdByAccountId(Long accountId) {
        return permitRepo.findPermitIdByAccountId(accountId);
    }

    public Map<Long, PermitEntityAccountDTO> getPermitByAccountIds(List<Long> accountIds) {
        return permitRepo.findByAccountIdIn(accountIds).stream().collect(Collectors.toMap(PermitEntityAccountDTO::getAccountId, Function.identity()));
    }

    public PermitEntityDto getPermitByAccountId(final Long accountId) {
        return PERMIT_ENTITY_MAPPER.toPermitEntityDto(
            permitRepo.findByAccountId(accountId).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }
    
    public PermitEntityDto getPermitByIdAndFileDocumentUuid(final String permitId, final String fileDocumentUuid) {
        return PERMIT_ENTITY_MAPPER.toPermitEntityDto(
            permitRepo.findPermitByIdAndFileDocumentUuid(permitId, fileDocumentUuid).orElseThrow(() -> new BusinessException(
                ErrorCode.RESOURCE_NOT_FOUND))
        );
    }
    
    public Optional<PermitDetailsDTO> getPermitDetailsByAccountId(Long accountId){
    	return permitRepo.findByAccountId(accountId)
    			.map(permitEntity -> PERMIT_ENTITY_MAPPER.toPermitDetailsDTO(permitEntity,
                        Optional.ofNullable(permitEntity.getFileDocumentUuid()).map((fileDocumentService::getFileInfoDTO)).orElse(null)));
    }
    
}
