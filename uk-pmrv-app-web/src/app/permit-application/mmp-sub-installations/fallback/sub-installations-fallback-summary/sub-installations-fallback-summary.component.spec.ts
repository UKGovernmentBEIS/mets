import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockDigitizedPlanAnnualProcessLevel } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { SubInstallationsFallbackSummaryComponent } from './sub-installations-fallback-summary.component';

describe('SubInstallationsFallbackSummaryComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SubInstallationsFallbackSummaryComponent;
  let fixture: ComponentFixture<SubInstallationsFallbackSummaryComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<SubInstallationsFallbackSummaryComponent> {
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
      declarations: [SubInstallationsFallbackSummaryComponent],
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
            digitizedPlan: mockDigitizedPlanAnnualProcessLevel,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsFallbackSummaryComponent);
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
            digitizedPlan: mockDigitizedPlanAnnualProcessLevel,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: route, state: { notification: true } });
  });
});
