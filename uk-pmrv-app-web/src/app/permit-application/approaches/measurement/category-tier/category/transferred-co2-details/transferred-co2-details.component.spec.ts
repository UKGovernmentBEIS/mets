import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasurementModule } from '@permit-application/approaches/measurement/measurement.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { MeasurementOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { TransferredCo2DetailsComponent } from './transferred-co2-details.component';

describe('TransferredCo2DetailsComponent', () => {
  let component: TransferredCo2DetailsComponent;
  let fixture: ComponentFixture<TransferredCo2DetailsComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let router: Router;
  let activatedRoute: ActivatedRouteSnapshotStub;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
  });

  class Page extends BasePage<TransferredCo2DetailsComponent> {
    get installationDetailsType() {
      return this.queryAll<HTMLInputElement>('input[name$="installationDetailsType"]');
    }

    set emitterId(value: string) {
      this.setInputValue('#emitterId', value);
    }

    set email(value: string) {
      this.setInputValue('#email', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TransferredCo2DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' });
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ActivatedRouteSnapshotStub, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for editing emission point category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          MEASUREMENT_CO2_Category: [true],
          MEASUREMENT_CO2_Measured_Emissions: [false],
          MEASUREMENT_CO2_Applied_Standard: [false],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.installationDetailsType[0].click();

      page.emitterId = '';
      page.email = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter an installation emitter ID', 'Enter your email address']);

      page.emitterId = '77777';
      page.email = 'test@beis.com';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...mockState,
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              MEASUREMENT_CO2: {
                ...mockState.permit.monitoringApproaches.MEASUREMENT_CO2,
                emissionPointCategoryAppliedTiers: [
                  {
                    ...(mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
                      .emissionPointCategoryAppliedTiers[0],
                    emissionPointCategory: {
                      ...(mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
                        .emissionPointCategoryAppliedTiers[0].emissionPointCategory,
                      transfer: {
                        ...(mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
                          .emissionPointCategoryAppliedTiers[0].emissionPointCategory.transfer,
                        installationDetailsType: 'INSTALLATION_EMITTER',
                        installationEmitter: {
                          ...(
                            mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
                          ).emissionPointCategoryAppliedTiers[0].emissionPointCategory.transfer.installationEmitter,
                          emitterId: '77777',
                          email: 'test@beis.com',
                        },
                        installationDetails: null,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Measured_Emissions: [false],
            MEASUREMENT_CO2_Applied_Standard: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});
