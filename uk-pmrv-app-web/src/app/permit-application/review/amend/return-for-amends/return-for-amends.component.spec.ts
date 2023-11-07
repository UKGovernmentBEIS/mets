import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewState } from '../../../testing/mock-state';
import { ReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let page: Page;
  let component: ReturnForAmendsComponent;
  let fixture: ComponentFixture<ReturnForAmendsComponent>;
  let route: ActivatedRouteStub;
  let router: Router;
  let tasksService: MockType<TasksService>;

  class Page extends BasePage<ReturnForAmendsComponent> {
    get summary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('issuance request', () => {
    let store: PermitIssuanceStore;

    beforeEach(async () => {
      tasksService = {
        processRequestTaskAction: jest.fn().mockReturnValue(of({})),
      };
      route = new ActivatedRouteStub({ taskId: '237' });

      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [ReturnForAmendsComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();

      await TestBed.configureTestingModule({
        declarations: [ReturnForAmendsComponent],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          ADDITIONAL_INFORMATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              notes: 'notes',
              requiredChanges: [
                {
                  reason: 'reason',
                },
              ],
            },
          },
          INSTALLATION_DETAILS: {
            type: 'ACCEPTED',
            notes: 'notes',
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the amends, submit and navigate to confirmation', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.summary).toEqual([
        ['Changes required', '1. reason'],
        ['Notes', 'notes'],
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['./confirmation'], { relativeTo: route });
    });
  });

  describe('variation request', () => {
    let store: PermitVariationStore;

    beforeEach(async () => {
      tasksService = {
        processRequestTaskAction: jest.fn().mockReturnValue(of({})),
      };
      route = new ActivatedRouteStub({ taskId: '237' });

      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [ReturnForAmendsComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();

      await TestBed.configureTestingModule({
        declarations: [ReturnForAmendsComponent],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        daysRemaining: 13,
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
        ],
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
      });
    });

    beforeEach(createComponent);

    it('should display the variation details amends', () => {
      expect(page.summary).toEqual([
        ['Changes required', '1. reason'],
        ['Notes', 'notes'],
      ]);
    });
  });
});
