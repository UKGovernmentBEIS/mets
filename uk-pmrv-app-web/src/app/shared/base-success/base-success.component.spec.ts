import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseSuccessComponent } from './base-success.component';

describe('BaseSuccessComponent', () => {
  let component: BaseSuccessComponent;
  let fixture: ComponentFixture<BaseSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BaseSuccessComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BaseSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
