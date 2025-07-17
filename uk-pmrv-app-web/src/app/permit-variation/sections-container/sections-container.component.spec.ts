import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { TaskStatusPipe } from '../../permit-application/shared/pipes/task-status.pipe';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { SharedModule } from '../../shared/shared.module';
import { PermitVariationStore } from '../store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../testing/mock';
import { SectionsContainerComponent } from './sections-container.component';

describe('SectionsContainerComponent', () => {
  let component: SectionsContainerComponent;
  let fixture: ComponentFixture<SectionsContainerComponent>;
  let hostElement: HTMLElement;

  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);

  let store: PermitVariationStore;

  @Component({
    selector: 'app-sections',
    template: `
      permit sections
    `,
  })
  class MockPermitSectionsComponent {}

  const createComponent = () => {
    fixture = TestBed.createComponent(SectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SectionsContainerComponent, MockPermitSectionsComponent, TaskStatusPipe],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: TaskStatusPipe },
        {
          provide: PermitApplicationStore,
          useValue: store,
        },
      ],
    }).compileComponents();
  });

  describe('variation operator led submit initial', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION'],
      });

      requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
      requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to your GHGE permit',
      );
    });

    it('should display about variation', () => {
      expect(hostElement.querySelector('li[title="Variation"] h2').textContent).toEqual('Variation');
      expect(hostElement.querySelector('li[title="Variation"] ul govuk-tag').textContent.trim()).toEqual('not started');
    });

    it('should not display submit link', () => {
      expect(hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li a')).toBeNull();
      expect(
        hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
      ).toEqual('cannot start yet');
    });
  });

  describe('variation operator led submit completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION'],
      });

      requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
      requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to your GHGE permit',
      );
    });

    it('should display about variation', () => {
      expect(hostElement.querySelector('li[title="Variation"] h2').textContent).toEqual('Variation');
      expect(hostElement.querySelector('li[title="Variation"] ul govuk-tag').textContent.trim()).toEqual('completed');
    });

    it('should display submit link', () => {
      expect(hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li a')).toBeTruthy();
      expect(
        hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
      ).toEqual('not started');
    });
  });

  describe('variation operator led amend needed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: false,
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION_AMEND'],
      });

      requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
      requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to your GHGE permit',
      );
    });

    it('should display about variation amend', () => {
      expect(
        hostElement.querySelector('li[linktext="Amends needed for about the variation"]').textContent,
      ).toBeTruthy();

      expect(
        hostElement.querySelector('li[linktext="Amends needed for about the variation"] govuk-tag').textContent.trim(),
      ).toEqual('not started');
    });

    it('should not display submit link', () => {
      expect(
        hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
      ).toEqual('cannot start yet');
    });
  });

  describe('variation operator led amend needed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: true,
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION_AMEND'],
      });

      requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
      requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to your GHGE permit',
      );
    });

    it('should display about variation amend', () => {
      expect(
        hostElement.querySelector('li[linktext="Amends needed for about the variation"]').textContent,
      ).toBeTruthy();

      expect(
        hostElement.querySelector('li[linktext="Amends needed for about the variation"] govuk-tag').textContent.trim(),
      ).toEqual('completed');
    });

    it('should display submit link', () => {
      expect(
        hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
      ).toEqual('not started');
    });
  });
});
