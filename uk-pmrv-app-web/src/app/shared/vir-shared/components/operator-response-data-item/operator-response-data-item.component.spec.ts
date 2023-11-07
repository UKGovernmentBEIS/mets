import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { BasePage } from '@testing';

import { OperatorResponseDataItemComponent } from './operator-response-data-item.component';

describe('OperatorResponseDataItemComponent', () => {
  let page: Page;
  let component: OperatorResponseDataItemComponent;
  let fixture: ComponentFixture<OperatorResponseDataItemComponent>;

  class Page extends BasePage<OperatorResponseDataItemComponent> {
    get verificationDataItemContents() {
      return this.queryAll<HTMLDListElement>('dt, dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorResponseDataItemComponent],
      imports: [VirSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(OperatorResponseDataItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.operatorImprovementResponse = {
      isAddressed: true,
      addressedDescription: 'Test description A1',
      addressedDate: '2040-06-03',
      uploadEvidence: true,
      files: [
        '74313a81-7182-4ead-9018-8f9e298ceb68',
        '6eca6133-491f-47d2-9085-949adaae025b',
        '10c7da4c-7c72-42a8-bb7f-043c6aaf0d72',
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.verificationDataItemContents).toEqual(["Operator's response", 'Test description A1', '']);
  });
});
