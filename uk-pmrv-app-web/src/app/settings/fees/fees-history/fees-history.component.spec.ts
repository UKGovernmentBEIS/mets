import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@testing';

import { FeeHistoryPage, FeeHistoryRecord } from '../fees.model';
import { FeesService } from '../fees.service';
import { FeesHistoryComponent } from './fees-history.component';

describe('FeesHistoryComponent', () => {
  let fixture: ComponentFixture<FeesHistoryComponent>;
  let page: Page;

  const record = (overrides: Partial<FeeHistoryRecord>): FeeHistoryRecord => ({
    createdAtDisplay: '21 Jun 2026, 12:20pm',
    changedByDisplay: 'Regulator England',
    actionType: 'IMMEDIATE_UPDATE',
    workflow: 'New entrant reserve (GHGE)',
    oldAmount: 1403,
    newAmount: 1443,
    effectiveDate: null,
    ...overrides,
  });

  const mockFeesService = { getFeeHistory: jest.fn() };

  class Page extends BasePage<FeesHistoryComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get recordHeadings() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
    get pagination() {
      return this.query('app-pagination');
    }
  }

  const createComponent = (historyPage: FeeHistoryPage) => {
    mockFeesService.getFeeHistory.mockReturnValue(of(historyPage));

    fixture = TestBed.createComponent(FeesHistoryComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    mockFeesService.getFeeHistory.mockReset();

    await TestBed.configureTestingModule({
      imports: [FeesHistoryComponent, RouterTestingModule],
      providers: [
        { provide: FeesService, useValue: mockFeesService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent({ records: [], total: 0 });
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders the Fees history heading', () => {
    createComponent({ records: [], total: 0 });
    expect(page.heading.textContent.trim()).toEqual('Fees history');
  });

  it('requests page 1 with a page size of 30 on load', () => {
    createComponent({ records: [], total: 0 });
    expect(mockFeesService.getFeeHistory).toHaveBeenCalledWith(1, 30);
  });

  it('shows a message when there is no history', () => {
    createComponent({ records: [], total: 0 });
    expect(fixture.nativeElement.textContent).toContain('There is no fee change history to show.');
  });

  it('renders a heading and no "Effective from" row for an IMMEDIATE_UPDATE record', () => {
    createComponent({ records: [record({ actionType: 'IMMEDIATE_UPDATE' })], total: 1 });

    expect(page.recordHeadings[0].textContent.trim()).toEqual('21 Jun 2026, 12:20pm');

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('New entrant reserve (GHGE) £1,403.00 updated to £1,443.00');
    expect(text).not.toContain('Effective from');
  });

  it('renders the SCHEDULED_UPDATE sentence with an "Effective from" row', () => {
    createComponent({
      records: [record({ actionType: 'SCHEDULED_UPDATE', effectiveDate: '2026-07-16' })],
      total: 1,
    });

    const text = fixture.nativeElement.textContent;
    expect(text).toContain(
      'The payment amount for New entrant reserve (GHGE) will change to £1,443.00 on 16 July 2026',
    );
    expect(text).toContain('Effective from');
  });

  it('renders the CANCEL_SCHEDULED sentence with no separate "Effective from" row (date is embedded in the sentence)', () => {
    createComponent({
      records: [
        record({ actionType: 'CANCEL_SCHEDULED', oldAmount: 3100, newAmount: 3200, effectiveDate: '2026-07-16' }),
      ],
      total: 1,
    });

    const text = fixture.nativeElement.textContent;
    expect(text).toContain(
      'Cancelled the scheduled change to the payment amount for New entrant reserve (GHGE). ' +
        'The amount was due to change from £3,100.00 to £3,200.00 on 16 July 2026',
    );
    expect(text).not.toContain('Effective from');
  });

  it('renders the SYSTEM_APPLIED sentence with no "Effective from" row', () => {
    createComponent({
      records: [
        record({
          actionType: 'SYSTEM_APPLIED',
          changedByDisplay: 'System',
          effectiveDate: '2026-07-16',
        }),
      ],
      total: 1,
    });

    const text = fixture.nativeElement.textContent;
    expect(text).toContain(
      'Applied the scheduled payment change for New entrant reserve (GHGE) from £1,403.00 to £1,443.00',
    );
    expect(text).toContain('System');
    expect(text).not.toContain('Effective from');
  });

  it('hides pagination when total is within a single page', () => {
    createComponent({ records: [record({})], total: 1 });
    expect(page.pagination).toBeFalsy();
  });

  it('shows pagination and refetches when total exceeds the page size', () => {
    createComponent({ records: [record({})], total: 31 });
    expect(page.pagination).toBeTruthy();

    mockFeesService.getFeeHistory.mockReturnValue(of({ records: [], total: 31 }));
    fixture.componentInstance.onPageChange(2);
    fixture.detectChanges();

    expect(mockFeesService.getFeeHistory).toHaveBeenCalledWith(2, 30);
  });
});
