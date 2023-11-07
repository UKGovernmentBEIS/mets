import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AccountReportingStatusPipe } from '../../pipes/account-reporting-status.pipe';
import { mockReportingStatusHistoryResults } from '../../testing/mock-data';
import { AccountReportingStatusHistoryListComponent } from './account-reporting-status-history-list.component';

describe('AccountReportingStatusHistoryListComponent', () => {
  let component: AccountReportingStatusHistoryListComponent;
  let fixture: ComponentFixture<AccountReportingStatusHistoryListComponent>;
  let page: Page;

  class Page extends BasePage<AccountReportingStatusHistoryListComponent> {
    get reportingStatus() {
      return this.queryAll<HTMLLIElement>('.govuk-summary-list .reporting-status dd');
    }

    get reportingReason() {
      return this.queryAll<HTMLLIElement>('.govuk-summary-list .reporting-reason dd');
    }

    get reportingSubmitter() {
      return this.queryAll<HTMLLIElement>('.govuk-summary-list .reporting-submitter dd');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [AccountReportingStatusHistoryListComponent, AccountReportingStatusPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountReportingStatusHistoryListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.history = mockReportingStatusHistoryResults.reportingStatusHistoryList;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show reporting status for each result', () => {
    expect(page.reportingStatus.map((status) => status.textContent.trim())).toEqual([
      'Required to report',
      'Exempt (commercial)',
      'Exempt (commercial)',
    ]);
  });

  it('should show reporting reason for each result', () => {
    expect(page.reportingReason.map((reason) => reason.textContent.trim())).toEqual([
      'some reason',
      'another reason',
      'some other reason',
    ]);
  });

  it('should show submitter for each result', () => {
    expect(page.reportingSubmitter.map((submitter) => submitter.textContent.trim())).toEqual([
      'Ted Lasso',
      'Roy Kent',
      'Dani Rojas',
    ]);
  });
});
