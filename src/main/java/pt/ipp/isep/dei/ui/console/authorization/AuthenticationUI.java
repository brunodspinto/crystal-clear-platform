package pt.ipp.isep.dei.ui.console.authorization;

import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.ui.console.menu.AdminUI;
import pt.ipp.isep.dei.ui.console.menu.CitizenUI;
import pt.ipp.isep.dei.ui.console.menu.EthicsCommitteeUI;
import pt.ipp.isep.dei.ui.console.menu.JournalistUI;
import pt.ipp.isep.dei.ui.console.menu.PoliticalAgentUI;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The type Authentication ui.
 */
public class AuthenticationUI implements Runnable {
    private final AuthenticationController ctrl;

    /**
     * Instantiates a new Authentication ui.
     */
    public AuthenticationUI() {
        ctrl = new AuthenticationController();
    }

    public void run() {
        boolean success = doLogin();

        if (success) {
            List<UserRoleDTO> roles = this.ctrl.getUserRoles();
            if ((roles == null) || (roles.isEmpty())) {
                System.out.println("No role assigned to user.");
            } else {
                UserRoleDTO role = selectsRole(roles);
                if (!Objects.isNull(role)) {
                    List<MenuItem> rolesUI = getMenuItemForRoles();
                    this.redirectToRoleUI(rolesUI, role);
                } else {
                    System.out.println("No role selected.");
                }
            }
        }
        this.logout();
    }

    /**
     * Builds the list of role-to-UI mappings used to redirect the user after login.
     *
     * @return list of {@link MenuItem} associating each role with its menu UI.
     */
    private List<MenuItem> getMenuItemForRoles() {
        List<MenuItem> rolesUI = new ArrayList<>();
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_ADMIN, new AdminUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_CITIZEN, new CitizenUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_POLITICAL_AGENT, new PoliticalAgentUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_JOURNALIST, new JournalistUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_ETHICS_COMMITTEE, new EthicsCommitteeUI()));
        return rolesUI;
    }

    private boolean doLogin() {
        System.out.println("\n\n--- LOGIN UI ---------------------------");

        int maxAttempts = 3;
        boolean success = false;
        do {
            maxAttempts--;
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            String pwd = Utils.readLineFromConsole("Enter Password: ");

            success = ctrl.doLogin(id, pwd);
            if (!success) {
                System.out.println("Invalid UserId and/or Password. \n You have  " + maxAttempts + " more attempt(s).");
            }

        } while (!success && maxAttempts > 0);
        return success;
    }

    private void logout() {
        ctrl.doLogout();
    }

    private void redirectToRoleUI(List<MenuItem> rolesUI, UserRoleDTO role) {
        boolean found = false;
        Iterator<MenuItem> it = rolesUI.iterator();
        while (it.hasNext() && !found) {
            MenuItem item = it.next();
            found = item.hasDescription(role.getDescription());
            if (found) {
                item.run();
            }
        }
        if (!found) {
            System.out.println("There is no UI for users with role '" + role.getDescription() + "'");
        }
    }

    private UserRoleDTO selectsRole(List<UserRoleDTO> roles) {
        if (roles.size() == 1) {
            return roles.get(0);
        } else {
            return (UserRoleDTO) Utils.showAndSelectOne(roles, "Select the role you want to adopt in this session:");
        }
    }
}