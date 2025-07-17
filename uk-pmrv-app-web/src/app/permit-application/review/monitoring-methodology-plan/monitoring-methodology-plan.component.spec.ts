import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { ReviewModule } from '../review.module';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan.component';

describe('MonitoringMethodologyPlanComponent', () => {
  let component: MonitoringMethodologyPlanComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MONITORING_METHODOLOGY_PLAN',
    },
  );

  @Component({
    selector: 'app-review-group-decision-container',
    template: `
      <div>
        Review group decision component.
        <div>Key:{{ groupKey }}</div>
        <div>Can edit:{{ canEdit }}</div>
      </div>
    `,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<MonitoringMethodologyPlanComponent> {
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
    get file() {
      const roles = this.queryAll<HTMLDListElement>('dd');
      return roles.slice(roles.length - 1)[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, ReviewModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
      declarations: [MonitoringMethodologyPlanComponent, MockDecisionComponent],
    }).compileComponents();
  });

  describe('without review group decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({ ...mockReviewState });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should mention the attachment', () => {
      expect(page.file.textContent.trim()).toEqual('No');
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Monitoring methodology plan', 'not started']]);
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Monitoring methodology plan', 'not started']]);
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);

      store.setState({
        ...mockReviewState,
        permit: {
          ...store.getState().permit,
          monitoringMethodologyPlans: {
            exist: true,
            digitizedPlan: {
              subInstallations: [
                {
                  subInstallationNo: '0',
                  subInstallationType: 'AROMATICS',
                  description: 'description',
                  annualLevel: undefined,
                },
              ],
              methodTask: {
                physicalPartsAndUnitsAnswer: true,
                connections: [
                  {
                    itemId: '0',
                    itemName: 'itemName1',
                    subInstallations: ['AROMATICS', 'REFINERY_PRODUCTS', 'ADIPIC_ACID'],
                  },
                  {
                    itemId: '1',
                    itemName: 'itemName2',
                    subInstallations: ['ADIPIC_ACID', 'HEAT_BENCHMARK_NON_CL'],
                  },
                ],
                assignParts: 'assignParts',
                avoidDoubleCount: 'avoidDoubleCount',
                avoidDoubleCountToggle: true,
              },
              procedures: {
                CONTROL_ACTIVITIES: {
                  procedureName: 'control activities procedureName',
                  procedureReference: 'control activities procedureReference',
                },
                DATA_FLOW_ACTIVITIES: {
                  procedureName: 'data flow activities procedureName',
                  procedureReference: 'data flow activities procedureReference',
                },
                ASSIGNMENT_OF_RESPONSIBILITIES: {
                  procedureName: 'assignment of responsibilities procedureName',
                  procedureReference: 'assignment of responsibilities procedureReference',
                },
                MONITORING_PLAN_APPROPRIATENESS: {
                  procedureName: 'monitoring pla appropriateness procedureName',
                  procedureReference: 'monitoring pla appropriateness procedureReference',
                },
              },
              energyFlows: {
                fuelInputFlows: {
                  fuelInputDataSources: [
                    {
                      dataSourceNumber: '0',
                      fuelInput: 'NOT_OPERATOR_CONTROL_NOT_POINT_B',
                      energyContent: 'CALCULATION_METHOD_MONITORING_PLAN',
                    },
                  ],
                  methodologyAppliedDescription: 'Pariatur Et deserun',
                  hierarchicalOrder: {
                    followed: true,
                  },
                },
                measurableHeatFlows: {
                  measurableHeatFlowsRelevant: false,
                },
                wasteGasFlows: {
                  wasteGasFlowsRelevant: false,
                },
                electricityFlows: {
                  electricityProduced: true,
                  electricityFlowsDataSources: [
                    {
                      quantification: 'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
                      dataSourceNumber: '0',
                    },
                  ],
                  methodologyAppliedDescription: 'jjlkjlkjkljlkjlk jlkjkljlk',
                  hierarchicalOrder: {
                    followed: false,
                    notFollowingHierarchicalOrderReason: 'USE_OF_BETTER_DATA_SOURCES_UNREASONABLE_COSTS',
                    notFollowingHierarchicalOrderDescription: 'kl;ljkjkljklkjljkl',
                  },
                  supportingFiles: ['ed2b9858-664f-40a1-88ec-21394f9f745f'],
                },
              },
            },
          },
        },
        features: { ['digitized-mmp']: true } as any,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
        },
        permitSectionsCompleted: {
          mmpMethods: [true],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review for mmp', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Monitoring methodology plan', 'not started'],
        ['Installation description', 'not started'],
        ['Sub-installations', 'in progress'],
        ['Methods', 'needs review'],
        ['Procedures', 'in progress'],
        ['Energy flows', 'in progress'],
      ]);
    });

    it('should display the table preview from the sub-installations review', () => {
      expect(page.reviewSections[2].querySelector('table').textContent.trim()).toEqual(
        'Sub-installation typeCarbon leakage Aromatics  Exposed  in progress',
      );
    });

    it('should display the table preview from the methods review', () => {
      expect(page.reviewSections[3].querySelector('.govuk-summary-list').textContent.trim()).toEqual(
        'Physical parts of the installation and units which serve more than one sub-installation  Are there any physical parts of the installation or units which serve more than one sub-installation? YesPhysical part of the installation or unitEmission sources itemName1  Aromatics  Refinery products  Adipic acid  Remove  Change  itemName2  Adipic acid  Heat benchmark not exposed to carbon leakage  Remove  Change  Add an item  Methods used to assign parts of installations and their emissions to sub-installations assignPartsData gaps and double countingMethods used for ensuring that data gaps and double counting are avoidedavoidDoubleCount',
      );
    });

    it('should display the procedures preview', () => {
      expect(page.reviewSections[4].querySelector('.govuk-summary-list').textContent.trim()).toEqual(
        'Assignment of responsibilitiesName of the procedureassignment of responsibilities procedureNameProcedure referenceassignment of responsibilities procedureReferenceDiagram referenceProcedure description  Department or role responsible for data maintenanceLocation of recordsIT system usedList of EN or other standards appliedMonitoring plan appropriatenessName of the proceduremonitoring pla appropriateness procedureNameProcedure referencemonitoring pla appropriateness procedureReferenceDiagram referenceProcedure description  Department or role responsible for data maintenanceLocation of recordsIT system usedList of EN or other standards appliedData flow activitiesName of the proceduredata flow activities procedureNameProcedure referencedata flow activities procedureReferenceDiagram referenceProcedure description  Department or role responsible for data maintenanceLocation of recordsIT system usedList of EN or other standards appliedControl activitiesName of the procedurecontrol activities procedureNameProcedure referencecontrol activities procedureReferenceDiagram referenceProcedure description  Department or role responsible for data maintenanceLocation of recordsIT system usedList of EN or other standards applied',
      );
    });

    it('should display the energy flows preview', () => {
      expect(page.reviewSections[5].querySelector('.govuk-summary-list').textContent.trim()).toEqual(
        "Fuel input flowData Source 1 Fuel input:  4.4.(d) Readings of measuring instruments not under the operators control for direct determination of a data set not falling under point b  Energy content:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012 Description of methodology applied for each data source Pariatur Et deserun Has the hierarchical order been followed? Yes Measurable heat flows of imports, exports, consumption and productionAre measurable heat flows relevant for the installation? No Waste gas flows of imports, exports, consumption and productionAre waste gas flows relevant for the installation? No Electricity flows of imports, exports, consumption and productionIs electricity produced within the installation? Yes Data Source 1 Quantification of energy flows:  4.5.(b) Readings of measuring instruments under the operator's control for direct determination of a data set not falling under point a Description of methodology applied for each data source jjlkjlkjkljlkjlk jlkjkljlk Has the hierarchical order been followed? No  Use of better data sources would incur unreasonable costs Details on any deviation from the hierarchy kl;ljkjkljklkjljkl Supporting files",
      );
    });
  });
});
