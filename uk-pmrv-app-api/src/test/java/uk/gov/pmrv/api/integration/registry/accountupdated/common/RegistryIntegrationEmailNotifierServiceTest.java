package uk.gov.pmrv.api.integration.registry.accountupdated.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistryIntegrationEmailNotifierServiceTest {

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationRegistryIntegrationEmailProperties;

    @Mock
    private AviationRegistryIntegrationEmailProperties aviationRegistryIntegrationEmailProperties;

    @InjectMocks
    private RegistryIntegrationEmailNotifierService registryIntegrationEmailNotifierService;

    @Test
    void registryIdNonExistenceNotifyRegulatorForAction_installation() {


        Map<String, String> installationEmails = new HashMap<>();
        installationEmails.put("EN", "installation@example.com");

        when(installationRegistryIntegrationEmailProperties.getEmail()).thenReturn(installationEmails);


        InstallationAccountDTO accountDTO = buildInstallationAccountDTO();

        registryIntegrationEmailNotifierService.registryIdNonExistenceNotifyRegulatorForAction(accountDTO);

        verify(installationRegistryIntegrationEmailProperties).getEmail();
        verify(notificationEmailService).notifyRecipient(any(), any());
    }

    @Test
    void registryIdNonExistenceNotifyRegulatorForAction_aviation() {


        Map<String, String> aviationEmails = new HashMap<>();
        aviationEmails.put("EN", "aviation@example.com");

        when(aviationRegistryIntegrationEmailProperties.getEmail()).thenReturn(aviationEmails);


        AviationAccountDTO accountDTO = buildAviationAccountDTO();

        registryIntegrationEmailNotifierService.registryIdNonExistenceNotifyRegulatorForAction(accountDTO);

        verify(aviationRegistryIntegrationEmailProperties).getEmail();
        verify(notificationEmailService).notifyRecipient(any(), any());
    }

    private InstallationAccountDTO buildInstallationAccountDTO() {
        return InstallationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.INSTALLATION)
                .name("Installation Account Name")
                .emitterId("emitterId")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
    }

    private AviationAccountDTO buildAviationAccountDTO() {
        return AviationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.AVIATION)
                .name("Aviation Account Name")
                .emitterId("emitterId")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
    }
}