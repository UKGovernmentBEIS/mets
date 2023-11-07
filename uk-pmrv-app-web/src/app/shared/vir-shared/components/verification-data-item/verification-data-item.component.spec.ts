import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@testing';

import { VirSharedModule } from '../../vir-shared.module';
import { VerificationDataItemComponent } from './verification-data-item.component';

describe('VerificationDataItemComponent', () => {
  let page: Page;
  let component: VerificationDataItemComponent;
  let fixture: ComponentFixture<VerificationDataItemComponent>;

  class Page extends BasePage<VerificationDataItemComponent> {
    get verificationDataItemContents() {
      return this.queryAll<HTMLDListElement>('dt, dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationDataItemComponent],
      imports: [VirSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationDataItemComponent);
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
    expect(page.verificationDataItemContents).toEqual(["Verifier's recommendation", 'Test explanation A1', '']);
  });
});
