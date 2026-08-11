import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { BasePage } from '@testing';

import { FeesComponent } from './fees.component';
import { FeeRow } from './fees.model';
import { FeesService } from './fees.service';

describe('FeesComponent', () => {
  let fixture: ComponentFixture<FeesComponent>;
  let page: Page;

  const rows: FeeRow[] = [
    { key: 'permitSurrender', workflow: 'Permit surrender (GHGE and HSE)', currentAmount: 5622, scheduledChange: null },
    {
      key: 'newEntrantReserve',
      workflow: 'New entrant reserve (GHGE)',
      currentAmount: 7496,
      scheduledChange: { amount: 8500, date: '2026-07-21' },
    },
  ];

  const mockFeesService = { getFees: () => of(rows) };

  class Page extends BasePage<FeesComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('.govuk-table__body .govuk-table__row');
    }
    get viewHistoryLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeesComponent],
      providers: [{ provide: FeesService, useValue: mockFeesService }],
    }).compileComponents();

    fixture = TestBed.createComponent(FeesComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders the Fees heading', () => {
    expect(page.heading.textContent.trim()).toEqual('Fees');
  });

  it('renders a row per fee returned by the service', () => {
    expect(page.rows.length).toEqual(rows.length);
  });

  it('shows "None" for a row with no scheduled change and "Change" as its action', () => {
    const rowText = page.rows[0].textContent;

    expect(rowText).toContain('None');
    expect(rowText).toContain('Change');
    expect(rowText).not.toContain('Cancel scheduled change');
  });

  it('shows the scheduled change amount/date and "Cancel scheduled change" for a scheduled row', () => {
    const rowText = page.rows[1].textContent;

    expect(rowText).toContain('£8,500.00');
    expect(rowText).toContain('Scheduled for 21 July 2026');
    expect(rowText).toContain('Cancel scheduled change');
  });
});
