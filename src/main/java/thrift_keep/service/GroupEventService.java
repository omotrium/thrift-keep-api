package thrift_keep.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GroupEventService {

    @Autowired
    private MyWebSocketHandler webSocketHandler;

    public void notifyGroupUpdate(Long groupId) {
        try {
            webSocketHandler.sendMessageToAll("GROUP_UPDATED:" + groupId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
