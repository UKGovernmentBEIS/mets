package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccountStatusChangedEvent;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCreationService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BDRAccountUpdatedEventListenerTest {

    @InjectMocks
    private BDRAccountUpdatedEventListener listener;

    @Mock
    private BDRCreationService bdrCreationService;

    @Mock
    private ConfigurationService configurationService;

    private static final String BDR_REPORTABLE_PERIOD_TOGGLE_KEY = "bdr.reporting-period.toggle";


    @Test
    public void testOnBDRAccountUpdatedCreatedEvent_accountStatusNotLive() {
        InstallationAccountStatusChangedEvent event = new InstallationAccountStatusChangedEvent(1L, InstallationAccountStatus.REVOKED);

        listener.onBDRAccountUpdatedCreatedEvent(event);

        verify(configurationService, never()).getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY);
        verify(bdrCreationService, never()).createBDR(1L);
    }

    @Test
    public void testOnBDRAccountUpdatedCreatedEvent_withToggleTrue_insidePeriod() {
        InstallationAccountStatusChangedEvent event = new InstallationAccountStatusChangedEvent(1L, InstallationAccountStatus.LIVE);
        Optional<ConfigurationDTO> optionalConfigurationDTO = Optional.of(new ConfigurationDTO(BDR_REPORTABLE_PERIOD_TOGGLE_KEY, "true"));

        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 4, 20).atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
            when(configurationService.getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY)).thenReturn(optionalConfigurationDTO);

            listener.onBDRAccountUpdatedCreatedEvent(event);
        }

        verify(bdrCreationService, times(1)).createBDR(1L);
    }

    @Test
    public void testOnBDRAccountUpdatedCreatedEvent_withToggleTrue_outsidePeriod() {
        InstallationAccountStatusChangedEvent event = new InstallationAccountStatusChangedEvent(1L, InstallationAccountStatus.LIVE);
        Optional<ConfigurationDTO> optionalConfigurationDTO = Optional.of(new ConfigurationDTO(BDR_REPORTABLE_PERIOD_TOGGLE_KEY, "true"));

        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 8, 13).atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
            when(configurationService.getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY)).thenReturn(optionalConfigurationDTO);

            listener.onBDRAccountUpdatedCreatedEvent(event);
        }

        verify(bdrCreationService, never()).createBDR(1L);
    }

    @Test
    public void testOnBDRAccountUpdatedCreatedEvent_withToggleFalse_insidePeriod() {
        InstallationAccountStatusChangedEvent event = new InstallationAccountStatusChangedEvent(1L, InstallationAccountStatus.LIVE);
        Optional<ConfigurationDTO> optionalConfigurationDTO = Optional.of(new ConfigurationDTO(BDR_REPORTABLE_PERIOD_TOGGLE_KEY, "false"));

        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 4, 20).atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
            when(configurationService.getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY)).thenReturn(optionalConfigurationDTO);

            listener.onBDRAccountUpdatedCreatedEvent(event);
        }

        verify(bdrCreationService, times(1)).createBDR(1L);
    }

    @Test
    public void testOnBDRAccountUpdatedCreatedEvent_withToggleFalse_outsidePeriod() {
        InstallationAccountStatusChangedEvent event = new InstallationAccountStatusChangedEvent(1L, InstallationAccountStatus.LIVE);
        Optional<ConfigurationDTO> optionalConfigurationDTO = Optional.of(new ConfigurationDTO(BDR_REPORTABLE_PERIOD_TOGGLE_KEY, "false"));

        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 11, 11).atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
            when(configurationService.getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY)).thenReturn(optionalConfigurationDTO);

            listener.onBDRAccountUpdatedCreatedEvent(event);
        }

        verify(bdrCreationService, never()).createBDR(1L);
    }
}
