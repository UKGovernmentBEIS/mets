import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { NerDetailsSummaryTemplateComponent } from '@shared/components/ner';
import { AttachedFile } from '@shared/types/attached-file.type';

import { NERApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NerActionService } from '../../core/ner.service';

interface ViewModel {
  ner: NERApplicationSubmittedRequestActionPayload['ner'];
  nerFile: AttachedFile;
  nerSupportingFiles: AttachedFile[];
  mmpFile: AttachedFile;
  mmpSupportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-ner-action-details',
  imports: [ActionSharedModule, NerDetailsSummaryTemplateComponent],
  template: `
    @let vm = this.vm();

    <app-action-task header="New entrant reserve" [breadcrumb]="true">
      <app-ner-details-summary-template
        [isEditable]="false"
        [ner]="vm.ner"
        [nerFile]="vm.nerFile"
        [nerSupportingFiles]="vm.nerSupportingFiles"
        [mmpFile]="vm.mmpFile"
        [mmpSupportingFiles]="vm.mmpSupportingFiles"></app-ner-details-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerActionDetailsComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly payload = this.nerActionService.payload as Signal<NERApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const ner = payload.ner;
    const nerFile = this.nerActionService.getOperatorDownloadUrlFile(ner.nerFiles.file);
    const nerSupportingFiles = this.nerActionService.getOperatorDownloadUrlFiles(ner.nerFiles.supportingFiles);
    const mmpFile = this.nerActionService.getOperatorDownloadUrlFile(ner.mmpFiles.file);
    const mmpSupportingFiles = this.nerActionService.getOperatorDownloadUrlFiles(ner.mmpFiles.supportingFiles);

    return { ner, nerFile, nerSupportingFiles, mmpFile, mmpSupportingFiles };
  });
}
