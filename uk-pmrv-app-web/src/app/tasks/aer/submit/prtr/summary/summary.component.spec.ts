import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { SummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get activities() {
      return this.queryAll<HTMLDListElement>('tr');
    }
    get activitiesTextContents() {
      return this.activities.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
    get addAnotherBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another',
      );
    }

    get summaryHeader() {
      return this.query<HTMLHeadingElement>('.govuk-heading-m');
    }
    get summaryDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  function createComponent() {
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  }

  describe('summary with activities', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          prtrCodes: {
            exist: true,
            codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES', '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION'],
          },
        }),
      );
      router = TestBed.inject(Router);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add another and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addAnotherBtn).toBeTruthy();
      expect(page.activities.length).toEqual(3);
    });

    it('should display the activities and submit status', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.summaryDefinitions).toEqual([]);
      expect(page.activitiesTextContents).toHaveLength(3);
      expect(page.activitiesTextContents).toEqual([
        [],
        ['Main activity', '1.(a) Mineral oil and gas refineries', 'Delete'],
        ['Main activity', '1.(b) Installations for gasification and liquefaction', 'Delete'],
      ]);

      expect(page.summaryHeader.textContent).toEqual('EPRTR codes added');

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            prtrCodes: {
              codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES', '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION'],
              exist: true,
            },
          },
          { prtrCodes: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
    });
  });

  describe('summary with no activities', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      router = TestBed.inject(Router);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not show add another button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addAnotherBtn).toBeFalsy();
      expect(page.activities.length).toEqual(0);
    });

    it('should display the summary and submit status', () => {
      expect(page.summaryDefinitions).toEqual([
        ['Are emissions from the installation reported under the Pollutant Release and Transfer Register?', 'No'],
      ]);
      expect(page.summaryHeader).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            prtrCodes: {
              codes: undefined,
              exist: false,
            },
          },
          { prtrCodes: [true] },
        ),
      );
    });
  });
});
