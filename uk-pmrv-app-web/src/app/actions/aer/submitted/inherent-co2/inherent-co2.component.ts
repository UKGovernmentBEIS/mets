import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { getInherentInstallations } from './inherent-co2';

@Component({
  selector: 'app-inherent-co2',
  template: `
    <app-action-task header="{{ 'INHERENT_CO2' | monitoringApproachEmissionDescription }}" [breadcrumb]="true">
      <app-inherent-co2-group [inherentInstallations]="inherentInstallations$ | async"></app-inherent-co2-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCo2Component {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  inherentInstallations$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => getInherentInstallations(payload)));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private readonly router: Router,
  ) {}
}
