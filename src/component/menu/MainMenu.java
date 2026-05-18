package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import component.AppContext;
import component.AppFrame;
import domain.User;

public class MainMenu implements AppContext {

	private final Container parent;
	private final Dimension size = new Dimension(1480, 960);

	// Containers section
	private JPanel rootContainer;
	private JPanel bodyContainer;
	private JPanel mainContainer;
	private JPanel sidebarContainer;
	private JPanel navbarContainer;

	private Sidebar sidebar;
	private NavigationBar navigationBar;

	public MainMenu(Container parent) {
		this.parent = parent;

		// Container initialization
		HomePage homePage = new HomePage(this.rootContainer, new Dimension(1130, size.height - 70));
		rootContainer = new JPanel();
		bodyContainer = new JPanel();
		mainContainer = (JPanel) homePage.getRootComponent();
		sidebar = new Sidebar(this.rootContainer, new Dimension(350, size.height - 70));
		sidebar.init();
		sidebarContainer = (JPanel) sidebar.getRootComponent();

		navigationBar = new NavigationBar(this.rootContainer, new Dimension(size.width, 70));
		navbarContainer = (JPanel) navigationBar.getRootComponent();

		// Root section
		rootContainer.setLayout(new BorderLayout());
		rootContainer.setPreferredSize(size);
		rootContainer.add(navbarContainer, BorderLayout.NORTH);
		rootContainer.add(bodyContainer, BorderLayout.SOUTH);
		rootContainer.setBorder(new LineBorder(Color.black, 3));
		rootContainer.setBackground(Color.gray);

		// Body section
		bodyContainer.setLayout(new BorderLayout(0, 0));
		bodyContainer.setPreferredSize(new Dimension(size.width, size.height - 80));
		bodyContainer.setBackground(Color.yellow);
		bodyContainer.add(sidebarContainer, BorderLayout.WEST);
		bodyContainer.add(mainContainer);

	}

	public void loadUser() {
		navigationBar.loadUser();
		UserProfile userProfile = (UserProfile) AppFrame.getInstance().getContextPools().getContext("userProfile");
		userProfile.loadUser();
	}

	public void loadDialogDetail(User user) {
		bodyContainer.remove(mainContainer);
		UserDialog userDialog = new UserDialog(this.rootContainer, new Dimension(1130, size.height - 70),
				List.of(user));
		mainContainer = (JPanel) userDialog.getRootComponent();
		bodyContainer.add(mainContainer);
		bodyContainer.revalidate();
		bodyContainer.repaint();
	}

	@Override
	public void draw() {
		this.parent.setSize(this.getSize());
		this.parent.setMinimumSize(this.getSize());
		this.parent.add(this.rootContainer);
	}

	@Override
	public void switchContext(String newContext) {
		this.parent.remove(this.rootContainer);

		AppFrame appFrame = AppFrame.getInstance();
		AppContext context = appFrame.getContextPools().getContext(newContext);
		this.parent.setSize(context.getSize());
		this.parent.add(context.getRootComponent());
		this.parent.repaint();
	}

	@Override
	public Component getRootComponent() {
		return this.rootContainer;
	}

	@Override
	public Dimension getSize() {
		return new Dimension((int) this.size.getWidth(), (int) this.size.getHeight() + 35);
	}

}
