package gobo.controller.tasks;

import gobo.model.Control;
import gobo.service.GbMailService;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

public class RestoreEndController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final Key controlId = asKey("controlId");
		final String wsTitle = asString("wsTitle");

		// Delete control row.
		Key childKey = Datastore.createKey(controlId, Control.class, wsTitle);
		Datastore.delete(childKey);

		List<Control> list = Datastore.query(Control.class, controlId).asList();
		if ((list == null) || (list.size() == 0)) {

			// Mail
			GbMailService.sendMail(controlId.getId(), "Restore");
			System.out.println("終了");
		}

		return null;
	}

}