import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { wizardItemsNotCompleted } from '@tasks/aer/submit/inherent-co2/errors/inherent-co2-validation.errors';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InherentCO2Emissions, TasksService } from 'pmrv-api';

import { InherentCo2Component } from './inherent-co2.component';
import { InherentCo2Module } from './inherent-co2.module';

describe('InherentCo2Component', () => {
  let component: InherentCo2Component;
  let fixture: ComponentFixture<InherentCo2Component>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<InherentCo2Component> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get addItemButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('a[govukButton]');
    }

    get addAnotherItemButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('a[govukSecondaryButton]');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
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
    fixture = TestBed.createComponent(InherentCo2Component);
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
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for an empty list', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              INHERENT_CO2: {
                type: 'INHERENT_CO2',
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

    it('should display add new item button', () => {
      expect(page.addItemButton).toBeTruthy();
      expect(page.addAnotherItemButton).toBeFalsy();
      expect(page.submitButton).toBeFalsy();
    });
  });

  describe('for a non empty list', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add another item button', () => {
      expect(page.addAnotherItemButton).toBeTruthy();
      expect(page.addItemButton).toBeFalsy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display the items in the list', () => {
      expect(page.summaryListValues).toEqual([
        ['Item', ''],
        ['Direction of travel', 'Exported to an ETS installation'],
        ['Installation emitter ID', 'EM12345'],
        ['Contact email address', '1@o.com'],
        ['Measurement devices used', 'Instruments belonging to your installation'],
        ['Reportable emissions', '3 tCO2e'],
        ['Item', ''],
        ['Direction of travel', 'Exported to a non-ETS consumer'],
        ['Installation name', 'Test installation'],
        ['Installation address', 'Test street Berlin54555'],
        ['Measurement devices used', 'Instruments belonging to the other installation'],
        ['Reportable emissions', '1 tCO2e'],
      ]);
    });

    it('should submit the item list and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({}, { INHERENT_CO2: [true] }));

      expect(navigateSpy).toHaveBeenCalledWith(['..'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for a list with non finished items', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              INHERENT_CO2: {
                type: 'INHERENT_CO2',
                inherentReceivingTransferringInstallations: [
                  {
                    measurementInstrumentOwnerTypes: [],
                  },
                ],
              } as unknown as InherentCO2Emissions,
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

    it('should display add another item button', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([wizardItemsNotCompleted]);
    });
  });
});
