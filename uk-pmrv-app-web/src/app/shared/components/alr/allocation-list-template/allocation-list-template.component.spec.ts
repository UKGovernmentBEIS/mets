import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AllocationListTemplateComponent } from './allocation-list-template.component';

describe('AllocationListTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-alr-allocation-list-template [data]="data" [editable]="editable"></app-alr-allocation-list-template>
    `,
  })
  class TestComponent {
    data = [
      {
        year: 2026,
        subInstallationName: 'DISTRICT_HEATING',
        allowances: 10,
        allocationId: 0,
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
      declarations: [TestComponent],
      imports: [SharedModule, AllocationListTemplateComponent],
      providers: [provideRouter([])],
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
    expect(page.rowsCells).toEqual([['2026', 'District heating', '10', 'Change', 'Remove']]);
  });
});
