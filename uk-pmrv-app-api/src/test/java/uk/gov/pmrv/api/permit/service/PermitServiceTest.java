package uk.gov.pmrv.api.permit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitServiceTest {

    public static final String TEST_PERMIT_ID = "UK-E-IN-00001";
    @InjectMocks
    private PermitService cut;

    @Mock
    private PermitRepository permitRepository;

    @Mock
    private PermitGrantedValidatorService permitGrantedValidatorService;

    @Mock
    private PermitIdentifierGenerator generator;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Test
    void submitPermit() {
        Long accountId = 1L;
        Permit permit = Permit.builder()
            .environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
            .build();
        PermitContainer permitContainer = PermitContainer.builder()
            .permit(permit)
            .build();

        PermitEntity permitEntity = PermitEntity.builder().permitContainer(permitContainer).build();
        String permitEntityId = "1";
        PermitEntity submittedPermitEntity =
            PermitEntity.builder().id(permitEntityId).permitContainer(permitContainer).build();

        when(permitRepository.save(permitEntity))
            .thenReturn(submittedPermitEntity);

        when(generator.generate(accountId)).thenReturn(TEST_PERMIT_ID);

        String submittedPermitId = cut.submitPermit(permitContainer, accountId);

        verify(permitGrantedValidatorService, times(1)).validatePermit(permitContainer);
        verify(permitRepository, times(1)).save(permitEntity);
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordForAccount(TEST_PERMIT_ID, accountId);

        assertThat(submittedPermitId).isEqualTo("1");
    }
    
    @Test
    void updatePermit() {
    	Long accountId = 1L;
    	String permitId = "permitId";
    	
    	PermitEntity permitEntity = 
    			PermitEntity.createPermit(permitId, PermitContainer.builder().build(), accountId, null);

        int previousConsolidationNumber = permitEntity.getConsolidationNumber();

        Permit newPermit = Permit.builder()
            .environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
            .build();
        PermitContainer newPermitContainer = PermitContainer.builder()
            .permit(newPermit)
            .build();
        
        when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
        
        cut.updatePermit(newPermitContainer, accountId);
        
        assertThat(permitEntity.getPermitContainer()).isEqualTo(newPermitContainer);
        assertThat(permitEntity.getConsolidationNumber()).isEqualTo(previousConsolidationNumber + 1);
        verify(permitGrantedValidatorService, times(1)).validatePermit(newPermitContainer);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void incrementPermitConsolidationNumber() {
    	Long accountId = 1L;
    	String permitId = "permitId";
    	
    	PermitEntity permitEntity = 
    			PermitEntity.createPermit(permitId, PermitContainer.builder().build(), accountId, null);

        int previousConsolidationNumber = permitEntity.getConsolidationNumber();

        Permit newPermit = Permit.builder()
            .environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
            .build();
        PermitContainer newPermitContainer = PermitContainer.builder()
            .permit(newPermit)
            .build();
        
        when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
        
        cut.updatePermit(newPermitContainer, accountId);
        
        assertThat(permitEntity.getPermitContainer()).isEqualTo(newPermitContainer);
        assertThat(permitEntity.getConsolidationNumber()).isEqualTo(previousConsolidationNumber + 1);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }

}
