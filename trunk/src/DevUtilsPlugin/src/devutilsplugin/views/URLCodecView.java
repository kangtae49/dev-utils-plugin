package devutilsplugin.views;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class URLCodecView extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.URLCodecView";
	final int pad_frame = 10;
	final int pad_ctrl = 5;

	public URLCodecView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		FormData layoutData = null;
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		
		Label lblCharset = new Label(parent, SWT.NONE);
		final Combo cboCharset = new Combo(parent, SWT.BORDER);
		final Text txtDEC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		final Text txtENC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		Button btnDEC = new Button(parent, SWT.PUSH);
		Button btnENC = new Button(parent, SWT.PUSH);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(0, pad_ctrl);
		lblCharset.setLayoutData(layoutData);
		lblCharset.setText("Charset : ");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(lblCharset, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.height = 100;
		cboCharset.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharset, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(50, -pad_ctrl-40);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtDEC.setLayoutData(layoutData);
		txtDEC.setText("Korea(´ëÇÑ¹Î±¹)");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharset, pad_frame);
		layoutData.left = new FormAttachment(50, pad_ctrl+40);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtENC.setLayoutData(layoutData);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharset, pad_frame);
		layoutData.left = new FormAttachment(txtDEC, pad_ctrl);
		layoutData.right = new FormAttachment(txtENC, -pad_ctrl);
		btnENC.setLayoutData(layoutData);
		btnENC.setText("=>encode");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnENC, pad_ctrl);
		layoutData.left = new FormAttachment(txtDEC, pad_ctrl);
		layoutData.right = new FormAttachment(txtENC, -pad_ctrl);
		btnDEC.setLayoutData(layoutData);
		btnDEC.setText("<=decode");
		
		Map charsets = Charset.availableCharsets();
	    Iterator iterator = charsets.values().iterator();
		while (iterator.hasNext()) {
			Charset cs = (Charset) iterator.next();
			cboCharset.add(cs.displayName());
//			System.out.print(cs.displayName());
//			if (cs.isRegistered()) {
//				System.out.print(" (registered): ");
//			} else {
//				System.out.print(" (unregistered): ");
//			}
//			System.out.print(cs.name());
//			Iterator names = cs.aliases().iterator();
//			while (names.hasNext()) {
//				System.out.print(", ");
//				System.out.print(names.next());
//			}
//			System.out.println();
		}
		cboCharset.setText(Charset.defaultCharset().displayName());

		btnENC.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtENC.setText("");
				String decData = txtDEC.getText();
		    	String charName = cboCharset.getText();
		    	String encData = "";

		    	URLCodec uc = new URLCodec();
	    		try {
	    			encData = uc.encode(decData, charName);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

		    	txtENC.append(encData);
		    }
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnDEC.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtDEC.setText("");
				String encData = txtENC.getText();
		    	String charName = cboCharset.getText();
		    	String decData = "";
		    	
		    	URLCodec uc = new URLCodec();
		    	try {
					decData = uc.decode(encData, charName);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (DecoderException e1) {
					e1.printStackTrace();
				}
		    	txtDEC.append(decData);
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
