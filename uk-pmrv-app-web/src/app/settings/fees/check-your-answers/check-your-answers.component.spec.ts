import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@testing';

import { FeeRow } from '../fees.model';
import { FeesService } from '../fees.service';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
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

  const mockFeesService = { getFee: jest.fn().mockReturnValue(of(feeRow)), updateFee: jest.fn() };

  class Page extends BasePage<CheckYourAnswersComponent> {
    get confirmButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
    get changeLinks() {
      return this.queryAll<HTMLAnchorElement>('a');
    }
  }

  const stubNavigationState = (state: unknown) => {
    jest.spyOn(TestBed.inject(Router), 'currentNavigation').mockReturnValue({ extras: { state } } as any);
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub({ key: 'NER' });
    mockFeesService.updateFee.mockReset();
    mockFeesService.getFee.mockReturnValue(of(feeRow));

    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent, RouterTestingModule],
      providers: [
        { provide: FeesService, useValue: mockFeesService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  it('redirects back to the change-payment-amount step when there is no draft in navigation state', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    createComponent();

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
  });

  describe('with a draft amount/date in navigation state', () => {
    beforeEach(() => {
      stubNavigationState({ amount: 8000, effectiveDate: new Date(Date.UTC(2099, 0, 15)) });
    });

    it('renders the current and new payment details', () => {
      createComponent();

      const text = fixture.nativeElement.textContent;
      expect(text).toContain('New entrant reserve (GHGE)');
      expect(text).toContain('£1,443.00');
      expect(text).toContain('£8,000.00');
      expect(text).toContain('15 January 2099');
    });

    it('passes the draft as router state on the Change links so the form step reopens pre-filled', () => {
      createComponent();

      expect(page.changeLinks.length).toBeGreaterThanOrEqual(2);
    });

    it('saves the fee update and shows the "updated" message when the effective date is today', () => {
      const now = new Date();
      const today = new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate()));
      stubNavigationState({ amount: 8000, effectiveDate: today });
      mockFeesService.updateFee.mockReturnValue(of(undefined));
      createComponent();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(mockFeesService.updateFee).toHaveBeenCalledWith({
        id: 7,
        feeType: undefined,
        amount: 8000,
        effectiveDate: expect.any(String),
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../../..'], {
        relativeTo: route,
        state: { notification: 'The payment amount for New entrant reserve (GHGE) has been updated to £8,000.00' },
      });
    });

    it('saves the fee update and shows the "will change" message when the effective date is in the future', () => {
      mockFeesService.updateFee.mockReturnValue(of(undefined));
      createComponent();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(navigateSpy).toHaveBeenCalledWith(['../../..'], {
        relativeTo: route,
        state: {
          notification: 'The payment amount for New entrant reserve (GHGE) will change to £8,000.00 on 15 January 2099',
        },
      });
    });

    it('re-enables the confirm button and shows an error message if the save fails', () => {
      mockFeesService.updateFee.mockReturnValue(throwError(() => new Error('failed')));
      createComponent();

      page.confirmButton.click();
      fixture.detectChanges();

      expect(page.confirmButton.disabled).toBe(false);
      expect(fixture.nativeElement.textContent).toContain(
        'Sorry, there was a problem saving the payment amount. Try again later.',
      );
    });
  });
});
