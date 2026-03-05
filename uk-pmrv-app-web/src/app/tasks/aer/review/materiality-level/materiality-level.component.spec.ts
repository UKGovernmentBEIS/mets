import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { MaterialityLevelComponent } from '@tasks/aer/review/materiality-level/materiality-level.component';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('MaterialityLevelComponent', () => {
  let component: MaterialityLevelComponent;
  let fixture: ComponentFixture<MaterialityLevelComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MATERIALITY_LEVEL',
    },
  );

  class Page extends BasePage<MaterialityLevelComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get sections(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('dl');
    }

    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MaterialityLevelComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Materiality level and reference documents');
    expect(
      page.sections.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent.trim())),
    ).toEqual([
      [
        'Materiality details',
        'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive',
      ],
      ['Accepted', 'Notes'],
    ]);
  });

  it('should render the review groups after 2025', () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestInfo: {
          ...mockState.requestTaskItem.requestInfo,
          requestMetadata: {
            ...mockState.requestTaskItem.requestInfo.requestMetadata,
            year: 2025,
          },
        },
      },
    });
    fixture.detectChanges();

    expect(page.heading).toEqual('Further information of relevance to the opinion');
    expect(page.summaryValues).toEqual([
      [
        'Objectives and scope of the Verification',
        "To verify the Operator's or Aircraft Operator's annual emissions to a reasonable level of assurance for the Annual Emissions Report (as summarised in the Opinion Statement) under UK ETS and confirm compliance with approved monitoring requirements, approved EMP, or approved monitoring plan and the MRR as amended by the Order.",
      ],
      [
        'Responsibilities',
        "The Operator or Aircraft Operator is solely responsible for the preparation and reporting of their annual greenhouse gas (GHG) emissions for the purposes of UK ETS in accordance with the rules and their approved monitoring plan or EMP (as listed in the Opinion Statement); for any information and assessments that support the reported data; for determining the Operator's or Aircraft Operator's objectives in relation to GHG information and for establishing and maintaining appropriate procedures, performance management and internal control systems from which the reported information is derived. The regulator is responsible for: issuing and varying applicable monitoring plans or EMPs to Operators or Aircraft Operators respectively; enforcing the requirements of the MRR as amended by the Order and the Permit or EMP Conditions; agreeing certain aspects of the verification process, e.g. site visit waivers. Under certain circumstances, the regulator may in accordance with Article 45 of the Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) determine an Operator's or Aircraft Operator's emissions for the purposes of UK ETS.  We (as named on the Opinion Statement), in accordance with the verification contract and the AVR as amended by the Order, are responsible for carrying out the verification of an Operator's or Aircraft Operator's annual CO2 emissions associated with its regulated activity or aviation activities respectively, independent of the Operator or Aircraft Operator and the regulator responsible for UK ETS. It is our responsibility to form an independent opinion, based on the examination of information and data presented in the Annual Emissions Report, and to report that opinion to the Operator or Aircraft Operator. We also report if, in our opinion: the Annual Emissions Report is or may be associated with misstatements (omissions, misrepresentations, or errors) or non-conformities; or  the Operator or Aircraft Operator is not complying with the MRR as amended by the Order, even if the monitoring plan or EMP is approved by the regulator.  the UK ETS lead auditor/auditor has not received all the information and explanations that they require to conduct their examination to a reasonable level of assurance; or  improvements can be made to the Operator's or Aircraft Operator's performance in monitoring and reporting of emissions and/or compliance with the approved monitoring plan or EMP and MRR as amended by the Order.",
      ],
      [
        'Work performed & basis of the opinion',
        "We conducted our examination having regard to the verification criteria reference documents outlined below. This involved examining, based upon our risk analysis, evidence to give us reasonable assurance that the amounts and disclosures relating to the data have been properly prepared in accordance with the Order and principles of UK ETS, as outlined in the UK ETS criteria reference documents below, and the Operator's approved monitoring plan or Aircraft Operator's approved EMP. This also involved assessing where necessary estimates and judgements made by the Operator or Aircraft Operator in preparing the data and considering the overall adequacy of the presentation of the data in the Annual Emissions Report and its potential for material misstatement.",
      ],
      [
        'Materiality level',
        'Materiality details  GHG quantification is subject to inherent uncertainty due to the designed capability of measurement instrumentation and testing methodologies and incomplete scientific knowledge used in the determination of emissions factors and global warming potentials.',
      ],
      [
        'Accreditation reference documents',
        'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive',
      ],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
