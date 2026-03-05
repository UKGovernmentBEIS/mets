import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, BDRRequestMetadata, RequestMetadata } from 'pmrv-api';

import { BdrTaskSharedModule } from '../shared/bdr-task-shared.module';
import { BdrService } from '../shared/services/bdr.service';
import { submitReviewWizardComplete } from './review.wizard';

@Component({
  selector: 'app-bdr-review-container',
  templateUrl: './review-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
})
export class ReviewContainerComponent {
  requestTaskType = toSignal(this.store.requestTaskType$);
  requestMetadata: Signal<RequestMetadata> = this.bdrService.requestMetadata;
  title: Signal<string> = computed(() => {
    const requestMetadata = this.requestMetadata();
    const requestTaskType = this.requestTaskType();

    switch (requestTaskType) {
      case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT':
        return 'Review ' + (requestMetadata as BDRRequestMetadata)?.year + ' baseline data report';
      case 'BDR_WAIT_FOR_AMENDS':
        return (requestMetadata as BDRRequestMetadata)?.year + ' baseline data report';
      case 'BDR_WAIT_FOR_PEER_REVIEW':
        return `${(requestMetadata as BDRRequestMetadata)?.year} baseline data report sent to peer reviewer`;
      case 'BDR_APPLICATION_PEER_REVIEW':
        return `Peer review ${(requestMetadata as BDRRequestMetadata)?.year} baseline data report`;
    }
  });
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  hasVerificationReport = computed(() => {
    return !!this.bdrPayload().verificationReport;
  });
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  daysRemaining = this.bdrService.daysRemaining;

  sectionsCompleted = computed(() => {
    const payload = this.bdrPayload();
    return submitReviewWizardComplete(payload);
  });

  baseUrl = computed(() => {
    const payload = this.bdrPayload();
    switch (payload?.payloadType) {
      case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
        return './';
      case 'BDR_WAIT_FOR_AMENDS_PAYLOAD':
      case 'BDR_APPLICATION_PEER_REVIEW_PAYLOAD':
        return '../review/';

      default:
        return '';
    }
  });

  readonly allowReturnForAmends$: Observable<boolean> = combineLatest([this.bdrService.getPayload(), this.store]).pipe(
    map(([payload, state]) => {
      return (
        payload.regulatorReviewGroupDecisions?.BDR?.type === 'OPERATOR_AMENDS_NEEDED' &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      );
    }),
  );

  readonly allowSendForPeerReview$: Observable<boolean> = combineLatest([
    this.bdrService.getPayload(),
    this.store,
  ]).pipe(
    map(([payload, state]) => {
      return (
        this.sectionsCompleted() &&
        (payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          'outcome'
        ] &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDR_REQUEST_PEER_REVIEW')
      );
    }),
  );

  readonly allowCompleteReview$: Observable<boolean> = combineLatest([this.bdrService.getPayload(), this.store]).pipe(
    map(([payload, state]) => {
      return (
        this.sectionsCompleted() &&
        (payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          'outcome'
        ] &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDR_REGULATOR_REVIEW_SUBMIT')
      );
    }),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    map((state) => state.requestTaskItem.allowedRequestTaskActions.includes('BDR_SUBMIT_PEER_REVIEW_DECISION')),
  );

  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
    private readonly route: ActivatedRoute,
  ) {}

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  sendForPeerReview() {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  completeReview(): void {
    this.router.navigate(['complete-review'], { relativeTo: this.route });
  }

  peerReviewDecision() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}
