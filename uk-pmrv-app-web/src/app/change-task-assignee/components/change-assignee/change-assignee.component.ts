import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, Observable, take } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-change-assignee',
  template: `
    <ng-container *ngIf="isTaskAssigned$ | async; else mainView">
      <app-assignment-confirmation [user]="taskAssignedTo$ | async"></app-assignment-confirmation>
    </ng-container>
    <ng-template #mainView>
      <app-assignment [noBorder]="true" [info]="info$ | async" (submitted)="assignedTask($event)"></app-assignment>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAssigneeComponent implements OnInit {
  readonly taskAssignedTo$ = new BehaviorSubject<string>(null);
  readonly isTaskAssigned$ = new BehaviorSubject<boolean>(false);
  readonly info$: Observable<RequestTaskItemDTO> = this.commonTasksStore.requestTaskItem$;

  constructor(
    private readonly commonTasksStore: CommonTasksStore,
    private readonly breadcrumbsService: BreadcrumbService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.resolveBreadcrumb();

    this.isTaskAssigned$
      .pipe(
        filter((ita) => ita === true),
        take(1),
      )
      .subscribe(() => this.breadcrumbsService.showDashboardBreadcrumb(this.router.url));
  }

  assignedTask(name: string): void {
    this.taskAssignedTo$.next(name);
    this.isTaskAssigned$.next(true);
  }

  private resolveBreadcrumb(): void {
    const breadcrumbs = this.breadcrumbsService.breadcrumbItem$.getValue();
    const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
    const parentRoute = this.route.routeConfig.path === '' ? this.route.parent.parent : this.route.parent;
    const parentRouteEndsWithTaskId = parentRoute.routeConfig.path.endsWith(':taskId');

    if (/\d+$/.test(lastBreadcrumb.link.join('/')) && !parentRouteEndsWithTaskId) {
      const parentRoutePath = parentRoute.routeConfig.path;
      if (parentRoute.routeConfig.path !== '') {
        lastBreadcrumb.link = [...lastBreadcrumb.link, parentRoutePath];
        this.breadcrumbsService.show([...breadcrumbs.slice(0, breadcrumbs.length - 1), lastBreadcrumb]);
      }
    }
  }
}
