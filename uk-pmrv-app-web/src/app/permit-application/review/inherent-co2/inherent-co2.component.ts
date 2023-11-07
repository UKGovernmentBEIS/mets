import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-inherent-co2',
  templateUrl: './inherent-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCO2Component {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  showDiff$ = this.store.showDiff$;
  originalInherentReceivingTransferringInstallations$ = this.store.findOriginalTask<
    InherentReceivingTransferringInstallation[]
  >('monitoringApproaches.INHERENT_CO2.inherentReceivingTransferringInstallations');

  inherentReceivingTransferringInstallations$ = this.store.findTask<InherentReceivingTransferringInstallation>(
    'monitoringApproaches.INHERENT_CO2.inherentReceivingTransferringInstallations',
  );

  constructor(
    private store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
