import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitApplicationModule } from '@permit-application/permit-application.module';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { mockDigitizedPlanWasteGasBalance } from '../testing/mock';
import { SubInstallationsSummaryComponent } from './sub-installations-summary.component';

describe('SubInstallationsSummaryComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SubInstallationsSummaryComponent;
  let fixture: ComponentFixture<SubInstallationsSummaryComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<SubInstallationsSummaryComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get confirmButton() {
      return this.queryAll<HTMLButtonElement>('.govuk-button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubInstallationsSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
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

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanWasteGasBalance,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanWasteGasBalance,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, state: { notification: true } });
  });
});
