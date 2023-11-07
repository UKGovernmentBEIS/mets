import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noTotalEmissions } from '@tasks/aer/submit/inherent-co2/errors/inherent-co2-validation.errors';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCO2Emissions, InherentReceivingTransferringInstallation, TasksService } from 'pmrv-api';

import { InherentCo2Module } from '../inherent-co2.module';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

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
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding an inherent co2 item', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              INHERENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.INHERENT_CO2,
                inherentReceivingTransferringInstallations: [
                  {
                    inherentReceivingTransferringInstallation: {
                      ...(mockAerApplyPayload.aer.monitoringApproachEmissions.INHERENT_CO2 as InherentCO2Emissions)
                        .inherentReceivingTransferringInstallations[0].inherentReceivingTransferringInstallation,
                      totalEmissions: null,
                    } as InherentReceivingTransferringInstallation,
                  },
                ],
              },
            },
          },
          {
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
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([noTotalEmissions]);

      page.emissions = '10';
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              INHERENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.INHERENT_CO2,
                inherentReceivingTransferringInstallations: [
                  {
                    inherentReceivingTransferringInstallation: {
                      ...(mockAerApplyPayload.aer.monitoringApproachEmissions.INHERENT_CO2 as InherentCO2Emissions)
                        .inherentReceivingTransferringInstallations[0].inherentReceivingTransferringInstallation,
                      totalEmissions: '10',
                    },
                  },
                ],
              },
            },
          },
          {
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
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.emissions).toEqual('3');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.emissions = '2';
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
