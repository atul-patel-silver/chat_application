package org.scm.chat.chat.mapper;

import org.scm.chat.chat.dto.*;
import org.scm.chat.chat.model.ChatMessage;
import org.scm.chat.chat.model.ChatParticipant;
import org.scm.chat.chat.model.ChatRoom;
import org.scm.chat.chat.model.MessagesStatus;
import org.scm.chat.contact.dto.ContactDto;
import org.scm.chat.contact.model.Contact;
import org.scm.chat.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomMapper {


    public  static ChatRoomDto chatRoomAndParticipantMapToChatRoomDto(ChatRoom chatRoom, ChatParticipant chatParticipant){

        return ChatRoomDto.builder().id(chatRoom.getId())
                .name(chatRoom.getName())
                .participants(List.of(chatParticipantToChatParticipantDto(chatParticipant)))
                .groupImage(chatRoom.getGroupImage())
                .type(String.valueOf(chatRoom.getType())).build();

    }

    public static UserDto userToUserDto(User user){

        return UserDto.builder().id(user.getId())
               .email(user.getEmail())
               .name(user.getName())
               .profilePic(user.getProfilePic())
               .phoneNumber(user.getPhoneNumber())
               .build();
    }

    public static ChatParticipantDto chatParticipantToChatParticipantDto(ChatParticipant chatParticipant){

        return ChatParticipantDto.builder().id(chatParticipant.getId())
                .user(userToUserDto(chatParticipant.getUser()))
                .isAdmin(chatParticipant.isAdmin())
                .joinedAt(chatParticipant.getJoinedAt().toString())
                .build();
    }


    public static  List<ChatRoomDto> listOFChatParticipantAndListOfChatRoomToChatRoomDto(List<ChatRoom> chatRooms){
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
             List<ChatParticipant> participants = chatRoom.getParticipants();
                List<ChatParticipantDto> chatParticipantDtos = new ArrayList<>();
                for (ChatParticipant chatParticipant : participants) {
                     ChatParticipantDto chatParticipantDto = chatParticipantToChatParticipantDto(chatParticipant);
                        chatParticipantDtos.add(chatParticipantDto);
                }
             ChatRoomDto chatRoomDto = ChatRoomDto.builder().id(chatRoom.getId())
                    .name(chatRoom.getName())
                    .participants(chatParticipantDtos)
                    .groupImage(chatRoom.getGroupImage())
                    .type(String.valueOf(chatRoom.getType())).build();

                chatRoomDtos.add(chatRoomDto);

        }

        return chatRoomDtos;
    }



    public static ChatMessageDtoForGroup toEntity(KafkaMessageDto kafkaMessageDto,User user) {
        if (kafkaMessageDto == null) {
            return null;
        } else {
            ChatMessageDtoForGroup.ChatMessageDtoForGroupBuilder dtoForGroupBuilder = ChatMessageDtoForGroup.builder();
            dtoForGroupBuilder.chatRoomId(kafkaMessageDto.getChatRoomId());
            dtoForGroupBuilder.senderId(kafkaMessageDto.getSenderId());
            dtoForGroupBuilder.message(kafkaMessageDto.getMessage());
            dtoForGroupBuilder.timestamp(kafkaMessageDto.getTimestamp());
            dtoForGroupBuilder.status(ChatMessage.MessageStatus.SENT.name());
            dtoForGroupBuilder.senderUser(userToUserDto(user));
            return dtoForGroupBuilder.build();
        }
    }


    public static  MessagesStatusDto messagesStatusToMessagesStatusDto(MessagesStatus messagesStatus){
        if (messagesStatus == null) {
            return null;
        } else {
            MessagesStatusDto.MessagesStatusDtoBuilder messagesStatusDtoBuilder = MessagesStatusDto.builder();
            messagesStatusDtoBuilder.id(messagesStatus.getId());
            messagesStatusDtoBuilder.user(userToUserDto(messagesStatus.getUser()));
            messagesStatusDtoBuilder.seenAt(messagesStatus.getSeenAt());
            return messagesStatusDtoBuilder.build();
        }
    }


    public static  List<MessagesStatusDto> messagesStatusToMessagesStatusDtoList(List<MessagesStatus> messagesStatus){
        if (messagesStatus.isEmpty()) {
            return List.of();
        } else {
            List<MessagesStatusDto> messagesStatusDto= new ArrayList<>();
            for (MessagesStatus messagesStatus1 : messagesStatus) {
                MessagesStatusDto.MessagesStatusDtoBuilder messagesStatusDtoBuilder = MessagesStatusDto.builder();
                messagesStatusDtoBuilder.id(messagesStatus1.getId());
                messagesStatusDtoBuilder.user(userToUserDto(messagesStatus1.getUser()));
                messagesStatusDtoBuilder.seenAt(messagesStatus1.getSeenAt());
               messagesStatusDto.add(messagesStatusDtoBuilder.build());
            }
          return messagesStatusDto;
        }
    }
}
