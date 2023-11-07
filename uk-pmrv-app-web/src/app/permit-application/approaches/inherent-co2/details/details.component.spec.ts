import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { InherentCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { InherentCo2Module } from '../inherent-co2.module';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2' },
  );

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
                    installationDetailsType: null,
                    inherentReceivingTransferringInstallationDetailsType: null,
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

    it('should create!', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
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
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              INHERENT_CO2: {
                ...mockState.permit.monitoringApproaches.INHERENT_CO2,
                inherentReceivingTransferringInstallations: [
                  {
                    ...(mockState.permit.monitoringApproaches.INHERENT_CO2 as InherentCO2MonitoringApproach)
                      .inherentReceivingTransferringInstallations[0],
                    installationDetailsType: 'INSTALLATION_EMITTER',
                    inherentReceivingTransferringInstallationDetailsType: {
                      installationEmitter: {
                        email: 'test@test.gr',
                        emitterId: '123',
                      },
                    },
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

      expect(navigateSpy).toHaveBeenCalledWith(['../instruments'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
