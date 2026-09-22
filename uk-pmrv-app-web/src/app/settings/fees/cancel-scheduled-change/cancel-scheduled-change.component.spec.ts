import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@testing';

import { FeeRow } from '../fees.model';
import { FeesService } from '../fees.service';
import { CancelScheduledChangeComponent } from './cancel-scheduled-change.component';

describe('CancelScheduledChangeComponent', () => {
  let fixture: ComponentFixture<CancelScheduledChangeComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRouteStub;

  const feeRow: FeeRow = {
    key: 'NER',
    id: 7,
    requestType: 'NER',
    workflow: 'New entrant reserve (GHGE)',
    currentAmount: 1443,
    scheduledChange: { amount: 8000, date: '2026-07-21' },
  };

  const mockFeesService = { getFee: jest.fn(), cancelScheduledFeeUpdate: jest.fn() };

  class Page extends BasePage<CancelScheduledChangeComponent> {
    get confirmButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
    get keepChangeLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CancelScheduledChangeComponent);
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub({ key: 'NER' });
    mockFeesService.getFee.mockReturnValue(of(feeRow));
    mockFeesService.cancelScheduledFeeUpdate.mockReset();

    await TestBed.configureTestingModule({
      imports: [CancelScheduledChangeComponent, RouterTestingModule],
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

  it('shows the workflow, current amount, scheduled amount and scheduled date', () => {
    createComponent();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('New entrant reserve (GHGE)');
    expect(text).toContain('£1,443.00');
    expect(text).toContain('£8,000.00');
    expect(text).toContain('21 July 2026');
  });

  it('links "No, keep scheduled change" back to the Fees page', () => {
    createComponent();

    expect(page.keepChangeLink.textContent.trim()).toBe('No, keep scheduled change');
  });

  it('cancels the scheduled fee update and shows a success notification on the Fees page', () => {
    mockFeesService.cancelScheduledFeeUpdate.mockReturnValue(of(undefined));
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();

    expect(mockFeesService.cancelScheduledFeeUpdate).toHaveBeenCalledWith(7, undefined);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], {
      relativeTo: route,
      state: { notification: 'The scheduled fee change has been cancelled. The current fee will stay the same.' },
    });
  });

  it('re-enables the confirm button and shows an error message if the cancellation fails', () => {
    mockFeesService.cancelScheduledFeeUpdate.mockReturnValue(throwError(() => new Error('failed')));
    createComponent();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.confirmButton.disabled).toBe(false);
    expect(fixture.nativeElement.textContent).toContain(
      'Sorry, there was a problem cancelling the scheduled change. Try again later.',
    );
  });
});
