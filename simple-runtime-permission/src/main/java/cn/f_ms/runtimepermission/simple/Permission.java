package cn.f_ms.runtimepermission.simple;

/**
 * Permission Bean
 *
 * @author f-ms
 * @time 2017/4/25
 */
public final class Permission {

    public final String name;
    public final boolean isGranted;
    public final boolean isShouldShowRequestPermissionRationale;

    public Permission(String name, boolean isGranted) { this(name, isGranted, false); }
    public Permission(String name, boolean isGranted, boolean isShouldShowRequestPermissionRationale) {
        this.name = name;
        this.isGranted = isGranted;
        this.isShouldShowRequestPermissionRationale = isShouldShowRequestPermissionRationale;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (isGranted != that.isGranted) return false;
        if (isShouldShowRequestPermissionRationale != that.isShouldShowRequestPermissionRationale)
            return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isGranted ? 1 : 0);
        result = 31 * result + (isShouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", isGranted=" + isGranted +
                ", isShouldShowRequestPermissionRationale=" + isShouldShowRequestPermissionRationale +
                '}';
    }
}
