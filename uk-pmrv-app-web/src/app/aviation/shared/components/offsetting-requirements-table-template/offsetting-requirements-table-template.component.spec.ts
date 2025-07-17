import { ComponentFixture, TestBed } from '@angular/core/testing';

import { getTableData } from '@aviation/shared/utils/3year-period-offsetting-shared.util';

import { AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING } from '../../../request-task/aer-corsia-3year-period-offsetting/mocks/mock-3year-period-offsetting';
import { ThreeYearOffsettingRequirementsTableTemplateComponent } from './offsetting-requirements-table-template.component';

describe('OffsettingRequirementsTableTemplateComponent', () => {
  let component: ThreeYearOffsettingRequirementsTableTemplateComponent;
  let fixture: ComponentFixture<ThreeYearOffsettingRequirementsTableTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreeYearOffsettingRequirementsTableTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ThreeYearOffsettingRequirementsTableTemplateComponent);
    component = fixture.componentInstance;
    component.data = getTableData(AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING);
    component.totalYearlyOffsettingData = AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.totalYearlyOffsettingData;
    component.periodOffsettingRequirements = {
      calculatedAnnualOffsetting: null,
      cefEmissionsReductions: AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.periodOffsettingRequirements,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show data in table', () => {
    const cells = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('tbody th , td'));

    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['2021', '221', '11'],
      ...['2022', '44', '33'],
      ...['2023', '66', '55'],
      ...['Total (tCO2)', '331', '99'],
      ...['Period offsetting requirements (tCO2)', '', '232'],
    ]);
  });
});
