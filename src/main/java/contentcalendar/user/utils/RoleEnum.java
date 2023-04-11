package contentcalendar.user.utils;

public enum RoleEnum {
    ROLE_USER(1),
    ROLE_MANAGER(5),
    ROLE_ADMIN(3);

    private final int role;

    RoleEnum(int i) {
        this.role = i;
    }
    public int asRole() {
        return this.role;
    }
}
