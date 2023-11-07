import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TotalPreliminaryAllocationListTemplateComponent } from '@shared/components/doal/total-preliminary-allocation-list-template/total-preliminary-allocation-list-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PreliminaryAllocation } from 'pmrv-api';

describe('TotalPreliminaryAllocationListTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-total-preliminary-allocation-list-template
        [data]="data"
      ></app-doal-total-preliminary-allocation-list-template>
    `,
  })
  class TestComponent {
    data: PreliminaryAllocation[] = [
      {
        year: 2026,
        subInstallationName: 'ALUMINIUM',
        allowances: 10,
      },
      {
        year: 2026,
        subInstallationName: 'AMMONIA',
        allowances: 10,
      },
      {
        year: 2022,
        subInstallationName: 'ALUMINIUM',
        allowances: 20,
      },
      {
        year: 2022,
        subInstallationName: 'AMMONIA',
        allowances: 20,
      },
    ];
    editable = true;
  }

  class Page extends BasePage<TestComponent> {
    get rowsCells() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, TotalPreliminaryAllocationListTemplateComponent],
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
    expect(page.rowsCells).toEqual([
      ['2022', '40'],
      ['2026', '20'],
    ]);
  });
});
