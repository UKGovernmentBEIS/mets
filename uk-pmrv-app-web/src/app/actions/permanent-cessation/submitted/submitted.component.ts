import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { PermanentCessationDetailsSummaryTemplateComponent } from '@shared/components/permanent-cessation/permanent-cessation-details-summary-template/permanent-cessation-details-summary-template.component';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';

import { PermanentCessation } from 'pmrv-api';

import { PermanentCessationActionService } from '../core/permanent-cessation-action.service';

interface ViewModel {
  permanentCessation: PermanentCessation;
  files: AttachedFile[];
}

@Component({
  selector: 'app-permanent-cessation-action-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, PermanentCessationDetailsSummaryTemplateComponent],
  template: `
    <app-base-action-container-component
      header="Notice sent to operator"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['PERMANENT_CESSATION_APPLICATION_SUBMITTED']"></app-base-action-container-component>

    <ng-template #customContentTemplate>
      <h2 app-summary-header class="govuk-heading-m">Details</h2>

      <app-permanent-cessation-details-summary-template
        *ngIf="vm() as vm"
        [isEditable]="false"
        [data]="vm.permanentCessation"
        [files]="vm.files"></app-permanent-cessation-details-summary-template>

      <app-action-recipients-template
        header="Recipients"
        officialNoticeText="Notice document"></app-action-recipients-template>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class PermanentCessationActionSubmittedComponent {
  payload = this.permanentCessationActionService.getPermanentCessationPayload();

  vm: Signal<ViewModel> = computed(() => {
    const { permanentCessation, permanentCessationAttachments } = this.payload() || {};

    return {
      permanentCessation: permanentCessation,
      files: permanentCessationAttachments
        ? this.permanentCessationActionService.getDownloadUrlFiles(permanentCessation?.files)
        : [],
    };
  });

  constructor(private readonly permanentCessationActionService: PermanentCessationActionService) {}
}
