import { Component, input, output, signal  } from '@angular/core';
import { Conversation } from '../../../../core/models/conversation.model';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { ChatMessageDto } from '../../../../core/models/chat-message-dto.model';

@Component({
  selector: 'app-messages-container',
  templateUrl: './messages-container.component.html',
  styleUrl: './messages-container.component.scss'
})
export class MessagesContainerComponent {
  conversation = input.required<Conversation | null>();

  chatUser = input.required<UserResponse>();

  messageSendEmit = output<{ content: string, friendshipId: string, receiverId: string, senderId: string }>();

  message = signal<string>('');

  sendMessage(): void {
    if (this.conversation()) {
      this.messageSendEmit.emit({
        content: this.message(),
        friendshipId: this.conversation()!.friendshipId,
        receiverId: this.conversation()!.user.id,
        senderId: this.chatUser().id
      });

      this.message.set('');
    }
  }

  compareDates(currMessage: ChatMessageDto, prevMessage: ChatMessageDto | undefined): boolean {
    const currTime = currMessage.createdAt ? new Date( currMessage.createdAt).getTime() : 0;
    const prevTime = prevMessage?.createdAt ? new Date(prevMessage.createdAt).getTime() : 0;

    return (currTime - prevTime) / (1000 * 60 * 60) > 3;
  }
}
