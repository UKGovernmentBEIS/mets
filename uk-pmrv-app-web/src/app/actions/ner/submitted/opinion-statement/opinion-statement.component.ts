import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { NerActionService } from '@actions/ner/core';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { NERApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  notes: string;
  opinionStatementFile: AttachedFile;
  supportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-action-ner-opinion-statement',
  imports: [ActionSharedModule, SharedModule],
  template: `
    @let vm = this.vm();

    <app-action-task header="NER verification opinion statement" [breadcrumb]="true">
      <app-opinion-statement-summary-template
        opinionStatementFilesText="Uploaded NER verification opinion statement"
        [notes]="vm?.notes"
        [opinionStatementFile]="vm?.opinionStatementFile"
        [supportingFiles]="vm?.supportingFiles"
        [isEditable]="false"></app-opinion-statement-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerOpinionStatementSubmittedComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly payload = this.nerActionService.payload as Signal<NERApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const opinionStatement = this.payload().verificationReport?.opinionStatement;

    return {
      notes: opinionStatement?.notes,
      opinionStatementFile: opinionStatement?.opinionStatementFile
        ? this.nerActionService.getVerifierDownloadUrlFile(opinionStatement?.opinionStatementFile)
        : ({} as any),
      supportingFiles: opinionStatement?.supportingFiles
        ? this.nerActionService.getVerifierDownloadUrlFiles(opinionStatement?.supportingFiles)
        : [],
    };
  });
}
