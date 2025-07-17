import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAccountViewService, RequestInfoDTO } from 'pmrv-api';

import { getPreviewDocumentsInfoDoe } from '../../utils/previewDocuments-doe.util';

interface ViewModel {
  taskId: number;
  accountId: number;
  hasLocation: boolean;
  referenceCode: string;
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-doe-notify-operator',
  templateUrl: './doe-notify-operator.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoeNotifyOperatorComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))),
    this.requestStore.pipe(map((state) => state?.requestTaskItem?.requestInfo?.accountId)),
    this.requestStore.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    switchMap(([taskId, accountId, requestInfo]: [number, number, RequestInfoDTO]) =>
      this.accountViewService.getAviationAccountById(accountId).pipe(
        first(),
        map((account) => ({
          taskId,
          accountId,
          hasLocation: !!(account && account?.aviationAccount?.location),
          referenceCode: requestInfo.id,
          previewDocuments: getPreviewDocumentsInfoDoe('AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR'),
        })),
      ),
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly requestStore: RequestTaskStore,
    private readonly accountViewService: AviationAccountViewService,
  ) {}
}
