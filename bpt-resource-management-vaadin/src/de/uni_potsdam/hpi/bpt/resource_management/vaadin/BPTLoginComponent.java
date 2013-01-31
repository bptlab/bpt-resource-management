package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginComponent extends CustomComponent implements Property.ValueChangeListener {
	
//	class WorkThread extends Thread {
//	    public void run () {
//	    	try {
//	    		redirectToOpenIDProvider();
//	    	}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//	    }
//	}
		
	private VerticalLayout layout;
	private Button loginWindowButton;
	private Button logoutButton;
	private Button registerButton;
	private Label welcomeLabel;
	private Window subWindow;
	private BPTNavigationBar navigationBar;
	private BPTSidebar sidebar;
	private static final String[] openIdProviders = new String[] { "Google", "Yahoo" };
	private String openIdProvider = openIdProviders[0];
	
	public BPTLoginComponent(boolean isLoggedIn, BPTSidebar sidebar){
		
		this.sidebar = sidebar;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		navigationBar = new BPTNavigationBar();
		
		if (isLoggedIn) {
			layout.addComponent(navigationBar);
			addLogoutButton();
		}
		else {
			addLoginButton();
		}
		
		
	}

	private void addLoginButton() {
		loginWindowButton = new Button("Login");
        loginWindowButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(loginWindowButton);
        
        loginWindowButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {			
//					final WorkThread thread = new WorkThread();
//			        thread.start();
			        try {
						redirectToOpenIDProvider();
					} catch (IOException e) {
						e.printStackTrace();
					}
//					createSubWindow(manager, association);
//					getWindow().addWindow(subWindow);
			}});
        
        NativeSelect openIdProviderNativeSelect = new NativeSelect("OpenID provider");
        for (String openIdProvider : openIdProviders) {
        	openIdProviderNativeSelect.addItem(openIdProvider);
        }
        openIdProviderNativeSelect.setNullSelectionAllowed(false);
        openIdProviderNativeSelect.setValue(openIdProviders[0]);
        openIdProviderNativeSelect.setImmediate(true);
        openIdProviderNativeSelect.addListener(this);
        
        layout.addComponent(openIdProviderNativeSelect);

	}

	private void addLogoutButton() {
		logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(logoutButton);
        
        logoutButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				application.setName("");
				application.setMailAddress("");
				application.setLoggedIn(false);
				application.setModerated(false);
				application.setOpenIdProvider("");
				application.finder();
				layout.removeAllComponents();
				addLoginButton();
				sidebar.logout();
			}});
		
	}

	private void createSubWindow(OpenIdManager manager, Association association){
			subWindow = new BPTLoginWindow("Login succesful!", this);
	}
	
	public void login(String name){
		layout.removeAllComponents();
		System.out.println(name);
		layout.addComponent(navigationBar);
		welcomeLabel = new Label("Hello " + ((BPTApplication) getApplication()).getName() + "!");
		layout.addComponent(welcomeLabel);
		addLogoutButton();
	}
	
	private void redirectToOpenIDProvider() throws IOException {
		OpenIdManager manager = new OpenIdManager();
		manager.setReturnTo("http://localhost:8080/bpt-resource-management-vaadin/");
        manager.setRealm("http://localhost:8080/");
		manager.setTimeOut(10000);
        Endpoint endpoint = manager.lookupEndpoint(openIdProvider);
        System.out.println(endpoint);
        Association association = manager.lookupAssociation(endpoint);
        System.out.println(association);
        String url = manager.getAuthenticationUrl(endpoint, association);
        System.out.println("Copy the authentication URL in browser:\n" + url);
        ((BPTApplication) getApplication()).getMainWindow().open(new ExternalResource(url), "_self");
//        System.out.println("After successfully sign on in browser, enter the URL of address bar in browser:");
//        String ret = readLine();
//        HttpServletRequest request = createRequest(ret);
//        Authentication authentication = manager.getAuthentication(request, association.getRawMacKey(), endpoint.getAlias());
//        System.out.println(authentication);

	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		openIdProvider = event.getProperty().toString();
		((BPTApplication) getApplication()).setOpenIdProvider(openIdProvider);
	}
	
//	private String readLine() throws IOException {
//        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
//        return r.readLine();
//    }
//
//    private static HttpServletRequest createRequest(String url) throws UnsupportedEncodingException {
//        int pos = url.indexOf('?');
//        if (pos==(-1))
//            throw new IllegalArgumentException("Bad url.");
//        String query = url.substring(pos + 1);
//        String[] params = query.split("[\\&]+");
//        final Map<String, String> map = new HashMap<String, String>();
//        for (String param : params) {
//            pos = param.indexOf('=');
//            if (pos==(-1))
//                throw new IllegalArgumentException("Bad url.");
//            String key = param.substring(0, pos);
//            String value = param.substring(pos + 1);
//            map.put(key, URLDecoder.decode(value, "UTF-8"));
//        }
//        return (HttpServletRequest) Proxy.newProxyInstance(
//                Application.class.getClassLoader(),
//                new Class[] { HttpServletRequest.class },
//                new InvocationHandler() {
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        if (method.getName().equals("getParameter"))
//                            return map.get((String)args[0]);
//                        throw new UnsupportedOperationException(method.getName());
//                    }
//                }
//        );
//    }

	
}
