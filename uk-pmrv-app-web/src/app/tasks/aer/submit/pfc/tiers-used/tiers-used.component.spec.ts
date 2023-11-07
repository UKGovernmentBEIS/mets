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
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { PfcModule } from '../pfc.module';
import { TiersUsedComponent } from './tiers-used.component';

describe('TiersUsedComponent', () => {
  let page: Page;
  let router: Router;
  let component: TiersUsedComponent;
  let fixture: ComponentFixture<TiersUsedComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  class Page extends BasePage<TiersUsedComponent> {
    get activityDataTierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="activityDataTier"]');
    }
    get emissionFactorTierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="emissionFactorTier"]');
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
    fixture = TestBed.createComponent(TiersUsedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, PfcModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [
                  {
                    massBalanceApproachUsed: true,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC: [],
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
      expect(page.errorSummaryLinks).toEqual(['You must select a suitable tier', 'You must select a suitable tier']);

      page.submitButton.click();
      fixture.detectChanges();

      page.activityDataTierRadios[0].click();
      page.emissionFactorTierRadios[0].click();

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
                    calculationCorrect: null,
                    massBalanceApproachUsed: true,
                    parameterMonitoringTier: {
                      activityDataTier: 'TIER_4',
                      emissionFactorTier: 'TIER_2',
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

      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-reason'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [
                  {
                    massBalanceApproachUsed: true,
                    parameterMonitoringTier: {
                      activityDataTier: 'TIER_4',
                      emissionFactorTier: 'TIER_2',
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
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.activityDataTierRadios.length).toEqual(4);
      expect(page.activityDataTierRadios[0].checked).toBeTruthy();
      expect(page.activityDataTierRadios[1].checked).toBeFalsy();
      expect(page.activityDataTierRadios[2].checked).toBeFalsy();
      expect(page.activityDataTierRadios[3].checked).toBeFalsy();

      expect(page.emissionFactorTierRadios.length).toEqual(2);
      expect(page.emissionFactorTierRadios[0].checked).toBeTruthy();
      expect(page.emissionFactorTierRadios[1].checked).toBeFalsy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-reason'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
