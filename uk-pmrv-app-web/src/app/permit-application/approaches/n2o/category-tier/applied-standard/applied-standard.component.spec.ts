import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier, TasksService } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '../../../../testing/mock-state';
import { N2oModule } from '../../n2o.module';
import { AppliedStandardComponent } from './applied-standard.component';

describe('AppliedStandardComponent', () => {
  let fixture: ComponentFixture<AppliedStandardComponent>;
  let component: AppliedStandardComponent;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: 0 }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_N2O_Applied_Standard',
  });
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<AppliedStandardComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get ulists() {
      return this.queryAll<HTMLUListElement>('ul > li');
    }

    get parameter() {
      return this.getInputValue('#parameter');
    }

    set parameter(value: string) {
      this.setInputValue('#parameter', value);
    }

    get appliedStandard() {
      return this.getInputValue<HTMLInputElement>('#appliedStandard');
    }

    get deviationFromAppliedStandardExist() {
      return this.query<HTMLInputElement>('#deviationFromAppliedStandardExist-option0');
    }

    get deviationFromAppliedStandardNotExist() {
      return this.query<HTMLInputElement>('#deviationFromAppliedStandardExist-option1');
    }

    get deviationFromAppliedStandardDetails() {
      return this.getInputValue<HTMLTextAreaElement>('#deviationFromAppliedStandardDetails');
    }

    get laboratoryName() {
      return this.getInputValue<HTMLInputElement>('#laboratoryName');
    }

    get laboratoryAccredited() {
      return this.query<HTMLInputElement>('#laboratoryAccredited-option0');
    }

    get laboratoryAccreditationEvidence() {
      return this.getInputValue<HTMLTextAreaElement>('#laboratoryAccreditationEvidence');
    }

    get caption() {
      return this.query<HTMLElement>('.govuk-caption-l');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AppliedStandardComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, N2oModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('applied standard not completed and category not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                emissionPointCategoryAppliedTiers: [],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_N2O_Category: [],
            MEASUREMENT_N2O_Applied_Standard: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement of nitrous oxide (N2O)');
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeTruthy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Emission point category')).toBeTruthy();
    });
  });

  describe('applied standard not completed and category and prerequisities completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: '16363790610230.8369404469603225',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: '23.5',
                      categoryType: 'MAJOR',
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Applied_Standard: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual(
        'Measurement of nitrous oxide (N2O), The big Ref Emission point 1: Major',
      );
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeFalsy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Emission point category')).toBeFalsy();
    });
  });

  describe('applied standard completed and category and prerequisities not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: '16363790610230.8369404469603225',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: '23.5',
                      categoryType: 'MAJOR',
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_N2O_Category: [false],
            MEASUREMENT_N2O_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should not display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual(
        'Measurement of nitrous oxide (N2O), The big Ref Emission point 1: Major',
      );
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeFalsy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Emission point category')).toBeFalsy();
    });
  });

  describe('applied standard completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: '16363790610230.8369404469603225',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: '23.5',
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should populate form with values', () => {
      expect(page.parameter).toEqual('parameter');
      expect(page.appliedStandard).toEqual('appliedStandard');
      expect(page.deviationFromAppliedStandardExist.checked).toBeTruthy();
      expect(page.deviationFromAppliedStandardDetails).toEqual('deviationFromAppliedStandardDetails');
      expect(page.laboratoryName).toEqual('laboratoryName');
      expect(page.laboratoryAccredited.checked).toBeTruthy();
      expect(page.laboratoryAccreditationEvidence).toBeFalsy();
    });

    it('should submit updated values', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.parameter = 'new parameter';
      page.deviationFromAppliedStandardNotExist.click();
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              MEASUREMENT_N2O: {
                ...store.getState().permit.monitoringApproaches.MEASUREMENT_N2O,
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: '16363790610230.8369404469603225',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: '23.5',
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'new parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: false,
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          { ...store.getState().permitSectionsCompleted, MEASUREMENT_N2O_Applied_Standard: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
    });
  });
});
