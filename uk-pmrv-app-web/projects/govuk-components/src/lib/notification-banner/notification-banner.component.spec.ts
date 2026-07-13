import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { NotificationBannerComponent } from './notification-banner.component';

describe('NotificationBannerComponent', () => {
  let component: NotificationBannerComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    imports: [NotificationBannerComponent],
    template: `
      <govuk-notification-banner [type]="type" [heading]="heading">
        <h3 class="govuk-notification-banner__heading">Training outcome recorded and trainee withdrawn</h3>
        <p>Test content</p>
      </govuk-notification-banner>
    `,
  })
  class TestComponent {
    type: 'success' | 'neutral' = 'neutral';
    heading?: string;
  }

  const getBanner = () => element.querySelector<HTMLDivElement>('.govuk-notification-banner');
  const getHeading = () => element.querySelector<HTMLHeadingElement>('.govuk-notification-banner__title');

  const createComponent = (type: 'success' | 'neutral', heading?: string) => {
    fixture = TestBed.createComponent(TestComponent);
    fixture.componentInstance.type = type;
    if (heading !== undefined) {
      fixture.componentInstance.heading = heading;
    }
    component = fixture.debugElement.query(By.directive(NotificationBannerComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();
  });

  it('should accept custom headers', () => {
    createComponent('neutral', 'Alert');

    expect(getHeading().textContent).toEqual('Alert');
  });

  describe('neutral notification', () => {
    beforeEach(() => createComponent('neutral'));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should color the heading as blue in neutral notifications', () => {
      expect(element.querySelector('.govuk-notification-banner--success')).toBeFalsy();
    });

    it('should set the role as region in neutral notifications', () => {
      expect(getBanner().getAttribute('role')).toEqual('region');
    });

    it('should project content', () => {
      expect(element.querySelector<HTMLHeadingElement>('.govuk-notification-banner__heading')).toBeTruthy();
      expect(element.querySelector<HTMLParagraphElement>('p')).toBeTruthy();
    });

    it('should not focus the element in neutral notifications', () => {
      expect(document.activeElement).not.toBe(getBanner());
    });

    it('should have a default important heading', () => {
      expect(getHeading().textContent).toEqual('Important');
    });
  });

  describe('alert notification', () => {
    beforeEach(() => createComponent('success'));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should color the heading as green', () => {
      expect(element.querySelector('.govuk-notification-banner--success')).toBeTruthy();
    });

    it('should set the role as alert', () => {
      expect(getBanner().getAttribute('role')).toEqual('alert');
    });

    it('should focus the element', () => {
      expect(document.activeElement).toBe(getBanner());
    });

    it('should have a default success heading', () => {
      expect(getHeading().textContent).toEqual('Success');
    });
  });
});
