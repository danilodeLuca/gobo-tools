package gobo;

import gobo.slim3.tester.MockHttpServletRequest;
import gobo.slim3.tester.MockHttpServletResponse;
import gobo.slim3.tester.MockServletContext;

public class ControllerTester {

	public MockHttpServletResponse response;
	public MockHttpServletRequest request;

	public ControllerTester() {
		this.request = new MockHttpServletRequest(new MockServletContext());
		this.response = new MockHttpServletResponse();
	}

	@SuppressWarnings("unchecked")
	public String start(String uri) throws Exception {

		uri = uri.replaceAll("/", ".");
		int pos = uri.lastIndexOf('.');
		String subPackage = uri.substring(0, pos + 1);
		String className = uri.substring(pos + 1);
		char chars[] = className.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		className = new String(chars);
		final String fullName = "gobo.controller" + subPackage + className + "Controller";

		Class clazz = Class.forName(fullName);
		ControllerBase test = (ControllerBase) clazz.newInstance();

		test.request = this.request;
		test.response = this.response;

		String run = test.run();

		this.request = (MockHttpServletRequest) test.request;
		this.response = (MockHttpServletResponse) test.response;

		return run;
	}

}
