import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { AviationEmissionsSummaryTemplateComponent } from '@aviation/shared/components/dre/aviation-emissions-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import {
  AviationDre,
  AviationDreApplicationSubmittedRequestActionPayload,
  EmpIssuanceDetermination,
  FileInfoDTO,
  RequestActionDTO,
  RequestActionUserInfo,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  creationDate: string;
  determination: EmpIssuanceDetermination;
  usersInfo: { [key: string]: RequestActionUserInfo };
  empDocument: FileInfoDTO;
  officialNotice: FileInfoDTO;
  downloadUrl: string;
  data: AviationDre | null;
  files: { downloadUrl: string; fileName: string }[];
  signatory: string;
  operators: string[];
}

@Component({
  selector: 'app-aviation-emissions-updated',
  templateUrl: './aviation-emissions-updated.component.html',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    RouterModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    AviationEmissionsSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationEmissionsUpdatedComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, creationDate, requestActionType]) => {
      const drePayload = payload as AviationDreApplicationSubmittedRequestActionPayload;
      if (!drePayload.dre?.supportingDocuments) {
        drePayload.dre.supportingDocuments = [];
      }
      return {
        requestActionType,
        pageHeader: 'Aviation emissions updated',
        creationDate,
        usersInfo: drePayload?.usersInfo,
        officialNotice: drePayload?.officialNotice,
        downloadUrl: this.store.dreDelegate.baseFileDocumentDownloadUrl + '/',
        data: drePayload.dre,
        files:
          drePayload?.dre?.supportingDocuments?.map((uuid) => ({
            fileName: drePayload?.dreAttachments?.[uuid],
            downloadUrl: `${this.store.dreDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          })) ?? [],
        signatory: drePayload?.decisionNotification?.signatory,
        operators: Object.keys(drePayload.usersInfo).filter(
          (userId) => userId !== drePayload?.decisionNotification?.signatory,
        ),
      } as ViewModel;
    }),
  );

  constructor(public store: RequestActionStore) {}
}
