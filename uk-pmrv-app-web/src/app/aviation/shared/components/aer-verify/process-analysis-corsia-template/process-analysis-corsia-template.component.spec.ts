import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ProcessAnalysisCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/process-analysis-corsia-template/process-analysis-corsia-template.component';
import { BasePage } from '@testing';

describe('ProcessAnalysisCorsiaTemplateComponent', () => {
  let page: Page;
  let component: ProcessAnalysisCorsiaTemplateComponent;
  let fixture: ComponentFixture<ProcessAnalysisCorsiaTemplateComponent>;

  class Page extends BasePage<ProcessAnalysisCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessAnalysisCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessAnalysisCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      verificationActivities: 'My verification activities',
      strategicAnalysis: 'My strategic analysis',
      dataSampling: 'My data sampling',
      dataSamplingResults: 'My data sampling results',
      emissionsMonitoringPlanCompliance: 'My emissions monitoring plan compliance',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(5);
    expect(page.summaryValues).toEqual([
      [
        'Describe the verification activities carried out and their corresponding results',
        'My verification activities',
      ],
      ['Strategic analysis and risk assessment', 'My strategic analysis'],
      ['Data sampling', 'My data sampling'],
      ['Results of data sampling', 'My data sampling results'],
      ['Compliance with the emissions monitoring plan', 'My emissions monitoring plan compliance'],
    ]);
  });
});
