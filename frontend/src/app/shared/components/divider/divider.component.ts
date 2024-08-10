import { Component, input } from '@angular/core';

@Component({
  selector: 'app-divider',
  templateUrl: './divider.component.html',
  styleUrl: './divider.component.scss'
})
export class DividerComponent {
  width = input.required<string>();
}
