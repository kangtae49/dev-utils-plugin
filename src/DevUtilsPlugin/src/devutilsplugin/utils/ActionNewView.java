package devutilsplugin.utils;

import java.util.UUID;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import devutilsplugin.Activator;

public class ActionNewView extends Action {

	private String _viewIdToOpen = null;

	public ActionNewView(String viewIdToOpen) {
		_viewIdToOpen = viewIdToOpen;
	}

	@Override
	public void run() {
		try {
			String secondaryId = UUID.randomUUID().toString();

			IWorkbenchWindow window = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					page.showView(_viewIdToOpen, secondaryId,
							IWorkbenchPage.VIEW_CREATE);
					IViewPart justActivated = page.showView(_viewIdToOpen,
							secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
