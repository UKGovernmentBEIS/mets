import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { VirSharedModule } from '../../vir-shared.module';
import { VerificationRecommendationItemComponent } from './verification-recommendation-item.component';

describe('VerificationRecommendationItemComponent', () => {
  let page: Page;
  let component: VerificationRecommendationItemComponent;
  let fixture: ComponentFixture<VerificationRecommendationItemComponent>;

  class Page extends BasePage<VerificationRecommendationItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationRecommendationItemComponent],
      imports: [VirSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationRecommendationItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.verificationDataItem = {
      reference: 'A1',
      explanation: 'Test explanation A1',
      materialEffect: true,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      "Verifier's recommendation",
      'Item code',
      'A1: an uncorrected error that remained before the verification report was issued',
      "Verifier's recommendation",
      'Test explanation A1',
      'Material?',
      'Yes',
    ]);
  });
});
