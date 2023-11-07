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
import { mockAerApplyPayload, mockState } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { PfcModule } from '../pfc.module';
import { EmissionNetworkComponent } from './emission-network.component';

describe('EmissionNetworkComponent', () => {
  let page: Page;
  let router: Router;

  let component: EmissionNetworkComponent;
  let fixture: ComponentFixture<EmissionNetworkComponent>;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);
  class Page extends BasePage<EmissionNetworkComponent> {
    get sourceStream(): string {
      return this.getInputValue('#sourceStream');
    }
    set sourceStream(value: string) {
      this.setInputValue('#sourceStream', value);
    }

    get emissionSources() {
      return this.fixture.componentInstance.form.get('emissionSources').value;
    }

    set emissionSources(value: string[]) {
      this.fixture.componentInstance.form.get('emissionSources').setValue(value);
    }

    get massBalanceApproachUsedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="massBalanceApproachUsed"]');
    }

    get calculationMethodRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="calculationMethod"]');
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
    fixture = TestBed.createComponent(EmissionNetworkComponent);
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
                sourceStreamEmissions: [],
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
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select at least one emission source',
        'Select a calculation method',
        'Select Yes or No',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      page.sourceStream = mockAerApplyPayload.aer.sourceStreams[0].id;
      page.emissionSources = [mockAerApplyPayload.aer.emissionSources[0].id];
      page.massBalanceApproachUsedRadios[1].click();
      page.calculationMethodRadios[0].click();

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
                    calculationCorrect: null,
                    sourceStream: mockAerApplyPayload.aer.sourceStreams[0].id,
                    emissionSources: [mockAerApplyPayload.aer.emissionSources[0].id],
                    massBalanceApproachUsed: false,
                    parameterMonitoringTier: {},
                    pfcSourceStreamEmissionCalculationMethodData: {
                      calculationMethod: 'SLOPE',
                    },
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

      expect(navigateSpy).toHaveBeenCalledWith(['../date-range'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      const sourceStream = mockAerApplyPayload.aer.sourceStreams[0];
      const emissionSources = mockAerApplyPayload.aer.emissionSources[0];

      expect(page.massBalanceApproachUsedRadios.length).toEqual(2);
      expect(page.massBalanceApproachUsedRadios[0].checked).toBeTruthy();
      expect(page.massBalanceApproachUsedRadios[1].checked).toBeFalsy();

      expect(page.calculationMethodRadios[0].checked).toBeTruthy();
      expect(page.calculationMethodRadios[1].checked).toBeFalsy();

      expect(page.sourceStream).toEqual(sourceStream.id);

      expect(page.emissionSources).toEqual([emissionSources.id]);
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      // component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../date-range'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
