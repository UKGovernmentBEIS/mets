import { AfterViewInit, ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable, shareReplay, switchMap, take } from 'rxjs';

import { RequestActionReportService } from '@shared/services/request-action-report.service';

import {
  AerApplicationVerificationSubmittedRequestActionPayload,
  RequestActionDTO,
  RequestActionInfoDTO,
  RequestActionsService,
} from 'pmrv-api';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { AerService } from '../../../core/aer.service';
import { getCalculationHeading } from '../../approaches-tier/approaches-tier';
import { pointsColumns } from '../../emission-points/emission-points';
import { sourcesColumns } from '../../emission-sources/emission-sources';
import { getAerTitle } from '../../submitted';

@Component({
  selector: 'app-operator-submitted',
  standalone: false,
  templateUrl: './operator-submitted.component.html',
  styleUrl: './operator-submitted.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorSubmittedComponent implements AfterViewInit {
  requestAction$: Observable<RequestActionDTO> = this.commonActionsStore.requestAction$;
  requestActionType$: Observable<RequestActionDTO['type']> = this.requestAction$.pipe(
    map((requestAction) => requestAction.type),
  );
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();
  aerTitle$ = combineLatest([this.requestActionType$, this.payload$]).pipe(
    map(([requestActionType, payload]) => getAerTitle(requestActionType, payload)),
  );

  getCalculationHeading = getCalculationHeading;

  sourcesColumns = sourcesColumns;
  pointsColumns = pointsColumns;

  additionalDocumentFiles$ = this.payload$.pipe(
    map((payload) =>
      payload.aer.additionalDocuments.exist
        ? this.aerService.getDownloadUrlFiles(payload.aer.additionalDocuments.documents)
        : [],
    ),
  );

  activityLevelReportFiles$ = this.payload$.pipe(
    map((payload) => {
      const file = payload.aer?.activityLevelReport?.file;
      return file ? this.aerService.getDownloadUrlFiles([file]) : [];
    }),
  );

  readonly actions$ = this.requestAction$.pipe(
    switchMap((requestAction) =>
      combineLatest([
        this.requestActionsService.getRequestActionsByRequestId(requestAction?.requestId),
        this.requestAction$,
      ]).pipe(
        map(([res, requestAction]) => this.sortTimeline(res.filter((timeline) => timeline.id <= requestAction.id))),
      ),
    ),
  );

  readonly submittedToVerifierDate$ = this.actions$.pipe(
    map(
      (actions) =>
        actions.filter(
          (action) =>
            action.type === 'AER_APPLICATION_SENT_TO_VERIFIER' ||
            action.type === 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER',
        )?.[0]?.creationDate,
    ),
  );

  readonly verifierSubmittedDate$ = this.actions$.pipe(
    map(
      (actions) =>
        actions.filter((action) => action.type === 'AER_APPLICATION_VERIFICATION_SUBMITTED')?.[0]?.creationDate,
    ),
  );

  readonly submittedToRegulatorDate$ = this.actions$.pipe(
    map(
      (actions) =>
        actions.filter(
          (action) => action.type === 'AER_APPLICATION_SUBMITTED' || action.type === 'AER_APPLICATION_AMENDS_SUBMITTED',
        )?.[0]?.creationDate,
    ),
  );

  vm$ = combineLatest({
    payload: this.payload$,
    submittedToVerifierDate: this.submittedToVerifierDate$,
    verifierSubmittedDate: this.verifierSubmittedDate$,
    submittedToRegulatorDate: this.submittedToRegulatorDate$,
  }).pipe(shareReplay({ bufferSize: 1, refCount: true }));

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  constructor(
    private readonly aerService: AerService,
    private readonly commonActionsStore: CommonActionsStore,
    private requestActionsService: RequestActionsService,
    private requestActionReportService: RequestActionReportService,
    protected readonly route: ActivatedRoute,
  ) {}

  ngAfterViewInit(): void {
    this.vm$.pipe(take(1)).subscribe(() => {
      setTimeout(() => {
        this.requestActionReportService.print();
      });
    });
  }
}
