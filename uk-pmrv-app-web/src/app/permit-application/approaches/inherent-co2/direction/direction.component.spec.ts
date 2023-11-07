import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { InherentCo2Module } from '../inherent-co2.module';
import { DirectionComponent } from './direction.component';

describe('DirectionComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;
  let component: DirectionComponent;
  let fixture: ComponentFixture<DirectionComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2' },
  );

  class Page extends BasePage<DirectionComponent> {
    get inherentCO2Direction() {
      return this.queryAll<HTMLInputElement>('input[name$="inherentCO2Direction"]');
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
    fixture = TestBed.createComponent(DirectionComponent);
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

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a category']);

      page.inherentCO2Direction[0].click();

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
                    inherentCO2Direction: 'EXPORTED_TO_ETS_INSTALLATION',
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

      expect(navigateSpy).toHaveBeenCalledWith(['../details'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for editing an inherent co2 item', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.inherentCO2Direction.length).toEqual(3);
      expect(page.inherentCO2Direction[0].checked).toBeFalsy();
      expect(page.inherentCO2Direction[1].checked).toBeTruthy();
      expect(page.inherentCO2Direction[2].checked).toBeFalsy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../details'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
