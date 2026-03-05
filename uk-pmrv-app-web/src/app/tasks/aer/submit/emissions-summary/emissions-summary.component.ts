import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-emissions-summary',
  templateUrl: './emissions-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryComponent {
  aerPayload$ = this.aerService
    .getPayload()
    .pipe(map((payload) => (payload as AerApplicationSubmitRequestTaskPayload).aer));

  constructor(private readonly aerService: AerService) {}
}
