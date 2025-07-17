import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let component: AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent;
  let fixture: ComponentFixture<AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the app-peer-review-shared component', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const peerReviewElement = compiled.querySelector('app-peer-review-shared');

    expect(peerReviewElement).toBeTruthy();
  });
});
