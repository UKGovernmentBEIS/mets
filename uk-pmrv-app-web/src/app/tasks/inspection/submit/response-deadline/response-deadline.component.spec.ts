import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { inspectionMockStateBuild } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { addDays, format, startOfDay } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ResponseDeadlineComponent } from './response-deadline.component';

describe('ResponseDeadlineComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: ResponseDeadlineComponent;
  let fixture: ComponentFixture<ResponseDeadlineComponent>;

  const datePipe = new GovukDatePipe();
  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' });

  class Page extends BasePage<ResponseDeadlineComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get responseDeadlineDay() {
      return this.getInputValue('#responseDeadline-day');
    }
    set responseDeadlineDay(value: string) {
      this.setInputValue('#responseDeadline-day', value);
    }

    get responseDeadlineMonth() {
      return this.getInputValue('#responseDeadline-month');
    }

    set responseDeadlineMonth(value: string) {
      this.setInputValue('#responseDeadline-month', value);
    }

    get responseDeadlineYear() {
      return this.getInputValue('#responseDeadline-year');
    }

    set responseDeadlineYear(value: string) {
      this.setInputValue('#responseDeadline-year', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponseDeadlineComponent, RouterTestingModule],
      providers: [
        KeycloakService,
        TaskTypeToBreadcrumbPipe,
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      inspectionMockStateBuild({
        installationInspection: {
          responseDeadline: null,
        },
      }),
    );

    fixture = TestBed.createComponent(ResponseDeadlineComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const responseDeadline = format(addDays(new Date(), 10), 'yyyy-MM-dd');
    const tomorrow = startOfDay(addDays(new Date(), 1));

    const date = responseDeadline.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    expect(page.errorSummary).toBeFalsy();
    expect(page.submitButton).toBeTruthy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual([
      `The date must be the same as or after ${datePipe.transform(format(tomorrow, 'yyyy-MM-dd'))}`,
      'Enter a date',
    ]);

    page.responseDeadlineYear = year;
    page.responseDeadlineMonth = month;
    page.responseDeadlineDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../follow-up-summary'], { relativeTo: activatedRoute });
  });
});
