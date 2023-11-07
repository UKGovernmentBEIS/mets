import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { PfcModule } from '../pfc.module';
import { PrimaryAluminiumComponent } from './primary-aluminium.component';

describe('PrimaryAluminiumComponent', () => {
  let page: Page;
  let router: Router;

  let component: PrimaryAluminiumComponent;
  let fixture: ComponentFixture<PrimaryAluminiumComponent>;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  class Page extends BasePage<PrimaryAluminiumComponent> {
    get totalPrimaryAluminium() {
      return this.getInputValue('#totalPrimaryAluminium');
    }
    set totalPrimaryAluminium(value: string) {
      this.setInputValue('#totalPrimaryAluminium', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PrimaryAluminiumComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, PfcModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [{}],
              },
            },
          },
          {
            CALCULATION_PFC: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a value']);

      page.submitButton.click();
      fixture.detectChanges();

      page.totalPrimaryAluminium = '30';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [
                  {
                    totalPrimaryAluminium: '30',
                    calculationCorrect: null,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../method-b'], { relativeTo: route });
    });
  });
});
