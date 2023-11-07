import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of, throwError } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { MiReportsService } from 'pmrv-api';

import { mockCustomMiReportResult } from '../testing/mock-data';
import { CustomReportComponent } from './custom.component';

describe('CustomComponent', () => {
  let component: CustomReportComponent;
  let fixture: ComponentFixture<CustomReportComponent>;
  let page: Page;
  let authStore: AuthStore;

  const miReportsService = mockClass(MiReportsService);

  class Page extends BasePage<CustomReportComponent> {
    set queryValue(value: string) {
      this.setInputValue('#query', value);
    }

    get queryErrorMessage() {
      return this.query<HTMLParagraphElement>('p.govuk-error-message');
    }

    get formErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="query"] span.govuk-error-message');
    }

    get executeButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [CustomReportComponent],
      providers: [{ provide: MiReportsService, useValue: miReportsService }, DestroySubject],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');

    fixture = TestBed.createComponent(CustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid sql', () => {
    page.executeButton.click();
    fixture.detectChanges();

    miReportsService.generateCustomReport.mockReturnValueOnce(of(mockCustomMiReportResult));

    expect(page.formErrorMessage).toBeTruthy();

    page.queryValue = 'select * from account';
    page.executeButton.click();
    fixture.detectChanges();

    expect(page.queryErrorMessage).toBeFalsy();
    expect(page.formErrorMessage).toBeFalsy();
    expect(miReportsService.generateCustomReport).toHaveBeenCalledTimes(1);
    expect(miReportsService.generateCustomReport).toHaveBeenCalledWith('INSTALLATION', {
      reportType: 'CUSTOM',
      sqlQuery: 'select * from account',
    });

    expect(page.queryErrorMessage).toBeFalsy();
  });

  it('should display error message when submitting an invalid sql', () => {
    jest.spyOn(miReportsService, 'generateCustomReport').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'REPORT1001',
              data: ['StatementCallback; bad SQL grammar [select * from (select * from accounts) t where 1=0];'],
              message: 'Custom query could not be executed',
            },
            status: 400,
          }),
      ),
    );

    page.queryValue = 'select * from accounts';
    page.executeButton.click();
    fixture.detectChanges();

    expect(page.formErrorMessage).toBeFalsy();
    expect(miReportsService.generateCustomReport).toHaveBeenCalledTimes(1);
    expect(miReportsService.generateCustomReport).toHaveBeenCalledWith('INSTALLATION', {
      reportType: 'CUSTOM',
      sqlQuery: 'select * from accounts',
    });

    expect(page.queryErrorMessage).toBeTruthy();
    expect(page.queryErrorMessage.textContent.trim()).toEqual('Custom query could not be executed');
  });
});
