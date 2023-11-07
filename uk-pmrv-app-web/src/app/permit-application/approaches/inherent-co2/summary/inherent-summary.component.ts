import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { ApproachTaskPipe } from '../../approach-task.pipe';

@Component({
  selector: 'app-inherent-summary',
  templateUrl: './inherent-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  installations$ = this.approachTaskPipe.transform('INHERENT_CO2').pipe(
    map((response: InherentCO2MonitoringApproach) => {
      return response?.inherentReceivingTransferringInstallations;
    }),
  );

  constructor(
    private approachTaskPipe: ApproachTaskPipe,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
