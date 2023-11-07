import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { originalOrder } from '@shared/keyvalue-order';

import { AirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../core/air.service';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$ = this.airService.payload$ as Observable<AirApplicationSubmittedRequestActionPayload>;
  title$ = this.payload$.pipe(map((payload) => payload.reportingYear + ' Annual improvement report submitted'));
  airImprovements$ = this.payload$.pipe(map((payload) => payload?.airImprovements));
  readonly originalOrder = originalOrder;

  constructor(private readonly airService: AirService) {}
}
