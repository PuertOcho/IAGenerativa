import { NgModule } from '@angular/core';
import { DayNamePipe, FormatAudio, FormatDateTimeGTMPipe, FormatFechaPipe, FormatNumberPipe, FormatSiNoDeBoolean, FormatTimeGTMPipe, FormatTimestampFecha, MarkdownPipe } from './pipes';
import { SortByPipe } from './sortby';

@NgModule({
  declarations: [
    SortByPipe,
    FormatNumberPipe,
    FormatTimeGTMPipe,
    DayNamePipe,
    FormatDateTimeGTMPipe,
    FormatFechaPipe,
    FormatTimestampFecha,
    FormatAudio,
    FormatSiNoDeBoolean,
    MarkdownPipe
  ],
  imports: [],
  exports: [
    SortByPipe,
    FormatNumberPipe,
    FormatTimeGTMPipe,
    DayNamePipe,
    FormatDateTimeGTMPipe,
    FormatFechaPipe,
    FormatTimestampFecha,
    FormatAudio,
    FormatSiNoDeBoolean,
    MarkdownPipe
  ]
})
export class PipesModule { }
