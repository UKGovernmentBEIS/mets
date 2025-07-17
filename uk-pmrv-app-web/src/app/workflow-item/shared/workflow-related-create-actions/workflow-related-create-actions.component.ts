import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, Observable, switchMap, take, takeUntil, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';

import {
  RequestCreateActionProcessDTO,
  RequestCreateActionProcessResponseDTO,
  RequestItemsService,
  RequestsService,
} from 'pmrv-api';

import { createRequestCreateActionProcessDTO, requestCreateActionTypeLabelMap } from './workflowCreateAction';

@Component({
  selector: 'app-workflow-related-create-actions',
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">Related actions</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ul class="govuk-list govuk-!-font-size-16">
          <li *ngFor="let requestCreateActionType of requestCreateActionsTypes$ | async">
            <a govukLink routerLink="." (click)="onClick(requestCreateActionType)">
              {{ requestCreateActionType | i18nSelect: requestCreateActionTypeLabelMap }}
            </a>
          </li>
          <li *ngIf="hasMarkAsNotRequiredAccess$ | async">
            <a govukLink routerLink="." (click)="onMarkAsNotRequired()">Mark workflow as not required</a>
          </li>
        </ul>
      </nav>
    </aside>
  `,
  styleUrl: './workflow-related-create-actions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class WorkflowRelatedCreateActionsComponent implements OnInit {
  @Input() accountId$: Observable<number>;
  @Input() requestId$: Observable<string>;
  @Input() requestCreateActionsTypes$: Observable<RequestCreateActionProcessDTO['requestCreateActionType'][]>;

  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, take(1));
  private readonly corsia3yearOffsettingEnabled$ = this.configStore.pipe(
    selectIsFeatureEnabled('corsia3yearOffsettingEnabled'),
  );

  isAviation: boolean;

  requestCreateActionTypeLabelMap = requestCreateActionTypeLabelMap;
  hasMarkAsNotRequiredAccess$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly destroy$: DestroySubject,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
    readonly pendingRequest: PendingRequestService,
    private readonly configStore: ConfigStore,
  ) {}

  ngOnInit(): void {
    this.corsia3yearOffsettingEnabled$
      .pipe(
        takeUntil(this.destroy$),
        tap((corsia3yearOffsettingEnabled) => {
          if (!corsia3yearOffsettingEnabled) {
            delete this.requestCreateActionTypeLabelMap.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING;
          }
        }),
      )
      .subscribe();

    this.currentDomain$.subscribe((domain) => {
      this.isAviation = domain === 'AVIATION';
    });

    this.requestId$
      .pipe(
        first(),
        switchMap((requestId) => {
          return this.requestsService.hasAccessMarkAsNotRequired(requestId);
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((access: boolean) => {
        this.hasMarkAsNotRequiredAccess$.next(access);
      });
  }

  onClick(requestCreateActionType: RequestCreateActionProcessDTO['requestCreateActionType']): void {
    if (requestCreateActionType === 'AER') {
      this.router.navigate(['aer-reinitialize'], { relativeTo: this.route });
    } else {
      combineLatest([this.requestId$, this.accountId$])
        .pipe(
          takeUntil(this.destroy$),
          switchMap(([requestId, accountId]) =>
            this.requestsService.processRequestCreateAction(
              createRequestCreateActionProcessDTO(requestCreateActionType, requestId),
              String(accountId),
            ),
          ),
          switchMap((response: RequestCreateActionProcessResponseDTO) =>
            this.requestItemsService.getItemsByRequest(response.requestId),
          ),
        )
        .subscribe(({ items }) => {
          const itemLinkPipe = new ItemLinkPipe();
          const link = items?.length == 1 ? itemLinkPipe.transform(items[0], this.isAviation) : ['/dashboard'];
          this.router.navigate(link);
        });
    }
  }

  onMarkAsNotRequired(): void {
    this.router.navigate(['aer-mark-as-not-required'], { relativeTo: this.route });
  }
}
