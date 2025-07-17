import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, of, switchMap } from 'rxjs';

import { requestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import { AviationVirRequestMetadata, RequestInfoDTO, RequestTaskActionProcessDTO, VirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-related-actions',
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">Related actions</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ul class="govuk-list govuk-!-font-size-16">
          <li *ngFor="let action of allActions$ | async">
            <a [routerLink]="action.link" [fragment]="action.fragment" govukLink>{{ action.text }}</a>
          </li>
        </ul>
      </nav>
    </aside>
  `,
  styleUrl: './related-actions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedActionsComponent implements OnInit {
  @Input() isAssignable$: Observable<boolean>;
  @Input() taskId$: Observable<number>;
  @Input() allowedActions$: Observable<Array<RequestTaskActionProcessDTO['requestTaskActionType']>>;
  @Input() baseUrl = '';
  @Input() requestInfo$: Observable<RequestInfoDTO>;

  allActions$: Observable<{ text: string; link: any[]; fragment?: string }[]>;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.allActions$ = combineLatest([this.taskId$, this.allowedActions$, this.isAssignable$]).pipe(
      switchMap(([taskId, allowedActions, isAssignable]) =>
        this.requestInfo$
          ? this.requestInfo$.pipe(map((requestInfo) => ({ taskId, allowedActions, isAssignable, requestInfo })))
          : of({ taskId, allowedActions, isAssignable, requestInfo: null }),
      ),
      map(({ taskId, allowedActions, isAssignable, requestInfo }) => {
        const isAviation = this.baseUrl === '/aviation';
        let allActions = this.checkCancelTask(
          requestTaskAllowedActions(allowedActions, taskId, isAviation, null, requestInfo),
        );
        allActions = this.transformRelativeRoutes(allActions);
        if (isAssignable) {
          const route = this.route.snapshot;
          const routeConfig = route.routeConfig;
          const hasChangeAssigneeRelatedRoute =
            routeConfig?.children?.map((child) => child.path)?.includes('change-assignee') ||
            (routeConfig?.path?.length === 0 &&
              route?.parent?.routeConfig?.children?.map((child) => child.path)?.includes('change-assignee'));

          const assigneeLink = hasChangeAssigneeRelatedRoute
            ? ['change-assignee']
            : this.router.url.includes('/workflows/')
              ? this.router.url.includes('/tasks/')
                ? [
                    ...this.router.url.substring(0, this.router.url.indexOf('/tasks/')).split('/'),
                    'tasks',
                    taskId,
                    'change-assignee',
                  ]
                : ['tasks', taskId, 'change-assignee']
              : ['/tasks', taskId, 'change-assignee'];

          if (!isAviation) {
            assigneeLink[0] = `${this.baseUrl + assigneeLink[0]}`;
          }

          allActions.unshift({ text: 'Reassign task', link: assigneeLink });
        }

        if (requestInfo?.type === 'VIR') {
          const aerRequestId = (requestInfo.requestMetadata as VirRequestMetadata).relatedAerRequestId;
          allActions.push({
            text: 'View AEM report',
            link: [`/accounts/${requestInfo.accountId}/workflows/${aerRequestId}`],
          });
        }

        if (requestInfo?.type === 'AVIATION_VIR') {
          const aerRequestId = (requestInfo.requestMetadata as AviationVirRequestMetadata).relatedAerRequestId;
          allActions.push({
            text: 'View annual emissions report',
            link: [`/aviation/accounts/${requestInfo.accountId}/workflows/${aerRequestId}`],
          });
        }

        if (allowedActions?.includes('AER_VERIFICATION_RETURN_TO_OPERATOR')) {
          allActions.push({
            text: 'Return to operator for changes',
            link: [`/tasks/${taskId}/aer/verification-submit/return-to-operator-for-changes`],
          });
        }

        return allActions;
      }),
    );
  }

  private transformRelativeRoutes(
    actions: { text: string; link: any[]; fragment?: string }[],
  ): { text: string; link: any[]; fragment?: string }[] {
    return actions.map((action) => ({
      ...action,
      link: this.transformRelativeLink(action.link),
    }));
  }

  private transformRelativeLink(link: any[]): any[] {
    const type = (link[0] as string).replace('/', '');
    switch (type) {
      case 'rfi':
      case 'rde':
        return this.hasRelatedRoute(type) ? [type, ...link.slice(1)] : link;
      default:
        return link;
    }
  }

  private hasRelatedRoute(segment: string): boolean {
    const route = this.route.snapshot;
    const routeConfig = route.routeConfig;
    return (
      routeConfig?.children?.map((child) => child.path)?.includes(segment) ||
      (routeConfig?.path?.length === 0 &&
        route?.parent?.routeConfig?.children?.map((child) => child.path)?.includes(segment))
    );
  }

  private checkCancelTask(actions: { text: string; link: any[] }[]): { text: string; link: any[] }[] {
    return actions.map((action) => {
      if (action.text === 'Cancel task') {
        const route = this.route.snapshot;
        const routeConfig = route.routeConfig;
        const hasCancelRelatedRoute =
          routeConfig?.children?.map((child) => child.path)?.includes('cancel') ||
          (routeConfig?.path?.length === 0 &&
            route?.parent?.routeConfig?.children?.map((child) => child.path)?.includes('cancel'));

        if (hasCancelRelatedRoute) {
          action.link = ['cancel'];
        }
      }

      return action;
    });
  }
}
