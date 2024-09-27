package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.chat.Message;
import org.springframework.data.jpa.domain.Specification;

public class MessageSpecification {
    public static Specification<Message> getMessagesByFriendshipId(String friendshipId) {
        return ((root, query, cb) -> {
            var friendshipJoin = root.join("friendship");
            return cb.equal(friendshipJoin.get("id"), friendshipId);
        });
    }
}
