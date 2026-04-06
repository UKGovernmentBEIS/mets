import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { BREADCRUMB_ITEMS } from '@core/navigation/breadcrumbs';
import { SharedModule } from '@shared/shared.module';

import { CancelComponent } from './cancel.component';

describe('CancelComponent', () => {
  let component: CancelComponent;
  let fixture: ComponentFixture<CancelComponent>;

  const route = {
    paramMap: of(convertToParamMap({ taskId: 1 })),
    routeConfig: { path: 'cancel' },
    parent: { routeConfig: { path: '' } },
  };
  const breadcrumbs = new BehaviorSubject([{ text: 'Parent', link: [''] }]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: BREADCRUMB_ITEMS, useValue: breadcrumbs },
        { provide: ActivatedRoute, useValue: route },
      ],
      declarations: [CancelComponent],
      imports: [SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
