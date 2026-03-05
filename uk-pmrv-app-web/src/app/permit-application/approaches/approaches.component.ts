import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-approaches',
  templateUrl: './approaches.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesComponent extends SectionComponent implements PendingRequest {
  monitoringApproaches$ = this.store
    .getTask('monitoringApproaches')
    .pipe(
      map((monitoringApproaches) =>
        monitoringApproaches !== undefined
          ? monitoringApproachTypeOptions.filter((option) => Object.keys(monitoringApproaches).includes(option))
          : [],
      ),
    );

  isEveryMonitoringApproachDefined$ = this.monitoringApproaches$.pipe(
    map((monitoringApproaches) => monitoringApproachTypeOptions.every((value) => monitoringApproaches.includes(value))),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus('monitoringApproaches', true, data.permitTask)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateSubmitSection('summary', 'monitoring-approaches'));
  }
}
