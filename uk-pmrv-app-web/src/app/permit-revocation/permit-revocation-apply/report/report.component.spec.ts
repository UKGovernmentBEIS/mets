import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { format, subDays } from 'date-fns';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { mockTaskState } from '../../testing/mock-state';
import { ReportComponent } from './report.component';

describe('Report Component', () => {
  let component: ReportComponent;
  let fixture: ComponentFixture<ReportComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<ReportComponent> {
    set radioOption(value: boolean) {
      this.setInputValue('#annualEmissionsReportRequired-option0', value);
    }

    get radioOption() {
      return this.getInputValue('#annualEmissionsReportRequired-option0');
    }

    set radioOption1(value: boolean) {
      this.setInputValue('#annualEmissionsReportRequired-option1', value);
    }

    get radioOption1() {
      return this.getInputValue('#annualEmissionsReportRequired-option1');
    }

    get radioBtnOption() {
      return this.query<HTMLFormElement>('input[type="radio"]#annualEmissionsReportRequired-option0');
    }

    get radioBtnOption1() {
      return this.query<HTMLButtonElement>('input[type="radio"]#annualEmissionsReportRequired-option1');
    }

    get annualEmissionsReportDateDay() {
      return this.getInputValue('#annualEmissionsReportDate-day');
    }
    set annualEmissionsReportDateDay(value: string) {
      this.setInputValue('#annualEmissionsReportDate-day', value);
    }

    get annualEmissionsReportDateMonth() {
      return this.getInputValue('#annualEmissionsReportDate-month');
    }
    set annualEmissionsReportDateMonth(value: string) {
      this.setInputValue('#annualEmissionsReportDate-month', value);
    }

    get annualEmissionsReportDateYear() {
      return this.getInputValue('#annualEmissionsReportDate-year');
    }
    set annualEmissionsReportDateYear(value: string) {
      this.setInputValue('#annualEmissionsReportDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_APPLY',
    pageTitle: 'Is an annual emissions monitoring revocation report required?',
    keys: ['annualEmissionsReportRequired', 'annualEmissionsReportDate'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitRevocation: {
        reason: 'some reason',
        activitiesStopped: false,
        stoppedDate: null,
        effectiveDate: null,
        surrenderRequired: null,
        feeCharged: null,
        annualEmissionsReportRequired: null,
      },
      sectionsCompleted: {},
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form', () => {
    expect(component.form.get('annualEmissionsReportRequired').value).toEqual(null);
  });

  it('should validate form and display an error message', () => {
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select yes or no']);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should validate form by selecting "No" and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.radioBtnOption1.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'surrender-allowances'], { relativeTo: route });
  });

  it('should validate form when a user selects option "Yes" without completing all the required fields', () => {
    page.radioBtnOption.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter a date']);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for minimum date limit', () => {
    const govukDatePipe = new GovukDatePipe();
    const yesterday = format(subDays(new Date(), 1), 'yyyy-MM-dd');
    const date = yesterday.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.annualEmissionsReportDateYear = year;
    page.annualEmissionsReportDateMonth = month;
    page.annualEmissionsReportDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();
    const dateForErrorMsg = format(new Date(), 'yyyy-MM-dd');
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      `This date must be the same as or after ${govukDatePipe.transform(new Date(dateForErrorMsg))}`,
    ]);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const today = format(new Date(), 'yyyy-MM-dd');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.annualEmissionsReportDateYear = year;
    page.annualEmissionsReportDateMonth = month;
    page.annualEmissionsReportDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'surrender-allowances'], { relativeTo: route });
  });
});
