import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { Bdrs2ActionService } from '@actions/bdrs2/core/bdrs2.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRS2ApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  bdrs2: BDRS2ApplicationSubmittedRequestActionPayload['bdrs2'];
  bdrFile: AttachedFile;
  files: AttachedFile[];
  mmpFile: AttachedFile;
  mmpFiles: AttachedFile[];
}

@Component({
  selector: 'app-baseline-submitted',
  standalone: true,
  imports: [ActionSharedModule, BDRS2BaselineSummaryTemplateComponent, NgIf, SharedModule],
  templateUrl: './baseline-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2BaselineSubmittedComponent {
  payload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const bdrs2 = this.payload().bdrs2;

    return {
      bdrs2,
      bdrFile: bdrs2?.bdrs2Files?.file
        ? this.bdrs2ActionService.getOperatorDownloadUrlBdrFile(bdrs2?.bdrs2Files?.file)
        : null,
      files: bdrs2?.bdrs2Files?.supportingFiles
        ? this.bdrs2ActionService.getOperatorDownloadUrlFiles(bdrs2?.bdrs2Files?.supportingFiles)
        : [],
      mmpFile: bdrs2?.mmpFiles?.file
        ? this.bdrs2ActionService.getOperatorDownloadUrlBdrFile(bdrs2?.mmpFiles?.file)
        : null,
      mmpFiles: bdrs2?.mmpFiles?.supportingFiles
        ? this.bdrs2ActionService.getOperatorDownloadUrlFiles(bdrs2?.mmpFiles?.supportingFiles)
        : [],
    };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
