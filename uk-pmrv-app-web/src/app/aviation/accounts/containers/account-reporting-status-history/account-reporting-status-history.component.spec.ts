import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService } from 'pmrv-api';

import { AviationAccountsStore } from '../../store';
import { AccountReportingStatusHistoryComponent } from './account-reporting-status-history.component';

describe('AccountReportingStatusHistoryComponent', () => {
  let component: AccountReportingStatusHistoryComponent;
  let fixture: ComponentFixture<AccountReportingStatusHistoryComponent>;
  let page: Page;

  const activatedRouteStub = new ActivatedRouteStub({ accountId: '1' });
  const aviationAccountsService = mockClass(AviationAccountsService);
  const aviationAccountsReportingService = mockClass(AviationAccountReportingStatusService);

  class Page extends BasePage<AccountReportingStatusHistoryComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get noResults() {
      return this.query<HTMLElement>('.no-results');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [AccountReportingStatusHistoryComponent],
      providers: [
        AviationAccountsStore,
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AviationAccountsService, aviationAccountsService },
        { provide: AviationAccountReportingStatusService, aviationAccountsReportingService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountReportingStatusHistoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', async () => {
    expect(page.heading.textContent.trim()).toEqual('Reporting status history');
  });

  it('should render the appropriate heading', async () => {
    aviationAccountsReportingService.getReportingStatusHistory.mockReturnValue(
      of({ reportingStatusHistoryList: [], total: 0 }),
    );
    expect(page.noResults.textContent.trim()).toEqual('There are no results to show');
  });
});
