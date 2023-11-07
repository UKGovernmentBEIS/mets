import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HighlightDiffComponent } from './highlight-diff.component';

describe('HighlightDiffComponent', () => {
  let component: HighlightDiffComponent;
  let fixture: ComponentFixture<HighlightDiffComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HighlightDiffComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HighlightDiffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
