import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { NerDetailsSummaryTemplateComponent } from '@shared/components/ner';
import { AttachedFile } from '@shared/types/attached-file.type';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { NER, NERApplicationVerificationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  requestTaskType: RequestTaskDTO['type'];
  ner: NER;
  nerFile: AttachedFile;
  nerSupportingFiles: AttachedFile[];
  mmpFile: AttachedFile;
  mmpSupportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-ner-details-verification',
  imports: [NerTaskComponent, NerDetailsSummaryTemplateComponent],
  template: `
    @let vm = this.vm();

    <app-ner-task heading="New entrant reserve" [taskType]="vm.requestTaskType">
      <app-ner-details-summary-template
        [isEditable]="false"
        [ner]="vm.ner"
        [nerFile]="vm.nerFile"
        [nerSupportingFiles]="vm.nerSupportingFiles"
        [mmpFile]="vm.mmpFile"
        [mmpSupportingFiles]="vm.mmpSupportingFiles"></app-ner-details-summary-template>
    </app-ner-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerDetailsVerificationComponent {
  private readonly nerService = inject(NerService);
  private readonly payload = this.nerService.payload as Signal<NERApplicationVerificationSubmitRequestTaskPayload>;
  private readonly requestTaskType = this.nerService.requestTaskType;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const ner = payload.ner;
    const requestTaskType = this.requestTaskType();
    const nerFile = this.nerService.getOperatorDownloadUrlFile(ner.nerFiles.file);
    const nerSupportingFiles = this.nerService.getOperatorDownloadUrlFiles(ner.nerFiles.supportingFiles);
    const mmpFile = this.nerService.getOperatorDownloadUrlFile(ner.mmpFiles.file);
    const mmpSupportingFiles = this.nerService.getOperatorDownloadUrlFiles(ner.mmpFiles.supportingFiles);

    return { requestTaskType, ner, nerFile, nerSupportingFiles, mmpFile, mmpSupportingFiles };
  });
}
