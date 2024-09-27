import { ChatMessageDto } from "./chat-message-dto.model";
import { UserResponse } from "./user/user-response.model";

export interface Conversation {
  friendshipId: string;
  user: UserResponse;
  lastMessage: ChatMessageDto;
  messages?: ChatMessageDto[];
}
