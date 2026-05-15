package common.protocol;

/**
 * Enum định nghĩa các loại message trong giao tiếp Client-Server
 */
public enum MessageType {
    /** Client gửi dữ liệu yêu cầu phân công */
    ASSIGNMENT_REQUEST,

    /** Server trả kết quả phân công thành công */
    ASSIGNMENT_RESULT,

    /** Server trả thông báo lỗi */
    ERROR,

    /** Client yêu cầu xem lịch sử phân công */
    HISTORY_REQUEST,

    /** Server trả danh sách lịch sử phân công */
    HISTORY_RESULT
}
