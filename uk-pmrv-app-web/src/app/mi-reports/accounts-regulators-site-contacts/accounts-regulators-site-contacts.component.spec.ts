import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { asyncData, BasePage } from '@testing';

import { MiReportsService } from 'pmrv-api';

import { AccountsRegulatorsSiteContactsComponent } from './accounts-regulators-site-contacts.component';

class Page extends BasePage<AccountsRegulatorsSiteContactsComponent> {
  get table() {
    return this.query<HTMLDivElement>('.govuk-table');
  }

  get executeButton() {
    return this.query<HTMLButtonElement>('button');
  }
}

describe('AccountsRegulatorsSiteContactsComponent', () => {
  let component: AccountsRegulatorsSiteContactsComponent;
  let fixture: ComponentFixture<AccountsRegulatorsSiteContactsComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsService>>;

  beforeEach(async () => {
    miReportsService = {
      generateReport: jest.fn().mockReturnValue(
        asyncData({
          reportType: 'LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS',
          columnNames: [
            'Account type',
            'Account ID',
            'Account name',
            'Account status',
            'Legal Entity name',
            'User status',
            'Assigned regulator',
          ],
          results: [
            {
              'Account type': 'INSTALLATION',
              'Account ID': 1,
              'Account name': 'Account 1',
              'Account status': 'NEW',
              'Legal Entity name': 'Samsung',
              'User status': 'ACTIVE',
              'Assigned regulator': 'Joe Doe',
            },
            {
              'Account type': 'INSTALLATION',
              'Account ID': 2,
              'Account name': 'Account 2',
              'Account status': 'NEW',
              'Legal Entity name': 'Apple',
              'User status': 'ACTIVE',
              'Assigned regulator': 'Alice Black',
            },
          ],
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [AccountsRegulatorsSiteContactsComponent],
      providers: [{ provide: MiReportsService, useValue: miReportsService }, DestroySubject],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsRegulatorsSiteContactsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render table rows', () => {
    page.executeButton.click();
    fixture.detectChanges();
    const cells = Array.from(page.table.querySelectorAll('td'));

    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['Installation', '2', 'Account 2', 'New', 'Apple', 'ACTIVE', 'Alice Black'],
      ...['Installation', '1', 'Account 1', 'New', 'Samsung', 'ACTIVE', 'Joe Doe'],
    ]);
  });
});
