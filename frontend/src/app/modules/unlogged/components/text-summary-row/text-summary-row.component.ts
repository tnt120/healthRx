import { Component, Input, input } from '@angular/core';

@Component({
  selector: 'app-text-summary-row',
  templateUrl: './text-summary-row.component.html',
  styleUrl: './text-summary-row.component.scss'
})
export class TextSummaryRowComponent {
  title = input.required<string>();
  value = input.required<string>();
}
