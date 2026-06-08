import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AviationAccoundDetailsHistoryCategoryPipe } from '@aviation/accounts/pipes/account-details-history-category.pipe';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationAccountDetailsListComponent } from './aviation-account-details-history-list.component';

describe('AviationAccountDetailsListComponent', () => {
  let component: AviationAccountDetailsListComponent;
  let fixture: ComponentFixture<AviationAccountDetailsListComponent>;
  let page: Page;

  class Page extends BasePage<AviationAccountDetailsListComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AviationAccountDetailsListComponent,
        PipesModule,
        AviationAccoundDetailsHistoryCategoryPipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationAccountDetailsListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.history = [
      {
        changedBy: 'Regulator',
        creationDate: '2026-04-22T11:07:17.476808"',
        category: 'FIRST_YEAR_OF_REPORTING_OBLIGATION',
        previousValue: '2021-11-12' as any,
        newValue: '2021-11-12' as any,
        reason: 'reason',
      },
    ];
    component.columns = [
      { header: 'Field', field: 'category', widthClass: 'govuk-!-width-one-quarter' },
      { header: 'Previous', field: 'previousValue', widthClass: 'govuk-!-width-one-quarter' },
      { header: 'New', field: 'newValue', widthClass: 'govuk-!-width-one-quarter' },
      { header: 'Reason', field: 'reason', widthClass: 'govuk-!-width-one-quarter' },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show table rows', () => {
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['First year of reporting obligation', '12 Nov 2021', '12 Nov 2021', 'reason'],
    ]);
  });
});
