import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserProfile } from '@core/store/auth';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { KeycloakProfile } from 'keycloak-js';

import { CardPaymentProcessResponseDTO } from 'pmrv-api';

import { mapGOVUKToPaymentDetails, mapMakePaymentToPaymentDetails, PaymentDetails } from '../../core/payment.map';
import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentState } from '../../store/payment.state';
import { PaymentStore } from '../../store/payment.store';

export interface PaymentDetailsItem {
  details?: PaymentDetails;
  nextUrl?: string;
  internalUrl?: string;
  message?: string;
}

@Component({
  selector: 'app-confirmation',
  template: `
    <ng-container *ngIf="details$ | async as details">
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <govuk-panel title="Payment complete">Your payment reference is {{ details.paymentRefNum }}</govuk-panel>
          <app-payment-summary [shouldDisplayAmount]="shouldDisplayAmount$ | async" [details]="details">
            <h2 app-summary-header class="govuk-heading-m">Payment summary</h2>
          </app-payment-summary>
        </div>
      </div>
    </ng-container>
    <app-return-link [requestType]="(store | async).requestType" [home]="true"></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfirmationComponent implements OnInit {
  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  userProfile$ = this.authStore.pipe(selectUserProfile);
  details$ = new BehaviorSubject<PaymentDetails>(null);

  constructor(
    readonly store: PaymentStore,
    private readonly authStore: AuthStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams
      .pipe(
        switchMap((params) => {
          switch (params.method) {
            case 'BANK_TRANSFER':
              return combineLatest([this.userProfile$, this.store]).pipe(
                map(
                  ([userProfile, state]) =>
                    ({
                      details: mapMakePaymentToPaymentDetails(userProfile, state),
                    }) as PaymentDetailsItem,
                ),
              );

            case 'CREDIT_OR_DEBIT_CARD':
              return this.route.paramMap.pipe(
                first(),
                switchMap((paramMap) =>
                  combineLatest([
                    this.userProfile$,
                    this.store,
                    this.store.postProcessExistingCardPayment(+paramMap.get('taskId')),
                  ]),
                ),
                map(([userProfile, state, res]) => this.GOVUKPaymentDetails(userProfile, state, res)),
              );

            default:
              return of({} as PaymentDetailsItem);
          }
        }),
        takeUntil(this.destroy$),
      )
      .subscribe((data) => {
        if (data.details) {
          this.details$.next(data.details);
        } else if (data.nextUrl) {
          window.location.assign(data.nextUrl);
        } else if (data.internalUrl) {
          this.router.navigate([data.internalUrl], {
            relativeTo: this.route,
            queryParams: { message: data.message },
          });
        } else {
          this.router.navigate(['..'], { relativeTo: this.route });
        }

        this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
      });
  }

  private GOVUKPaymentDetails(
    userProfile: KeycloakProfile,
    state: PaymentState,
    res: CardPaymentProcessResponseDTO,
  ): PaymentDetailsItem {
    const paymentState = res.state;

    return {
      details:
        paymentState.finished && paymentState.status === 'success'
          ? mapGOVUKToPaymentDetails(userProfile, state, 'COMPLETED')
          : null,
      nextUrl: !paymentState.finished ? res.nextUrl : null,
      ...(paymentState.finished && paymentState.status !== 'success'
        ? {
            internalUrl: '../not-success',
            message: paymentState.message,
          }
        : {}),
    };
  }
}
