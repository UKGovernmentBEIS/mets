import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  first,
  map,
  of,
  shareReplay,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { QuarterNamePipe } from '@shared/pipes/quarter-name.pipe';

import {
  RequestActionInfoDTO,
  RequestActionsService,
  RequestCreateActionProcessDTO,
  RequestItemsService,
  RequestsService,
  WithholdFlagRequestsService,
} from 'pmrv-api';

import { statusesTagMap } from './shared/statusesTagMap';
import { requestCreateActionTypeLabelMap } from './shared/workflow-related-create-actions/workflowCreateAction';
import { workflowDetailsTypesMap } from './shared/workflowDetailsTypesMap';
import { WorkflowItemAbstractComponent } from './workflow-item-abstract.component';

@Component({
  selector: 'app-workflow-item',
  standalone: false,
  templateUrl: './workflow-item.component.html',
  styles: `
    div.search-results-list_item_status govuk-tag {
      float: right;
    }
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowItemComponent extends WorkflowItemAbstractComponent implements OnInit {
  private readonly quarterNamePipe = new QuarterNamePipe();
  hasMarkAsNotRequiredAccess$ = new BehaviorSubject<boolean>(false);

  currentTab$ = new BehaviorSubject<string>(null);
  isAviation = this.router.url.includes('/aviation/');
  navigationState = { returnUrl: this.router.url };

  readonly workflowStatusesTagMap = statusesTagMap;
  readonly workflowDetailsTypesMap = workflowDetailsTypesMap;

  readonly requestInfo$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestsService.getRequestDetailsById(requestId)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  readonly title$ = this.requestInfo$.pipe(
    map((requestInfo) =>
      (
        `${this.quarterNamePipe.transform((requestInfo.requestMetadata as any)?.quarter ?? '')} ` +
        ((requestInfo.requestMetadata as any)?.isFinal
          ? 'Final year'
          : ((requestInfo.requestMetadata as any)?.year ?? ''))
      ).trim(),
    ),
  );
  readonly requestType$ = this.requestInfo$.pipe(map((requestInfo) => requestInfo?.requestType));

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

  validRequestCreateActionsTypes$ = combineLatest([
    this.requestInfo$,
    this.userRoleType$,
    this.requestId$,
    this.accountId$,
  ]).pipe(
    switchMap(([requestInfo, roleType, requestId, accountId]) => {
      if (
        roleType === 'REGULATOR' &&
        ['AER', 'AVIATION_AER_CORSIA', 'AVIATION_AER_UKETS'].includes(requestInfo.requestType)
      ) {
        return this.requestsService.getAvailableAerWorkflows(requestInfo.id);
      } else if (
        roleType === 'REGULATOR' &&
        ['BDR', 'NER'].includes(requestInfo.requestType) &&
        requestInfo.requestStatus === 'COMPLETED'
      ) {
        return of({ [requestInfo.requestType]: { valid: true } });
      } else if (
        roleType === 'REGULATOR' &&
        ['BDRS2'].includes(requestInfo.requestType) &&
        requestInfo.requestStatus === 'COMPLETED'
      ) {
        return of({ BDRS2: { valid: true } });
      } else if (
        roleType === 'REGULATOR' &&
        ['ALR'].includes(requestInfo.requestType) &&
        requestInfo.requestStatus === 'IN_PROGRESS'
      ) {
        return this.requestsService.hasAccessMarkAsNotRequiredAlr(requestId).pipe(
          switchMap((hasAccess) => {
            return hasAccess ? of({ ALR: { valid: true } }) : of({});
          }),
        );
      } else if (
        roleType === 'REGULATOR' &&
        ['WITHHOLDING_OF_ALLOWANCES'].includes(requestInfo.requestType) &&
        requestInfo.requestStatus === 'COMPLETED'
      ) {
        return this.withholdFlagRequestsService.isWithholdFlagReopenAvailable(accountId).pipe(
          switchMap((hasAccess) => {
            return hasAccess ? of({ WITHHOLDING_OF_ALLOWANCES: { valid: true } }) : of({});
          }),
        );
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
    private readonly withholdFlagRequestsService: WithholdFlagRequestsService,
    private readonly titleService: Title,
    readonly pendingRequest: PendingRequestService,
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

    combineLatest([this.requestInfo$, this.userRoleType$, this.requestId$])
      .pipe(
        first(),
        switchMap(([requestInfo, roleType, requestId]) => {
          if (
            roleType === 'REGULATOR' &&
            ['AER', 'AVIATION_AER_CORSIA', 'AVIATION_AER_UKETS'].includes(requestInfo.requestType)
          )
            return this.requestsService.hasAccessMarkAsNotRequired(requestId);

          return of(false);
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((access: boolean) => {
        this.hasMarkAsNotRequiredAccess$.next(access);
      });
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
