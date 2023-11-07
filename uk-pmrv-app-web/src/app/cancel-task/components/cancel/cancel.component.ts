import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { cancelActionMap } from '../../cancel-action.map';

@Component({
  selector: 'app-cancel-surrender',
  template: `
    <app-page-heading size="xl"> Are you sure you want to cancel this task?</app-page-heading>
    <p class="govuk-body">This task and its data will be deleted.</p>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="cancel()" govukWarnButton>Yes, cancel this task</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelComponent implements OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly breadcrumbsService: BreadcrumbService,
    private readonly backLinkService: BackLinkService,
    private readonly commonTasksStore: CommonTasksStore,
  ) {}

  ngOnInit() {
    this.resolveBreadcrumb();
    //check if cancel action is available for given request type
    this.commonTasksStore.requestTaskItem$
      .pipe(
        filter((item) => !!item),
        switchMap(() => this.commonTasksStore.requestTaskType$),
        first(),
      )
      .subscribe((type) => {
        if (cancelActionMap[type] == null) {
          this.router.navigate(['error', '404']);
        } else {
          this.backLinkService.show();
        }
      });
  }

  cancel(): void {
    this.commonTasksStore
      .cancelCurrentTask()
      .pipe(
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }

  private resolveBreadcrumb(): void {
    const breadcrumbs = this.breadcrumbsService.breadcrumbItem$.getValue();
    const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
    const parentRoute = this.route.routeConfig.path === '' ? this.route.parent.parent : this.route.parent;
    const parentRouteEndsWithTaskId = parentRoute.routeConfig.path.endsWith(':taskId');

    if (/\d+$/.test(lastBreadcrumb.link.join('/')) && !parentRouteEndsWithTaskId) {
      const parentRoutePath = parentRoute.routeConfig.path;
      lastBreadcrumb.link = [...lastBreadcrumb.link, parentRoutePath];
      this.breadcrumbsService.show([...breadcrumbs.slice(0, breadcrumbs.length - 1), lastBreadcrumb]);
    }
  }
}
