import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { doeQuery } from '@aviation/request-task/doe/doe.selectors';
import { requestTaskQuery } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { DoeEmissionsSummaryTemplateComponent } from '@aviation/shared/components/doe/doe-emissions-summary-template/doe-emissions-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationDoECorsia } from 'pmrv-api';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

export interface ViewModel {
  data: AviationDoECorsia | null;
  files: { downloadUrl: string; fileName: string }[];
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
}
@Component({
  selector: 'app-doe-emissions-summary',
  templateUrl: './emissions-summary.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    DoeEmissionsSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoeEmissionsSummaryComponent extends BaseDoeCorsiaEmissionsComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(doeQuery.selectSectionCompleted),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: this.formProvider.getFormValue(),
        files:
          this.form.getRawValue()?.emissions?.supportingDocuments?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.doeDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        pageHeader: getSummaryHeaderForTaskType(type, 'doe'),
        isEditable,
        hideSubmit: !isEditable || !(taskStatus === null || taskStatus === false),
      };
    }),
  );

  onSubmit() {
    if (this.form?.valid) {
      this.saveDoeAndNavigate(this.formProvider.getFormValue(), 'complete', '../../../', true);
    }
  }
}
