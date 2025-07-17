import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { MmpInstallationDescriptionComponent } from './mmp-installation-description.component';

describe('MmpInstallationDescriptionComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: MmpInstallationDescriptionComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<MmpInstallationDescriptionComponent>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MmpInstallationDescriptionComponent> {
    get descriptionValue() {
      return this.getInputValue('#description');
    }
    set descriptionValue(value: string) {
      this.setInputValue('#description', value);
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
    fixture = TestBed.createComponent(MmpInstallationDescriptionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MmpInstallationDescriptionComponent],
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

  describe('for new mmp installation description', () => {
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
            mmpInstallationDescription: [false],
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

      page.descriptionValue = 'description';
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
              digitizedPlan: { installationDescription: { description: 'description' } },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            mmpInstallationDescription: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['upload-file'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
