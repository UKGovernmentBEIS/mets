import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { AviationAccountsStore } from '@aviation/accounts/store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { AccountDetailsHistoryService, AviationAccountsService } from 'pmrv-api';

import { AccountDetailsHistoryComponent } from './aviation-account-details-history.component';

describe('AccountDetailsHistoryComponent', () => {
  let component: AccountDetailsHistoryComponent;
  let fixture: ComponentFixture<AccountDetailsHistoryComponent>;
  let page: Page;

  const activatedRouteStub = new ActivatedRouteStub({ accountId: '1' });
  const aviationAccountsService = mockClass(AviationAccountsService);
  const accountDetailsHistoryService = mockClass(AccountDetailsHistoryService);

  class Page extends BasePage<AccountDetailsHistoryComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get noResults() {
      return this.query<HTMLElement>('.no-results');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AccountDetailsHistoryComponent],
      providers: [
        provideRouter([]),
        AviationAccountsStore,
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AviationAccountsService, aviationAccountsService },
        { provide: AccountDetailsHistoryService, accountDetailsHistoryService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountDetailsHistoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', async () => {
    expect(page.heading.textContent.trim()).toEqual('Operator details history');
  });

  it('should render the appropriate heading', async () => {
    accountDetailsHistoryService.getAccountDetailsHistory.mockReturnValue(
      of({ accountDetailsHistoryList: [], total: 0 }),
    );
    expect(page.noResults.textContent.trim()).toEqual('There are no results to show');
  });
});
