import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';
import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-approaches',
  templateUrl: './approaches.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [PendingRequestService],
})
export class ApproachesComponent implements PendingRequest {
  monitoringApproaches$ = this.aerService
    .getTask('monitoringApproachEmissions')
    .pipe(
      map((monitoringApproaches) =>
        monitoringApproaches !== undefined
          ? monitoringApproachTypeOptions.filter(
              (option) => monitoringApproaches[option]?.type || monitoringApproaches[option] !== undefined,
            )
          : [],
      ),
    );

  constructor(
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave({}, {}, true, 'monitoringApproachEmissions')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
