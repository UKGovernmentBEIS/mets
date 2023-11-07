import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '@testing';

import { AccountReportingStatusPipe } from '../../pipes/account-reporting-status.pipe';
import { mockedAccount } from '../../testing/mock-data';
import { AccountReportingStatusComponent } from './account-reporting-status.component';

describe('AccountReportingStatusComponent', () => {
  let component: AccountReportingStatusComponent;
  let fixture: ComponentFixture<AccountReportingStatusComponent>;
  let page: Page;

  class Page extends BasePage<AccountReportingStatusComponent> {
    get heading() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule],
      declarations: [AccountReportingStatusComponent, AccountReportingStatusPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountReportingStatusComponent);
    component = fixture.componentInstance;
    component.userRoleType = 'REGULATOR';
    component.accountInfo = mockedAccount.aviationAccount;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', () => {
    expect(page.heading.map((el) => el.textContent.trim())).toEqual(['Reporting status']);
  });

  it('should show reporting status details when existing', () => {
    const reportingStatus = fixture.debugElement.query(By.css('#reporting-status > dd')).nativeElement;
    const reportingReason = fixture.debugElement.query(By.css('#reporting-reason > dd')).nativeElement;

    expect(reportingStatus.textContent.trim()).toStrictEqual('Exempt (commercial)');
    expect(reportingReason.textContent.trim()).toStrictEqual('Explanation text added by the regulator');
  });
});
