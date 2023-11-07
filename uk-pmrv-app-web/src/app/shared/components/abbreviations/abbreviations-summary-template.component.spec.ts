import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AbbreviationsSummaryTemplateComponent } from './abbreviations-summary-template.component';

describe('AbbreviationsSummaryTemplateComponent', () => {
  let page: Page;
  let component: AbbreviationsSummaryTemplateComponent;
  let fixture: ComponentFixture<AbbreviationsSummaryTemplateComponent>;

  class Page extends BasePage<AbbreviationsSummaryTemplateComponent> {
    get abbreviationDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(AbbreviationsSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('for existing abbreviations', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = {
        exist: true,
        abbreviationDefinitions: [
          {
            abbreviation: 'Abbreviation1',
            definition: 'Definition1',
          },
          {
            abbreviation: 'Abbreviation2',
            definition: 'Definition2',
          },
        ],
      };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.abbreviationDefinitions).toHaveLength(4);
      expect(page.abbreviationDefinitions).toEqual([
        ['Abbreviation, acronym or terminology', 'Abbreviation1'],
        ['Definition', 'Definition1'],
        ['Abbreviation, acronym or terminology', 'Abbreviation2'],
        ['Definition', 'Definition2'],
      ]);
    });
  });

  describe('for no abbreviations', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.data = { exist: false };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.abbreviationDefinitions).toHaveLength(1);
      expect(page.abbreviationDefinitions).toEqual([
        [
          'Are you using any abbreviations, acronyms or terminology in your permit application which may require definition?',
          'No',
        ],
      ]);
    });
  });
});
