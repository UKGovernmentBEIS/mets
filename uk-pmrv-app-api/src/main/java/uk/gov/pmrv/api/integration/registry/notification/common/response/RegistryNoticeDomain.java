package uk.gov.pmrv.api.integration.registry.notification.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;

@Getter
@RequiredArgsConstructor
public enum RegistryNoticeDomain {

    INSTALLATION(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, NotifyRegistryUtils.ACCOUNT_INSTALLATION_NOTIFICATION_INTEGRATION_POINT_KEY),
    AVIATION(NotifyRegistryUtils.AVIATION_SERVICE_KEY, NotifyRegistryUtils.ACCOUNT_AVIATION_NOTIFICATION_INTEGRATION_POINT_KEY);

    private final String serviceKey;
    private final String integrationKey;
}
