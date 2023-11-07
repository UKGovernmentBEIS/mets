import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let hostElement: HTMLElement;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  @Component({
    selector: 'app-make-payment-help',
    template: `<div class="help">
      <p class="competentAuthority">{{ competentAuthority$ | async }}</p>
      <p class="requestType">{{ requestType$ | async }}</p>
      <p class="requestTaskType">{{ requestTaskType$ | async }}</p>
      <p class="default">{{ default }}</p>
    </div>`,
  })
  class MockPaymentHelpComponent {
    @Input() competentAuthority$: Observable<RequestInfoDTO['competentAuthority']>;
    @Input() requestType$: Observable<RequestInfoDTO['type']>;
    @Input() requestTaskType$: Observable<RequestTaskDTO['type']>;
    default: string;
    @Input() set defaultHelp(defaultHelp: string) {
      this.default = defaultHelp;
    }
  }

  class Page extends BasePage<DetailsComponent> {
    get paymentDetails() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get makePaymentButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [DetailsComponent, MockPaymentHelpComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  describe('for permit issuance', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState(mockPaymentState);
      fixture = TestBed.createComponent(DetailsComponent);
      component = fixture.componentInstance;
      hostElement = fixture.nativeElement;
      router = TestBed.inject(Router);
      activatedRoute = TestBed.inject(ActivatedRoute);
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display payment details', () => {
      expect(page.heading.textContent.trim()).toEqual(
        'Pay permit application fee Assigned to: Foo BarDays Remaining: 10',
      );
      expect(page.paymentDetails).toEqual([
        ['Date created', '5 May 2022'],
        ['Reference number', 'AEM-323-1'],
        ['Amount to pay', '£2,500.20'],
      ]);

      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
      page.makePaymentButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledWith(['../options'], { relativeTo: activatedRoute });
    });

    it('should display help details', () => {
      expect(hostElement.querySelector<HTMLParagraphElement>('p.competentAuthority').textContent.trim()).toEqual(
        'ENGLAND',
      );
      expect(hostElement.querySelector<HTMLParagraphElement>('p.requestType').textContent.trim()).toEqual(
        'PERMIT_ISSUANCE',
      );
      expect(hostElement.querySelector<HTMLParagraphElement>('p.requestTaskType').textContent.trim()).toEqual(
        'PERMIT_ISSUANCE_MAKE_PAYMENT',
      );
      expect(hostElement.querySelector<HTMLParagraphElement>('p.default').textContent.trim()).toEqual(
        'Your permit application cannot be processed until this payment is received',
      );
    });
  });

  describe('for permit variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState({
        ...mockPaymentState,
        competentAuthority: 'WALES',
        requestType: 'PERMIT_VARIATION',
        requestTaskItem: {
          ...mockPaymentState.requestTaskItem,
          requestTask: {
            ...mockPaymentState.requestTaskItem.requestTask,
            type: 'PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT',
          },
        },
      });
      fixture = TestBed.createComponent(DetailsComponent);
      component = fixture.componentInstance;
      hostElement = fixture.nativeElement;
      router = TestBed.inject(Router);
      activatedRoute = TestBed.inject(ActivatedRoute);
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should hide amount', () => {
      expect(page.paymentDetails).toEqual([
        ['Date created', '5 May 2022'],
        ['Reference number', 'AEM-323-1'],
        ['Amount to pay', ''],
      ]);
    });
  });

  describe('for dre', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState({
        ...mockPaymentState,
        competentAuthority: 'ENGLAND',
        requestType: 'DRE',
        requestTaskItem: {
          ...mockPaymentState.requestTaskItem,
          requestTask: {
            ...mockPaymentState.requestTaskItem.requestTask,
            type: 'DRE_MAKE_PAYMENT',
          },
          requestInfo: {
            ...mockPaymentState.requestTaskItem.requestInfo,
            type: 'DRE',
            requestMetadata: {
              type: 'DRE',
              year: '2022',
              emissions: '9.00000',
            } as any,
          },
        },
      });
      fixture = TestBed.createComponent(DetailsComponent);
      component = fixture.componentInstance;
      hostElement = fixture.nativeElement;
      router = TestBed.inject(Router);
      activatedRoute = TestBed.inject(ActivatedRoute);
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should display year in header', () => {
      expect(hostElement.querySelector<HTMLElement>('app-page-heading h1.govuk-heading-xl').textContent.trim()).toEqual(
        'Pay 2022 reportable emissions fee Assigned to: Foo BarDays Remaining: 10',
      );
    });
  });
});
