import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { HseTiService } from '@tasks/hseti/core';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { HsetiNotifyOperatorComponent } from './notify-operator.component';

describe('HsetiNotifyOperatorComponent', () => {
  let component: HsetiNotifyOperatorComponent;
  let fixture: ComponentFixture<HsetiNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HsetiNotifyOperatorComponent],
      providers: [provideRouter([]), HseTiService, CapitalizeFirstPipe, ItemNamePipe, DestroySubject],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(hsetiMockReviewState);

    fixture = TestBed.createComponent(HsetiNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
