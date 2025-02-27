package org.scm.chat.chat.service;

import org.scm.chat.chat.dto.*;

import java.util.List;

public interface ChatMessageService {


    UserChatContactData getChatParticipants(Long id, String loggedInUserId);
    UserChatContactData getChatParticipantsInSeparatePage(String loggedInUserId);


    List<ChatMessageDto> getChatMessages(Long roomId, String loggedInUserId);
    List<ChatMessageDto> getChatMessagesBuyUserId(Long roomId, String loggedInUserId);


    List<ChatMessageDtoForGroup> getChatsForGroup(Long roomId, String loggedInUserId);

    UpdateStatusDto updateStatusSentToDeliveredAddSeenBy(ChatStatusUpdateDto chatStatusUpdateDto);


}
