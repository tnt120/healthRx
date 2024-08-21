import { Component, input } from '@angular/core';

@Component({
  selector: 'app-section-layout',
  templateUrl: './section-layout.component.html',
  styleUrl: './section-layout.component.scss'
})
export class SectionLayoutComponent {
  title = input.required<string>();

  description = input<string>('');
}
