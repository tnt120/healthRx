import { Component, HostBinding, input, OnInit, output, signal } from '@angular/core';
import { Conversation } from '../../../../../core/models/conversation.model';
import { UserResponse } from '../../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-conversation-item',
  templateUrl: './conversation-item.component.html',
  styleUrl: './conversation-item.component.scss'
})
export class ConversationItemComponent implements OnInit {
  conversation = input.required<Conversation>();

  chatUser = input.required<UserResponse>();

  isActive = input<boolean>(false);

  profilePicture = signal<string>('../../../../../assets/images/user.png');

  @HostBinding('class.active') get activeClass() {
    return this.isActive();
  }

  ngOnInit(): void {
    if (this.conversation().user?.pictureUrl) {
      this.profilePicture.set('data:image/jpeg;base64 ,' + this.conversation().user.pictureUrl);
    }
  }

  selectEmit = output<Conversation>();

  onSelect() {
    this.selectEmit.emit(this.conversation());
  }
}
