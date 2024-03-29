package gobo.controller.dump;

import gobo.AuthSubBase;
import gobo.model.GbControl;
import gobo.service.GbSpreadsheetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class StartController extends AuthSubBase {

	private static final Logger logger = Logger.getLogger(StartController.class.getName());

	@Override
	protected String runAuth() throws Exception {

		final String[] kinds = request.getParameterValues("kindArray");
		final String token = sessionScope("token");
		final UserService user = UserServiceFactory.getUserService();
		final User currentUser = user.getCurrentUser();

		// Craete new spreadsheet
		GbSpreadsheetService gss = new GbSpreadsheetService(token);
		SpreadsheetEntry spreadsheet;
		while (true) {
			try {
				spreadsheet = gss.createSpreadsheet(Arrays.asList(kinds));
				break;
			} catch (IOException ioe) {
				// retry when timeout.
			}
		}
		logger.fine("ssKey=" + spreadsheet.getKey());

		List<Key> putKeys = null;
		try {
			// Prepare control table.
			Key controlId = datastore.allocateIds("dump", 1).getStart();
			List<Entity> list = new ArrayList<Entity>();
			Queue queue = QueueFactory.getDefaultQueue();
			List<TaskOptions> taskList = Lists.newArrayList();
			int countDown = 10000;
			for (int i = 0; i < kinds.length; i++) {
				Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, kinds[i]);
				Entity control = new Entity(childKey);
				control.setProperty(GbControl.KIND_NAME, kinds[i]);
				control.setProperty(GbControl.COUNT, 0);
				if (currentUser != null) {
					control.setProperty(GbControl.REPORT_TO, new Email(currentUser.getEmail()));
				}
				control.setProperty(GbControl.AUTH_SUB_TOKEN, token);
				control.setProperty(GbControl.SPREADSHEET_KEY, spreadsheet.getKey());
				control.setProperty(GbControl.UPDATE_DATE, new Date());
				list.add(control);

				// Start task queue chain for each kind.
				taskList
					.add(TaskOptions.Builder.url("/tasks/dump.gobo").param(
						"controlKey",
						KeyFactory.keyToString(childKey)).countdownMillis(countDown).method(
						Method.GET));

				// to avoid the collision on the spreadsheet.
				countDown += 500;
			}
			putKeys = datastore.put(list);
			queue.add(taskList);

		} catch (Exception e) {
			if (putKeys != null) {
				try {
					datastore.delete(putKeys);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			throw e;
		}

		return redirect("started.gobo?docURL="
			+ response.encodeRedirectURL(spreadsheet.getHtmlLink().getHref()));
	}
}
