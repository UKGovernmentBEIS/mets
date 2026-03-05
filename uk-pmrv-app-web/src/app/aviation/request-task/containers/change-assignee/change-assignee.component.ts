import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, filter, map, Observable, take } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { RequestTaskItemDTO } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../store';

interface ViewModel {
  isTaskReassigned: boolean;
  taskReassignedTo: string;
  info: RequestTaskItemDTO;
}

@Component({
  selector: 'app-change-assignee',
  templateUrl: './change-assignee.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAssigneeComponent implements OnInit, OnDestroy {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsTaskReassigned),
    this.store.pipe(requestTaskQuery.selectTaskReassignedTo),
    this.store.pipe(requestTaskQuery.selectRequestTaskItem),
  ]).pipe(map(([isTaskReassigned, taskReassignedTo, info]) => ({ isTaskReassigned, taskReassignedTo, info })));

  constructor(
    private readonly store: RequestTaskStore,
    private readonly backLinkService: BackLinkService,
    private readonly breadcrumbsService: BreadcrumbService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.vm$
      .pipe(
        map((vm) => vm.isTaskReassigned),
        filter((itr) => itr === true),
        take(1),
      )
      .subscribe(() => this.breadcrumbsService.showDashboardBreadcrumb(this.router.url));
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  assignTask(assignee: string) {
    this.store.setIsTaskReassigned(true);
    this.store.setTaskReassignedTo(assignee);
    this.backLinkService.hide();
  }
}
