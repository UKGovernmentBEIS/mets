package uk.gov.pmrv.api.permit.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;

@Service
@RequiredArgsConstructor
public class PermitService {

    private final PermitRepository permitRepo;
    private final PermitGrantedValidatorService permitGrantedValidatorService;
    private final PermitIdentifierGenerator generator;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Transactional
    public String submitPermit(PermitContainer permitContainer, Long accountId) {
        //validate
        permitGrantedValidatorService.validatePermit(permitContainer);

        String permitId = generator.generate(accountId);

        //submit
        PermitEntity permitEntity = PermitEntity.builder()
            .id(permitId)
            .permitContainer(permitContainer)
            .accountId(accountId)
            .build();
        PermitEntity submittedPermitEntity = permitRepo.save(permitEntity);

        accountSearchAdditionalKeywordService.storeKeywordForAccount(permitId, accountId);

        return submittedPermitEntity.getId();
    }
    
    @Transactional
    public void updatePermit(PermitContainer newPermitContainer, Long accountId) {
    	// validate
        permitGrantedValidatorService.validatePermit(newPermitContainer);

        PermitEntity permitEntity = permitRepo.findByAccountId(accountId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        
        // update permit
		permitEntity.setPermitContainer(newPermitContainer);
		doIncrementPermitConsolidationNumber(permitEntity);
    }

    @Transactional
    public void incrementPermitConsolidationNumber(Long accountId) {
    	PermitEntity permitEntity = permitRepo.findByAccountId(accountId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    	doIncrementPermitConsolidationNumber(permitEntity);
    }

    @Transactional
    public void setFileDocumentUuid(final String permitId, final String fileDocumentUuid) {
        permitRepo.updateFileDocumentUuid(permitId, fileDocumentUuid);
    }
    
    private void doIncrementPermitConsolidationNumber(PermitEntity permitEntity) {
		permitEntity.setConsolidationNumber(permitEntity.getConsolidationNumber() + 1);
	}
}
