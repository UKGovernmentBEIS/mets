import { AfterViewInit, ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionReportService } from '@shared/services/request-action-report.service';

import { AerApplicationVerificationSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { AerService } from '../../../core/aer.service';
import { pointsColumns } from '../../emission-points/emission-points';
import { sourcesColumns } from '../../emission-sources/emission-sources';
import { getAerTitle } from '../../submitted';

@Component({
  selector: 'app-operator-to-verifier',
  templateUrl: './operator-to-verifier.component.html',
  styleUrl: './operator-to-verifier.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorToVerifierComponent implements AfterViewInit {
  requestAction$: Observable<RequestActionDTO> = this.commonActionsStore.requestAction$;
  requestActionType$: Observable<RequestActionDTO['type']> = this.requestAction$.pipe(
    map((requestAction) => requestAction.type),
  );
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();

  aerTitle$ = combineLatest([this.requestActionType$, this.payload$]).pipe(
    map(([requestActionType, payload]) => getAerTitle(requestActionType, payload)),
  );

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

  constructor(
    private readonly aerService: AerService,
    private readonly commonActionsStore: CommonActionsStore,
    private requestActionReportService: RequestActionReportService,
  ) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      //give some time to angular to render the data
      this.requestActionReportService.print();
    }, 500);
  }
}
