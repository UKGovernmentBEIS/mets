import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { DoeEmissionsSummaryTemplateComponent } from '@aviation/shared/components/doe/doe-emissions-summary-template/doe-emissions-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';

import { AviationDoECorsia, AviationDoECorsiaSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  creationDate: string;
  data: AviationDoECorsia | null;
  files: { downloadUrl: string; fileName: string }[];
}

@Component({
  selector: 'app-doe-emissions-updated',
  templateUrl: './doe-emissions-updated.component.html',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    RouterModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    DoeEmissionsSummaryTemplateComponent,
    ActionSharedModule,
  ],
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoeEmissionsUpdatedComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, creationDate, requestActionType]) => {
      const doePayload = payload as AviationDoECorsiaSubmittedRequestActionPayload;
      if (!doePayload.doe?.supportingDocuments) {
        doePayload.doe.supportingDocuments = [];
      }
      return {
        requestActionType,
        pageHeader: 'Aviation emissions updated',
        creationDate,

        data: doePayload.doe,
        files:
          doePayload?.doe?.supportingDocuments?.map((uuid) => ({
            fileName: doePayload?.doeAttachments?.[uuid],
            downloadUrl: `${this.store.doeDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          })) ?? [],
      } as ViewModel;
    }),
  );

  constructor(public store: RequestActionStore) {}
}
