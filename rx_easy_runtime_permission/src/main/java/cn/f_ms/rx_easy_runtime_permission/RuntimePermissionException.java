package cn.f_ms.rx_easy_runtime_permission;

/**
 * Permission Refuse Exception
 */
public class RuntimePermissionException extends RuntimeException {

    private TYPE mExceptionType;

    public enum TYPE {
        USER_REFUSE, USER_REFUSE_TIPS, REFUSE_NEVER_ASK
    }

    public RuntimePermissionException(TYPE exceptionType) {
        super("RuntimePermissionException: Refuse, " + exceptionType.toString());

        mExceptionType = exceptionType;
    }

    public TYPE type() {
        return mExceptionType;
    }
}