import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { SummaryDetailsComponent as PermitVariationSummaryDetails } from '../../../../permit-variation/shared/review/determination/summary-details.component';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { ReviewDeterminationSummaryDetailsComponent } from '../../../shared/review-determination-summary-details/summary-details.component';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewStateBuild } from '../../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [SummaryComponent, ReviewDeterminationSummaryDetailsComponent, PermitVariationSummaryDetails],
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

    describe('for determination summary', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(
          mockReviewStateBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            },
            {
              determination: true,
            },
          ),
        );
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display the determination summary', () => {
        expect(page.summaryDefinitions).toEqual(['Grant', 'Change', 'reason', 'Change', '1 Jan 2030', 'Change']);
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [SummaryComponent, ReviewDeterminationSummaryDetailsComponent, PermitVariationSummaryDetails],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    describe('for determination summary for GRANT for variation task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          determination: {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            logChanges: 'log changes',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should display the determination summary', () => {
        expect(page.summaryDefinitions).toEqual([
          'Approve',
          'Change',
          'reason',
          'Change',
          '1 Jan 2030',
          'Change',
          'log changes',
          'Change',
        ]);
      });
    });

    describe('for determination for variation regulator led task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationRegulatorLedPayload,
          permitVariationDetails: {
            reason: 'reason',
            modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
          },
          permitVariationDetailsCompleted: true,
          determination: {
            reason: 'reason',
            activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            reasonTemplate: 'OTHER',
            reasonTemplateOtherSummary: 'other reason',
            logChanges: 'logChanges',
          },
          reviewSectionsCompleted: { determination: true },
        });
      });
      beforeEach(createComponent);

      it('should display the determination', () => {
        expect(page.summaryDefinitions).toEqual([
          'reason',
          'Change',
          '1 Jan 2030',
          'Change',
          'other reason',
          'Change',
          'logChanges',
          'Change',
        ]);
      });
    });
  });
});
