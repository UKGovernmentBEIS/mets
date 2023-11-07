import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '../core/aer.service';

@Component({
  selector: 'app-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsComponent {
  aerTitle$ = this.aerService.requestAction$.pipe(
    map((requestAction) => requestAction?.requestId?.split('-')[1] + ' emissions report returned for amends'),
  );

  decisionAmends$: Observable<any> = this.aerService.getPayload().pipe(
    map((payload) =>
      Object.keys(payload?.reviewGroupDecisions ?? [])
        .filter((key) => payload?.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({ groupKey: key, data: payload.reviewGroupDecisions[key] } as any)),
    ),
  );

  constructor(readonly aerService: AerService) {}
}
