import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AviationAccountsStore } from '@aviation/accounts/store';
import { mockedAccount, mockReportingStatusResults } from '@aviation/accounts/testing/mock-data';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AccountReportingStatusPipe } from '../../pipes/account-reporting-status.pipe';
import { AccountReportingStatusComponent } from './account-reporting-status.component';

describe('AccountReportingStatusComponent', () => {
  let component: AccountReportingStatusComponent;
  let fixture: ComponentFixture<AccountReportingStatusComponent>;
  let page: Page;
  const activatedRoute = new ActivatedRouteStub();
  let store: AviationAccountsStore;

  class Page extends BasePage<AccountReportingStatusComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), AviationAccountsStore, { provide: ActivatedRoute, useValue: activatedRoute }],
      imports: [SharedModule, RouterLink],
      declarations: [AccountReportingStatusComponent, AccountReportingStatusPipe],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);
    store.setReportingStatuses(mockReportingStatusResults);

    store.setReportingStatusTotal(2);
    fixture = TestBed.createComponent(AccountReportingStatusComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show reporting status details when existing', () => {
    const tableElement = fixture.debugElement.query(By.css('govuk-table'));
    expect(tableElement).toBeTruthy();
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['2025', 'Exempt (commercial)', 'Update status', '16 Dec 2025, 3:13pm'],
      ['2024', 'Required to report', 'Update status', '16 Dec 2025, 2:10pm'],
    ]);
  });
});
