import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DataGapsListTemplateComponent } from './data-gaps-list-template.component';

describe('DataGapsListTemplateComponent', () => {
  let page: Page;
  let component: DataGapsListTemplateComponent;
  let fixture: ComponentFixture<DataGapsListTemplateComponent>;
  let element: HTMLElement;

  class Page extends BasePage<DataGapsListTemplateComponent> {
    get dataGapsSummary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataGapsListTemplateComponent, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(DataGapsListTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for existing data gaps', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.dataGaps = [
        {
          reason: 'reason 1',
          type: 'type 1',
          replacementMethod: 'replacement method 1',
          flightsAffected: 5,
          totalEmissions: '7',
        },
        {
          reason: 'reason 2',
          type: 'type 2',
          replacementMethod: 'replacement method 2',
          flightsAffected: 6,
          totalEmissions: '8',
        },
      ];

      component.isEditable = true;
      element = fixture.nativeElement;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.dataGapsSummary).toHaveLength(10);
      expect(page.dataGapsSummary).toEqual([
        ['Reason for the data gap', 'reason 1'],
        ['Type of data gap', 'type 1'],
        ['Replacement method for determining surrogate data', 'replacement method 1'],
        ['Number of flights', '5'],
        ['Total emissions', '7 tonnes CO2'],

        ['Reason for the data gap', 'reason 2'],
        ['Type of data gap', 'type 2'],
        ['Replacement method for determining surrogate data', 'replacement method 2'],
        ['Number of flights', '6'],
        ['Total emissions', '8 tonnes CO2'],
      ]);
    });

    it('should render 2 remove buttons', () => {
      expect(element.querySelectorAll<HTMLAnchorElement>('a[role="button"]').length).toEqual(2);
    });
  });
});
