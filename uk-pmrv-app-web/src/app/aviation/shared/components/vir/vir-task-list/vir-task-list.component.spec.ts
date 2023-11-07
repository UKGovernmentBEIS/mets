import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VirTaskListComponent } from '@aviation/shared/components/vir/vir-task-list/vir-task-list.component';
import { BasePage } from '@testing';

describe('VirTaskListComponent', () => {
  let page: Page;
  let component: VirTaskListComponent;
  let fixture: ComponentFixture<VirTaskListComponent>;

  class Page extends BasePage<VirTaskListComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
    get headers(): HTMLHeadingElement[] {
      return Array.from(this.queryAll<HTMLHeadingElement>('h2'));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(VirTaskListComponent);
    component = fixture.componentInstance;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskListComponent, RouterTestingModule],
    }).compileComponents();
  });

  describe('for submit', () => {
    beforeEach(() => {
      createComponent();

      component.virPayload = {
        payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
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
        virSectionsCompleted: {},
      };

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
        'Respond to recommendation',
        'Respond to recommendation',
        'Respond to recommendation',
        'Send to the regulator',
      ]);

      expect(page.headers).toHaveLength(4);
      expect(page.headers.map((el) => el.textContent.trim())).toEqual([
        'B1: an uncorrected error in the monitoring plan',
        'D1: recommended improvement',
        'E1: an unresolved breach from a previous year',
        'Submit',
      ]);
    });
  });

  describe('for review', () => {
    beforeEach(() => {
      createComponent();

      component.virPayload = {
        payloadType: 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD',
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
            isAddressed: true,
            addressedDescription: 'Test description B1, when yes',
            addressedDate: '2023-12-01',
            uploadEvidence: false,
            files: [],
          },
          D1: {
            isAddressed: true,
            addressedDescription: 'Test description B1, when yes',
            addressedDate: '2023-12-01',
            uploadEvidence: false,
            files: [],
          },
          E1: {
            isAddressed: true,
            addressedDescription: 'Test description B1, when yes',
            addressedDate: '2023-12-01',
            uploadEvidence: false,
            files: [],
          },
        },
        reviewSectionsCompleted: {},
      };

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show task list details', () => {
      expect(page.sections).toHaveLength(5);
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Respond to operator',
        'Respond to operator',
        'Respond to operator',
        'Create summary',
        'Send to the operator',
      ]);

      expect(page.headers).toHaveLength(5);
      expect(page.headers.map((el) => el.textContent.trim())).toEqual([
        'B1: an uncorrected error in the monitoring plan',
        'D1: recommended improvement',
        'E1: an unresolved breach from a previous year',
        'Create report summary',
        'Submit',
      ]);
    });
  });

  describe('for respond', () => {
    beforeEach(() => {
      createComponent();

      component.virPayload = {
        payloadType: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
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
        operatorImprovementFollowUpResponses: {},
        virRespondToRegulatorCommentsSectionsCompleted: {},
      };

      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show task list details', () => {
      expect(page.sections).toHaveLength(6);
      expect(page.sections.map((el) => el.textContent.trim())).toEqual([
        'Respond to the Regulator',
        'Send to the regulator',
        'Respond to the Regulator',
        'Send to the regulator',
        'Respond to the Regulator',
        'Send to the regulator',
      ]);

      expect(page.headers).toHaveLength(6);
      expect(page.headers.map((el) => el.textContent.trim())).toEqual([
        'B1: an uncorrected error in the monitoring plan',
        'B1: submit',
        'D1: recommended improvement',
        'D1: submit',
        'E1: an unresolved breach from a previous year',
        'E1: submit',
      ]);
    });
  });
});
