package iposca.service;

import iposca.dao.UserDAO;
import iposca.model.User;

public class AuthService {
    private static User currentUser = null;
    private static UserDAO userDAO = new UserDAO();

    public static boolean login(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);
            if (user != null && user.isActive() && user.getPasswordHash().equals(password)) {
                currentUser = user;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getCurrentUser() { return currentUser; }
    public static boolean isAdmin() { return currentUser != null && currentUser.getRole().equals("Admin"); }
    public static boolean isManager() { return currentUser != null && currentUser.getRole().equals("Manager"); }
    public static void logout() { currentUser = null; }
}