import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { mockAlrAuthorityPostBuild, mockAlrAuthorityStateBuild } from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrPreliminaryAllocationComponent } from './preliminary-allocation.component';

describe('AlrPreliminaryAllocationComponent', () => {
  let component: AlrPreliminaryAllocationComponent;
  let fixture: ComponentFixture<AlrPreliminaryAllocationComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrPreliminaryAllocationComponent> {
    get subInstallationName(): string {
      return this.getInputValue('#subInstallationName');
    }
    set subInstallationName(value: string) {
      this.setInputValue('#subInstallationName', value);
    }

    get year(): string {
      return this.getInputValue('#year');
    }
    set year(value: string) {
      this.setInputValue('#year', value);
    }
    get yearSelect(): HTMLSelectElement {
      return this.query('select[name="year"]');
    }
    get yearOptions(): string[] {
      return Array.from(this.yearSelect.options).map((option) => option.textContent.trim());
    }

    get allowances(): string {
      return this.getInputValue('#allowances');
    }
    set allowances(value: string) {
      this.setInputValue('#allowances', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrPreliminaryAllocationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule],
      providers: [
        provideRouter([]),
        AlrService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new preliminary allocation', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockAlrAuthorityStateBuild({
          authorityReviewOutcome: {
            authorityResponse: {
              type: 'VALID_WITH_CORRECTIONS',
              decisionNotice: '14545',
              authorityRespondDate: '2024-02-11',
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display years', () => {
      expect(page.yearOptions).toEqual([
        '2021',
        '2022',
        '2023',
        '2024',
        '2025',
        '2026',
        '2027',
        '2028',
        '2029',
        '2030',
        '2031',
        '2032',
        '2033',
        '2034',
        '2035',
      ]);
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.year = '2025';
      page.subInstallationName = 'EAF_CARBON_STEEL';
      page.allowances = '10';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockAlrAuthorityPostBuild(
          {
            authorityReviewOutcome: {
              authorityResponse: {
                type: 'VALID_WITH_CORRECTIONS',
                authorityRespondDate: '2024-02-11',
                decisionNotice: '14545',
                preliminaryAllocations: [
                  {
                    year: '2025',
                    subInstallationName: 'EAF_CARBON_STEEL',
                    allowances: 10,
                    allocationId: 0,
                  },
                ],
              },
            },
          },
          {
            authorityResponse: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });
});
