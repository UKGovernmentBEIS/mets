import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, of, shareReplay, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';

import {
  RequestActionInfoDTO,
  RequestActionsService,
  RequestCreateActionProcessDTO,
  RequestItemsService,
  RequestsService,
} from 'pmrv-api';

import { statusesTagMap } from './shared/statusesTagMap';
import { requestCreateActionTypeLabelMap } from './shared/workflow-related-create-actions/workflowCreateAction';
import { workflowDetailsTypesMap } from './shared/workflowDetailsTypesMap';
import { WorkflowItemAbstractComponent } from './workflow-item-abstract.component';

@Component({
  selector: 'app-workflow-item',
  templateUrl: './workflow-item.component.html',
  styles: `
    span.search-results-list_item_status {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class WorkflowItemComponent extends WorkflowItemAbstractComponent implements OnInit {
  currentTab$ = new BehaviorSubject<string>(null);

  isAviation = this.router.url.includes('/aviation/');

  navigationState = { returnUrl: this.router.url };
  readonly workflowStatusesTagMap = statusesTagMap;
  readonly workflowDetailsTypesMap = workflowDetailsTypesMap;

  readonly requestInfo$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestsService.getRequestDetailsById(requestId)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  readonly title$ = this.requestInfo$.pipe(map((requestInfo) => (requestInfo.requestMetadata as any)?.year ?? ''));

  readonly relatedTasks$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestItemsService.getItemsByRequest(requestId)),
    map((items) => items.items),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  readonly actions$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestId(requestId)),
    map((res) => this.sortTimeline(res)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  userRoleType$ = this.authStore.pipe(selectUserRoleType);

  validRequestCreateActionsTypes$ = combineLatest([this.requestInfo$, this.userRoleType$]).pipe(
    switchMap(([requestInfo, roleType]) => {
      if (
        roleType === 'REGULATOR' &&
        ['AER', 'AVIATION_AER_CORSIA', 'AVIATION_AER_UKETS'].includes(requestInfo.requestType)
      ) {
        return this.requestsService.getAvailableAerWorkflows(requestInfo.id);
      } else if (
        roleType === 'REGULATOR' &&
        ['BDR'].includes(requestInfo.requestType) &&
        requestInfo.requestStatus === 'COMPLETED'
      ) {
        return of({ BDR: { valid: true } });
      } else {
        return of({});
      }
    }),
    map(
      (availableCreateActions: RequestCreateActionProcessDTO['requestCreateActionType']) =>
        Object.keys(requestCreateActionTypeLabelMap).filter(
          (key) => availableCreateActions[key]?.valid,
        ) as RequestCreateActionProcessDTO['requestCreateActionType'][],
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    public readonly router: Router,
    protected readonly authStore: AuthStore,
    protected readonly route: ActivatedRoute,
    protected readonly backLinkService: BackLinkService,
    protected readonly destroy$: DestroySubject,
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly titleService: Title,
  ) {
    super(authStore, router, route, backLinkService, destroy$);
  }

  ngOnInit(): void {
    this.prefixUrl$
      .pipe(withLatestFrom(this.accountId$), takeUntil(this.destroy$))
      .subscribe(([prefixUrl, accountId]) =>
        accountId ? this.backLinkService.show(prefixUrl, 'workflows') : this.backLinkService.show(prefixUrl),
      );

    this.requestInfo$.subscribe(({ requestType }) => this.titleService.setTitle(workflowDetailsTypesMap[requestType]));
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  selectedTab(selected: string) {
    // upon pagination queryParams is shown, for example "?page=3". In order to avoid any bug from navigation to tabs, clear query params.
    this.router.navigate([], {
      relativeTo: this.route,
      preserveFragment: true,
    });
    this.currentTab$.next(selected);
  }
}
