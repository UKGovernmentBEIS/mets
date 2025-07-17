import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { DreApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DreService } from '../../core/dre.service';

@Component({
  selector: 'app-dre-summary',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>Reportable emissions</app-page-heading>
        <app-dre-summary-template
          cssClass="summary-list--edge-border"
          [dre]="dre$ | async"
          [isEditable]="false"
          [supportingDocumentFiles]="supportingDocumentFiles$ | async"></app-dre-summary-template>
        <app-official-notice-recipients [action]="action$ | async"></app-official-notice-recipients>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DreSummaryComponent {
  dre$ = (this.dreService.getPayload() as Observable<DreApplicationSubmittedRequestActionPayload>).pipe(
    map((payload) => payload.dre),
  );

  action$ = this.store.pipe(map((store) => store.action));

  constructor(
    private readonly dreService: DreService,
    private readonly store: CommonActionsStore,
  ) {}

  get supportingDocumentFiles$() {
    return this.dre$.pipe(
      map((dre) => this.dreService.getDownloadUrlFiles(dre.determinationReason?.supportingDocuments)),
    );
  }
}
