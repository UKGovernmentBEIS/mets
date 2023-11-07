import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { monitoringApproachMap } from '@tasks/aer/core/monitoringApproaches';

import { AerRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-verification-submit',
  templateUrl: './verification-submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationSubmitContainerComponent {
  requestTaskType$ = this.store.requestTaskType$;
  aerTitle$ = this.aerService.requestMetadata$.pipe(
    map((metadata) => (metadata as AerRequestMetadata).year + ' emissions report'),
  );
  monitoringApproaches$ = this.aerService.getTask('monitoringApproachEmissions').pipe(
    map(
      (monitoringApproachEmissions) =>
        Object.keys(monitoringApproachEmissions).map((approach) => monitoringApproachMap(approach)) as {
          link: string;
          linkText: string;
        }[],
    ),
  );
  isGHGE$: Observable<boolean> = this.aerService
    .getPayload()
    .pipe(map((payload) => payload?.permitOriginatedData?.permitType === 'GHGE'));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  readonly daysRemaining$ = this.aerService.daysRemaining$;

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
  ) {}
}
