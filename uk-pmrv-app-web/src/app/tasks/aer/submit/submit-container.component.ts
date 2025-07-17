import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { monitoringApproachMap } from '@tasks/aer/core/monitoringApproaches';

import { AerRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { getTotalEmissionsKeys } from '../core/aer.amend.types';
import { StatusKey } from '../core/aer-task.type';

@Component({
  selector: 'app-submit',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  readonly daysRemaining$ = this.aerService.daysRemaining$;

  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  aerTitle$ = this.aerService.requestMetadata$.pipe(
    map((metadata) => (metadata as AerRequestMetadata)?.year + ' emissions report'),
  );

  isMeasurementOrN2OApproachesSelected$ = this.aerService.getTask('monitoringApproachEmissions').pipe(
    filter((monitoringApproachEmissions) => !!monitoringApproachEmissions),
    map(
      (monitoringApproachEmissions) =>
        monitoringApproachEmissions['MEASUREMENT_CO2'] !== undefined ||
        monitoringApproachEmissions['MEASUREMENT_N2O'] !== undefined,
    ),
  );

  monitoringApproaches$ = this.aerService.getTask('monitoringApproachEmissions').pipe(
    filter((monitoringApproachEmissions) => !!monitoringApproachEmissions),
    map(
      (monitoringApproachEmissions) =>
        Object.keys(monitoringApproachEmissions).map((approach) => monitoringApproachMap(approach)) as {
          link: string;
          linkText: string;
          status: StatusKey;
        }[],
    ),
  );

  isGHGE$: Observable<boolean> = this.aerService
    .getPayload()
    .pipe(map((payload) => payload?.permitOriginatedData?.permitType === 'GHGE'));

  isTaskTypeAmendsSubmit = this.aerService.requestTaskType === 'AER_APPLICATION_AMENDS_SUBMIT';

  reviewGroupsForAmend$ = this.aerService.reviewGroupsForAmend$;

  totalEmissions = getTotalEmissionsKeys();

  constructor(
    readonly aerService: AerService,
    private readonly router: Router,
    readonly store: CommonTasksStore,
  ) {}
}
