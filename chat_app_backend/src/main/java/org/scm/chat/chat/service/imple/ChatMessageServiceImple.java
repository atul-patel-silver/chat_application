package org.scm.chat.chat.service.imple;

import org.scm.chat.chat.dto.ChatDisplayDto;
import org.scm.chat.chat.dto.ChatMessageDto;
import org.scm.chat.chat.dto.UserChatContactData;
import org.scm.chat.chat.model.ChatMessage;
import org.scm.chat.chat.repository.ChatMessageRepository;
import org.scm.chat.chat.service.ChatMessageService;
import org.scm.chat.contact.model.Contact;
import org.scm.chat.contact.repository.ContactRepository;
import org.scm.chat.exception.ResourceNotFoundException;
import org.scm.chat.user.model.User;
import org.scm.chat.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageServiceImple implements ChatMessageService {


    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ContactRepository contactRepository;


    @Autowired
    private UserRepository userRepository;


    @Override
    public UserChatContactData getChatParticipants(Long id, String loggedInUserId) {

        Contact contact = this.contactRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Contact", "Id", id.toString())
        );
        UserChatContactData userChatContactData = new UserChatContactData();
        try {
            Optional<User> chatWithUser = this.userRepository.findByEmail(contact.getEmail());
            Optional<User> loginUser = this.userRepository.findByEmail(loggedInUserId);
            List<Object[]> result = this.chatMessageRepository.getSingleUserLastMessageDataForDisplay(loginUser.get().getId(), chatWithUser.get().getId());
            List<Object[]> latestMessagesForLoggedInUser = this.chatMessageRepository.findLatestMessagesForLoggedInUser(loginUser.get().getId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            if (!result.isEmpty()) {
                Object[] chatsBetweenUsersDesc = result.get(0);
                ChatDisplayDto chatDisplayDto = new ChatDisplayDto();
                chatDisplayDto.setUserId(chatsBetweenUsersDesc[0].toString());
                chatDisplayDto.setRoomId((Long) chatsBetweenUsersDesc[1]);
                chatDisplayDto.setName(chatsBetweenUsersDesc[2].toString());
                chatDisplayDto.setEmail(chatsBetweenUsersDesc[3].toString());
                chatDisplayDto.setProfilePic(chatsBetweenUsersDesc[4].toString());
                chatDisplayDto.setLastMessage(chatsBetweenUsersDesc[5].toString());
                String formattedDate;
                if (chatsBetweenUsersDesc[6] instanceof Timestamp) {
                    formattedDate = ((Timestamp) chatsBetweenUsersDesc[6]).toLocalDateTime().format(formatter);
                } else if (chatsBetweenUsersDesc[6] instanceof LocalDateTime) {
                    formattedDate = ((LocalDateTime) chatsBetweenUsersDesc[6]).format(formatter);
                } else {
                    formattedDate = chatsBetweenUsersDesc[6] != null ? chatsBetweenUsersDesc[6].toString() : "";
                }
                chatDisplayDto.setLastMessageTime(formattedDate);
                userChatContactData.setSingleEmployee(chatDisplayDto);

                List<ChatMessage> chatMessageList = this.chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatDisplayDto.getRoomId());
                List<ChatMessageDto> list = new ArrayList<>();
                if (!chatMessageList.isEmpty()) {
                    for (ChatMessage chatMessage : chatMessageList) {
                        ChatMessageDto chatMessageDto = new ChatMessageDto();
                        chatMessageDto.setId(chatMessage.getId().toString());
                        chatMessageDto.setSenderId(chatMessage.getSenderId().getId());
                        chatMessageDto.setReceiverId(chatMessage.getReceiverId().getId());
                        chatMessageDto.setMessage(chatMessage.getMessage());


                        String formattedDate2;
                        if (chatMessage.getTimestamp() != null) {
                            formattedDate = chatMessage.getTimestamp().format(formatter);
                            chatMessageDto.setTimestamp(formattedDate);
                        } else {
                            chatMessageDto.setTimestamp("");
                        }
                        chatMessageDto.setSender(chatMessage.getSenderId().getId().equalsIgnoreCase(loginUser.get().getId()) ? "You" : chatMessage.getSenderId().getName());
                        list.add(chatMessageDto);
                    }

                    userChatContactData.setChatMessageDtos(list);

                }
                else {
                    userChatContactData.setChatMessageDtos(null);
                }

            } else {
                userChatContactData.setSingleEmployee(null);
            }
            List<ChatDisplayDto> list = new ArrayList<>();
            if (!latestMessagesForLoggedInUser.isEmpty()) {

                for (Object[] data : latestMessagesForLoggedInUser) {
                    ChatDisplayDto chatDisplayDto = new ChatDisplayDto();
                    chatDisplayDto.setRoomId((Long) data[0]);
                    chatDisplayDto.setUserId(data[1].toString());
                    chatDisplayDto.setName(data[2].toString());
                    chatDisplayDto.setEmail(data[3].toString());
                    chatDisplayDto.setProfilePic(data[4].toString());

                    chatDisplayDto.setLastMessage(data[5].toString());
                    String formattedDate;
                    if (data[6] instanceof Timestamp) {
                        formattedDate = ((Timestamp) data[6]).toLocalDateTime().format(formatter);
                    } else if (data[6] instanceof LocalDateTime) {
                        formattedDate = ((LocalDateTime) data[6]).format(formatter);
                    } else {
                        formattedDate = data[6] != null ? data[6].toString() : "";
                    }
                    chatDisplayDto.setLastMessageTime(formattedDate);

                    list.add(chatDisplayDto);

                }
                userChatContactData.setChatDisplayDtos(list);
            } else {
                userChatContactData.setChatDisplayDtos(List.of());
            }

            return userChatContactData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<ChatMessageDto> getChatMessages(Long roomId, String loggedInUserId) {
        User user = this.userRepository.findByEmail(loggedInUserId).orElseThrow(
                () -> new ResourceNotFoundException("User", "Email", loggedInUserId)
        );
        try {
            List<ChatMessage> chatMessageList = this.chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
            List<ChatMessageDto> list = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            for (ChatMessage chatMessage : chatMessageList) {
                ChatMessageDto chatMessageDto = new ChatMessageDto();
                chatMessageDto.setId(chatMessage.getId().toString());
                chatMessageDto.setSenderId(chatMessage.getSenderId().getId());
                chatMessageDto.setReceiverId(chatMessage.getReceiverId().getId());
                chatMessageDto.setMessage(chatMessage.getMessage());


                String formattedDate;
                if (chatMessage.getTimestamp() != null) {
                    formattedDate = chatMessage.getTimestamp().format(formatter);
                    chatMessageDto.setTimestamp(formattedDate);
                } else {
                    chatMessageDto.setTimestamp("");
                }
                chatMessageDto.setSender(chatMessage.getSenderId().getId().equalsIgnoreCase(user.getId()) ? "You" : chatMessage.getSenderId().getName());
                list.add(chatMessageDto);
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
