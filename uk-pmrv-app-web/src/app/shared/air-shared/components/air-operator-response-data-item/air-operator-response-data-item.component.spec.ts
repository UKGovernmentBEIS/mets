import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { BasePage } from '@testing';

import { OperatorAirImprovementYesResponse } from 'pmrv-api';

import { AirOperatorResponseDataItemComponent } from './air-operator-response-data-item.component';

describe('AirOperatorResponseDataItemComponent', () => {
  let page: Page;
  let component: AirOperatorResponseDataItemComponent;
  let fixture: ComponentFixture<AirOperatorResponseDataItemComponent>;

  class Page extends BasePage<AirOperatorResponseDataItemComponent> {
    get airOperatorResponseDataItemContents() {
      return this.queryAll<HTMLDListElement>('dt, dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirOperatorResponseDataItemComponent],
      imports: [AirSharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirOperatorResponseDataItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.improvementResponse = {
      type: 'YES',
      proposal: 'Test explanation 1',
      proposedDate: '2040-06-03',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementYesResponse as OperatorAirImprovementResponseAll;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.airOperatorResponseDataItemContents).toEqual(['Operator comments', 'Test explanation 1', '']);
  });
});
