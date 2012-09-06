package devutilsplugin.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class DigestUtilView extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.DigestUtilView";
	final int pad_frame = 10;
	final int pad_ctrl = 5;
	final int DIGEST_MD5 = 0;
	final int DIGEST_SHA = 1;
	final int DIGEST_SHA256 = 2;
	final int DIGEST_SHA384 = 3;
	final int DIGEST_SHA512 = 4;

	public DigestUtilView() {
	}

	@Override
	public void createPartControl(final Composite parent) {
		
		FormData layoutData = null;
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		
		Label lblCharset = new Label(parent, SWT.NONE);
		Label lblDigest = new Label(parent, SWT.NONE);
		final Combo cboCharset = new Combo(parent, SWT.BORDER);
		final Combo cboDigest = new Combo(parent, SWT.BORDER|SWT.READ_ONLY);
		final Text txtDEC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		final Text txtENC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
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
		layoutData.top = new FormAttachment(cboCharset, pad_ctrl);
		layoutData.left = new FormAttachment(0, pad_ctrl);
		lblDigest.setLayoutData(layoutData);
		lblDigest.setText("Digest : ");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharset, pad_frame);
		layoutData.left = new FormAttachment(lblDigest, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.height = 100;
		cboDigest.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboDigest, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(50, -pad_ctrl-40);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtDEC.setFont(new Font(parent.getDisplay(), "Fixedsys", 12, SWT.NONE));
		txtDEC.setLayoutData(layoutData);
		txtDEC.setBackground(new Color(parent.getDisplay(), 243, 246, 250));
		txtDEC.setText("Input string or Drag&Drop File(s)!");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboDigest, pad_frame);
		layoutData.left = new FormAttachment(50, pad_ctrl+40);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtENC.setFont(new Font(parent.getDisplay(), "Fixedsys", 12, SWT.NONE));
		txtENC.setBackground(new Color(parent.getDisplay(), 243, 246, 250));
		txtENC.setLayoutData(layoutData);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboDigest, pad_frame);
		layoutData.left = new FormAttachment(txtDEC, pad_ctrl);
		layoutData.right = new FormAttachment(txtENC, -pad_ctrl);
		btnENC.setLayoutData(layoutData);
		btnENC.setText("=>encode");
		
		Map<String, Charset> charsets = Charset.availableCharsets();
	    Iterator<Charset> iterator = charsets.values().iterator();
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

		cboDigest.add("md5", DIGEST_MD5);
		cboDigest.add("sha", DIGEST_SHA);
		cboDigest.add("sha256", DIGEST_SHA256);
		cboDigest.add("sha384", DIGEST_SHA384);
		cboDigest.add("sha512", DIGEST_SHA512);
		cboDigest.setText("md5");
		btnENC.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String decData = txtDEC.getText();
		    	String charName = cboCharset.getText();
		    	String encData = "";

		    	byte [] byteData = null;
		    	try {
					byteData = decData.getBytes(charName);
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
				}

				int digestType = cboDigest.getSelectionIndex();
				encData = digest(byteData, digestType);

		    	txtENC.append(encData + "\n");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		
		
		
		
		DropTargetListener dtl = new DropTargetListener() {

			@Override
			public void dropAccept(DropTargetEvent event) {
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (FileTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
			    	String encData = "";
					int digestType = cboDigest.getSelectionIndex();

			    	String[] files = (String[]) event.data;
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < files.length; i++) {
						File fd = new File(files[i]);
						FileInputStream is = null;
						try {
							is = new FileInputStream(fd);
							encData = digest(is, digestType);
							sb.append(encData + "\t" + fd.getAbsolutePath() + "\n");
						} catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(is != null){
								try {
									is.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
					}
					txtENC.append(sb.toString());
				}
			}

			@Override
			public void dragOver(DropTargetEvent event) {
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}
		};
		DropTarget dropTarget = new DropTarget(txtDEC, DND.DROP_COPY
				| DND.DROP_DEFAULT);
		Transfer[] transfers = new Transfer[] { FileTransfer.getInstance() };

		dropTarget.setTransfer(transfers);
		dropTarget.addDropListener(dtl);

		dropTarget = new DropTarget(txtENC, DND.DROP_COPY
				| DND.DROP_DEFAULT);
		transfers = new Transfer[] { FileTransfer.getInstance() };

		dropTarget.setTransfer(transfers);
		dropTarget.addDropListener(dtl);
	

	}

	@Override
	public void setFocus() {

	}
	
	String digest(byte [] byteData, int digestType){
		String encData = "";
		try {
			switch (digestType) {
			case DIGEST_MD5:
				encData = DigestUtils.md5Hex(byteData);
				break;
			case DIGEST_SHA:
				encData = DigestUtils.shaHex(byteData);
				break;
			case DIGEST_SHA256:
				encData = DigestUtils.sha256Hex(byteData);
				break;
			case DIGEST_SHA384:
				encData = DigestUtils.sha384Hex(byteData);
				break;
			case DIGEST_SHA512:
				encData = DigestUtils.sha512Hex(byteData);
				break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return encData;
	}

	String digest(InputStream is, int digestType){
		String encData = "";
		try {
			switch (digestType) {
			case DIGEST_MD5:
				encData = DigestUtils.md5Hex(is);
				break;
			case DIGEST_SHA:
				encData = DigestUtils.shaHex(is);
				break;
			case DIGEST_SHA256:
				encData = DigestUtils.sha256Hex(is);
				break;
			case DIGEST_SHA384:
				encData = DigestUtils.sha384Hex(is);
				break;
			case DIGEST_SHA512:
				encData = DigestUtils.sha512Hex(is);
				break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return encData;
	}
	
	
}
