import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2RequestMetadata,
  RequestMetadata,
} from 'pmrv-api';

import { BdrS2Service } from '../core';
import { BdrS2TaskSharedModule } from '../shared/bdrs2-task-shared.module';
import { submitReviewWizardComplete } from './review.wizard';

@Component({
  selector: 'app-bdrs2-review',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  templateUrl: './review.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewComponent {
  requestTaskType = toSignal(this.store.requestTaskType$);
  requestMetadata: Signal<RequestMetadata> = this.bdrs2Service.requestMetadata;
  title: Signal<string> = computed(() => {
    const requestMetadata = this.requestMetadata();
    const requestTaskType = this.requestTaskType();

    switch (requestTaskType) {
      case 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT':
        return 'Review ' + (requestMetadata as BDRS2RequestMetadata)?.year + ' stage 2 baseline data report';
      case 'BDRS2_WAIT_FOR_AMENDS':
        return (requestMetadata as BDRS2RequestMetadata)?.year + ' stage 2 baseline data report';
      case 'BDRS2_WAIT_FOR_PEER_REVIEW':
        return `${(requestMetadata as BDRS2RequestMetadata)?.year} stage 2 baseline data report sent to peer reviewer`;
      case 'BDRS2_APPLICATION_PEER_REVIEW':
        return `Peer review ${(requestMetadata as BDRS2RequestMetadata)?.year} stage 2 baseline data report`;
    }
  });
  bdrs2Payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;
  hasVerificationReport = computed(() => {
    return !!this.bdrs2Payload().verificationReport;
  });
  notification = this.router.currentNavigation()?.extras.state?.notification;
  daysRemaining = this.bdrs2Service.daysRemaining;

  sectionsCompleted = computed(() => {
    return submitReviewWizardComplete(this.bdrs2Payload());
  });

  baseUrl = computed(() => {
    const payload = this.bdrs2Payload();
    switch (payload?.payloadType) {
      case 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
        return './';
      case 'BDRS2_WAIT_FOR_AMENDS_PAYLOAD':
      case 'BDRS2_APPLICATION_PEER_REVIEW_PAYLOAD':
        return '../review/';
      default:
        return '';
    }
  });

  readonly allowReturnForAmends$: Observable<boolean> = combineLatest([
    this.bdrs2Service.getPayload(),
    this.store,
  ]).pipe(
    map(([payload, state]) => {
      return (
        payload.regulatorReviewGroupDecisions?.BDRS2?.type === 'OPERATOR_AMENDS_NEEDED' &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      );
    }),
  );

  readonly allowCompleteReview$: Observable<boolean> = combineLatest([this.bdrs2Service.getPayload(), this.store]).pipe(
    map(([payload, state]) => {
      return (
        this.sectionsCompleted() &&
        (payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          'outcome'
        ] &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDRS2_REGULATOR_REVIEW_SUBMIT')
      );
    }),
  );

  readonly allowSendForPeerReview$: Observable<boolean> = combineLatest([
    this.bdrs2Service.getPayload(),
    this.store,
  ]).pipe(
    map(([payload, state]) => {
      return (
        this.sectionsCompleted() &&
        (payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          'outcome'
        ] &&
        state.requestTaskItem.allowedRequestTaskActions.includes('BDRS2_REQUEST_PEER_REVIEW')
      );
    }),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    map((state) => state.requestTaskItem.allowedRequestTaskActions.includes('BDRS2_SUBMIT_PEER_REVIEW_DECISION')),
  );

  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
    private readonly route: ActivatedRoute,
  ) {}

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  completeReview(): void {
    this.router.navigate(['complete-review'], { relativeTo: this.route });
  }
  sendForPeerReview() {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  peerReviewDecision() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}
