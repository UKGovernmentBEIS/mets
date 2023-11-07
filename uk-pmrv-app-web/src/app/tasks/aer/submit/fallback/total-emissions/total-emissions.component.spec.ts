import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  noTotalBiomassEmissions,
  noTotalEnergyContentBiomass,
  noTotalFossilEmissions,
  noTotalFossilEnergyContent,
  noTotalNonSustainableBiomassEmissions,
} from '@tasks/aer/submit/fallback/errors/fallback-validation.errors';
import { FallbackModule } from '@tasks/aer/submit/fallback/fallback.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { FallbackEmissions, TasksService } from 'pmrv-api';

import { TotalEmissionsComponent } from './total-emissions.component';

describe('TotalEmissionsComponent', () => {
  let component: TotalEmissionsComponent;
  let fixture: ComponentFixture<TotalEmissionsComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const expectedNextRoute = '../upload-documents';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

  class Page extends BasePage<TotalEmissionsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get totalFossilEmissions() {
      return this.getInputValue('#totalFossilEmissions');
    }

    set totalFossilEmissions(value: string) {
      this.setInputValue('#totalFossilEmissions', value);
    }

    get totalFossilEnergyContent() {
      return this.getInputValue('#totalFossilEnergyContent');
    }

    set totalFossilEnergyContent(value: string) {
      this.setInputValue('#totalFossilEnergyContent', value);
    }

    get totalSustainableBiomassEmissions() {
      return this.getInputValue('#totalSustainableBiomassEmissions');
    }

    set totalSustainableBiomassEmissions(value: string) {
      this.setInputValue('#totalSustainableBiomassEmissions', value);
    }

    get totalEnergyContentFromBiomass() {
      return this.getInputValue('#totalEnergyContentFromBiomass');
    }

    set totalEnergyContentFromBiomass(value: string) {
      this.setInputValue('#totalEnergyContentFromBiomass', value);
    }

    get totalNonSustainableBiomassEmissions() {
      return this.getInputValue('#totalNonSustainableBiomassEmissions');
    }

    set totalNonSustainableBiomassEmissions(value: string) {
      this.setInputValue('#totalNonSustainableBiomassEmissions', value);
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
    fixture = TestBed.createComponent(TotalEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new fallback emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              FALLBACK: {
                type: 'FALLBACK',
                biomass: {
                  contains: true,
                },
              },
            },
          },
          {
            FALLBACK: [false],
          },
        ),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it(`should submit a valid form, update the store and navigate to ${expectedNextRoute}`, () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent).toEqual('Calculate the total emissions for your fall back approach');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        noTotalFossilEmissions,
        noTotalFossilEnergyContent,
        noTotalBiomassEmissions,
        noTotalEnergyContentBiomass,
        noTotalNonSustainableBiomassEmissions,
      ]);

      page.totalFossilEmissions = '1.11111';
      page.totalFossilEnergyContent = '2.22222';
      page.totalSustainableBiomassEmissions = '3.33333';
      page.totalEnergyContentFromBiomass = '4.44444';
      page.totalNonSustainableBiomassEmissions = '5.55555';
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              FALLBACK: {
                type: 'FALLBACK',
                totalFossilEmissions: '1.11111',
                totalFossilEnergyContent: '2.22222',
                biomass: {
                  contains: true,
                  totalEnergyContentFromBiomass: '4.44444',
                  totalNonSustainableBiomassEmissions: '5.55555',
                  totalSustainableBiomassEmissions: '3.33333',
                },
                reportableEmissions: '6.66666',
              } as FallbackEmissions,
            },
          },
          {
            FALLBACK: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for editing existing fallback emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.totalFossilEmissions).toEqual('1.1');
      expect(page.totalFossilEnergyContent).toEqual('2.2');
      expect(page.totalSustainableBiomassEmissions).toEqual('3.3');
      expect(page.totalEnergyContentFromBiomass).toEqual('4.4');
      expect(page.totalNonSustainableBiomassEmissions).toEqual('8.8');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.totalNonSustainableBiomassEmissions = '9.9';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
