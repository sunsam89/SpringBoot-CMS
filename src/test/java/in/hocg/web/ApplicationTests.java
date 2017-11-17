package in.hocg.web;

import in.hocg.web.modules.domain.Menu;
import in.hocg.web.modules.domain.Role;
import in.hocg.web.modules.domain.User;
import in.hocg.web.modules.domain.repository.MenuRepository;
import in.hocg.web.modules.domain.repository.RoleRepository;
import in.hocg.web.modules.domain.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by hocgin on 2017/10/25.
 * email: hocgin@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    
    // 注入虚拟 TestController 对象
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MenuRepository permissionRepository;
    
    @Test
    public void testTestControllerIndex() throws Exception {
        User user = new User();
        Menu permission = new Menu();
        permission.setName("用户添加");
        permission.setAvailable(true);
        permission.setPermission("user.add");
        permission.setParent("0");
        permissionRepository.save(permission);
        
        Role role = new Role();
        role.setName("管理员");
        role.setAvailable(true);
        role.setPermissions(Arrays.asList(permission));
        role.setRole("ROLE_ADMIN");
        roleRepository.save(role);
    
        user.setUsername("rootx");
        user.setPassword("hocgin");
        user.setRole(Arrays.asList(role));
        userRepository.save(user);
    }
    
    
    @Test
    public void testFindRole() {
        List<Role> all = roleRepository.findAll();
        Collection<Menu> permissions = all.get(0).getPermissions();
        System.out.println(permissions);
        
    }
    
    @Test
    public void testAdminHtml() {
    
    }
}
