import { Component, HostBinding, input, output } from '@angular/core';
import { Conversation } from '../../../../../core/models/conversation.model';

@Component({
  selector: 'app-conversation-item',
  templateUrl: './conversation-item.component.html',
  styleUrl: './conversation-item.component.scss'
})
export class ConversationItemComponent {
  conversation = input.required<Conversation>();

  isActive = input<boolean>(false);

  @HostBinding('class.active') get activeClass() {
    return this.isActive();
  }

  selectEmit = output<Conversation>();

  onSelect() {
    this.selectEmit.emit(this.conversation());
  }
}
