import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { AerModule } from '../../../../aer.module';
import { mockAerApplyPayload, mockState } from '../../../../submit/testing/mock-aer-apply-action';
import { mockStateBuild } from '../../../../submit/testing/mock-state';
import { mockPostBuild } from '../../../../submit/testing/mock-state';
import { AerSharedModule } from '../../../aer-shared.module';
import { DateRangeComponent } from './date-range.component';

describe('DateRangeComponent', () => {
  let page: Page;
  let router: Router;

  let component: DateRangeComponent;
  let fixture: ComponentFixture<DateRangeComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'CALCULATION_PFC',
  });

  class Page extends BasePage<DateRangeComponent> {
    get fullYearCoveredRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="fullYearCovered"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DateRangeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, AerSharedModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    const sourceStreamEmission = {
      sourceStream: mockAerApplyPayload.aer.sourceStreams[0].id,
      emissionSources: [mockAerApplyPayload.aer.emissionSources[0].id],
    };

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [sourceStreamEmission],
              },
            },
          },
          {
            CALCULATION_PFC: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'You must enter a date range which is either the whole year or part of the year',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      page.fullYearCoveredRadios[0].click();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [
                  {
                    ...sourceStreamEmission,
                    durationRange: {
                      fullYearCovered: true,
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-used'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.fullYearCoveredRadios.length).toEqual(2);
      expect(page.fullYearCoveredRadios[0].checked).toBeTruthy();
      expect(page.fullYearCoveredRadios[1].checked).toBeFalsy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-used'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
