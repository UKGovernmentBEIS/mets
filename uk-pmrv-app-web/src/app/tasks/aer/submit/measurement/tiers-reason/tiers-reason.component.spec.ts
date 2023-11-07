import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload, mockState } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { MeasurementModule } from '../measurement.module';
import { TiersReasonComponent } from './tiers-reason.component';

describe('TiersReasonComponent', () => {
  let page: Page;
  let router: Router;

  let component: TiersReasonComponent;
  let fixture: ComponentFixture<TiersReasonComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'MEASUREMENT_CO2',
  });

  class Page extends BasePage<TiersReasonComponent> {
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
    }

    get reasonDataGap() {
      return this.getInputValue('#reasonDataGap');
    }
    set reasonDataGap(value: string) {
      this.setInputValue('#reasonDataGap', value);
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
    fixture = TestBed.createComponent(TiersReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, MeasurementModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });
  describe('for adding a new emission point emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [{}],
              },
            },
          },
          {
            MEASUREMENT_CO2: [],
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
        'You must say why you could not use the tiers from your monitoring plan',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      page.typeRadios[0].click();
      page.reasonDataGap = 'this is a reason';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    parameterMonitoringTierDiffReason: {
                      reason: 'this is a reason',
                      relatedNotifications: null,
                      type: 'DATA_GAP',
                    },
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../ghg-concentration'], { relativeTo: route });
    });
  });

  describe('for editing a emission point emission', () => {
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

      expect(page.typeRadios.length).toEqual(2);
      expect(page.typeRadios[0].checked).toBeTruthy();
      expect(page.typeRadios[1].checked).toBeFalsy();

      expect(page.reasonDataGap).toBe('reason');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../ghg-concentration'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
