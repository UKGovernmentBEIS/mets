import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { EmpBatchReissueFiltersTemplateComponent } from './filters-template.component';

describe('EmpBatchReissueFiltersTemplateComponent', () => {
  let component: EmpBatchReissueFiltersTemplateComponent;
  let fixture: ComponentFixture<EmpBatchReissueFiltersTemplateComponent>;

  let page: Page;

  class Page extends BasePage<EmpBatchReissueFiltersTemplateComponent> {
    get values() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpBatchReissueFiltersTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(EmpBatchReissueFiltersTemplateComponent);
    component = fixture.componentInstance;
  };

  beforeEach(createComponent);

  describe('for in progress batch reissue', () => {
    beforeEach(() => {
      component.filters = {
        reportingStatuses: ['EXEMPT_COMMERCIAL', 'REQUIRED_TO_REPORT'],
        emissionTradingSchemes: ['UK_ETS_AVIATION'],
        numberOfEmitters: null,
      };
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show details', () => {
      expect(page.values).toEqual([
        ['Reporting status', 'Exempt (commercial)Required to report'],
        ['Scheme', 'UK ETS'],
      ]);
    });
  });

  describe('for completed batch reissue', () => {
    beforeEach(() => {
      component.filters = {
        reportingStatuses: ['EXEMPT_COMMERCIAL', 'REQUIRED_TO_REPORT'],
        emissionTradingSchemes: ['UK_ETS_AVIATION'],
        numberOfEmitters: 10,
      };
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show details', () => {
      expect(page.values).toEqual([
        ['Reporting status', 'Exempt (commercial)Required to report'],
        ['Scheme', 'UK ETS'],
        ['Total emitters selected', '10'],
      ]);
    });
  });
});
