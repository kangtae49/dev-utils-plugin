package devutilsplugin.views;


//import org.eclipse.core.filesystem.EFS;
//import org.eclipse.core.filesystem.IFileStore;
//import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TestView extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.TestView";
	final int pad_frame = 10;
	final int pad_ctrl = 5;

	public TestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		FormData layoutData = null;
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		Button btnTest = new Button(parent, SWT.PUSH);
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_ctrl);
		layoutData.left = new FormAttachment(0, pad_ctrl);
		btnTest.setLayoutData(layoutData);
		btnTest.setText("<=decode");
		btnTest.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

	}

	@Override
	public void setFocus() {

	}

}
