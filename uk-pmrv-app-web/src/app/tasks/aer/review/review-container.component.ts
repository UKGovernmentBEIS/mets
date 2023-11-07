import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { atLeastOneSectionForAmendsExists, isEverySectionAccepted } from '@tasks/aer/core/aer-task-statuses';

import { AerApplicationReviewRequestTaskPayload, AerRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { StatusKey } from '../core/aer-task.type';
import { monitoringApproachMap } from '../core/monitoringApproaches';

@Component({
  selector: 'app-review-container',
  templateUrl: './review-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewContainerComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  requestTaskType$ = this.store.requestTaskType$;
  readonly daysRemaining$ = this.aerService.daysRemaining$;

  baseUrl$ = this.aerService.getPayload().pipe(
    map((payload) => {
      switch (payload?.payloadType) {
        case 'AER_WAIT_FOR_AMENDS_PAYLOAD':
          return '../review/';
        case 'AER_APPLICATION_REVIEW_PAYLOAD':
          return './';
        default:
          return '';
      }
    }),
  );

  aerTitle$ = combineLatest([this.aerService.requestMetadata$, this.store.requestTaskType$]).pipe(
    map(([metadata, requestTaskType]) => {
      const year = (metadata as AerRequestMetadata).year;
      switch (requestTaskType) {
        case 'AER_APPLICATION_REVIEW':
          return 'Review ' + year + ' emissions report';
        case 'AER_WAIT_FOR_AMENDS':
          return year + ' emissions report';
      }
    }),
  );

  monitoringApproaches$ = this.aerService.getTask('monitoringApproachEmissions').pipe(
    map(
      (monitoringApproachEmissions) =>
        Object.keys(monitoringApproachEmissions).map((approach) => monitoringApproachMap(approach)) as {
          link: string;
          linkText: string;
          status: StatusKey;
        }[],
    ),
  );
  hasVerificationReport$ = this.aerService
    .getPayload()
    .pipe(map((payload) => !!(payload as AerApplicationReviewRequestTaskPayload).verificationReport));

  isGHGE$: Observable<boolean> = this.aerService
    .getPayload()
    .pipe(map((payload) => payload?.permitOriginatedData?.permitType === 'GHGE'));

  readonly allowCompleteReview$: Observable<boolean> = combineLatest([this.aerService.getPayload(), this.store]).pipe(
    map(([payload, state]) => {
      return (
        isEverySectionAccepted(payload) &&
        state.requestTaskItem.allowedRequestTaskActions.includes('AER_COMPLETE_REVIEW')
      );
    }),
  );

  readonly allowReturnForAmends$: Observable<boolean> = combineLatest([this.aerService.getPayload(), this.store]).pipe(
    map(([payload, state]) => {
      return (
        atLeastOneSectionForAmendsExists(payload) &&
        state.requestTaskItem.allowedRequestTaskActions.includes('AER_REVIEW_RETURN_FOR_AMENDS')
      );
    }),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: CommonTasksStore,
  ) {}

  completeReview(): void {
    this.router.navigate(['complete-review'], { relativeTo: this.route });
  }

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }
}
