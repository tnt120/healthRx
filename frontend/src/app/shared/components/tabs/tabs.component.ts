import { AfterContentInit, Component, ContentChildren, Input, QueryList } from '@angular/core';
import { TabComponent } from './tab/tab.component';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrl: './tabs.component.scss'
})
export class TabsComponent implements AfterContentInit {

  @ContentChildren(TabComponent) tabs!: QueryList<TabComponent>;

  @Input()
  test = false;

  padding = '10px 24px';

  ngAfterContentInit(): void {
    const activeTabs = this.tabs.filter(tab => tab.active)

    if (this.test) {
      this.selectTab(this.tabs.last);
    } else {
      this.selectTab(this.tabs.first);
    }

    if (activeTabs.length === 0) {
    }
  }

  selectTab(tab: TabComponent) {
    this.tabs.toArray().forEach(tab => (tab.active = false));
    tab.active = true;
  }
}
