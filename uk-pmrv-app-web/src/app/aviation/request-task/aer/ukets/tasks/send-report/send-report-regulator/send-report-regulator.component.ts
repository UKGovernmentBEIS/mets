import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

export interface SendReportRegulatorViewModel {
  header: string;
  competentAuthority: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
  submitHidden: boolean;
}

@Component({
  selector: 'app-send-report-regulator',
  templateUrl: './send-report-regulator.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportRegulatorComponent {
  isSubmitted$ = new BehaviorSubject(false);

  vm$: Observable<SendReportRegulatorViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectReportingObligation),
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([reportingObligation, authority, isEditable]) => {
      return {
        header: reportingObligation.reportingRequired
          ? 'Send report to regulator'
          : 'Send report to regulator without verification',
        competentAuthority: authority,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(
    private readonly store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store
      .pipe(
        take(1),
        requestTaskQuery.selectRequestTaskType,
        switchMap((requestTaskType) => {
          let actionType;

          switch (requestTaskType) {
            case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
              actionType = 'AVIATION_AER_UKETS_SUBMIT_APPLICATION';
              break;
            case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND';
              break;
          }

          return this.store.aerDelegate.submitAer(actionType);
        }),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation-regulator'], { relativeTo: this.route });
      });
  }
}
