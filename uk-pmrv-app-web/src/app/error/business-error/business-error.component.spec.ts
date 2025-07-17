import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { BusinessError } from './business-error';
import { BusinessErrorComponent } from './business-error.component';
import { BusinessErrorService } from './business-error.service';

describe('BusinessErrorComponent', () => {
  let component: BusinessErrorComponent;
  let fixture: ComponentFixture<BusinessErrorComponent>;

  const error = new BusinessError('Test header').withLink({ link: ['/dashboard'], linkText: 'Go to dashboard' });
  const clear = jest.fn();
  const businessErrorService = { error$: new BehaviorSubject(error), clear };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [BusinessErrorComponent],
      providers: [{ provide: BusinessErrorService, useValue: businessErrorService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BusinessErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the heading and link of the latest business error', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h1').textContent).toEqual('Test header');
    expect(element.querySelector('a').textContent).toEqual('Go to dashboard');
    expect(element.querySelector('a').href).toMatch(/\/dashboard$/);
  });

  it('should clear the error on destroy', async () => {
    fixture.destroy();

    const clearSpy = jest.spyOn(businessErrorService, 'clear');

    expect(clearSpy).toHaveBeenCalled();
  });
});
