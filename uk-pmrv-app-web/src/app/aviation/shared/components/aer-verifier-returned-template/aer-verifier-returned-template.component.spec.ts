import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { AerVerifierReturnedTemplateComponent } from './aer-verifier-returned-template.component';

describe('AerVerifierReturnedTemplateComponent', () => {
  let component: AerVerifierReturnedTemplateComponent;
  let fixture: ComponentFixture<AerVerifierReturnedTemplateComponent>;
  let page: Page;

  class Page extends BasePage<AerVerifierReturnedTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerVerifierReturnedTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AerVerifierReturnedTemplateComponent);
    component = fixture.componentInstance;
    component.changesRequired = 'changes';
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(1);
    expect(page.summaryValues).toEqual([['Changes required by the operator', 'changes']]);
  });
});
