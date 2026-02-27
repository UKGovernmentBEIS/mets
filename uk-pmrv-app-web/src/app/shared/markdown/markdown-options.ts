import { SecurityContext } from '@angular/core';

import { MarkdownModuleConfig, MARKED_OPTIONS, SANITIZE } from 'ngx-markdown';

import { markedOptionsFactory } from './marked-options-factory';

export const markdownModuleConfig: MarkdownModuleConfig = {
  markedOptions: {
    provide: MARKED_OPTIONS,
    useFactory: markedOptionsFactory,
  },
  sanitize: {
    provide: SANITIZE,
    useValue: SecurityContext.URL,
  },
};
