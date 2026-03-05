import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, first, Observable, take } from 'rxjs';

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
    private readonly store: CommonTasksStore,
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

    this.store?.pipe(first()).subscribe((state) => {
      switch (state.requestTaskItem.requestTask.type) {
        case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('peer-review-wait');
          break;
        case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('dpn-peer-review');
          break;
        case 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('peer-review-wait');
          break;
        case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('noi-peer-review');
          break;
        case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('peer-review-wait');
          break;
        case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('cpn-peer-review');
          break;
        case 'PERMIT_REVOCATION_CESSATION_SUBMIT':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('cessation');
          break;
        case 'PERMIT_SURRENDER_WAIT_FOR_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('wait');
          break;
        default:
          break;
      }
    });
  }
}
