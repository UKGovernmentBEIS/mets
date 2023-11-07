import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, takeUntil, tap } from 'rxjs';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { UrlRequestType } from '@shared/types/url-request-type';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { isVariationRegulatorLedRequestTask } from '../utils/permit';

@Component({
  selector: 'app-permit-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class PermitTaskComponent implements OnInit {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;

  relatedContentView: boolean;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        takeUntil(this.destroy$),
        map((store) => store.userViewRole),
        tap((roleType) => (this.relatedContentView = roleType === 'OPERATOR')),
      )
      .subscribe();

    if (Array.isArray(this.breadcrumb)) {
      combineLatest([this.route.paramMap, this.store])
        .pipe(takeUntil(this.destroy$))
        .subscribe(([paramMap, state]) => {
          const requestType: UrlRequestType = this.store.urlRequestType;
          const permitApplicationTaskLink = [`/${requestType}`, paramMap.get('taskId')];
          const permitApplicationActionLink = [`/${requestType}/action`, paramMap.get('actionId')];

          let firstBreadcrumb: { link: string[]; text: string } = {
            link: [],
            text: '',
          };

          let reviewGroupBreadcrumb = [];

          let taskUrlApproach = false; //whether we follow the new task url approach (i.e. tasks/{task_id}/{flow}) or the old one (i.e. permit-application/{task_id}/)

          if (state.isRequestTask) {
            switch (state.requestTaskType) {
              case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
              case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
              case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
              case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
              case 'PERMIT_VARIATION_APPLICATION_REVIEW':
              case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
              case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
              case 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
              case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
              case 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS':
              case 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW':
                firstBreadcrumb = {
                  link: [...permitApplicationTaskLink, 'review'],
                  text: this.reviewTypeText(),
                };
                if (this.reviewGroupTitle) {
                  reviewGroupBreadcrumb = [
                    {
                      text: this.reviewGroupTitle,
                      link: [...firstBreadcrumb.link, this.reviewGroupUrl],
                    },
                  ];
                }
                break;
              case 'PERMIT_VARIATION_APPLICATION_SUBMIT':
                taskUrlApproach = true;
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit variation',
                };
                break;
              case 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT':
                taskUrlApproach = true;
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit transfer',
                };
                break;
              default:
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit',
                };
            }
          } else if (state.requestActionType) {
            switch (state.requestActionType) {
              case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
              case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
              case 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED':
                firstBreadcrumb = {
                  link: [`/${requestType}/action`, paramMap.get('actionId')],
                  text: this.store.getApplyForHeader(),
                };
                break;
              case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
              case 'PERMIT_VARIATION_APPLICATION_GRANTED':
              case 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED':
              case 'PERMIT_TRANSFER_B_APPLICATION_GRANTED':
                firstBreadcrumb.text = this.reviewTypeText();
                if (state.userViewRole === 'REGULATOR') {
                  firstBreadcrumb.link = [...permitApplicationActionLink, 'review'];
                  if (this.reviewGroupTitle) {
                    reviewGroupBreadcrumb = [
                      {
                        text: this.reviewGroupTitle,
                        link: [...firstBreadcrumb.link, this.reviewGroupUrl],
                      },
                    ];
                  }
                } else {
                  firstBreadcrumb.link = permitApplicationActionLink;
                }
                break;
              default:
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit',
                };
            }
          }

          const dashboardBreadcrumb: BreadcrumbItem = {
            text: 'Dashboard',
            link: ['/dashboard'],
          };

          this.breadcrumbService.show([
            dashboardBreadcrumb,
            firstBreadcrumb,
            ...(Array.isArray(this.breadcrumb)
              ? this.breadcrumb.reduce(
                  (result, item, index) => [
                    ...result,
                    {
                      text: item.text,
                      link: [
                        ...(index === 1
                          ? result[0].link.slice(0, 2)
                          : result[index - 1]?.link ?? taskUrlApproach
                          ? permitApplicationTaskLink
                          : firstBreadcrumb.link),
                        ...item.link,
                      ],
                    },
                  ],
                  [] as any,
                )
              : reviewGroupBreadcrumb),
          ]);
        });
    }
  }

  private reviewTypeText(): string {
    switch (this.store.urlRequestType) {
      case 'permit-issuance':
        return 'Permit determination';

      case 'permit-variation':
        return isVariationRegulatorLedRequestTask(this.store.getState().requestTaskType)
          ? 'Make a change to the permit'
          : 'Variation determination';

      case 'permit-transfer':
        return 'Transfer determination';
    }
  }
}
