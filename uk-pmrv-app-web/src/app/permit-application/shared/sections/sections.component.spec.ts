import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';

import { PermitApplicationStore } from '../../store/permit-application.store';
import {
  mockPermitCompletedReviewSectionsCompleted,
  mockPermitCompletePayload,
} from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { TaskPipe } from '../pipes/task.pipe';
import { TaskStatusPipe } from '../pipes/task-status.pipe';
import { SectionsComponent } from './sections.component';

describe('SectionsComponent', () => {
  let component: SectionsComponent;
  let fixture: ComponentFixture<SectionsComponent>;
  let hostElement: HTMLElement;

  let store: PermitIssuanceStore;

  const createComponent = () => {
    fixture = TestBed.createComponent(SectionsComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SectionsComponent, TaskStatusPipe, TaskPipe],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TaskStatusPipe },
        { provide: TaskPipe },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('issuance submit', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        ...mockPermitCompletePayload,
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
      });
      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display sections', () => {
      expect(
        Array.from(hostElement.querySelectorAll('li[app-task-item] > span')).map((section) =>
          section.textContent.trim(),
        ),
      ).toEqual([
        'Define permit type',
        'Installation and operator details',
        'Other environmental permits or licences',
        'Description of the installation',
        'Regulated activities carried out at the installation',
        'Estimated annual CO2e',
        'Source streams (fuels and materials)',
        'Emission sources',
        'Emission points',
        'Emissions summaries and regulated activities',
        'Measurement devices or methods',
        'Site diagram',
        'Preparing to define monitoring approaches',
        'Define monitoring approaches',
        'Measurement of CO2',
        'Fallback approach',
        'Measurement of nitrous oxide (N2O)',
        'Calculation of perfluorocarbons (PFC)',
        'Inherent CO2 emissions',
        'Procedures for transferred CO2 or N2O',
        'Uncertainty analysis',
        'Monitoring and reporting roles',
        'Assignment of responsibilities',
        'Monitoring plan appropriateness',
        'Data flow activities',
        'Quality assurance of IT used for data flow activities',
        'Review and validation of data',
        'Assessing and controlling risks',
        'Quality assurance of metering and measuring equipment',
        'Corrections and corrective actions',
        'Control of outsourced activities',
        'Record keeping and documentation',
        'Environmental management system',
        'Monitoring Methodology Plan',
        'Abbreviations, acronyms and definitions',
        'Additional documents and information',
        'Confidentiality statement',
      ]);

      expect(
        Array.from(hostElement.querySelectorAll('li[app-task-item] > govuk-tag')).map((section) =>
          section.textContent.trim(),
        ),
      ).toEqual([
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
        'completed',
      ]);
    });
  });

  describe('issuance amend sections not started', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        ...mockPermitCompletePayload,
        reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              requiredChanges: [{ reason: 'Changes required' }],
            },
          },
        },
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'],
      });
      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display amend section', () => {
      expect(
        hostElement.querySelector('li[app-task-item][linktext="Amends needed for installation details"]'),
      ).toBeTruthy();

      expect(
        hostElement
          .querySelector('li[app-task-item][linktext="Amends needed for installation details"] govuk-tag')
          .textContent.trim(),
      ).toEqual('not started');
    });
  });

  describe('issuance amend sections completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        ...mockPermitCompletePayload,
        permitSectionsCompleted: {
          ...mockPermitCompletePayload.permitSectionsCompleted,
          AMEND_details: [true],
        },
        reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              requiredChanges: [{ reason: 'Changes required' }],
            },
          },
        },
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'],
      });
      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display amend section', () => {
      expect(
        hostElement.querySelector('li[app-task-item][linktext="Amends needed for installation details"]'),
      ).toBeTruthy();

      expect(
        hostElement
          .querySelector('li[app-task-item][linktext="Amends needed for installation details"] govuk-tag')
          .textContent.trim(),
      ).toEqual('completed');
    });
  });
});
