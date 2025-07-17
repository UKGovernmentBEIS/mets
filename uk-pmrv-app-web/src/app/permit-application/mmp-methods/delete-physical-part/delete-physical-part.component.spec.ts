import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { DigitizedPlan, TasksService } from 'pmrv-api';

import { DeletePhysicalPartComponent } from './delete-physical-part.component';

describe('DeletePhysicalPartComponent', () => {
  let component: DeletePhysicalPartComponent;
  let fixture: ComponentFixture<DeletePhysicalPartComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;

  const route = new ActivatedRouteStub({ id: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeletePhysicalPartComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button-group button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeletePhysicalPartComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
          digitizedPlan: {
            methodTask: {
              physicalPartsAndUnitsAnswer: mockDigitizedPlanDetails.methodTask.physicalPartsAndUnitsAnswer,
              connections: mockDigitizedPlanDetails.methodTask.connections,
            },
          },
        },
      }),
    );

    fixture = TestBed.createComponent(DeletePhysicalPartComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete physical part Test 1?`);
  });

  it('should delete physical part and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
          digitizedPlan: {
            methodTask: { connections: [], physicalPartsAndUnitsAnswer: true },
          } as DigitizedPlan,
        },
      }),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
