import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { IndependentReviewCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/independent-review-corsia-template/independent-review-corsia-template.component';
import { BasePage } from '@testing';

describe('IndependentReviewCorsiaTemplateComponent', () => {
  let page: Page;
  let component: IndependentReviewCorsiaTemplateComponent;
  let fixture: ComponentFixture<IndependentReviewCorsiaTemplateComponent>;

  class Page extends BasePage<IndependentReviewCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IndependentReviewCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndependentReviewCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      reviewResults: 'My review results',
      name: 'My name',
      position: 'My position',
      email: 'test@pmrv.com',
      line1: 'Korinthou 4, Neo Psychiko',
      line2: 'line 2 legal test',
      city: 'Athens',
      state: 'Korinthou',
      country: 'GR',
      postcode: '15452',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(5);
    expect(page.summaryValues).toEqual([
      ['Results of the independent review', 'My review results'],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Email', 'test@pmrv.com'],
      ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test KorinthouAthens15452`],
    ]);
  });
});
