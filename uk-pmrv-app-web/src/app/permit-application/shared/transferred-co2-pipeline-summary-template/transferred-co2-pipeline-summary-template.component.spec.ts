import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TransferredCo2PipelineSummaryTemplateComponent } from './transferred-co2-pipeline-summary-template.component';

describe('TransferredCo2PipelineSummaryTemplateComponent', () => {
  let component: TransferredCo2PipelineSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-transferred-co2-pipeline-summary-template
        [pipelineSystems]="pipelineSystems"></app-transferred-co2-pipeline-summary-template>
    `,
  })
  class TestComponent {
    pipelineSystems = pipelineSystemsForm;
  }

  const pipelineSystemsForm = {
    exist: false,
  };

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, TransferredCo2PipelineSummaryTemplateComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(
      By.directive(TransferredCo2PipelineSummaryTemplateComponent),
    ).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details that have value', () => {
    expect(page.summaryListValues).toEqual([['No']]);
  });
});
