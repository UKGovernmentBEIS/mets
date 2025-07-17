import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerCorsiaAnnualOffsettingPeerReviewComponent } from './peer-review.component';

describe('AerCorsiaAnnualOffsettingPeerReviewComponent', () => {
  let component: AerCorsiaAnnualOffsettingPeerReviewComponent;
  let fixture: ComponentFixture<AerCorsiaAnnualOffsettingPeerReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerCorsiaAnnualOffsettingPeerReviewComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AerCorsiaAnnualOffsettingPeerReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the app-peer-review-shared component', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const peerReviewElement = compiled.querySelector('app-peer-review-shared');
    expect(peerReviewElement).toBeTruthy();
  });
});
