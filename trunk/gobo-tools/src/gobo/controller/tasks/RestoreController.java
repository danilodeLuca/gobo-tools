package gobo.controller.tasks;

import gobo.ControllerBase;
import gobo.dto.GbEntity;
import gobo.model.GbControl;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class RestoreController extends ControllerBase {

	final Integer RANGE = 100;

	@Override
	protected String run() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity control = datastore.get(controlKey);
		final String ssKey = (String) control.getProperty(GbControl.SPREADSHEET_KEY);
		final String kind = (String) control.getProperty(GbControl.KIND_NAME);
		final Integer rowNum = new Integer((String) control.getProperty(GbControl.COUNT));
		final String token = (String) control.getProperty(GbControl.AUTH_SUB_TOKEN);
		System.out.println("Restoring kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		// Spreadsheetからデータを取得
		GbSpreadsheetService service = new GbSpreadsheetService(token);
		List<GbEntity> data = service.getDataOrNull(ssKey, kind, rowNum + 1, RANGE);

		if (data == null) {
			// Call the final task
			queue.add(TaskOptions.Builder.url("/tasks/RestoreEnd.gobo").param(
				"controlKey",
				KeyFactory.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Restoring to Datastore.
		GbDatastoreService datastoreUtil = new GbDatastoreService();
		datastoreUtil.restoreData(kind, data);

		// Update control row.
		control.setProperty(GbControl.COUNT, rowNum + RANGE);
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		datastore.put(control);

		// タスクチェーンを継続
		queue.add(TaskOptions.Builder.url("/tasks/Restore.gobo").param(
			"controlKey",
			KeyFactory.keyToString(controlKey)).method(Method.GET));

		return null;
	}
}
