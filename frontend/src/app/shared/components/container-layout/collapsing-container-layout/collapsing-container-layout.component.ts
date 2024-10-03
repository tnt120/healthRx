import { animate, style, transition, trigger } from '@angular/animations';
import { AfterViewInit, ChangeDetectionStrategy, Component, input, model, signal } from '@angular/core';

@Component({
  selector: 'app-collapsing-container-layout',
  templateUrl: './collapsing-container-layout.component.html',
  styleUrl: './collapsing-container-layout.component.scss',
  animations: [
    trigger('expandNestedMenu', [
      transition(':enter', [
        style({ opacity: 0, height: '0px', 'padding-top': '0px' }),
        animate('.3s ease', style({ opacity: 1, height: '*', 'padding-top': '*' }))
      ]),
      transition(':leave', [
        animate('.3s ease', style({ opacity: 0, height: '0px', 'padding-top': '0px' }))
      ])
    ])
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CollapsingContainerLayoutComponent implements AfterViewInit {
  sectionTitle = input.required<string>();

  description = input<string>('');

  isCollapsed = model<boolean>(false);

  isFirstLoad = signal(true);

  ngAfterViewInit(): void {
    this.isFirstLoad.set(false);
  }

  toggleCollapse(): void {
    this.isCollapsed.set(!this.isCollapsed());
  }
}
