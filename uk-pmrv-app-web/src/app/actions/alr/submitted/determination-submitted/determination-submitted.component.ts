import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AlrDeterminationSummaryTemplateComponent } from '@shared/components/alr/determination-summary-template/determination-summary-template.component';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  ALRApplicationProceededToAuthorityRequestActionPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
} from 'pmrv-api';

interface ViewModel {
  alrFile: AttachedFile;
  files: AttachedFile[];
  determination: DoalProceedToAuthorityDetermination | ALRClosedDetermination;
}

@Component({
  selector: 'app-alr-determination-submitted',
  standalone: true,
  imports: [ActionSharedModule, AlrDeterminationSummaryTemplateComponent, NgIf],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="Determination of activity level" [breadcrumb]="true">
        <app-alr-determination-summary-template
          [determination]="vm.determination"
          [alrFile]="vm.alrFile"
          [files]="vm.files"
          [editable]="false"></app-alr-determination-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrDeterminationSubmittedComponent {
  payload = this.alrActionService.payload as Signal<ALRApplicationProceededToAuthorityRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const determination = payload.regulatorReviewOutcome.determination as ALRClosedDetermination;
    const isOperatorsAlrFile = determination.alrFile === payload.alr?.alrFile;
    const alrFile = determination.alrFile
      ? isOperatorsAlrFile
        ? this.alrActionService.getOperatorDownloadUrlAlrFile(determination.alrFile)
        : this.alrActionService.getRegulatorDownloadUrlAlrFile(determination.alrFile)
      : this.alrActionService.getOperatorDownloadUrlAlrFile(payload.alr?.alrFile);
    const files = determination.files
      ? this.alrActionService.getRegulatorDownloadUrlFiles(determination.files)
      : this.alrActionService.getOperatorDownloadUrlFiles(payload.alr?.files);

    return { alrFile, files, determination };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
