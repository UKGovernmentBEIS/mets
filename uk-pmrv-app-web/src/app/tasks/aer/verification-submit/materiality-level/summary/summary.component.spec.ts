import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { SummaryComponent } from '@tasks/aer/verification-submit/materiality-level/summary/summary.component';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.summaryListValues).toEqual([
      [
        'Materiality details',
        'Change',
        'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive',
        'Change',
      ],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(null, { materialityLevel: [true] }),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });

  it('should submit and navigate to task list after 2025', () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            isVerifierAerTaskContentUpdate: true,
          },
        },
      },
    });

    fixture.detectChanges();

    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.summaryListValues).toEqual([
      [
        "To verify the Operator's or Aircraft Operator's annual emissions to a reasonable level of assurance for the Annual Emissions Report (as summarised in the Opinion Statement) under UK ETS and confirm compliance with approved monitoring requirements, approved emissions monitoring plan (EMP), or approved monitoring plan and the Monitoring and Reporting Regulation 2018 (MRR) as amended by the Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) (“the Order”).",
        '',
        "The Operator or Aircraft Operator is solely responsible for: the preparation and reporting of their annual greenhouse gas (GHG) emissions for the purposes of UK ETS in accordance with the rules and their approved monitoring plan or EMP (as listed in the Opinion Statement); for any information and assessments that support the reported data; for determining the Operator's or Aircraft Operator's objectives in relation to GHG information and for establishing and maintaining appropriate procedures, performance management and internal control systems from which the reported information is derived. The regulator is responsible for: issuing and varying applicable monitoring plans or EMPs to Operators or Aircraft Operators respectively; enforcing the requirements of the MRR as amended by the Order and the Permit or EMP Conditions;agreeing certain aspects of the verification process, e.g. site visit waivers. Under certain circumstances, the regulator may in accordance with Article 45 of the Order determine an Operator's reportable emissions or Aircraft Operator's aviation emissions for the purposes of UK ETS.  We (as named on the Opinion Statement), in accordance with the verification contract and the Verification Regulation 2018 (AVR) as amended by the Order, are responsible for carrying out the verification of an Operator's or Aircraft Operator's annual CO2 emissions associated with its regulated activity or aviation activities respectively, independent of the Operator or Aircraft Operator and the regulator responsible for UK ETS. It is our responsibility to form an independent opinion, based on the examination of information and data presented in the Annual Emissions Report, and to report that opinion to the Operator or Aircraft Operator. We also report if, in our opinion: the Annual Emissions Report is or may be associated with misstatements (omissions, misrepresentations, or errors) or non-conformities; or  the Operator or Aircraft Operator is not complying with the MRR as amended by the Order, even if the monitoring plan or EMP is approved by the regulator.  the UK ETS lead auditor/auditor has not received all the information and explanations that they require to conduct their examination to a reasonable level of assurance; or  improvements can be made to the Operator's or Aircraft Operator's performance in monitoring and reporting of emissions and/or compliance with the approved monitoring plan or EMP and MRR as amended by the Order.",
        '',
        "We conducted our examination having regard to the verification criteria reference documents outlined below. This involved examining, based upon our risk analysis, evidence to give us reasonable assurance that the amounts and disclosures relating to the data have been properly prepared in accordance with the Order and principles of UK ETS, as outlined in the UK ETS criteria reference documents below, and the Operator's approved monitoring plan or Aircraft Operator's approved EMP. This also involved assessing where necessary estimates and judgements made by the Operator or Aircraft Operator in preparing the data and considering the overall adequacy of the presentation of the data in the Annual Emissions Report and its potential for material misstatement.",
        '',
        'Materiality details  GHG quantification is subject to inherent uncertainty due to the designed capability of measurement instrumentation and testing methodologies and incomplete scientific knowledge used in the determination of emissions factors and global warming potentials.',
        'Change',
        'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive',
        'Change',
      ],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(null, { materialityLevel: [true] }),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
