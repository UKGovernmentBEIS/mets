import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionService } from '../../core/doal-action.service';

@Component({
  selector: 'app-doal-additional-documents',
  template: `
    <app-doal-action-task header="Upload additional documents" [actionType]="requestActionType$ | async">
      <app-doal-additional-documents-summary-template
        [additionalDocuments]="additionalDocuments$ | async"
        [editable]="false"
        [documents]="files$ | async"
      ></app-doal-additional-documents-summary-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsComponent {
  additionalDocuments$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.doal.additionalDocuments));
  files$ = this.additionalDocuments$.pipe(
    map((additionalDocuments) =>
      additionalDocuments?.documents ? this.doalActionService.getDownloadUrlFiles(additionalDocuments.documents) : [],
    ),
  );

  requestActionType$: Observable<RequestActionInfoDTO['type']> = this.commonActionsStore.requestAction$.pipe(
    map((ra) => ra.type),
  );

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
