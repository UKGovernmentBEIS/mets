import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../../testing/mock';
import { RequestPaymentComponent } from './request-payment.component';

describe('RequestPaymentComponent', () => {
  let fixture: ComponentFixture<RequestPaymentComponent>;
  let store: PermitVariationStore;
  let router: Router;
  let page: Page;

  const requestsService = mockClass(RequestsService);

  class Page extends BasePage<RequestPaymentComponent> {
    set amount(value: string) {
      this.setInputValue('#amount', value);
    }
    set dueDate(date: { day: string; month: string; year: string }) {
      this.setInputValue('#dueDate-day', date.day);
      this.setInputValue('#dueDate-month', date.month);
      this.setInputValue('#dueDate-year', date.year);
    }
    set notes(value: string) {
      this.setInputValue('#notes', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get returnToLink() {
      return this.queryAll('a').find((anchor) => anchor.textContent.trim().startsWith('Return to:'));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RequestPaymentComponent);
    page = new Page(fixture);
    jest.clearAllMocks();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [RequestPaymentComponent],
      providers: [{ provide: RequestsService, useValue: requestsService }],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(PermitVariationStore);
    store.setState({ ...mockPermitVariationReviewOperatorLedPayload, requestId: 'REQ-1', permitType: 'GHGE' });

    createComponent();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('shows validation errors when submitting an empty form', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toEqual([
      'Enter a new payment amount in pounds, like 1500 or 1500.50',
      'Enter a date',
    ]);
  });

  it('shows an error when the amount is not a number', () => {
    page.amount = 'abc';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('New payment amount must be a number, like 1500 or 1500.50');
  });

  it('shows an error when the amount is 0', () => {
    page.amount = '0';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('New payment amount must be more than 0');
  });

  it('shows an error when the due date is in the past', () => {
    page.amount = '1500';
    page.dueDate = { day: '1', month: '1', year: '2020' };
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('The date must be today or in the future');
  });

  it('shows the return link with the actual permit type, not hardcoded', () => {
    expect(page.returnToLink.textContent.trim()).toEqual('Return to: GHGE permit variation review');
  });

  it('accepts today as a valid due date and submits the request, then navigates back with a success flag', () => {
    jest.useFakeTimers();
    jest.setSystemTime(new Date(Date.UTC(2026, 0, 2, 12, 0, 0)));

    page.amount = '1692';
    page.dueDate = { day: '2', month: '1', year: '2026' };
    page.notes = 'Visible to the operator';

    requestsService.requestPayment.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(requestsService.requestPayment).toHaveBeenCalledWith('REQ-1', {
      amount: '1692',
      dueDate: '2026-01-02',
      notes: 'Visible to the operator',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['..'], {
      relativeTo: expect.anything(),
      state: { paymentRequestSent: true },
    });

    jest.useRealTimers();
  });
});
