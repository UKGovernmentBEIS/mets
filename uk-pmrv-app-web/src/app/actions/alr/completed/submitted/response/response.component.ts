import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AlrAuthoritySummaryTemplateComponent } from '@shared/components/alr/authority-summary-template/authority-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  ALRApplicationAcceptedRequestActionPayload,
  ALRApplicationAcceptedWithCorrectionsRequestActionPayload,
  ALRApplicationAuthorityReviewOutcome,
  ALRApplicationRejectedRequestActionPayload,
  ALRGrantAuthorityResponse,
} from 'pmrv-api';

interface ViewModel {
  authorityResponse: ALRApplicationAuthorityReviewOutcome['authorityResponse'];
  documentFiles: AttachedFile[];
}

@Component({
  selector: 'app-alr-action-response',
  standalone: true,
  imports: [ActionSharedModule, SharedModule, NgIf, AlrAuthoritySummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="Provide the date application was submitted to UK authorities" [breadcrumb]="true">
        <app-alr-authority-summary-template
          [data]="vm.authorityResponse"
          [documents]="vm.documentFiles"
          [editable]="false"></app-alr-authority-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActionResponseComponent {
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();

    return {
      authorityResponse: payload.authorityReviewOutcome.authorityResponse,
      documentFiles: this.alrActionService.getOperatorDownloadUrlFiles(
        (payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse).documents,
      ),
    };
  });

  private readonly payload = this.alrActionService.payload as Signal<
    | ALRApplicationAcceptedRequestActionPayload
    | ALRApplicationAcceptedWithCorrectionsRequestActionPayload
    | ALRApplicationRejectedRequestActionPayload
  >;

  constructor(private readonly alrActionService: AlrActionService) {}
}
