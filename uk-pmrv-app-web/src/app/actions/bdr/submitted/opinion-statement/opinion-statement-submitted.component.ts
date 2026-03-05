import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BdrActionService } from '@actions/bdr/core/bdr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  notes: string;
  opinionStatementFiles: AttachedFile[];
}

@Component({
  selector: 'app-bdr-opinion-statement-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="BDR verification opinion statement" [breadcrumb]="true">
        <app-opinion-statement-summary-template
          [notes]="vm?.notes"
          [opinionStatementFiles]="vm?.opinionStatementFiles"
          [isEditable]="false"></app-opinion-statement-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOpinionStatementSubmittedComponent {
  requestActionType = this.bdrActionService.requestActionType;
  payload = this.bdrActionService.payload as Signal<BDRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const opinionStatement = this.payload().verificationReport?.opinionStatement;

    return {
      notes: opinionStatement?.notes,
      opinionStatementFiles: opinionStatement?.opinionStatementFiles
        ? this.bdrActionService.getVerifierDownloadUrlFiles(opinionStatement?.opinionStatementFiles)
        : [],
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
