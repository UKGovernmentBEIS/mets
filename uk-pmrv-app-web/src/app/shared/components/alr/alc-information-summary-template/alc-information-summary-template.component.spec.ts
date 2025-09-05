import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AlrAlcInformationSummaryTemplateComponent } from './alc-information-summary-template.component';

describe('AlcInformationTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-alr-alc-information-summary-template
        [data]="data"
        [editable]="editable"></app-alr-alc-information-summary-template>
    `,
  })
  class TestComponent {
    data = {
      activityLevels: [
        {
          year: 2025,
          subInstallationName: 'ADIPIC_ACID',
          changeType: 'INCREASE',
          changedActivityLevel: '44.11',
          comments: 'Activity Level 1 comment',
          activityLevelChangeId: 0,
        },
      ],
      historicalActivityLevels: [
        {
          year: 2025,
          subInstallationName: 'ADIPIC_ACID',
          changeType: 'INCREASE',
          changedActivityLevel: '44.11',
          comments: 'Activity Level 1 comment',
          creationDate: '2023-05-25T12:12:48.469862Z',
        },
      ],
      conservativeDeterminesActivity: true,
      conservativeDeterminesActivityComment: 'Explain estimates',
      allocations: [
        {
          subInstallationName: 'ALUMINIUM',
          year: 2025,
          allowances: 10,
          allocationId: 0,
        },
      ],
      ukEtsAuthorityComments: 'Comments for UkEts authority comment',
    } as any;
    editable = true;
  }

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      providers: [provideRouter([])],
      imports: [SharedModule, AlrAlcInformationSummaryTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the results', () => {
    expect(page.summaryListValues).toHaveLength(5);
    expect(page.summaryListValues).toEqual([
      ['Were conservative estimates made to determine the activity level?', 'Yes'],
      ['Explain why the estimate was made', 'Explain estimates'],
      ['Comments', 'Comments for UkEts authority comment'],
      ['New activity level changes', 'Change'],
      ['Allocation for each sub-installation details', 'Change'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2025', 'Adipic acid', 'Increase', '44.11', 'Activity Level 1 comment', '25 May 2023'],
      [],
      ['2025', 'Adipic acid', 'Increase', '44.11', 'Activity Level 1 comment'],
      [],
      ['2025', 'Aluminium', '10'],
    ]);
  });
});
