/**
 * IndexController.java.java
 * @author FengMy
 * @since 2015年6月30日
 */
package plugin.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import plugin.portal.context.WebContext;
import plugin.portal.permission.PermissionManager;
import plugin.portal.permission.SystemMenu;
import plugin.portal.permission.SystemPage;

/**  
 * 功能描述：主页
 * 
 * @author FengMy
 * @since 2015年6月30日
 */
@Controller
public class IndexController extends BaseController {
//	@Autowired
//	private AdminUserService userService;
	
	@Autowired
	private PermissionManager permissionManager;

	@RequestMapping(value="index")
	public String index(@RequestParam(value="_m",required=false)String menu,@RequestParam(value="_p",required=false)String page,ModelMap model){
//		AdminUser user = WebContext.getLoginUser();
		model.put("user", WebContext.getLoginUser());		
		//TODO 要验证权限获取菜单列表
		StringBuilder openMenus = new StringBuilder();
		SystemMenu activeMenu = permissionManager.getDefaultMenu();
		SystemPage activePage = permissionManager.getDefaultPage();
		List<SystemMenu> menus = permissionManager.getMenus();
		if(menu != null){
			SystemMenu openSystemMenu = permissionManager.getMenu(menu);
			if(openSystemMenu != null){
				activeMenu = openSystemMenu;
				SystemPage systemPage = openSystemMenu.getPageMap().get(page);
				if(systemPage != null){
					activePage = systemPage;
				}
			}
		}
		SystemMenu tempMenu = activeMenu;
		do{
			openMenus.append(tempMenu.getId()).append(";");
			tempMenu = tempMenu.getParentMenu();
		}while(tempMenu != null);
		model.put("menus", menus);
		model.put("openMenus", openMenus.toString());
		model.put("activeMenu", activeMenu);
		model.put("activePage", activePage);
		return "index";
	}
}
