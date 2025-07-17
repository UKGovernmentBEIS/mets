import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';

import { map, Observable } from 'rxjs';

import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { FollowUpItemComponent } from '@tasks/inspection/shared/follow-up-item/follow-up-item.component';

import { RequestActionDTO } from 'pmrv-api';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { CommonActionsStore } from '../../store/common-actions.store';
import { InspectionActionService } from '../core/inspection-action.service';

@Component({
  selector: 'app-onsite-audit-submitted',
  standalone: true,
  imports: [FollowUpItemComponent, SharedModule, ActionSharedModule, RouterModule],
  templateUrl: './onsite-audit-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class OnsiteAuditSubmittedComponent {
  expectedActionType: RequestActionDTO['type'][] = [
    'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED',
    'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED',
    'INSTALLATION_AUDIT_APPLICATION_SUBMITTED',
    'INSTALLATION_AUDIT_OPERATOR_RESPONDED',
  ];

  actionType$ = this.commonActionsStore.requestActionType$;

  inspectionPayload$ = this.inspectionActionService.getOnSiteAuditPayload$().pipe(map((payload) => payload));
  inspectionPayload = toSignal(this.inspectionPayload$);

  details$ = this.inspectionPayload$.pipe(map((payload) => payload.installationInspection.details));

  inspectionRespondedPayload$ = this.inspectionActionService
    .getOnSiteAuditResondedPayload$()
    .pipe(map((payload) => payload));

  isOperatorRespond$ = this.actionType$.pipe(
    map(
      (type) =>
        type === 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED' ||
        type === 'INSTALLATION_AUDIT_OPERATOR_RESPONDED',
    ),
  );

  attachments$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? payload.installationInspection.followUpActions.map((val) => {
            return this.inspectionActionService.getDownloadUrlFiles(val.followUpActionAttachments);
          })
        : [],
    ),
  );

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.inspectionPayload();
    return payload?.installationInspection?.followUpActionsOmissionFiles
      ? this.inspectionActionService.getDownloadUrlFiles(payload?.installationInspection?.followUpActionsOmissionFiles)
      : [];
  });

  attachmentsResponses$ = this.inspectionRespondedPayload$.pipe(
    map((payload) =>
      payload.followupActionsResponses
        ? Object.values(payload.followupActionsResponses).map((val) =>
            this.inspectionActionService.getDownloadUrlFiles(val.followUpActionResponseAttachments),
          )
        : [],
    ),
  );

  visibleFiles$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? this.inspectionActionService.getDownloadUrlFiles(payload.installationInspection.details?.files)
        : [],
    ),
  );
  notVisibleFiles$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.inspectionAttachments
        ? this.inspectionActionService.getDownloadUrlFiles(payload.installationInspection.details?.regulatorExtraFiles)
        : [],
    ),
  );

  followUpActions$ = this.inspectionPayload$.pipe(
    map((payload) =>
      payload?.installationInspection?.followUpActions ? payload.installationInspection.followUpActions : [],
    ),
  );

  followUpActionsResponses$ = this.inspectionRespondedPayload$.pipe(
    map((payload) => (payload?.followupActionsResponses ? Object.values(payload?.followupActionsResponses) : [])),
  );

  nameOnSignature$ = this.inspectionPayload$.pipe(
    map((payload) => {
      const signatory = payload.decisionNotification.signatory;
      return signatory && payload.usersInfo[signatory] ? payload.usersInfo[signatory].name : null;
    }),
  );

  notificationUsers$ = this.inspectionPayload$.pipe(
    map(
      (payload) =>
        payload?.usersInfo &&
        Object.keys(payload.usersInfo)
          .filter((id) => id !== payload.decisionNotification.signatory)
          .map((id) => this.userInfoResolverPipe.transform(id, payload.usersInfo)),
    ),
  );

  officialNoticeFile$ = this.inspectionPayload$.pipe(
    map((payload) => {
      const actionId = this.commonActionsStore.actionId;

      return payload?.officialNotice
        ? [
            {
              downloadUrl: `/actions/${actionId}/file-download/document/${payload.officialNotice.uuid}`,
              fileName: payload.officialNotice.name,
            },
          ]
        : null;
    }),
  );

  followUpActionResponseAttachments$ = (action): Observable<AttachedFile[]> => {
    return this.inspectionRespondedPayload$.pipe(
      map((payload) =>
        payload.followupActionsResponses[action]
          ? this.inspectionActionService.getDownloadUrlFiles(
              payload.followupActionsResponses[action].followUpActionResponseAttachments,
            )
          : null,
      ),
    );
  };

  isOnsiteInspection$ = this.actionType$.pipe(
    map(
      (type) =>
        type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED' ||
        type === 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED',
    ),
  );

  action$ = this.commonActionsStore.requestAction$;

  auditYear$ = this.commonActionsStore.requestAction$.pipe(map((a) => a.requestId?.split('-')[1]));

  constructor(
    private readonly inspectionActionService: InspectionActionService,
    private readonly commonActionsStore: CommonActionsStore,
    private readonly userInfoResolverPipe: UserInfoResolverPipe,
  ) {}
}
