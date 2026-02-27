import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { Bdrs2ActionService } from '@actions/bdrs2/core/bdrs2.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRS2ApplicationSubmittedRequestActionPayload } from 'pmrv-api';
interface ViewModel {
  notes: string;
  opinionStatementFiles: AttachedFile[];
}

@Component({
  selector: 'app-bdrs2-opinion-statement-submitted',
  imports: [ActionSharedModule, SharedModule],
  standalone: true,
  template: `
    @if (vm(); as vm) {
      <app-action-task header="Stage 2 BDR verification opinion statement" [breadcrumb]="true">
        <app-opinion-statement-summary-template
          opinionStatementFilesText="Uploaded stage 2 BDR verification opinion statement"
          [notes]="vm?.notes"
          [opinionStatementFiles]="vm?.opinionStatementFiles"
          [isEditable]="false"></app-opinion-statement-summary-template>
      </app-action-task>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2OpinionStatementSubmittedComponent {
  requestActionType = this.bdrs2ActionService.requestActionType;
  payload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const opinionStatement = this.payload().verificationReport?.opinionStatement;

    return {
      notes: opinionStatement?.notes,
      opinionStatementFiles: opinionStatement?.opinionStatementFiles
        ? this.bdrs2ActionService.getVerifierDownloadUrlFiles(opinionStatement?.opinionStatementFiles)
        : [],
    };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
