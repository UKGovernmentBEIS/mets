import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { UIConfigurationService } from 'pmrv-api';

@Component({
  selector: 'app-service-banner',
  templateUrl: './service-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceBannerComponent {
  notifications$ = this.configurationService.getUIConfiguration().pipe(map((res) => res.notificationAlerts));

  constructor(private readonly configurationService: UIConfigurationService) {}
}
