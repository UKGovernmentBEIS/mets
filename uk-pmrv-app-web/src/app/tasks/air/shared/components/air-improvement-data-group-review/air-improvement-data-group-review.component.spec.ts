import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AirImprovementDataGroupReviewComponent } from './air-improvement-data-group-review.component';

describe('AirImprovementDataGroupReviewComponent', () => {
  let component: AirImprovementDataGroupReviewComponent;
  let fixture: ComponentFixture<AirImprovementDataGroupReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirImprovementDataGroupReviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AirImprovementDataGroupReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
