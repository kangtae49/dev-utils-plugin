package devutilsplugin.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import devutilsplugin.utils.ActionNewView;

public class Base64View extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.Base64View";
	final int pad_frame = 10;
	final int pad_ctrl = 5;

	public Base64View() {
	}
	void setMultiView(final IViewPart view){
		try {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					getViewSite().getPage().hideView(view);
					ActionNewView anv = new ActionNewView(getViewSite().getId());
					anv.run();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		String secondaryId = getViewSite().getSecondaryId();
		if (secondaryId == null) {
			setMultiView(this);
			return;
		}

		FormData layoutData = null;
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		
		Label lblCharset = new Label(parent, SWT.NONE);
		final Combo cboCharset = new Combo(parent, SWT.BORDER);
		final Button chkChunked = new Button(parent, SWT.CHECK);
		final Button chkUrlSafe = new Button(parent, SWT.CHECK);
		
		final Text txtDEC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		final Text txtENC = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		Button btnENC = new Button(parent, SWT.PUSH);
		Button btnDEC = new Button(parent, SWT.PUSH);
		Button btnDecAndSaveFile = new Button(parent, SWT.PUSH | SWT.WRAP);

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
		chkChunked.setLayoutData(layoutData);
		chkChunked.setText("Chunked");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharset, pad_frame);
		layoutData.left = new FormAttachment(chkChunked, pad_ctrl);
		chkUrlSafe.setLayoutData(layoutData);
		chkUrlSafe.setText("UrlSafe");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUrlSafe, pad_ctrl);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(50, -pad_ctrl-40);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtDEC.setFont(new Font(parent.getDisplay(), "Fixedsys", 12, SWT.NONE));
		txtDEC.setLayoutData(layoutData);
		txtDEC.setBackground(new Color(parent.getDisplay(), 243, 246, 250));
		txtDEC.setText("Input string or Drag&Drop File!");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUrlSafe, pad_ctrl);
		layoutData.left = new FormAttachment(50, pad_ctrl+40);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtENC.setFont(new Font(parent.getDisplay(), "Fixedsys", 12, SWT.NONE));
		txtENC.setLayoutData(layoutData);
		txtENC.setBackground(new Color(parent.getDisplay(), 243, 246, 250));

		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUrlSafe, pad_ctrl);
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
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnDEC, pad_ctrl);
		layoutData.left = new FormAttachment(txtDEC, pad_ctrl);
		layoutData.right = new FormAttachment(txtENC, -pad_ctrl);
		layoutData.height = 50;
		btnDecAndSaveFile.setLayoutData(layoutData);
		btnDecAndSaveFile.setText("<=decode (save file)");
		
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

		btnENC.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtENC.setText("");
				String decData = txtDEC.getText();
		    	String charName = cboCharset.getText();
		    	String encData = "";
		    	boolean bChunked = chkChunked.getSelection();
		    	boolean bUrlSafe = chkUrlSafe.getSelection();
		    	byte [] byteData = null;
		    	try {
					byteData = decData.getBytes(charName);
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
				}
		    	try {
					encData = new String(Base64.encodeBase64(byteData, bChunked, bUrlSafe), charName);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
		    	
		    	txtENC.setText(encData);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnDEC.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtDEC.setText("");
				String decData = "";
		    	String charName = cboCharset.getText();
		    	String encData = txtENC.getText();
		    	try {
					decData = new String(Base64.decodeBase64(encData), charName);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
		    	
		    	txtDEC.setText(decData);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnDecAndSaveFile.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);
				String path = dialog.open();
				if(path == null){
					return;
				}
				FileOutputStream os = null;
				try {
					String encData = txtENC.getText();
					byte [] byteData = null;
					os = new FileOutputStream(path);
					byteData = Base64.decodeBase64(encData);
					os.write(byteData);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				} finally{
					if(os != null){
						try {
							os.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
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
					txtENC.setText("");
			    	String encData = "";
			    	String charName = cboCharset.getText();
			    	boolean bChunked = chkChunked.getSelection();
			    	boolean bUrlSafe = chkUrlSafe.getSelection();

			    	String[] files = (String[]) event.data;
					StringBuffer sb = new StringBuffer();
					//for (int i = 0; i < files.length; i++) {
					for (int i = 0; i < 1; i++) {
						File fd = new File(files[i]);
						FileInputStream is = null;
						try {
							is = new FileInputStream(fd);
							byte byteData[] = new byte[(int) fd.length()]; 
							is.read(byteData);
							encData = new String(Base64.encodeBase64(byteData, bChunked, bUrlSafe), charName);
							//sb.append("[" + fd.getAbsolutePath() + "]\n" + encData + "\n\n");
							sb.append(encData);
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
					txtENC.setText(sb.toString());
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

}
