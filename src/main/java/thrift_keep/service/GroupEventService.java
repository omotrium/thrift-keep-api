package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
//@RequiredArgsConstructor
public class GroupEventService {

    @Autowired
    private MyWebSocketHandler webSocketHandler;


    //private final SimpMessagingTemplate messagingTemplate;

//    public void notifyGroupUpdate(Long groupId) {
//        messagingTemplate.convertAndSend(
//                "/topic/groups/" + groupId,
//                "UPDATE"
//        );
//    }

    public void notifyGroupUpdate(Long groupId) {
        try {
            webSocketHandler.sendMessageToAll("GROUP_UPDATED:" + groupId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
