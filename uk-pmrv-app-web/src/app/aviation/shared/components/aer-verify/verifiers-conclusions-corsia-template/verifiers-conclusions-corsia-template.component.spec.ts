import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VerifiersConclusionsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifiers-conclusions-corsia-template/verifiers-conclusions-corsia-template.component';
import { BasePage } from '@testing';

describe('VerifiersConclusionsCorsiaTemplateComponent', () => {
  let page: Page;
  let component: VerifiersConclusionsCorsiaTemplateComponent;
  let fixture: ComponentFixture<VerifiersConclusionsCorsiaTemplateComponent>;

  class Page extends BasePage<VerifiersConclusionsCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifiersConclusionsCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifiersConclusionsCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      dataQualityMateriality: 'My quality of data',
      materialityThresholdType: 'THRESHOLD_2_PER_CENT',
      materialityThresholdMet: true,
      emissionsReportConclusion: 'My conclusion',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(4);
    expect(page.summaryValues).toEqual([
      ['Conclusions on data quality', 'My quality of data'],
      ['Materiality threshold', '2%'],
      ['Is this materiality threshold being met in the emissions report?', 'Yes'],
      [`Conclusion relating to the operator's emissions report`, 'My conclusion'],
    ]);
  });
});
