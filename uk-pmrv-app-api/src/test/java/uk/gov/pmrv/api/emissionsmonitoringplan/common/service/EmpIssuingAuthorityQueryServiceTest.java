package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmpIssuingAuthorityRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuingAuthorityQueryServiceTest {

    @InjectMocks
    private EmpIssuingAuthorityQueryService service;

    @Mock
    private EmpIssuingAuthorityRepository repository;

    @Test
    void getAllIssuingAuthorityNames() {
        when(repository.findAllIssuingAuthorityNames()).thenReturn(List.of("name"));

        final List<String> actual = service.getAllIssuingAuthorityNames();
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsOnly("name");
        verify(repository, times(1)).findAllIssuingAuthorityNames();
    }

    @Test
    void existsByIssuingAuthorityName() {
        String issuingAuthority = "Issuing authority";
        when(repository.existsByName(issuingAuthority)).thenReturn(true);
        final boolean actual = service.existsByName(issuingAuthority);
        assertTrue(actual);
        verify(repository, times(1)).existsByName(issuingAuthority);
    }
}
