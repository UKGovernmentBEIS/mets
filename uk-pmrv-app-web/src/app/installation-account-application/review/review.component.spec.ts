import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { ReviewComponent } from './review.component';
import { ReviewSummaryComponent } from './review-summary.component';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let router: Router;

  const requestActionsService = mockClass(RequestActionsService);
  requestActionsService.getRequestActionsByRequestId.mockReturnValue(of([]));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReviewComponent, ReviewSummaryComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: true } } as any);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
