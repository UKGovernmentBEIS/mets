import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import {
  hasRelatedViewActions,
  hasRequestTaskAllowedActions,
} from '@shared/components/related-actions/request-task-allowed-actions.map';

import {
  ItemDTO,
  RequestActionInfoDTO,
  RequestInfoDTO,
  RequestTaskActionProcessDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-task-layout',
  templateUrl: './task-layout.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskLayoutComponent implements OnInit, OnDestroy {
  @Input() header: string;
  @Input() daysRemaining: number;
  @Input() requestTaskItem$: Observable<RequestTaskItemDTO>;
  @Input() customContentTemplate: TemplateRef<any>;
  @Input() relatedTasks: ItemDTO[];
  @Input() timelineActions: RequestActionInfoDTO[];
  @Input() showSectionBreak: boolean;

  navigationState = { returnUrl: this.router.url };
  hasRelatedActions: Observable<boolean>;
  isAssignableAndCapableToAssign$: Observable<boolean>;
  taskId$: Observable<number>;
  allowedActions$: Observable<Array<RequestTaskActionProcessDTO['requestTaskActionType']>>;
  requestInfo$: Observable<RequestInfoDTO>;

  constructor(private readonly backService: BackLinkService, private readonly router: Router) {}

  ngOnInit(): void {
    this.isAssignableAndCapableToAssign$ = this.requestTaskItem$.pipe(
      map((requestTaskItem) => requestTaskItem.requestTask.assignable && requestTaskItem.userAssignCapable),
    );
    this.taskId$ = this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestTask.id));

    this.allowedActions$ = this.requestTaskItem$.pipe(
      map((requestTaskItem) => requestTaskItem.allowedRequestTaskActions),
    );

    this.requestInfo$ = this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo));

    this.hasRelatedActions = this.requestTaskItem$.pipe(
      map(
        (requestTaskItem) =>
          (requestTaskItem.requestTask.assignable && requestTaskItem.userAssignCapable) ||
          hasRequestTaskAllowedActions(requestTaskItem.allowedRequestTaskActions) ||
          hasRelatedViewActions(requestTaskItem.requestInfo.type),
      ),
    );
    this.backService.show();
  }

  ngOnDestroy(): void {
    this.backService.hide();
  }
}
