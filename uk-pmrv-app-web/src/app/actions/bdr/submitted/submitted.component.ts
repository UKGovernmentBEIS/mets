import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  BDRApplicationCompletedRequestActionPayload,
  BDRApplicationSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

import { BdrActionService } from '../core/bdr.service';
import { getBdrActionTitle } from './submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  bdrFile: AttachedFile;
  bdr: BDRApplicationSubmittedRequestActionPayload['bdr'];
  files: AttachedFile[];
  mmpFiles: AttachedFile[];
  isVerificationSubmitted: boolean;
  hasVerificationReport: boolean;
}

@Component({
  selector: 'app-bdr-action-submitted',
  imports: [ActionSharedModule, NgIf, SharedModule],
  templateUrl: './submitted.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrSubmittedComponent {
  payload = this.bdrActionService.payload as Signal<BDRApplicationSubmittedRequestActionPayload>;
  completedPayload = this.bdrActionService.payload as Signal<BDRApplicationCompletedRequestActionPayload>;

  requestActionType = this.bdrActionService.requestActionType;

  readonly hasVerificationReport = computed(() => {
    return !!this.payload().verificationReport;
  });

  readonly hasOutcome = computed(() => {
    return !!this.completedPayload().regulatorReviewOutcome;
  });

  readonly isVerificationSubmitted = computed(() => {
    return this.requestActionType() === 'BDR_APPLICATION_VERIFICATION_SUBMITTED';
  });

  readonly vm: Signal<ViewModel> = computed(() => {
    const header = getBdrActionTitle(this.requestActionType());
    const bdr = this.payload().bdr;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      bdr,
      bdrFile: bdr?.bdrFile ? this.bdrActionService.getOperatorDownloadUrlBdrFile(bdr?.bdrFile) : null,
      files: bdr?.files ? this.bdrActionService.getOperatorDownloadUrlFiles(bdr?.files) : [],
      mmpFiles: bdr?.mmpFiles ? this.bdrActionService.getOperatorDownloadUrlFiles(bdr?.mmpFiles) : [],
      isVerificationSubmitted: this.isVerificationSubmitted(),
      hasVerificationReport: this.hasVerificationReport(),
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
