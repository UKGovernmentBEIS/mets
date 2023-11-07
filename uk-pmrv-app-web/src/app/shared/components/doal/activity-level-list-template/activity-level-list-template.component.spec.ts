import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';
import { ActivityLevelListTemplateComponent } from './activity-level-list-template.component';

describe('ActivityLevelListTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-activity-level-list-template
        [data]="data"
        [heading]="heading"
        [historical]="historical"
        [editable]="editable"
      ></app-doal-activity-level-list-template>
    `,
  })
  class TestComponent {
    data = [
      {
        year: 2026,
        subInstallationName: 'DISTRICT_HEATING',
        changeType: 'REGULATOR_REJECTS_ADJUSTMENT',
        changedActivityLevel: '10%',
        comments: 'activityLevel1Comment',
        creationDate: '2023-05-25T12:12:48.469862Z',
      },
    ];
    heading = 'Some heading';
    historical = true;
    editable = true;
  }

  class Page extends BasePage<TestComponent> {
    get heading() {
      return this.query<HTMLHeadElement>('h2.govuk-heading-m');
    }

    get rowsCells() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, ActivityLevelListTemplateComponent],
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

  it('should render heading', () => {
    expect(page.heading.textContent.trim()).toEqual('Some heading');
  });

  it('should render the results with edit actions if not historical', () => {
    expect(page.rowsCells).toEqual([
      ['2026', 'District heating', 'Regulator rejects adjustment', '10%', 'activityLevel1Comment', '25 May 2023'],
    ]);
  });
});
