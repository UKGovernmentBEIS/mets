import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ConfidentialitySummaryTemplateComponent } from './confidentiality-summary-template.component';

describe('ConfidentialitySummaryTemplateComponent', () => {
  let component: ConfidentialitySummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-confidentiality-summary-template
        [confidentialityData]="confidentialityData"
        [totalEmissionsFiles]="totalEmissionsFiles"
        [aggregatedStatePairDataFiles]="aggregatedStatePairDataFiles"
        [isEditable]="isEditable"></app-confidentiality-summary-template>
    `,
  })
  class TestComponent {
    confidentialityData = {
      totalEmissionsPublished: true,
      totalEmissionsExplanation: 'expl',
      totalEmissionsDocuments: ['total file'],
      aggregatedStatePairDataPublished: false,
    };
    totalEmissionsFiles = [{ downloadUrl: 'link1', fileName: 'totalEmissionsFile.png' }];
    aggregatedStatePairDataFiles = [];
    isEditable = true;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfidentialitySummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(ConfidentialitySummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(summaryListValues()).toEqual([
      [
        'Do you want to request ICAO not to publish your total annual emissions data at the operator level?',
        ['Yes', 'Change'],
      ],
      [
        'Emission data you are requesting should not be published and reasons why disclosure of this data would harm your commercial interests',
        ['expl', 'Change'],
      ],
      ['Supporting documents', ['totalEmissionsFile.png', 'Change']],
      ['Do you want to request ICAO not to publish your aggregated state pair data?', ['No', 'Change']],
    ]);

    hostComponent.confidentialityData = {
      totalEmissionsPublished: false,
      totalEmissionsExplanation: '',
      totalEmissionsDocuments: [],
      aggregatedStatePairDataPublished: false,
    };
    hostComponent.totalEmissionsFiles = [];
    hostComponent.aggregatedStatePairDataFiles = [];
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      [
        'Do you want to request ICAO not to publish your total annual emissions data at the operator level?',
        ['No', 'Change'],
      ],
      ['Do you want to request ICAO not to publish your aggregated state pair data?', ['No', 'Change']],
    ]);

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Do you want to request ICAO not to publish your total annual emissions data at the operator level?', ['No']],
      ['Do you want to request ICAO not to publish your aggregated state pair data?', ['No']],
    ]);
  });

  function summaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list__row')).map((row) => [
      row.querySelector('dt').textContent.trim(),
      Array.from(row.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }
});
