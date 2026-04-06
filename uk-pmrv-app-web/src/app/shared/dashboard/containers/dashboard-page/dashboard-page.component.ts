import { ChangeDetectionStrategy, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, distinctUntilChanged, filter, map, Observable, switchMap, takeUntil } from 'rxjs';

import { selectFeatures } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore, selectCurrentDomain, selectUserRoleType, UserState } from '@core/store/auth';

import { GovukTableColumn } from 'govuk-components';

import { ItemDTO, RequestsService } from 'pmrv-api';

import { WorkflowItemsService } from '../../services';
import {
  DashboardStore,
  selectAccountSearchTerm,
  selectActiveTab,
  selectFilterBy,
  selectItems,
  selectOrderBy,
  selectPage,
  selectPageSize,
  selectTotal,
  WorkflowItemsAssignmentType,
} from '../../store';

interface ViewModel {
  domain: AccountType;
  role: UserState['roleType'];
  activeTab: WorkflowItemsAssignmentType;
  tableColumns: GovukTableColumn<ItemDTO>[];
  items: ItemDTO[];
  total: number;
  page: number;
  pageSize: number;
  order: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE';
  filterWorkflow: string;
  filterRequestTypes: string[];
  accountSearchTerm: string;
}

const DEFAULT_TABLE_COLUMNS: GovukTableColumn<ItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: false },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: false },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: false },
];

const AVIATION_EXTRA_COLUMNS: GovukTableColumn<ItemDTO>[] = [
  { field: 'requestId', header: 'Workflow ID', isSortable: false },
  { field: 'accountName', header: `Aviation operator`, isSortable: false },
];

const INSTALLATION_EXTRA_COLUMNS: GovukTableColumn<ItemDTO>[] = [
  { field: 'requestId', header: 'Workflow ID', isSortable: false },
  { field: 'accountName', header: `Installation`, isSortable: false },
  { field: 'leName', header: 'Operator', isSortable: false },
];

const getTableColumns = (domain: AccountType, activeTab: WorkflowItemsAssignmentType): GovukTableColumn<ItemDTO>[] => {
  let cols = DEFAULT_TABLE_COLUMNS;
  switch (domain) {
    case 'AVIATION':
      cols = cols.concat(AVIATION_EXTRA_COLUMNS);
      break;
    case 'INSTALLATION':
    default:
      cols = cols.concat(INSTALLATION_EXTRA_COLUMNS);
      break;
  }

  return cols.filter((column) => {
    return activeTab === 'assigned-to-others' || column.field !== 'taskAssignee';
  });
};

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'app-dashboard-page',
  standalone: false,
  templateUrl: './dashboard-page.component.html',
  styles: `
    .js-enabled .govuk-tabs__panel {
      border: 0px;
    }
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class DashboardPageComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.authStore.pipe(selectCurrentDomain),
    this.authStore.pipe(selectUserRoleType),
    this.store.pipe(selectActiveTab),
    this.store.pipe(selectItems),
    this.store.pipe(selectTotal),
    this.store.pipe(selectPage),
    this.store.pipe(selectPageSize),
    this.store.pipe(selectOrderBy),
    this.store.pipe(selectFilterBy),
    this.configStore.pipe(
      selectFeatures,
      switchMap((features) =>
        this.requestsService.getRequestTypesByUserRolesAndService().pipe(
          map((requestTypes) =>
            requestTypes.filter((type) => (features.wasteQdrEnabled ? true : type !== 'WASTE_QDR')),
          ),
          map((requestTypes) => requestTypes.filter((type) => (features.bdrs2Enabled ? true : type !== 'BDRS2'))),
          map((requestTypes) => requestTypes.filter((type) => (features.nerEnabled ? true : type !== 'NER'))),
        ),
      ),
    ),
    this.store.pipe(selectAccountSearchTerm),
  ]).pipe(
    map(
      ([
        domain,
        role,
        activeTab,
        items,
        total,
        page,
        pageSize,
        order,
        filterWorkflow,
        filterRequestTypes,
        accountSearchTerm,
      ]) => ({
        domain,
        role,
        activeTab,
        tableColumns: getTableColumns(domain, activeTab),
        items,
        total,
        page,
        pageSize,
        order,
        filterWorkflow,
        filterRequestTypes,
        accountSearchTerm,
      }),
    ),
  );

  currPage: number;
  currPageSize: number;

  constructor(
    private readonly service: WorkflowItemsService,
    private readonly store: DashboardStore,
    private readonly authStore: AuthStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly requestsService: RequestsService,
    private readonly configStore: ConfigStore,
  ) {}

  ngOnInit(): void {
    (this.route.fragment as Observable<WorkflowItemsAssignmentType>)
      .pipe(
        distinctUntilChanged(),
        filter((fragment) => !!fragment),
        takeUntil(this.destroy$),
      )
      .subscribe((tab) => {
        this.store.setPage(1);
        this.store.setActiveTab(tab);
      });

    this.vm$
      .pipe(
        map((vm) => ({
          activeTab: vm.activeTab,
          page: vm.page,
          pageSize: vm.pageSize,
          order: vm.order,
          filterWorkflow: vm.filterWorkflow,
          accountSearchTerm: vm.accountSearchTerm,
        })),
        distinctUntilChanged((prev, curr) => {
          this.currPage = curr.page;
          this.currPageSize = curr.pageSize;
          return (
            prev.activeTab === curr.activeTab &&
            prev.page === curr.page &&
            prev.pageSize === curr.pageSize &&
            prev.order === curr.order &&
            prev.filterWorkflow === curr.filterWorkflow &&
            prev.accountSearchTerm === curr.accountSearchTerm
          );
        }),
        switchMap(({ activeTab, page, pageSize, order, filterWorkflow, accountSearchTerm }) => {
          return combineLatest([
            this.service.getItems(activeTab, page, pageSize, order, filterWorkflow, accountSearchTerm),
            this.route.fragment,
          ]);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe(([{ items, totalItems }, routeFragment]) => {
        this.store.setItems(items);
        this.store.setTotal(totalItems);

        const totalPages = Math.ceil(totalItems / this.currPageSize);
        if (this.currPage > totalPages) {
          this.currPage = 1;
          this.store.setPage(1);

          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { page: this.currPage },
            queryParamsHandling: 'merge',
            fragment: routeFragment,
            state: { scrollSkip: true },
          });
        }
      });
  }

  addAnotherInstallation(): void {
    this.router.navigate(['/'], { state: { addAnotherInstallation: true } });
  }

  selectTab(selected: string) {
    this.router.navigate([], {
      relativeTo: this.route,
      fragment: selected,
    });
  }

  changePage(page: number) {
    this.store.setPage(page);
  }

  changeOrder(event) {
    this.store.setOrderBy(event);
  }

  changeFilter(event) {
    this.store.setFilterBy(event);
  }

  changeSearch(event) {
    this.store.setSearchBy(event ?? '');
  }
}
