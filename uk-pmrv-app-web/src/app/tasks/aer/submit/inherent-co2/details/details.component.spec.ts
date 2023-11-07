import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCO2Emissions, InherentReceivingTransferringInstallation, TasksService } from 'pmrv-api';

import { InherentCo2Module } from '../inherent-co2.module';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

  class Page extends BasePage<DetailsComponent> {
    get inherentCO2Direction() {
      return this.queryAll<HTMLInputElement>('input[name$="inherentCO2Direction"]');
    }

    get emitterIdOption() {
      return this.query<HTMLButtonElement>('#installationDetailsType-option0');
    }

    set emailInput(value: string) {
      this.setInputValue('#email', value);
    }

    set installationEmitterIdInput(value: string) {
      this.setInputValue('#emitterId', value);
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
    fixture = TestBed.createComponent(DetailsComponent);
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
                      installationDetailsType: null,
                      inherentReceivingTransferringInstallationDetailsType: null,
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

    it('should create!', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to instruments', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select an option']);

      page.emitterIdOption.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter an installation emitter ID', 'Enter your email address']);

      page.installationEmitterIdInput = '123';
      page.emailInput = 'test@test.gr';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
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
                      installationDetailsType: 'INSTALLATION_EMITTER',
                      inherentReceivingTransferringInstallationDetailsType: {
                        installationEmitter: {
                          email: 'test@test.gr',
                          emitterId: '123',
                        },
                      },
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

      expect(navigateSpy).toHaveBeenCalledWith(['../instruments'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
