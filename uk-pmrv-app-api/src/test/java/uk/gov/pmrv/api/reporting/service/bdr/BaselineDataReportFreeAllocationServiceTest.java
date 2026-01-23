package uk.gov.pmrv.api.reporting.service.bdr;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.bdr.BaselineDataReportFreeAllocation;
import uk.gov.pmrv.api.reporting.repository.BaselineDataReportFreeAllocationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaselineDataReportFreeAllocationServiceTest {

    @Mock
    private BaselineDataReportFreeAllocationRepository repository;

    @InjectMocks
    private BaselineDataReportFreeAllocationService service;

    @Test
    void createFreeAllocationEntry_when_entry_exists_then_update() {
        Long accountId = 1L;
        Boolean freeAllocation = true;
        BaselineDataReportFreeAllocation existingEntry = BaselineDataReportFreeAllocation.builder()
                .accountId(accountId)
                .freeAllocation(false)
                .build();

        when(repository.findByAccountId(accountId)).thenReturn(Optional.of(existingEntry));

        service.createFreeAllocationEntry(accountId, freeAllocation);

        ArgumentCaptor<BaselineDataReportFreeAllocation> captor = ArgumentCaptor.forClass(BaselineDataReportFreeAllocation.class);
        verify(repository).save(captor.capture());

        BaselineDataReportFreeAllocation savedEntry = captor.getValue();
        assertEquals(accountId, savedEntry.getAccountId());
        assertEquals(freeAllocation, savedEntry.getFreeAllocation());
    }

    @Test
    void createFreeAllocationEntry_when_entry_not_exists_then_create_new() {
        Long accountId = 1L;
        Boolean freeAllocation = false;

        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        service.createFreeAllocationEntry(accountId, freeAllocation);

        ArgumentCaptor<BaselineDataReportFreeAllocation> captor = ArgumentCaptor.forClass(BaselineDataReportFreeAllocation.class);
        verify(repository).save(captor.capture());

        BaselineDataReportFreeAllocation savedEntry = captor.getValue();
        assertEquals(accountId, savedEntry.getAccountId());
        assertEquals(freeAllocation, savedEntry.getFreeAllocation());
    }
}