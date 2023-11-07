import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { BankTransferComponent } from './bank-transfer.component';

describe('BankTransferComponent', () => {
  let component: BankTransferComponent;
  let fixture: ComponentFixture<BankTransferComponent>;
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

  class Page extends BasePage<BankTransferComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get markAsPaidButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [BankTransferComponent, ReturnLinkComponent, MockPaymentHelpComponent],
    }).compileComponents();
  });

  describe('for permit issuance', () => {
    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState(mockPaymentState);
      fixture = TestBed.createComponent(BankTransferComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      activatedRoute = TestBed.inject(ActivatedRoute);
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to confirm page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

      expect(page.summaryListValues).toEqual([
        ['Sort code', 'sortCode'],
        ['Account number', 'accountNumber'],
        ['Account name', 'accountName'],
        ['Your payment reference', 'AEM-323-1'],
        ['Amount to pay', '£2,500.20'],

        ['Bank identifier code (BIC)', 'swiftCode'],
        ['Account number (IBAN)', 'iban'],
        ['Account name', 'accountName'],
      ]);

      page.markAsPaidButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledWith(['../mark-paid'], { relativeTo: activatedRoute });
    });
  });

  describe('for permit variation with Wales CA', () => {
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
      fixture = TestBed.createComponent(BankTransferComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      activatedRoute = TestBed.inject(ActivatedRoute);
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should hide amount value', () => {
      expect(page.summaryListValues).toEqual([
        ['Sort code', 'sortCode'],
        ['Account number', 'accountNumber'],
        ['Account name', 'accountName'],
        ['Your payment reference', 'AEM-323-1'],
        ['Amount to pay', ''],

        ['Bank identifier code (BIC)', 'swiftCode'],
        ['Account number (IBAN)', 'iban'],
        ['Account name', 'accountName'],
      ]);
    });
  });
});
