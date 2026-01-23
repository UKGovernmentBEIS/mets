import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';

import { Bdrs2SendReportConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: Bdrs2SendReportConfirmationComponent;
  let fixture: ComponentFixture<Bdrs2SendReportConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bdrs2SendReportConfirmationComponent],
      providers: [BdrS2Service, ItemNamePipe, provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(Bdrs2SendReportConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
