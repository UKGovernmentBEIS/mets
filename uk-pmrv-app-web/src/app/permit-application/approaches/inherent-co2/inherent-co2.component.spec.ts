import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { InherentCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { InherentCO2Component } from './inherent-co2.component';
import { InherentCo2Module } from './inherent-co2.module';

describe('InherentCO2Component', () => {
  let component: InherentCO2Component;
  let fixture: ComponentFixture<InherentCO2Component>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    { taskId: '123' },
    {},
    { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2' },
  );

  class Page extends BasePage<InherentCO2Component> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get addItemButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('#addItem');
    }

    get addAnotherItemButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('#addAnotherItem');
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
    fixture = TestBed.createComponent(InherentCO2Component);
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

  describe('for an empty list', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              INHERENT_CO2: {
                type: 'INHERENT_CO2',
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

    it('should display add new item button', () => {
      expect(page.addItemButton).toBeTruthy();
      expect(page.addAnotherItemButton).toBeFalsy();
      expect(page.submitButton).toBeFalsy();
    });
  });

  describe('for an non empty list', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
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
        ['Direction of travel', 'Exported to a non-ETS consumer'],

        ['Installation name', 'Angela Mcdowell'],
        ['Installation address', '997 Rocky Fabien Avenue, Qui consequat Non r Est libero nulla cuQuaerat in amet qua'],
        ['Measurement devices used', 'Instruments belonging to your installation'],
        ['Estimated emissions', '1'],
      ]);
    });

    it('should submit the item list  and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
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
            INHERENT_CO2: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for a list with non finished items', () => {
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
                    measurementInstrumentOwnerTypes: [],
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

    it('should display add another item button', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Not all items are complete']);
    });
  });
});
