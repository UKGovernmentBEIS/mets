import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { ALRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  notes: string;
  opinionStatementFiles: AttachedFile[];
  supportingFiles: AttachedFile[];
}

@Component({
  selector: 'app-opinion-statement-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="ALR verification opinion statement" [breadcrumb]="true">
        <app-opinion-statement-summary-template
          opinionStatementFilesText="Uploaded ALR verification opinion statement"
          [notes]="vm?.notes"
          [opinionStatementFiles]="vm?.opinionStatementFiles"
          [supportingFiles]="vm?.supportingFiles"
          [isEditable]="false"></app-opinion-statement-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOpinionStatementSubmittedComponent {
  payload = this.alrActionService.payload as Signal<ALRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const opinionStatement = this.payload().verificationReport?.opinionStatement;

    return {
      notes: opinionStatement?.notes,
      opinionStatementFiles: opinionStatement?.opinionStatementFiles
        ? this.alrActionService.getVerifierDownloadUrlFiles(opinionStatement?.opinionStatementFiles)
        : [],
      supportingFiles: opinionStatement?.supportingFiles
        ? this.alrActionService.getVerifierDownloadUrlFiles(opinionStatement?.supportingFiles)
        : [],
    };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
