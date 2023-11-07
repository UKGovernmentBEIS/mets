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
import { AerService } from '../../../core/aer.service';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { DeliveryZoneComponent } from './delivery-zone.component';

describe('DeliveryZoneComponent', () => {
  let page: Page;
  let router: Router;

  let component: DeliveryZoneComponent;
  let fixture: ComponentFixture<DeliveryZoneComponent>;

  let store: CommonTasksStore;
  let aerService: AerService;

  const tasksService = mockClass(TasksService);
  tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const parameterMonitoringTiers = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
    .sourceStreamEmissions[0].parameterMonitoringTiers;

  class Page extends BasePage<DeliveryZoneComponent> {
    get localZoneCodeValue() {
      return this.getInputValue('#localZoneCode');
    }

    set localZoneCodeValue(value: string) {
      this.setInputValue('#localZoneCode', value);
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
    fixture = TestBed.createComponent(DeliveryZoneComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    aerService = TestBed.inject(AerService);
    jest.spyOn(aerService, 'getDeliveryZones').mockReturnValue(
      of([
        {
          name: 'South east',
          code: 'SE',
        },
      ]),
    );

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
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
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      postCode: 'abc 123',
                      type: 'REGIONAL_DATA',
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select your local delivery zone']);

      page.submitButton.click();
      fixture.detectChanges();

      page.localZoneCodeValue = 'SE';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            CALCULATION_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
              sourceStreamEmissions: [
                {
                  parameterCalculationMethod: {
                    localZoneCode: 'SE',
                    postCode: 'abc 123',
                    type: 'REGIONAL_DATA',
                  },
                  parameterMonitoringTiers,
                },
              ],
            },
          },
        },
        undefined,
        [false],
        'CALCULATION_CO2',
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../conditions-metered'], { relativeTo: route });
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
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      localZoneCode: 'SE',
                      postCode: 'abc 123',
                      type: 'REGIONAL_DATA',
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
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

      expect(page.localZoneCodeValue).toEqual('SE');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../conditions-metered'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
