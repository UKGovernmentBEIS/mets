import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirActionTaskListComponent } from '@aviation/shared/components/vir/vir-action-task-list/vir-action-task-list.component';
import { BasePage } from '@testing';

import { AviationVirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

describe('VirActionTaskListComponent', () => {
  let page: Page;
  let component: VirActionTaskListComponent;
  let fixture: ComponentFixture<VirActionTaskListComponent>;

  class Page extends BasePage<VirActionTaskListComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
    get headers(): HTMLHeadingElement[] {
      return Array.from(this.queryAll<HTMLHeadingElement>('h2'));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(VirActionTaskListComponent);
    component = fixture.componentInstance;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionTaskListComponent, RouterTestingModule],
    }).compileComponents();
  });

  describe('for submitted', () => {
    beforeEach(() => {
      createComponent();

      component.virPayload = {
        payloadType: 'AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD',
        reportingYear: 2022,
        verificationData: {
          uncorrectedNonConformities: {
            B1: {
              reference: 'B1',
              explanation: 'Test uncorrectedNonConformity',
              materialEffect: true,
            },
          },
          recommendedImprovements: {
            D1: {
              reference: 'D1',
              explanation: 'Test recommended improvement',
            },
          },
          priorYearIssues: {
            E1: {
              reference: 'E1',
              explanation: 'Test priorYearIssue',
            },
          },
        },
        operatorImprovementResponses: {},
      } as AviationVirApplicationSubmittedRequestActionPayload;

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show task list details', () => {
      expect(page.sections).toHaveLength(3);
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Respond to recommendation',
        'Respond to recommendation',
        'Respond to recommendation',
      ]);

      expect(page.headers).toHaveLength(3);
      expect(page.headers.map((el) => el.textContent.trim())).toEqual([
        'B1: an uncorrected error in the monitoring plan',
        'D1: recommended improvement',
        'E1: an unresolved breach from a previous year',
      ]);
    });
  });

  describe('for reviewed', () => {
    beforeEach(() => {
      createComponent();

      component.virPayload = {
        payloadType: 'AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD',
        reportingYear: 2022,
        verificationData: {
          uncorrectedNonConformities: {
            B1: {
              reference: 'B1',
              explanation: 'Test uncorrectedNonConformity',
              materialEffect: true,
            },
          },
          recommendedImprovements: {
            D1: {
              reference: 'D1',
              explanation: 'Test recommended improvement',
            },
          },
          priorYearIssues: {
            E1: {
              reference: 'E1',
              explanation: 'Test priorYearIssue',
            },
          },
        },
        operatorImprovementResponses: {
          B1: {
            isAddressed: false,
            addressedDescription: 'Test description B1, when no',
            uploadEvidence: false,
            files: [],
          },
          D1: {
            isAddressed: false,
            addressedDescription: 'Test description D1, when no',
            uploadEvidence: false,
            files: [],
          },
          E1: {
            isAddressed: false,
            addressedDescription: 'Test description E1, when no',
            uploadEvidence: false,
            files: [],
          },
        },
        regulatorReviewResponse: {
          regulatorImprovementResponses: {
            B1: {
              improvementRequired: true,
              improvementDeadline: '2023-12-01',
              improvementComments: 'Test improvement comments B1',
              operatorActions: 'Test operator actions B1',
            },
            D1: {
              improvementRequired: true,
              improvementDeadline: '2023-12-01',
              improvementComments: 'Test improvement comments D1',
              operatorActions: 'Test operator actions D1',
            },
            E1: {
              improvementRequired: true,
              improvementDeadline: '2023-12-01',
              improvementComments: 'Test improvement comments E1',
              operatorActions: 'Test operator actions E1',
            },
          },
          reportSummary: 'Test summary',
        },
      } as AviationVirApplicationSubmittedRequestActionPayload;

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show task list details', () => {
      expect(page.sections).toHaveLength(4);
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Respond to operator',
        'Respond to operator',
        'Respond to operator',
        'Create summary',
      ]);

      expect(page.headers).toHaveLength(4);
      expect(page.headers.map((el) => el.textContent.trim())).toEqual([
        'B1: an uncorrected error in the monitoring plan',
        'D1: recommended improvement',
        'E1: an unresolved breach from a previous year',
        'Create report summary',
      ]);
    });
  });
});
