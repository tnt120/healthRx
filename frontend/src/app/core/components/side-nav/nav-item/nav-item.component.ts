import { Component, inject, input, OnChanges, output, signal, SimpleChanges } from '@angular/core';
import { IsActiveMatchOptions, Router } from '@angular/router';
import { NavItem } from '../../../constants/headers';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-nav-item',
  templateUrl: './nav-item.component.html',
  styleUrl: './nav-item.component.scss',
  animations: [
    trigger('expandNestedMenu', [
      transition(':enter', [
        style({ opacity: 0, height: '0px' }),
        animate('.3s ease', style({ opacity: 1, height: '*' }))
      ]),
      transition(':leave', [
        animate('.3s ease', style({ opacity: 0, height: '0px' }))
      ])
    ])
  ]
})
export class NavItemComponent implements OnChanges {
  private readonly router = inject(Router);

  item = input.required<NavItem>();

  isOpened = input.required<boolean>();

  emitClick = output<void>();

  openNestedClick = output<void>();

  nestedMenuOpen = signal(false);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpened'] && !this.isOpened() && !this.isSubItemActive()) {
      this.nestedMenuOpen.set(false);
      return;
    }

    if (changes['isOpened'] && this.isSubItemActive()) {
      this.nestedMenuOpen.set(true);
    }
  }

  onClick(subItemClicked: boolean) {
    if (!this.isOpened() && this.item().subItems) {
      this.openNestedClick.emit();
    }

    if (subItemClicked || !this.item().subItems) {
      this.emitClick.emit();
    }

    if (this.item().subItems && !subItemClicked) {
      this.nestedMenuOpen.set(!this.nestedMenuOpen());
    }
  }

  isSubItemActive(): boolean {
    const matchOptions: IsActiveMatchOptions = { paths: 'exact', queryParams: 'exact', fragment: 'ignored', matrixParams: 'ignored' };
    return this.item().subItems?.some(subItem => this.router.isActive(subItem.route, matchOptions)) ?? false;
  }

}
