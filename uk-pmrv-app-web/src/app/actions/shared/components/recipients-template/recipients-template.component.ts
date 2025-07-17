import { ChangeDetectionStrategy, Component, computed, Input, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { recipientsPayloadType } from './recipients.types';

interface ViewModel {
  notificationUsers: Array<string>;
  nameOnSignature: string;
  noticeFile: Array<AttachedFile>;
}

@Component({
  selector: 'app-action-recipients-template',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './recipients-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecipientsTemplateComponent {
  @Input() header: string;
  @Input() officialNoticeText: string = '';
  @Input() hasBorders = true;

  private readonly payload: Signal<recipientsPayloadType> = toSignal(this.store.payload$);

  vm: Signal<ViewModel> = computed(() => {
    const actionId = this.store.actionId;
    const { usersInfo, decisionNotification, reviewDecisionNotification, officialNotice } = this.payload();
    const signatory = decisionNotification?.signatory || reviewDecisionNotification?.signatory;

    return {
      notificationUsers:
        usersInfo &&
        Object.keys(usersInfo)
          .filter((id) => id !== signatory)
          .map((id) => this.userInfoResolverPipe.transform(id, usersInfo)),
      nameOnSignature: signatory && usersInfo[signatory] ? usersInfo[signatory].name : null,
      noticeFile: officialNotice
        ? [
            {
              downloadUrl: `/actions/${actionId}/file-download/document/${officialNotice.uuid}`,
              fileName: officialNotice.name,
            },
          ]
        : null,
    };
  });

  constructor(
    private readonly store: CommonActionsStore,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
