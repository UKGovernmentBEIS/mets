import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DoeEmissionsSummaryTemplateComponent } from './doe-emissions-summary-template.component';

describe('DoeEmissionsSummaryTemplateComponent', () => {
  let fixture: ComponentFixture<DoeEmissionsSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DoeEmissionsSummaryTemplateComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
