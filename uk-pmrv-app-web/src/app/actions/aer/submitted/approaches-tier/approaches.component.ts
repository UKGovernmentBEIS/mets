import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-approaches',
  template: `
    <app-action-task header="{{ this.taskKey | monitoringApproachEmissionDescription }}" [breadcrumb]="true">
      <div [ngSwitch]="this.taskKey">
        <div *ngSwitchCase="'CALCULATION_CO2'">
          <app-calculation-emissions-group [data]="aer$ | async"></app-calculation-emissions-group>
        </div>
        <div *ngSwitchCase="'CALCULATION_PFC'">
          <app-pfc-group [data]="aer$ | async"></app-pfc-group>
        </div>
      </div>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesComponent {
  taskKey = this.route.snapshot.data.taskKey;
  aer$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer));

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
