import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationAerMaterialityLevel } from 'pmrv-api';

import { AerVerifyMaterialityLevelGroupComponent } from './materiality-level-group.component';

describe('AerVerifyMaterialityLevelGroupComponent', () => {
  let component: AerVerifyMaterialityLevelGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    standalone: false,
    template: `
      <app-aer-verify-materiality-level-group
        [isEditable]="isEditable"
        [materialityLevel]="materialityLevel"
        [queryParams]="queryParams"
        [isMaterialityUpdated]="isMaterialityUpdated"></app-aer-verify-materiality-level-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    materialityLevel = {
      materialityDetails: 'Materiality details',
      accreditationReferenceDocumentTypes: ['UK_ETS_ACCREDITED_VERIFIERS_AUTHORITY_GUIDANCE', 'OTHER'],
      otherReference: 'Other type',
    } as AviationAerMaterialityLevel;
    queryParams = { change: true };
    isMaterialityUpdated = false;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerVerifyMaterialityLevelGroupComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AerVerifyMaterialityLevelGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Materiality level', 'Accreditation reference documents'],
        [
          'Materiality details',
          '',
          `Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation  Other type`,
          '',
        ],
      ],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Materiality level', 'Accreditation reference documents'],
        [
          'Materiality details',
          'Change',
          `Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation  Other type`,
          'Change',
        ],
      ],
    ]);
  });

  it('should render the review groups after 2025', () => {
    hostComponent.isMaterialityUpdated = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Objectives and scope of the Verification',
          'Responsibilities',
          'Work performed & basis of the opinion',
          'Materiality level',
          'Accreditation reference documents',
        ],
        [
          "To verify the Operator's or Aircraft Operator's annual emissions to a reasonable level of assurance for the Annual Emissions Report (as summarised in the Opinion Statement) under UK ETS and confirm compliance with approved monitoring requirements, approved emissions monitoring plan (EMP), or approved monitoring plan and the Monitoring and Reporting Regulation 2018 (MRR) as amended by the Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) (“the Order”).",
          '',
          "The Operator or Aircraft Operator is solely responsible for: the preparation and reporting of their annual greenhouse gas (GHG) emissions for the purposes of UK ETS in accordance with the rules and their approved monitoring plan or EMP (as listed in the Opinion Statement); for any information and assessments that support the reported data; for determining the Operator's or Aircraft Operator's objectives in relation to GHG information and for establishing and maintaining appropriate procedures, performance management and internal control systems from which the reported information is derived. The regulator is responsible for: issuing and varying applicable monitoring plans or EMPs to Operators or Aircraft Operators respectively; enforcing the requirements of the MRR as amended by the Order and the Permit or EMP Conditions;agreeing certain aspects of the verification process, e.g. site visit waivers. Under certain circumstances, the regulator may in accordance with Article 45 of the Order determine an Operator's reportable emissions or Aircraft Operator's aviation emissions for the purposes of UK ETS.  We (as named on the Opinion Statement), in accordance with the verification contract and the Verification Regulation 2018 (AVR) as amended by the Order, are responsible for carrying out the verification of an Operator's or Aircraft Operator's annual CO2 emissions associated with its regulated activity or aviation activities respectively, independent of the Operator or Aircraft Operator and the regulator responsible for UK ETS. It is our responsibility to form an independent opinion, based on the examination of information and data presented in the Annual Emissions Report, and to report that opinion to the Operator or Aircraft Operator. We also report if, in our opinion: the Annual Emissions Report is or may be associated with misstatements (omissions, misrepresentations, or errors) or non-conformities; or  the Operator or Aircraft Operator is not complying with the MRR as amended by the Order, even if the monitoring plan or EMP is approved by the regulator.  the UK ETS lead auditor/auditor has not received all the information and explanations that they require to conduct their examination to a reasonable level of assurance; or  improvements can be made to the Operator's or Aircraft Operator's performance in monitoring and reporting of emissions and/or compliance with the approved monitoring plan or EMP and MRR as amended by the Order.",
          '',
          "We conducted our examination having regard to the verification criteria reference documents outlined below. This involved examining, based upon our risk analysis, evidence to give us reasonable assurance that the amounts and disclosures relating to the data have been properly prepared in accordance with the Order and principles of UK ETS, as outlined in the UK ETS criteria reference documents below, and the Operator's approved monitoring plan or Aircraft Operator's approved EMP. This also involved assessing where necessary estimates and judgements made by the Operator or Aircraft Operator in preparing the data and considering the overall adequacy of the presentation of the data in the Annual Emissions Report and its potential for material misstatement.",
          '',
          'Materiality details  GHG quantification is subject to inherent uncertainty due to the designed capability of measurement instrumentation and testing methodologies and incomplete scientific knowledge used in the determination of emissions factors and global warming potentials.',
          '',
          `Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation  Other type`,
          '',
        ],
      ],
    ]);
  });
});
