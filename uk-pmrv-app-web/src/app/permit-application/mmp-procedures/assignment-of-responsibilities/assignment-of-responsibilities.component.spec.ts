import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { MmpProcedureFormComponent } from '../mmp-procedure-form/mmp-procedure-form.component';
import { AssignmentOfResponsibilitiesComponent } from './assignment-of-responsibilities.component';

describe('AssignmentOfResponsibilitiesComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AssignmentOfResponsibilitiesComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<AssignmentOfResponsibilitiesComponent>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<AssignmentOfResponsibilitiesComponent> {
    get procedureNameValue() {
      return this.getInputValue('[name="procedureName"]');
    }

    set procedureNameValue(value) {
      this.setInputValue('[name="procedureName"]', value);
    }

    get procedureRefValue() {
      return this.getInputValue('[name="procedureReference"]');
    }

    set procedureRefValue(value) {
      this.setInputValue('[name="procedureReference"]', value);
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  const tasksService = mockClass(TasksService);
  const createComponent = () => {
    fixture = TestBed.createComponent(AssignmentOfResponsibilitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssignmentOfResponsibilitiesComponent, MmpProcedureFormComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
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

  describe('for new mmp methods', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
            },
          },
          {
            monitoringMethodologyPlans: [true],
            mmpProcedures: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.procedureNameValue = 'procedure name';
      page.procedureRefValue = 'procedure reference';
      fixture.detectChanges();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
              digitizedPlan: {
                procedures: {
                  ASSIGNMENT_OF_RESPONSIBILITIES: {
                    procedureName: 'procedure name',
                    procedureReference: 'procedure reference',
                    dataMaintenanceResponsibleEntity: null,
                    diagramReference: null,
                    itSystemUsed: null,
                    locationOfRecords: null,
                    procedureDescription: null,
                    standardsAppliedList: null,
                  },
                },
              },
            },
          },

          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            mmpProcedures: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['./monitoring-plan-appropriateness'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
