import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router, RouterLinkWithHref } from '@angular/router';

import { GenericServiceErrorCode } from '@error/service-errors';
import { SharedModule } from '@shared/shared.module';
import { RouterStubComponent } from '@testing';

import { InternalServerErrorComponent } from './internal-server-error.component';

describe('InternalServerErrorComponent', () => {
  let component: InternalServerErrorComponent;
  let fixture: ComponentFixture<InternalServerErrorComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterLinkWithHref],
      declarations: [InternalServerErrorComponent],
      providers: [provideRouter([{ path: 'contact-us', component: RouterStubComponent }])],
    }).compileComponents();
  });

  describe('for default error', () => {
    beforeEach(() => {
      router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: {} } as any);

      fixture = TestBed.createComponent(InternalServerErrorComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTML elements', async () => {
      const element: HTMLElement = fixture.nativeElement;
      const paragraphContents = Array.from(element.querySelectorAll<HTMLParagraphElement>('p')).map((el) =>
        el.textContent.trim(),
      );

      expect(element.querySelector('h1').textContent).toEqual('Sorry, there is a problem with the service');
      expect(paragraphContents).toEqual([
        'Try again later.',
        'Contact the UK ETS reporting helpdesk  if you have any questions.',
      ]);
    });
  });

  describe('for custom errors', () => {
    const errorCode = GenericServiceErrorCode.INTREGEMISSIONSINSTAVUKETS1007;

    beforeEach(() => {
      router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { errorCode } } } as any);

      fixture = TestBed.createComponent(InternalServerErrorComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTML elements', async () => {
      const element: HTMLElement = fixture.nativeElement;
      const paragraphContents = Array.from(element.querySelectorAll<HTMLParagraphElement>('p')).map((el) =>
        el.textContent.trim(),
      );

      expect(element.querySelector('h1').textContent).toEqual('Registry application data synchronisation error');
      expect(paragraphContents).toEqual([
        "We're experiencing temporary difficulties in syncing data. Please try again later or contact the  UK ETS reporting helpdesk  for assistance.",
      ]);
    });
  });
});
