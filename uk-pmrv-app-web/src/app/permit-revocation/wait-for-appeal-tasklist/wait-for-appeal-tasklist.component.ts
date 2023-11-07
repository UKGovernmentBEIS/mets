import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map, Observable, switchMap } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import { RequestActionInfoDTO, RequestActionsService, RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-wait-for-appeal-tasklist',
  templateUrl: './wait-for-appeal-tasklist.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitForAppealTasklistComponent {
  readonly navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.store.pipe(
    map(
      (state) =>
        (state.assignable && state.userAssignCapable) || hasRequestTaskAllowedActions(state.allowedRequestTaskActions),
    ),
  );
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  readonly requestTaskItem$: Observable<RequestTaskItemDTO> = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        type: state.requestTaskType,
        assigneeUserId: state.assignee.assigneeUserId,
        assigneeFullName: state.assignee.assigneeFullName,
        assignable: state.assignable,
      },
      requestInfo: { id: state.requestId },
      userAssignCapable: state.userAssignCapable,
      allowedRequestTaskActions: state.allowedRequestTaskActions,
    })),
  );
  readonly timelineActions$: Observable<RequestActionInfoDTO[]> = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestId(state.requestId)),
    map((res) => this.sortTimeline(res, 'creationDate')),
  );
  constructor(
    readonly store: PermitRevocationStore,
    private readonly requestActionsService: RequestActionsService,
    private router: Router,
    readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['withdraw-notify-operator'], { relativeTo: this.route });
  }

  checkAllowedActions(actions: RequestTaskItemDTO['allowedRequestTaskActions']): boolean {
    return actions.includes('PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL');
  }

  private sortTimeline(res: RequestActionInfoDTO[], key: string): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b[key]).getTime() - new Date(a[key]).getTime());
  }
}
