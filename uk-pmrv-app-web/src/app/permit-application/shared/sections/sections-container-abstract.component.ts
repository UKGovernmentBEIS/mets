import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, shareReplay, switchMap } from 'rxjs';

import { ItemDTOResponse, RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { hasRequestTaskAllowedActions } from '../../../shared/components/related-actions/request-task-allowed-actions.map';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { permitTypeMap } from '../utils/permit';

export abstract class SectionsContainerAbstractComponent {
  navigationState = { returnUrl: this.router.url };

  isTask$ = this.store.pipe(map((state) => state.isRequestTask));
  taskId$ = this.store.pipe(map((state) => state.requestTaskId));

  permitType$ = this.store.pipe(
    map((state) => state.permitType),
    map((permitType) => permitTypeMap[permitType]),
  );

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions)),
  );

  isRelatedActionsSectionVisible$ = combineLatest([this.isTask$, this.hasRelatedActions$]).pipe(
    map(([isTask, hasRelatedActions]) => isTask && hasRelatedActions),
  );

  relatedTasks$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    switchMap((state) => {
      const requestTaskId = state.requestTaskId;
      return this.requestItemsService.getItemsByRequest(state.requestId).pipe(
        map((response) => {
          return [response, requestTaskId];
        }),
      );
    }),
    map(([items, requestTaskId]) => {
      const taskId = requestTaskId as number;
      return (items as ItemDTOResponse)?.items.filter((item) => item.taskId !== taskId);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  requestActions$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    map(({ requestId }) => requestId),
    switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestId(requestId)),
    map((res) => this.sortTimeline(res)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    protected readonly store: PermitApplicationStore<PermitApplicationState>,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
  ) {}

  protected init(): void {
    this.store.setState({
      ...this.store.getState(),
      returnUrl: window.history.state?.returnUrl ? window.history.state?.returnUrl : this.store.value.returnUrl,
    });
  }

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}
