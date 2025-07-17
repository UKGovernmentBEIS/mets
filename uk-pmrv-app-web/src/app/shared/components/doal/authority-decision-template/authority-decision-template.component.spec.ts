import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import {
  AuthorityDecisionTemplateComponent,
  DoalSummaryAuthorityResponse,
} from '@shared/components/doal/authority-decision-template/authority-decision-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DoalGrantAuthorityResponse, DoalRejectAuthorityResponse } from 'pmrv-api';

describe('AuthorityDecisionTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-authority-decision-template [data]="data"></app-doal-authority-decision-template>
    `,
  })
  class TestComponent {
    data = {
      type: 'VALID_WITH_CORRECTIONS',
      authorityRespondDate: '2023-03-12',
      decisionNotice: 'Decision notice',
      preliminaryAllocations: [
        {
          year: 2024,
          allowances: 200,
          subInstallationName: 'ALUMINIUM',
        },
        {
          year: 2023,
          allowances: 100,
          subInstallationName: 'ALUMINIUM',
        },
      ],
    } as DoalSummaryAuthorityResponse;
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
      declarations: [TestComponent, AuthorityDecisionTemplateComponent],
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
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['When did the Authority respond?', '12 Mar 2023'],
      ['Authority decision', 'Approved with corrections'],
      ['Explanation of Authority decision for notice', 'Decision notice'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100'],
      ['2024', 'Aluminium', '200'],
      [],
      ['2023', '100'],
      ['2024', '200'],
    ]);

    component.data = {
      type: 'VALID',
      authorityRespondDate: '2023-03-12',
      preliminaryAllocations: [
        {
          year: 2024,
          allowances: 200,
          subInstallationName: 'ALUMINIUM',
        },
        {
          year: 2023,
          allowances: 100,
          subInstallationName: 'ALUMINIUM',
        },
      ],
    } as DoalGrantAuthorityResponse;
    fixture.detectChanges();

    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['When did the Authority respond?', '12 Mar 2023'],
      ['Authority decision', 'Approved'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100'],
      ['2024', 'Aluminium', '200'],
      [],
      ['2023', '100'],
      ['2024', '200'],
    ]);

    component.data = {
      type: 'INVALID',
      authorityRespondDate: '2023-03-12',
      decisionNotice: 'Rejected decision notice',
    } as DoalRejectAuthorityResponse;
    fixture.detectChanges();

    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['When did the Authority respond?', '12 Mar 2023'],
      ['Authority decision', 'Not approved'],
      ['Explanation of Authority decision for notice', 'Rejected decision notice'],
    ]);

    expect(page.tableValues).toEqual([]);
  });
});
