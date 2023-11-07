import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
  mockVariationRegulatorLedDeterminationPostBuild,
} from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockDeterminationPostBuild, mockState } from '../../../testing/mock-state';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;
  let router: Router;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionsComponent> {
    get year() {
      return this.getInputValue('#annualEmissionsTargets.0.year');
    }

    set year(value: string) {
      this.setInputValue('#annualEmissionsTargets.0.year', value);
    }

    get emissions() {
      return this.getInputValue('#annualEmissionsTargets.0.emissions');
    }

    set emissions(value: string) {
      this.setInputValue('#annualEmissionsTargets.0.emissions', value);
    }

    get addTargetBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another target',
      );
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, SharedPermitModule, RouterTestingModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
        declarations: [EmissionsComponent],
      }).compileComponents();
    });

    describe('for new emissions targets', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({ ...mockState, permitType: 'HSE' });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display a form with emprty values', () => {
        expect(page.addTargetBtn).toBeTruthy();
        expect(page.continueButton).toBeTruthy();
        expect(page.errorSummary).toBeFalsy();
      });

      it('should submit a valid form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.continueButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryList).toEqual(['Enter a year value', 'Enter tonnes CO2e']);

        page.year = '2022';
        page.emissions = '22222';

        page.continueButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockDeterminationPostBuild(
            {
              annualEmissionsTargets: { '2022': 22222 },
            },
            {
              determination: false,
            },
          ),
        );
        expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, SharedPermitModule, RouterTestingModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
        declarations: [EmissionsComponent],
      }).compileComponents();
    });

    describe('for variation task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitType: 'HSE',
          determination: {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.year = '2022';
        page.emissions = '22222';

        page.continueButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: '1-1-2030',
              annualEmissionsTargets: { '2022': 22222 },
            },
            {
              determination: false,
            },
          ),
        );
        expect(navigateSpy).toHaveBeenCalledWith(['../log-changes'], { relativeTo: route });
      });
    });

    describe('for variation regulator led task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationRegulatorLedPayload,
          permitType: 'HSE',
          permitVariationDetails: {
            reason: 'reason',
            modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
          },
          permitVariationDetailsCompleted: true,
          determination: {
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.year = '2022';
        page.emissions = '22222';

        page.continueButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationRegulatorLedDeterminationPostBuild(
            {
              reason: 'reason',
              activationDate: '1-1-2030',
              annualEmissionsTargets: { '2022': 22222 },
            },
            {
              determination: false,
            },
          ),
        );
        expect(navigateSpy).toHaveBeenCalledWith(['../reason-template'], { relativeTo: route });
      });
    });
  });
});
