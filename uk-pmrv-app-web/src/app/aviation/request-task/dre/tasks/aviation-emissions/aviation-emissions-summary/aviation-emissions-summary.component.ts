import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { dreQuery } from '@aviation/request-task/dre/dre.selectors';
import { requestTaskQuery } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { AviationEmissionsSummaryTemplateComponent } from '@aviation/shared/components/dre/aviation-emissions-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationDre } from 'pmrv-api';

import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

export interface ViewModel {
  data: AviationDre | null;
  files: { downloadUrl: string; fileName: string }[];
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
}
@Component({
  selector: 'app-aviation-emissions-summary',
  templateUrl: './aviation-emissions-summary.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    AviationEmissionsSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AviationEmissionsSummaryComponent extends BaseAviationEmissionsComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(dreQuery.selectSectionCompleted),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: this.formProvider.getFormValue(),
        files:
          this.form.getRawValue()?.calculationApproach?.supportingDocuments?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.dreDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        pageHeader: getSummaryHeaderForTaskType(type, 'dre'),
        isEditable,
        hideSubmit: !isEditable || !(taskStatus === null || taskStatus === false),
      };
    }),
  );

  onSubmit() {
    if (this.form?.valid) {
      this.saveDreAndNavigate(this.formProvider.getFormValue(), 'complete', '../../../', true);
    }
  }
}
