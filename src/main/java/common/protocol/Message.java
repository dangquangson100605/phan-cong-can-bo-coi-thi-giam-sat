package common.protocol;

import java.io.Serializable;

/**
 * Wrapper message cho giao tiếp Client-Server qua Socket.
 * Chứa loại message và dữ liệu đính kèm.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private Object data;

    public Message() {}

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    @Override
    public String toString() {
        return "Message{type=" + type + ", data=" + (data != null ? data.getClass().getSimpleName() : "null") + "}";
    }
}
