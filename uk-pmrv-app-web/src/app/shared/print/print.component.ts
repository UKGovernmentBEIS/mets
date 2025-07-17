import { ChangeDetectionStrategy, Component, ElementRef, Renderer2, RendererFactory2 } from '@angular/core';

@Component({
  selector: 'app-print',
  template: `
    <div class="printable-content-wrapper">
      <div class="printable-content">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styleUrl: './print.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrintComponent {
  private renderer: Renderer2;
  private styleElement: HTMLStyleElement;

  constructor(
    private rendererFactory: RendererFactory2,
    private el: ElementRef,
  ) {
    this.renderer = rendererFactory.createRenderer(null, null);
  }

  print() {
    this.enablePrintStyles();

    this.printContent();

    this.disablePrintStyles();
  }

  enablePrintStyles(): void {
    this.styleElement = this.renderer.createElement('style');
    this.styleElement.textContent = `
        @media print {
          body * {
            visibility: hidden;
          }
       
          .printable-content-wrapper * {
            visibility: visible;
          }
  
          .printable-content {
            position: absolute;
            left: 0;
            top: 0;
          }
      }
    `;
    this.renderer.appendChild(document.head, this.styleElement);
  }

  disablePrintStyles(): void {
    if (this.styleElement) {
      this.renderer.removeChild(document.head, this.styleElement);
    }
  }

  printContent(): void {
    const printableContent = this.el.nativeElement.querySelector('.printable-content-wrapper');

    const clonedElement = printableContent.cloneNode(true);
    this.renderer.appendChild(document.body, clonedElement);
    this.renderer.setStyle(clonedElement, 'display', 'block');

    window.print();

    if (clonedElement) {
      this.renderer.removeChild(document.body, clonedElement);
    }
  }
}
