import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BasePage } from '@testing';

import { SettingsFeesPermissionService } from '../core/settings-fees-permission.service';
import { FeesComponent } from './fees.component';
import { FeeRow } from './fees.model';
import { FeesService } from './fees.service';

describe('FeesComponent', () => {
  let fixture: ComponentFixture<FeesComponent>;
  let page: Page;

  const rows: FeeRow[] = [
    {
      key: 'permitSurrender',
      requestType: 'PERMIT_SURRENDER',
      feeType: 'FIXED',
      workflow: 'Permit surrender (GHGE and HSE)',
      currentAmount: 5622,
      scheduledChange: null,
    },
    {
      key: 'newEntrantReserve',
      requestType: 'NER',
      feeType: 'FIXED',
      workflow: 'New entrant reserve (GHGE)',
      currentAmount: 7496,
      scheduledChange: { amount: 8500, date: '2026-07-21' },
    },
  ];

  const mockFeesService = { getFees: () => of(rows) };
  const mockSettingsFeesPermissionService = { canExecute: () => of(true) };

  class Page extends BasePage<FeesComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('.govuk-table__body .govuk-table__row');
    }
    get viewHistoryLink() {
      return this.query<HTMLAnchorElement>('p.govuk-body a');
    }
    get notificationBanner(): HTMLElement {
      return this.query('govuk-notification-banner');
    }
    get headers() {
      return this.queryAll<HTMLTableCellElement>('.govuk-table__head .govuk-table__header');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeesComponent, RouterTestingModule],
      providers: [
        { provide: FeesService, useValue: mockFeesService },
        { provide: SettingsFeesPermissionService, useValue: mockSettingsFeesPermissionService },
      ],
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

  it('shows "None" for a row with no scheduled change and links "Change" to the change-payment-amount page', () => {
    const row = page.rows[0];

    expect(row.textContent).toContain('None');
    expect(row.textContent).toContain('Change');
    expect(row.textContent).not.toContain('Cancel scheduled change');
    expect(row.querySelector('a').getAttribute('href')).toBe('/permitSurrender/change');
  });

  it('shows the scheduled change amount/date and links "Cancel scheduled change" to the cancel-change page', () => {
    const row = page.rows[1];

    expect(row.textContent).toContain('£8,500.00');
    expect(row.textContent).toContain('Scheduled for 21 July 2026');
    expect(row.textContent).toContain('Cancel scheduled change');
    expect(row.querySelector('a').getAttribute('href')).toBe('/newEntrantReserve/cancel-change');
  });

  it('links "View history" to the fees history page', () => {
    expect(page.viewHistoryLink.getAttribute('href')).toBe('/history');
  });

  it('hides the "Change" and "Cancel scheduled change" links when the user cannot execute fee actions', async () => {
    await TestBed.resetTestingModule()
      .configureTestingModule({
        imports: [FeesComponent, RouterTestingModule],
        providers: [
          { provide: FeesService, useValue: mockFeesService },
          { provide: SettingsFeesPermissionService, useValue: { canExecute: () => of(false) } },
        ],
      })
      .compileComponents();

    fixture = TestBed.createComponent(FeesComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.rows[0].querySelector('a')).toBeFalsy();
    expect(page.rows[1].querySelector('a')).toBeFalsy();
    expect(page.headers.map((header) => header.textContent.trim())).not.toContain('Actions');
    expect(page.rows[0].querySelectorAll('td').length).toEqual(3);
  });

  it('should not show the success banner without a notification', () => {
    expect(page.notificationBanner).toBeFalsy();
  });

  it('should show the success banner when navigated to with a notification', () => {
    jest.spyOn(TestBed.inject(Router), 'currentNavigation').mockReturnValue({
      extras: { state: { notification: 'The payment amount for NER has been updated to £8,000.00' } },
    } as any);

    fixture = TestBed.createComponent(FeesComponent);
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.notificationBanner).toBeTruthy();
    expect(page.notificationBanner.textContent).toContain('The payment amount for NER has been updated to £8,000.00');
  });
});
