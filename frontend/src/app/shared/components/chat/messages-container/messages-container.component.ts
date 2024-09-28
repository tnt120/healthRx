import { Component, input  } from '@angular/core';
import { Conversation } from '../../../../core/models/conversation.model';
import { UserResponse } from '../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-messages-container',
  templateUrl: './messages-container.component.html',
  styleUrl: './messages-container.component.scss'
})
export class MessagesContainerComponent {
  conversation = input.required<Conversation | null>();

  chatUser = input.required<UserResponse>();
}
