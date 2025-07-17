import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerEmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { EmissionsReductionClaimSummaryTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerSaf, AviationAerSafPurchase } from 'pmrv-api';

interface ViewModel {
  data: AviationAerSaf;
  pageHeader: string;
  purchases: {
    purchase: AviationAerSafPurchase;
    files: { downloadUrl: string; fileName: string }[];
  }[];
  declarationFile: { downloadUrl: string; fileName: string };
}

@Component({
  selector: 'app-aer-emissions-reduction-claim-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmissionsReductionClaimSummaryTemplateComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <ng-container *ngIf="vm.data">
        <app-aer-emissions-reduction-claim-summary-template
          [data]="vm.data"
          [declarationFile]="vm.declarationFile"
          [purchases]="vm.purchases"></app-aer-emissions-reduction-claim-summary-template>
      </ng-container>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EmissionsReductionClaimSummaryComponent {
  form = this.formProvider.form;
  downloadUrl = `${this.store?.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskType)]).pipe(
    map(() => {
      return {
        data: {
          ...this.form.value,
          safDetails: {
            ...this.form.value.safDetails,
            noDoubleCountingDeclarationFile: this.form.value.safDetails?.noDoubleCountingDeclarationFile?.file.name,
          },
        },
        purchases:
          this.form.value.safDetails?.purchases?.map((item) => ({
            purchase: item,
            files:
              item?.evidenceFiles?.map((doc) => {
                return {
                  fileName: doc.file.name,
                  downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
                };
              }) ?? [],
          })) ?? [],
        declarationFile: {
          fileName: this.form.value.safDetails?.noDoubleCountingDeclarationFile?.file.name,
          downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${this.form.value.safDetails?.noDoubleCountingDeclarationFile?.uuid}`,
        },
        pageHeader: 'Emissions reduction claim',
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AerEmissionsReductionClaimFormProvider,
    private store: RequestTaskStore,
  ) {}
}
