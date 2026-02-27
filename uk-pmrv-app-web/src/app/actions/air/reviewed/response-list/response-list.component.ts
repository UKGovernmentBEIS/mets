import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../../core/air.service';

@Component({
  selector: 'app-response-list',
  standalone: false,
  templateUrl: './response-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponseListComponent {
  airPayload$ = this.airService.payload$ as Observable<AirApplicationReviewedRequestActionPayload>;
  airTitle$ = this.airPayload$.pipe(
    map((payload) => payload.reportingYear + ' Annual improvement report decision submitted'),
  );

  constructor(private readonly airService: AirService) {}
}
