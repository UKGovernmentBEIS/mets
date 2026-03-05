import { SecurityContext } from '@angular/core';

import { MarkdownModuleConfig, MARKED_OPTIONS } from 'ngx-markdown';

import { markedOptionsFactory } from './marked-options-factory';

export const markdownModuleConfig: MarkdownModuleConfig = {
  markedOptions: {
    provide: MARKED_OPTIONS,
    useFactory: markedOptionsFactory,
  },
  sanitize: SecurityContext.URL,
};
