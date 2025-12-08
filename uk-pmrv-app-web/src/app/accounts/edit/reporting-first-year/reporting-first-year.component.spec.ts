import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import { InstallationAccountUpdateService } from 'pmrv-api';

import { mockedAccountPermit } from '../../testing/mock-data';
import { AccountEditReportingFirstYearComponent } from './reporting-first-year.component';

describe('ReportingFirstYearComponent', () => {
  let component: AccountEditReportingFirstYearComponent;
  let fixture: ComponentFixture<AccountEditReportingFirstYearComponent>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<AccountEditReportingFirstYearComponent> {
    set registryReportingFirstYear(value: string) {
      this.setInputValue('#registryReportingFirstYear', value);
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      accountPermit: mockedAccountPermit,
    });

    accountUpdateService = {
      updateRegistryReportingFirstYear: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [AccountEditReportingFirstYearComponent],
      providers: [
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountEditReportingFirstYearComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change site name on form submit', () => {
    page.registryReportingFirstYear = '2022';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateRegistryReportingFirstYear).toHaveBeenCalled();
    expect(accountUpdateService.updateRegistryReportingFirstYear).toHaveBeenCalledWith(1, {
      registryReportingFirstYear: 2022,
    });
  });
});
