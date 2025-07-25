package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalEntityServiceTest {

    private static String USER_ID = "userId";

    @InjectMocks
    private LegalEntityService service;

    @Mock
    private LegalEntityRepository legalEntityRepository;
    
    @Mock
    private LegalEntityMapper legalEntityMapper;

    @Mock
    private LegalEntityValidationService legalEntityValidationService;

    @Test
    void getUserLegalEntities_regulator() {
        List<LegalEntity> legalEntities = 
        		List.of(LegalEntity.builder().name("name1").build(),
        				LegalEntity.builder().name("name2").build());
        
        List<LegalEntityInfoDTO> legalEntitiesInfoDTO = 
        		List.of(LegalEntityInfoDTO.builder().name("name1").build(),
        				LegalEntityInfoDTO.builder().name("name2").build());

        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        when(legalEntityRepository.findAllByStatusOrderByName(LegalEntityStatus.ACTIVE)).thenReturn(legalEntities);
        when(legalEntityMapper.toLegalEntityInfoDTOs(legalEntities)).thenReturn(legalEntitiesInfoDTO);
        
        //invoke
        List<LegalEntityInfoDTO> actualLegalEntitiesInfoDTO = service.getUserLegalEntities(appUser);
        
        //assert
        assertThat(actualLegalEntitiesInfoDTO).extracting(LegalEntityInfoDTO::getName)
        										.containsExactly("name1", "name2");
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findAllByStatusOrderByName(LegalEntityStatus.ACTIVE);
        verify(legalEntityMapper, times(1)).toLegalEntityInfoDTOs(legalEntities);
        verify(legalEntityRepository, never()).findActiveLegalEntitiesByAccountsOrderByName(Mockito.anySet());
    }
    
    @Test
    void getUserLegalEntities_operator() {
        List<LegalEntity> legalEntities = 
        		List.of(LegalEntity.builder().name("name1").build(),
        				LegalEntity.builder().name("name2").build());
        
        List<LegalEntityInfoDTO> legalEntitiesInfoDTO = 
        		List.of(LegalEntityInfoDTO.builder().name("name1").build(),
        				LegalEntityInfoDTO.builder().name("name2").build());

        AppUser appUser = AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.OPERATOR)
                            .authorities(List.of(
                                    AppAuthority.builder().accountId(1L).build()
                                    )).build();

        when(legalEntityRepository.findActiveLegalEntitiesByAccountsOrderByName(appUser.getAccounts())).thenReturn(legalEntities);
        when(legalEntityMapper.toLegalEntityInfoDTOs(legalEntities)).thenReturn(legalEntitiesInfoDTO);
        
        //invoke
        List<LegalEntityInfoDTO> actualLegalEntitiesInfoDTO = service.getUserLegalEntities(appUser);
        
        //assert
        assertThat(actualLegalEntitiesInfoDTO).extracting(LegalEntityInfoDTO::getName)
        										.containsExactly("name1", "name2");
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findActiveLegalEntitiesByAccountsOrderByName(appUser.getAccounts());
        verify(legalEntityMapper, times(1)).toLegalEntityInfoDTOs(legalEntities);
        verify(legalEntityRepository, never()).findAllByStatusOrderByName(LegalEntityStatus.ACTIVE);
    }
    
    @Test
    void getLegalEntityById() {
        final Long id = 1L;
        final LegalEntity le = LegalEntity.builder().name("name1").id(id).build();
        when(legalEntityRepository.findById(id)).thenReturn(Optional.of(le));
        
        //invoke
        LegalEntity result = service.getLegalEntityById(id);
        
        //assert
        assertThat(result.getId()).isEqualTo(id);
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findById(id);
    }
    
    @Test
    void getUserLegalEntityById_not_found() {
    	final Long id = 1L;
    	final AppUser appUser = AppUser.builder().build();
    	
    	when(legalEntityRepository.findById(id)).thenReturn(Optional.empty());
    	
    	//invoke
    	BusinessException businessException =
              assertThrows(BusinessException.class, () -> service.getUserLegalEntityById(id, appUser));
    	assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }
    
    @Test
    void getUserLegalEntityById_regulator() {
    	final Long id = 1L;
    	final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
    	LegalEntity legalEntity = LegalEntity.builder().id(id).build();
    	
    	when(legalEntityRepository.findById(id)).thenReturn(Optional.of(legalEntity));
        
        //invoke
        LegalEntity result = service.getUserLegalEntityById(id, appUser);
        
        //assert
        assertThat(result.getId()).isEqualTo(id);
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findById(id);
        verify(legalEntityRepository, never()).existsLegalEntityInAnyOfAccounts(Mockito.anyLong(), Mockito.anySet());
    }
    
    @Test
    void getUserLegalEntityById_operator_not_in_account() {
    	final Long id = 1L;
        AppUser appUser = AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(
                        AppAuthority.builder().accountId(1L).build()
                        )).build();
    	LegalEntity legalEntity = LegalEntity.builder().id(id).build();
    	
    	when(legalEntityRepository.findById(id)).thenReturn(Optional.of(legalEntity));
    	when(legalEntityRepository.existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts())).thenReturn(false);
    	
        //invoke
    	BusinessException businessException =
                assertThrows(BusinessException.class, () -> service.getUserLegalEntityById(id, appUser));
      	assertEquals(MetsErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER, businessException.getErrorCode());
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findById(id);
        verify(legalEntityRepository, times(1)).existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts());
    }
    
    @Test
    void getUserLegalEntityById_operator() {
    	final Long id = 1L;
        AppUser appUser = AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(
                        AppAuthority.builder().accountId(1L).build()
                        )).build();
    	LegalEntity legalEntity = LegalEntity.builder().id(id).build();
    	
    	when(legalEntityRepository.findById(id)).thenReturn(Optional.of(legalEntity));
    	when(legalEntityRepository.existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts())).thenReturn(true);
    	
        //invoke
    	LegalEntity result = service.getUserLegalEntityById(id, appUser);
        
    	assertThat(result).isEqualTo(legalEntity);
        //verify mocks
        verify(legalEntityRepository, times(1)).findById(id);
        verify(legalEntityRepository, times(1)).existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts());
    }
    
    @Test
    void getUserLegalEntityDTOById_operator() {
        final Long id = 1L;
        AppUser appUser = AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(
                        AppAuthority.builder().accountId(1L).build()
                        )).build();
        LegalEntity legalEntity = LegalEntity.builder().id(id).build();
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(id).build();
        
        when(legalEntityRepository.findById(id)).thenReturn(Optional.of(legalEntity));
        when(legalEntityRepository.existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts())).thenReturn(true);
        when(legalEntityMapper.toLegalEntityDTO(legalEntity)).thenReturn(legalEntityDTO);
        
        //invoke
        LegalEntityDTO result = service.getUserLegalEntityDTOById(id, appUser);
        assertThat(result).isEqualTo(legalEntityDTO);
        
        //verify mocks
        verify(legalEntityRepository, times(1)).findById(id);
        verify(legalEntityMapper, times(1)).toLegalEntityDTO(legalEntity);
        verify(legalEntityRepository, times(1)).existsLegalEntityInAnyOfAccounts(id, appUser.getAccounts());
    }
    
    @Test
    void deleteLegalEntity() {
    	LegalEntity legalEntity = LegalEntity.builder().id(1L).build();
    	
        //invoke
    	service.deleteLegalEntity(legalEntity);
        
        //verify mocks
        verify(legalEntityRepository, times(1)).delete(legalEntity);
    }

    @Test
    void handleLegalEntityDenied() {
        LegalEntity legalEntity = LegalEntity.builder().id(1L).status(LegalEntityStatus.PENDING).build();

        // Invoke
        service.handleLegalEntityDenied(legalEntity);

        // Verify
        assertThat(legalEntity.getStatus()).isEqualTo(LegalEntityStatus.DENIED);
        verify(legalEntityRepository, times(1)).save(legalEntity);
    }
    
    @Test
    void createLegalEntity() {
        Long id = 1L;
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(id).build();
        LegalEntity legalEntity = LegalEntity.builder().id(id).build();
        LegalEntity savedLegalEntity = LegalEntity.builder().id(id).status(LegalEntityStatus.PENDING).build();
        
        when(legalEntityMapper.toLegalEntity(legalEntityDTO)).thenReturn(legalEntity);
        when(legalEntityRepository.save(legalEntity)).thenReturn(savedLegalEntity);
        
        LegalEntity result = service.createLegalEntity(legalEntityDTO, LegalEntityStatus.PENDING);
        
        assertThat(result).isEqualTo(savedLegalEntity);
        
        verify(legalEntityMapper, times(1)).toLegalEntity(legalEntityDTO);
        verify(legalEntityRepository, times(1)).save(legalEntity);
    }
    
    @Test
    void resolveLegalEntity_id_exists() {
        Long id = 1L;
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(id).build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        
        LegalEntity legalEntity = LegalEntity.builder().name("name1").id(id).build();
        when(legalEntityRepository.findById(id)).thenReturn(Optional.of(legalEntity));
        
        LegalEntity result = service.resolveLegalEntity(legalEntityDTO, authUser);
        
        assertThat(result).isEqualTo(legalEntity);
        verify(legalEntityRepository, times(1)).findById(id);
        verifyNoMoreInteractions(legalEntityRepository);
    }
    
    @Test
    void resolveLegalEntity_id_not_exists_active_name_already_exists_regulator() {
        String leName = "le";
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().name(leName).build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName(leName, authUser)).thenReturn(true);

        BusinessException be =
                assertThrows(BusinessException.class, () -> service.resolveLegalEntity(legalEntityDTO, authUser));
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName(leName, authUser);
        verifyNoMoreInteractions(legalEntityRepository);
    }
    
    @Test
    void resolveLegalEntity_id_not_exists_active_name_already_exists_operator() {
        String leName = "le";
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().name(leName).build();
        AppUser authUser = AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(AppAuthority.builder().accountId(1L).build())).build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName(leName, authUser)).thenReturn(true);

        BusinessException be =
                assertThrows(BusinessException.class, () -> service.resolveLegalEntity(legalEntityDTO, authUser));
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName(leName, authUser);
        verifyNoMoreInteractions(legalEntityRepository);
    }
    
    @Test
    void resolveLegalEntity_id_active_name_not_exists_non_active_exists() {
        String leName = "le";
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().name(leName).build();
        LegalEntity legalEntity = LegalEntity.builder().status(LegalEntityStatus.PENDING).name(leName).id(1L).build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName(leName, authUser)).thenReturn(false);
        when(legalEntityRepository.findByNameAndStatus(leName, LegalEntityStatus.PENDING)).thenReturn(Optional.of(legalEntity));
        
        LegalEntity result = service.resolveLegalEntity(legalEntityDTO, authUser);
        
        assertThat(result).isEqualTo(legalEntity);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName(leName, authUser);
        verify(legalEntityRepository, times(1)).findByNameAndStatus(leName, LegalEntityStatus.PENDING);
        verifyNoMoreInteractions(legalEntityRepository, legalEntityMapper);
    }
    
    @Test
    void resolveLegalEntity_id_active_name_not_exists_non_active_not_exists() {
        String leName = "le";
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().name(leName).build();
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name(leName).build();
        LegalEntity savedLegalEntity = LegalEntity.builder().id(1L).name(leName).status(LegalEntityStatus.PENDING).build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName(leName, authUser)).thenReturn(false);
        when(legalEntityRepository.findByNameAndStatus(leName, LegalEntityStatus.PENDING)).thenReturn(Optional.empty());
        when(legalEntityMapper.toLegalEntity(legalEntityDTO)).thenReturn(legalEntity);
        when(legalEntityRepository.save(legalEntity)).thenReturn(savedLegalEntity);
        
        LegalEntity result = service.resolveLegalEntity(legalEntityDTO, authUser);
        
        assertThat(result).isEqualTo(savedLegalEntity);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName(leName, authUser);
        verify(legalEntityRepository, times(1)).findByNameAndStatus(leName, LegalEntityStatus.PENDING);
        verify(legalEntityMapper, times(1)).toLegalEntity(legalEntityDTO);
        verify(legalEntityRepository, times(1)).save(legalEntity);
    }
    
    @Test
    void resolveAmendedLegalEntity_id_not_exists_names_are_same() {
        String leName = "le";
        LegalEntityDTO newLegalEntityDTO = LegalEntityDTO.builder().name(leName).build();
        LegalEntity currentLegalEntity = LegalEntity.builder().id(1L).name(leName).build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        
        LegalEntity result = service.resolveAmendedLegalEntity(newLegalEntityDTO, currentLegalEntity, authUser);
        
        assertThat(result).isEqualTo(currentLegalEntity);
        
        verifyNoInteractions(legalEntityRepository, legalEntityMapper);
    }
    
    @Test
    void resolveAmendedLegalEntity_id_exists() {
        LegalEntityDTO newLegalEntityDTO = LegalEntityDTO.builder().id(2L).build();
        LegalEntity newLegalEntity = LegalEntity.builder().id(2L).name("name2").build();
        LegalEntity currentLegalEntity = LegalEntity.builder().id(1L).name("name1").build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        
        when(legalEntityRepository.findById(2L)).thenReturn(Optional.of(newLegalEntity));
        
        LegalEntity result = service.resolveAmendedLegalEntity(newLegalEntityDTO, currentLegalEntity, authUser);
        
        assertThat(result).isEqualTo(newLegalEntity);
        
        verify(legalEntityRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(legalEntityRepository, legalEntityMapper);
    }
    
    @Test
    void resolveAmendedLegalEntity_different_name_exist() {
        LegalEntityDTO newLegalEntityDTO = LegalEntityDTO.builder().name("name2").build();
        LegalEntity newLegalEntity = LegalEntity.builder().id(2L).name("name2").status(LegalEntityStatus.PENDING).build();
        LegalEntity currentLegalEntity = LegalEntity.builder().id(1L).name("name1").build();
        AppUser authUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName("name2", authUser)).thenReturn(false);
        when(legalEntityRepository.findByNameAndStatus("name2", LegalEntityStatus.PENDING)).thenReturn(Optional.of(newLegalEntity));
        
        LegalEntity result = service.resolveAmendedLegalEntity(newLegalEntityDTO, currentLegalEntity, authUser);
        
        assertThat(result).isEqualTo(newLegalEntity);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName("name2", authUser);
        verify(legalEntityRepository, times(1)).findByNameAndStatus("name2", LegalEntityStatus.PENDING);
        verifyNoMoreInteractions(legalEntityRepository, legalEntityMapper);
    }
    
    @Test
    void activateLegalEntity_with_id() {
        LegalEntityDTO latestLegalEntityDTO = LegalEntityDTO.builder()
                .id(1L)
                .name("le").build();
        
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name("le").status(LegalEntityStatus.ACTIVE).build();
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(legalEntity));
        
        LegalEntity result = service.activateLegalEntity(latestLegalEntityDTO);
        
        assertThat(result).isEqualTo(legalEntity);
        verify(legalEntityRepository, times(1)).findById(latestLegalEntityDTO.getId());
        verifyNoMoreInteractions(legalEntityRepository);
    }
    
    @Test
    void activateLegalEntity_with_name_and_active_already_exists() {
        LegalEntityDTO latestLegalEntityDTO = LegalEntityDTO.builder()
                .name("le").build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName("le")).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class,
                () -> service.activateLegalEntity(latestLegalEntityDTO));
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName("le");
        verifyNoMoreInteractions(legalEntityRepository);
    }
    
    @Test
    void activateLegalEntity_with_name_and_active_already_not_exists() {
        LegalEntityDTO latestLegalEntityDTO = LegalEntityDTO.builder()
                .name("le")
                .build();
        
        LocationOnShore location = new LocationOnShore();
        location.setId(1L);
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name("le").status(LegalEntityStatus.PENDING)
                .location(location)
                .build();
        
        LocationOnShore latestLocation = new LocationOnShore();
        latestLocation.setId(2L);
        LegalEntity latestLegalEntity = LegalEntity.builder().id(1L).name("le")
                .location(latestLocation)
                .build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName("le")).thenReturn(false);
        when(legalEntityRepository.findByNameAndStatus("le", LegalEntityStatus.PENDING)).thenReturn(Optional.of(legalEntity));
        when(legalEntityMapper.toLegalEntity(latestLegalEntityDTO)).thenReturn(latestLegalEntity);
        
        service.activateLegalEntity(latestLegalEntityDTO);
        
        ArgumentCaptor<LegalEntity> leCaptor = ArgumentCaptor.forClass(LegalEntity.class);
        verify(legalEntityRepository, times(1)).save(leCaptor.capture());
        LegalEntity newLegalEntityCaptured = leCaptor.getValue();
        assertThat(newLegalEntityCaptured.getStatus()).isEqualTo(LegalEntityStatus.ACTIVE);
        assertThat(newLegalEntityCaptured.getLocation().getId()).isEqualTo(latestLocation.getId());
        
        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName("le");
        verify(legalEntityRepository, times(1)).findByNameAndStatus("le", LegalEntityStatus.PENDING);
        verify(legalEntityMapper, times(1)).toLegalEntity(latestLegalEntityDTO);
    }

    @Test
    void activateLegalEntity_with_name_and_denied() {
        LegalEntityDTO latestLegalEntityDTO = LegalEntityDTO.builder()
                .name("le")
                .build();

        LocationOnShore location = new LocationOnShore();
        location.setId(1L);

        LocationOnShore latestLocation = new LocationOnShore();
        latestLocation.setId(2L);
        LegalEntity latestLegalEntity = LegalEntity.builder().id(1L).name("le")
                .location(latestLocation)
                .build();
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name("le")
                .location(latestLocation)
                .status(LegalEntityStatus.PENDING)
                .build();

        when(legalEntityValidationService.isExistingActiveLegalEntityName("le")).thenReturn(false);
        when(legalEntityRepository.findByNameAndStatus("le", LegalEntityStatus.PENDING)).thenReturn(Optional.empty());
        when(legalEntityMapper.toLegalEntity(latestLegalEntityDTO)).thenReturn(latestLegalEntity);
        when(legalEntityRepository.save(legalEntity)).thenReturn(legalEntity);

        service.activateLegalEntity(latestLegalEntityDTO);

        ArgumentCaptor<LegalEntity> leCaptor = ArgumentCaptor.forClass(LegalEntity.class);
        verify(legalEntityRepository, times(2)).save(leCaptor.capture());
        LegalEntity newLegalEntityCaptured = leCaptor.getValue();
        assertThat(newLegalEntityCaptured.getStatus()).isEqualTo(LegalEntityStatus.ACTIVE);
        assertThat(newLegalEntityCaptured.getLocation().getId()).isEqualTo(latestLocation.getId());

        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName("le");
        verify(legalEntityRepository, times(1)).findByNameAndStatus("le", LegalEntityStatus.PENDING);
        verify(legalEntityMapper, times(2)).toLegalEntity(latestLegalEntityDTO);
    }
}
