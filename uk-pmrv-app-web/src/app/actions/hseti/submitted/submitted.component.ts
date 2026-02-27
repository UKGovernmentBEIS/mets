import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  HSETIApplicationSubmittedRequestActionPayload,
  HSETICompletedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

import { HseTiActionService } from '../core/hseti.service';
import { getHseTiActionTitle } from './submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  hseTiFile: AttachedFile;
  hseTi: HSETIApplicationSubmittedRequestActionPayload['hseti'];
  files: AttachedFile[];
  hasOverallDecision: boolean;
  officialNoticeFiles: AttachedFile[];
  notificationUsers: string[];
  signatoryName: string;
}

@Component({
  selector: 'app-hseti-action-submitted',
  imports: [ActionSharedModule, NgIf, SharedModule],
  templateUrl: './submitted.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HseTiSubmittedComponent {
  payload = this.hseTiActionService?.payload as Signal<HSETICompletedRequestActionPayload | undefined>;
  allocationPeriod = this.hseTiActionService?.allocationPeriod as Signal<string | undefined>;

  requestActionType = this.hseTiActionService.requestActionType;

  private getPayload() {
    return this.payload();
  }

  private readonly hasOverallDecision = computed(() => !!this.getPayload()?.overallDecision);

  private readonly officialNoticeFiles = computed(() => {
    const officialNotice = this.getPayload()?.officialNotice;
    return officialNotice ? this.hseTiActionService.getOfficialNoticeFiles(officialNotice) : [];
  });

  private readonly notificationUsers = computed(() => {
    const payload = this.getPayload();
    const usersInfo = payload?.usersInfo;
    const signatory = payload?.decisionNotification?.signatory;
    if (!usersInfo) return [];
    return Object.keys(usersInfo)
      .filter((userId) => userId !== signatory)
      .map((id) => this.userInfoResolverPipe.transform(id, usersInfo));
  });

  private readonly signatoryName = computed(() => {
    const payload = this.getPayload();
    const usersInfo = payload?.usersInfo;
    const signatory = payload?.decisionNotification?.signatory;
    if (!signatory || !usersInfo) return '';
    return this.userInfoResolverPipe.transform(signatory, usersInfo) || '';
  });

  vm: Signal<ViewModel> = computed(() => {
    const header = `${this.allocationPeriod()} ${getHseTiActionTitle(this.requestActionType())}`;
    const hseTi = this.payload().hseti;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      hseTi,
      hseTiFile: hseTi?.hsetiFile ? this.hseTiActionService.getOperatorDownloadUrlHseTiFile(hseTi?.hsetiFile) : null,
      files: hseTi?.files ? this.hseTiActionService.getOperatorDownloadUrlFiles(hseTi?.files) : [],
      hasOverallDecision: this.hasOverallDecision(),
      officialNoticeFiles: this.officialNoticeFiles() || [],
      notificationUsers: this.notificationUsers() || [],
      signatoryName: this.signatoryName() || '',
    };
  });

  constructor(
    private readonly hseTiActionService: HseTiActionService,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
