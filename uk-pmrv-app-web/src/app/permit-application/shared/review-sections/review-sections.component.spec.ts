import { Component, OnInit, PipeTransform } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ReviewGroupStatusWrapperPipe } from '@permit-application/shared/pipes/review-group-status-wrapper.pipe';
import {
  mockPermitCompletedAcceptedReviewGroupDecisions,
  mockPermitCompletedReviewSectionsCompleted,
  mockPermitCompletePayload,
} from '@permit-application/testing/mock-permit-apply-action';
import { mockReviewState } from '@permit-application/testing/mock-state';
import { ReviewGroupStatusPermitIssuancePipe } from '@permit-issuance/review/review-group-status-permit-issuance.pipe';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { TaskPipe } from '../pipes/task.pipe';
import { ReviewSectionsComponent } from './review-sections.component';

describe('ReviewSectionsComponent', () => {
  @Component({
    selector: 'app-test-component-wrapper',
    template: `<app-review-sections [statusResolverPipe]="statusResolverPipe"></app-review-sections>`,
  })
  class TestWrapperComponent implements OnInit {
    statusResolverPipe: PipeTransform;
    constructor(protected readonly store: PermitIssuanceStore) {}
    ngOnInit(): void {
      this.statusResolverPipe = new ReviewGroupStatusPermitIssuancePipe(this.store);
    }
  }

  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;
  let hostElement: HTMLElement;

  let store: PermitIssuanceStore;

  const createComponent = () => {
    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TestWrapperComponent,
        ReviewSectionsComponent,
        ReviewGroupStatusPermitIssuancePipe,
        ReviewGroupStatusWrapperPipe,
        TaskPipe,
      ],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('completed permit', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockReviewState,
        ...mockPermitCompletePayload,
        reviewSectionsCompleted: {
          ...mockPermitCompletedReviewSectionsCompleted,
          determination: false,
        },
        reviewGroupDecisions: mockPermitCompletedAcceptedReviewGroupDecisions,
        payloadType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD',
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display group sections', () => {
      expect(
        Array.from(hostElement.querySelectorAll('li[app-task-item] > span')).map((section) =>
          section.textContent.trim(),
        ),
      ).toEqual([
        'Permit type',
        'Installation details',
        'Fuels and equipment',
        'Define monitoring approaches',
        'Measurement of CO2',
        'Fallback approach',
        'Measurement of nitrous oxide (N2O)',
        'Calculation of perfluorocarbons (PFC)',
        'Inherent CO2 emissions',
        'Procedures for transferred CO2 or N2O',
        'Uncertainty analysis',
        'Management procedures',
        'Monitoring methodology',
        'Additional information',
        'Confidentiality',
      ]);

      expect(
        Array.from(hostElement.querySelectorAll('li[app-task-item] > govuk-tag')).map((section) =>
          section.textContent.trim(),
        ),
      ).toEqual([
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
        'accepted',
      ]);
    });
  });
});
