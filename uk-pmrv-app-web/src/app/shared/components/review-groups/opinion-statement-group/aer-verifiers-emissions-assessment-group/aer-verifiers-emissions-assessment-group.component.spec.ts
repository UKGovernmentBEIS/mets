import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerVerifiersEmissionsAssessmentGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-verifiers-emissions-assessment-group/aer-verifiers-emissions-assessment-group.component';
import { SharedModule } from '@shared/shared.module';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';

describe('AerVerifiersEmissionsAssessmentGroupComponent', () => {
  let page: Page;
  let component: AerVerifiersEmissionsAssessmentGroupComponent;
  let fixture: ComponentFixture<AerVerifiersEmissionsAssessmentGroupComponent>;

  const mockOpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;

  const expectedCells = [
    [],
    ['Calculation of combustion emissions', '1.11111 t', '11.11111 t', 'Change'],
    ['Calculation of process emissions', '2.22222 t', '22.22222 t', 'Change'],
    ['Calculation of mass balance emissions', '3 t', '33 t', 'Change'],
    ['Calculation of transferred CO2 emissions', '4 t', '44 t', 'Change'],
    ['Measurement of CO2 emissions', '5 t', '55 t', 'Change'],
    ['Measurement of transferred CO2 emissions', '6 t', '66 t', 'Change'],
    ['Measurement of N2O emissions', '7 t', '77 t', 'Change'],
    ['Measurement of transferred N2O emissions', '8 t', '88 t', 'Change'],
    ['Calculation of PFC emissions', '9 t', '', 'Change'],
    ['Inherent CO2 emissions', '10 t', '', 'Change'],
    ['Fallback emissions', '11 t', '1111 t', 'Change'],
    ['Total', '66.33333 tCO2e', '1507.33333 tCO2e', ''],
  ];

  class Page extends BasePage<AerVerifiersEmissionsAssessmentGroupComponent> {
    get heading2(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h2');
    }

    get tableCells() {
      return Array.from(this.queryAll<HTMLTableRowElement>('govuk-table tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerVerifiersEmissionsAssessmentGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when operatorEmissionsAcceptable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerVerifiersEmissionsAssessmentGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      component.opinionStatement = mockOpinionStatement;
      component.isEditable = true;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the verifiers emissions', () => {
      expect(page.heading2.textContent.trim()).toEqual('Emissions reported by the verifier');
      expect(page.tableCells).toEqual(expectedCells);
    });
  });

  describe('when operatorEmissionsAcceptable is true', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerVerifiersEmissionsAssessmentGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      mockOpinionStatement.operatorEmissionsAcceptable = true;
      component.opinionStatement = mockOpinionStatement;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should hide all elements', () => {
      expect(page.heading2).toBeFalsy();
      expect(page.tableCells).toHaveLength(0);
    });
  });
});
