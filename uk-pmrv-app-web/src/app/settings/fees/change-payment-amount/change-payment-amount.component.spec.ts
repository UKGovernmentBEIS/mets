import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@testing';

import { FeeRow } from '../fees.model';
import { FeesService } from '../fees.service';
import { ChangePaymentAmountComponent } from './change-payment-amount.component';

describe('ChangePaymentAmountComponent', () => {
  let fixture: ComponentFixture<ChangePaymentAmountComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRouteStub;

  const feeRow: FeeRow = {
    key: 'NER',
    id: 7,
    requestType: 'NER',
    workflow: 'New entrant reserve (GHGE)',
    currentAmount: 1443,
    scheduledChange: null,
  };

  const mockFeesService = { getFee: jest.fn().mockReturnValue(of(feeRow)) };

  class Page extends BasePage<ChangePaymentAmountComponent> {
    get amount() {
      return this.getInputValue('#amount');
    }
    set amount(value: string) {
      this.setInputValue('#amount', value);
    }
    selectImmediately() {
      this.query<HTMLInputElement>('[value="immediately"]').click();
    }
    selectFutureDate() {
      this.query<HTMLInputElement>('[value="future"]').click();
    }
    set effectiveDate(date: { day: string; month: string; year: string }) {
      this.setInputValue('#effectiveDate-day', date.day);
      this.setInputValue('#effectiveDate-month', date.month);
      this.setInputValue('#effectiveDate-year', date.year);
    }
    get effectiveDate() {
      return {
        day: this.getInputValue('#effectiveDate-day'),
        month: this.getInputValue('#effectiveDate-month'),
        year: this.getInputValue('#effectiveDate-year'),
      };
    }
    get returnToFeesLink() {
      return this.queryAll('a').find((anchor) => anchor.textContent.trim() === 'Return to: Fees');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ChangePaymentAmountComponent);
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub({ key: 'NER' });

    await TestBed.configureTestingModule({
      imports: [ChangePaymentAmountComponent, RouterTestingModule],
      providers: [
        { provide: FeesService, useValue: mockFeesService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('shows the current payment details for the fee being changed', () => {
    createComponent();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('New entrant reserve (GHGE)');
    expect(text).toContain('£1,443.00');
  });

  it('shows validation errors when submitting an empty form', () => {
    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toEqual([
      'Enter a new payment amount in pounds, like 1500 or 1500.50',
      'Select when the payment amount should change',
    ]);
  });

  it('shows an error when the amount is not a number', () => {
    createComponent();

    page.amount = 'abc';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('New payment amount must be a number, like 1500 or 1500.50');
  });

  it('shows an error when the amount is 0', () => {
    createComponent();

    page.amount = '0';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('New payment amount must be more than 0');
  });

  it('shows an error when the effective date is in the past', () => {
    createComponent();

    page.amount = '8000';
    page.selectFutureDate();
    fixture.detectChanges();
    page.effectiveDate = { day: '1', month: '1', year: '2020' };
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('The date that the payment amount should change must be in the future');
  });

  it('shows an error when the effective date is today', () => {
    jest.useFakeTimers();
    jest.setSystemTime(new Date(Date.UTC(2026, 0, 2, 12, 0, 0)));

    createComponent();

    page.amount = '8000';
    page.selectFutureDate();
    fixture.detectChanges();
    page.effectiveDate = { day: '2', month: '1', year: '2026' };
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryList).toContain('The date that the payment amount should change must be in the future');

    jest.useRealTimers();
  });

  it('navigates to check-your-answers with today as effective date when Immediately is selected', () => {
    jest.useFakeTimers();
    jest.setSystemTime(new Date(Date.UTC(2026, 0, 2, 12, 0, 0)));

    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.amount = '8000';
    page.selectImmediately();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['check-your-answers'], {
      relativeTo: route,
      state: { amount: 8000, effectiveDate: new Date(Date.UTC(2026, 0, 2)), changeWhen: 'immediately' },
    });

    jest.useRealTimers();
  });

  it('navigates to check-your-answers with the entered future date when On a future date is selected', () => {
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.amount = '8000';
    page.selectFutureDate();
    fixture.detectChanges();
    page.effectiveDate = { day: '15', month: '1', year: '2099' };
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['check-your-answers'], {
      relativeTo: route,
      state: { amount: 8000, effectiveDate: new Date(Date.UTC(2099, 0, 15)), changeWhen: 'future' },
    });
  });

  it('pre-fills the form from router navigation state when returning from Check your answers', () => {
    jest.spyOn(TestBed.inject(Router), 'currentNavigation').mockReturnValue({
      extras: { state: { amount: 8000, effectiveDate: new Date(Date.UTC(2099, 0, 15)), changeWhen: 'future' } },
    } as any);

    createComponent();

    expect(page.amount).toBe('8000');
  });

  it('does not prefill the effective date when returning with Immediately previously selected and the user switches to On a future date', () => {
    jest.spyOn(TestBed.inject(Router), 'currentNavigation').mockReturnValue({
      extras: { state: { amount: 8000, effectiveDate: new Date(Date.UTC(2026, 0, 2)), changeWhen: 'immediately' } },
    } as any);

    createComponent();
    page.selectFutureDate();
    fixture.detectChanges();

    expect(page.effectiveDate).toEqual({ day: '', month: '', year: '' });
  });

  it('renders a working "Return to: Fees" link', () => {
    createComponent();

    const link = page.returnToFeesLink;
    expect(link).toBeTruthy();
    expect(link.getAttribute('href')).toBeTruthy();
  });
});
