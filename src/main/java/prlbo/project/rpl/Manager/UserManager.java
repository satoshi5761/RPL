package prlbo.project.rpl.Manager;

import prlbo.project.rpl.data.User;

public class UserManager {
    public static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
