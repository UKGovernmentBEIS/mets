package uk.gov.pmrv.api.permit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitQueryServiceTest {

    @InjectMocks
    private PermitQueryService service;

    @Mock
    private PermitRepository permitRepository;
    
    @Mock
    private FileDocumentService fileDocumentService;
    
    @Test
    void getPermitContainerById() {
        String id = "1";
        PermitEntity permitEntity = PermitEntity.builder()
            .permitContainer(PermitContainer.builder()
                .permit(Permit.builder()
                    .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                        .quantity(BigDecimal.valueOf(1000))
                        .build())
                    .build())
                .build())
            .build();

        when(permitRepository.findById(id)).thenReturn(Optional.of(permitEntity));
        PermitContainer actual = service.getPermitContainerById(id);

        assertThat(actual).isEqualTo(permitEntity.getPermitContainer());
    }

    @Test
    void getPermitAccountById() {
        String id = "1";
        Long accountId = 1L;

        when(permitRepository.findPermitAccountById(id)).thenReturn(Optional.of(accountId));
        Long actual = service.getPermitAccountById(id);

        assertThat(actual).isEqualTo(accountId);
    }
    
    @Test
    void getPermitContainerByAccountId() {
    	Long accountId = 1L;
    	PermitContainer permitContainer = PermitContainer.builder()
                    .permit(Permit.builder()
                        .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                            .quantity(BigDecimal.valueOf(1000))
                            .build())
                        .build())
                    .build();
    	PermitEntity permitEntity = PermitEntity.builder()
                .permitContainer(permitContainer)
                .build();

    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
    	PermitContainer result = service.getPermitContainerByAccountId(accountId);

        assertThat(result).isEqualTo(permitContainer);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getPermitContainerByAccountId_not_found() {
    	Long accountId = 1L;
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
    	BusinessException be = assertThrows(BusinessException.class, () -> service.getPermitContainerByAccountId(accountId));

    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getPermitByAccountIds() {
        Long accountId = 1L;
        PermitEntityAccountDTO expectedPermitEntityAccountDTO = mock(PermitEntityAccountDTO.class);

        when(expectedPermitEntityAccountDTO.getAccountId()).thenReturn(accountId);
        when(permitRepository.findByAccountIdIn(List.of(accountId))).thenReturn(List.of(expectedPermitEntityAccountDTO));

        Map<Long, PermitEntityAccountDTO> actual = service.getPermitByAccountIds(Collections.singletonList(accountId));

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(accountId)).isEqualTo(expectedPermitEntityAccountDTO);
    }
    
    @Test
    void getPermitConsolidationNumberByAccountId() {
    	Long accountId = 1L;
    	PermitEntity permitEntity = PermitEntity.createPermit("permitId", 
    			PermitContainer.builder().build(), 
    			accountId, null);
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
    	
    	int result = service.getPermitConsolidationNumberByAccountId(accountId);
    	assertThat(result).isEqualTo(1);
    }
    
    @Test
    void getPermitConsolidationNumberByAccountId_throws_error_when_not_found() {
    	Long accountId = 1L;
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
    	
		BusinessException be = assertThrows(BusinessException.class,
				() -> service.getPermitConsolidationNumberByAccountId(accountId));

    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getPermitDetailsByAccountId() {
    	Long accountId = 1L;

    	Map<UUID, String> permitAttachments = Map.of(
				UUID.randomUUID(), "att1"
				);
    	
    	PermitContainer permitContainer = PermitContainer.builder()
	    	.permitType(PermitType.GHGE)
	    	.activationDate(LocalDate.of(2000, 1, 1))
	    	.permitAttachments(permitAttachments)
	    	.build();
    	
    	String fileDocumentUuid = UUID.randomUUID().toString();
    	
    	PermitEntity permitEntity = PermitEntity.createPermit("permitId", 
    			permitContainer,
    			accountId,
    			fileDocumentUuid);
    	
    	FileInfoDTO fileDocument = FileInfoDTO.builder()
    			.name("permitDoc")
    			.uuid(fileDocumentUuid)
    			.build();
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
    	when(fileDocumentService.getFileInfoDTO(fileDocumentUuid)).thenReturn(fileDocument);
    	
    	Optional<PermitDetailsDTO> result = service.getPermitDetailsByAccountId(accountId);
    	
    	assertThat(result.get()).isEqualTo(PermitDetailsDTO.builder()
    			.id("permitId")
    			.activationDate(permitContainer.getActivationDate())
    			.permitAttachments(permitAttachments)
    			.fileDocument(fileDocument)
    			.build());
    	
    	verify(permitRepository, times(1)).findByAccountId(accountId);
    	verify(fileDocumentService, times(1)).getFileInfoDTO(fileDocumentUuid);
    }
    
    @Test
    void getPermitDetailsByAccountId_no_permit_document_exist() {
    	Long accountId = 1L;

    	Map<UUID, String> permitAttachments = Map.of(
				UUID.randomUUID(), "att1"
				);
    	
    	PermitContainer permitContainer = PermitContainer.builder()
	    	.permitType(PermitType.GHGE)
	    	.activationDate(LocalDate.of(2000, 1, 1))
	    	.permitAttachments(permitAttachments)
	    	.build();
    	
    	PermitEntity permitEntity = PermitEntity.createPermit("permitId", 
    			permitContainer,
    			accountId,
    			null);
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
    	
    	Optional<PermitDetailsDTO> result = service.getPermitDetailsByAccountId(accountId);
    	
    	assertThat(result.get()).isEqualTo(PermitDetailsDTO.builder()
    			.id("permitId")
    			.activationDate(permitContainer.getActivationDate())
    			.permitAttachments(permitAttachments)
    			.build());
    	
    	verify(permitRepository, times(1)).findByAccountId(accountId);
    	verifyNoInteractions(fileDocumentService);
    }
    
    @Test
    void getPermitDetailsByAccountId_no_permit_exist() {
    	Long accountId = 1L;

    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
    	
    	Optional<PermitDetailsDTO> result = service.getPermitDetailsByAccountId(accountId);
    	
    	assertThat(result).isEmpty();
    	
    	verify(permitRepository, times(1)).findByAccountId(accountId);
    	verifyNoInteractions(fileDocumentService);
    }

}
