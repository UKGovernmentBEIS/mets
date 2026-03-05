import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DateSubmittedSummaryTemplateComponent } from '@shared/components/doal/date-submitted-summary-template/date-submitted-summary-template.component';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { BasePage } from '@testing';

import { DateSubmittedToAuthority } from 'pmrv-api';

describe('DateSubmittedSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-date-submitted-summary-template
        [dateSubmittedToAuthority]="dateSubmittedToAuthority"
        [editable]="editable"></app-doal-date-submitted-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    dateSubmittedToAuthority = {
      date: '2023-03-12',
    } as DateSubmittedToAuthority;
  }

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('dl')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, DateSubmittedSummaryTemplateComponent, GovukDatePipe],
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([
      ['When was the relevant information submitted to the authority?', '12 Mar 2023'],
    ]);
  });
});
