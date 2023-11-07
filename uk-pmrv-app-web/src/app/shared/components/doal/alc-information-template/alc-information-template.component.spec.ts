import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AlcInformationTemplateComponent } from '@shared/components/doal/alc-information-template/alc-information-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { ActivityLevelChangeInformation } from 'pmrv-api';

describe('AlcInformationTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-alc-information-template [data]="data" [editable]="editable"></app-doal-alc-information-template>
    `,
  })
  class TestComponent {
    data = {
      activityLevels: [
        {
          year: 2025,
          subInstallationName: 'ADIPIC_ACID',
          changeType: 'INCREASE',
          changedActivityLevel: 'Changed activity level',
          comments: 'Activity Level 1 comment',
        },
      ],
      areConservativeEstimates: true,
      explainEstimates: 'Explain estimates',
      preliminaryAllocations: [
        {
          subInstallationName: 'ALUMINIUM',
          year: 2025,
          allowances: 10,
        },
      ],
      commentsForUkEtsAuthority: 'Comments for UkEts authority comment',
    } as ActivityLevelChangeInformation;
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
      declarations: [TestComponent, AlcInformationTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
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
      ['Why the estimate was made', 'Explain estimates'],
      ['Comments', 'Comments for UkEts authority comment'],
      ['Activity level details', 'Change'],
      ['Allocation for each sub-installation details', 'Change'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2025', 'Adipic acid', 'Increase', 'Changed activity level', 'Activity Level 1 comment'],
      [],
      ['2025', 'Aluminium', '10'],
    ]);
  });
});
