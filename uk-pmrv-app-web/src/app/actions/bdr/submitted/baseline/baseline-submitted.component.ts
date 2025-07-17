import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BdrActionService } from '@actions/bdr/core/bdr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  bdr: BDRApplicationSubmittedRequestActionPayload['bdr'];
  files: AttachedFile[];
  bdrFile: AttachedFile;
  mmpFiles: AttachedFile[];
}

@Component({
  selector: 'app-baseline-submitted',
  standalone: true,
  imports: [ActionSharedModule, BaselineSummaryTemplateComponent, NgIf, SharedModule],
  templateUrl: './baseline-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineSubmittedComponent {
  payload = this.bdrActionService.payload as Signal<BDRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const bdr = this.payload().bdr;

    return {
      bdr,
      files: bdr?.files ? this.bdrActionService.getOperatorDownloadUrlFiles(bdr?.files) : [],
      bdrFile: bdr?.bdrFile ? this.bdrActionService.getOperatorDownloadUrlBdrFile(bdr?.bdrFile) : null,
      mmpFiles: bdr?.mmpFiles ? this.bdrActionService.getOperatorDownloadUrlFiles(bdr?.mmpFiles) : [],
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
