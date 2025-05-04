import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements Serializable {
    private final List<User> users;
    private static UserManager instance; // 单例模式

    private UserManager() {
        users = new ArrayList<>();
    }

    // 获取 UserManager 实例
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 添加用户
    public void addUser(User user) {
        if (!userExists(user.getUsername())) {
            users.add(user);
        }
    }

    // 检查用户是否存在
    public boolean userExists(String username) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return new ArrayList<>(users); // 返回副本以保护内部数据
    }

    // 用户登录验证
    public User findUser(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) &&
                           u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // 根据用户名查找用户
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // 根据角色获取用户列表
    public List<User> getUsersByRole(String role) {
        List<User> roleUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals(role)) {
                roleUsers.add(user);
            }
        }
        return roleUsers;
    }

    // 删除用户
    public boolean removeUser(String username) {
        return users.removeIf(u -> u.getUsername().equals(username));
    }

    // 更新用户信息
    public boolean updateUser(String username, String newPassword) {
        User user = getUserByUsername(username);
        if (user != null) {
            // 找到用户在列表中的索引
            int index = users.indexOf(user);
            // 创建新用户对象并更新到列表中
            users.set(index, new User(username, newPassword, user.getRole()));
            return true;
        }
        return false;
    }

    // 清空所有用户
    public void clearUsers() {
        users.clear();
    }
}