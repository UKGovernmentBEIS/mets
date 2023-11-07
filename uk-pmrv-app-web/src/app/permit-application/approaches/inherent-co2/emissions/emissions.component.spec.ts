import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { InherentCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { InherentCo2Module } from '../inherent-co2.module';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2' },
  );

  class Page extends BasePage<EmissionsComponent> {
    get emissions() {
      return this.getInputValue('#totalEmissions');
    }

    set emissions(value: string) {
      this.setInputValue('#totalEmissions', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InherentCo2Module, RouterTestingModule],
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

  describe('for adding an inherent co2 item', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              INHERENT_CO2: {
                ...mockState.permit.monitoringApproaches.INHERENT_CO2,
                inherentReceivingTransferringInstallations: [
                  {
                    ...(mockState.permit.monitoringApproaches.INHERENT_CO2 as InherentCO2MonitoringApproach)
                      .inherentReceivingTransferringInstallations[0],
                    totalEmissions: null,
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            INHERENT_CO2: [false],
          },
        ),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter estimated emissions']);

      page.emissions = '10';

      fixture.detectChanges();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              INHERENT_CO2: {
                ...mockState.permit.monitoringApproaches.INHERENT_CO2,
                inherentReceivingTransferringInstallations: [
                  {
                    ...(mockState.permit.monitoringApproaches.INHERENT_CO2 as InherentCO2MonitoringApproach)
                      .inherentReceivingTransferringInstallations[0],
                    totalEmissions: '10',
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            INHERENT_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../..'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for editing an inherent co2 item', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              INHERENT_CO2: {
                ...mockState.permit.monitoringApproaches.INHERENT_CO2,
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            INHERENT_CO2: [false],
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

      expect(page.emissions).toEqual('1');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../..'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
