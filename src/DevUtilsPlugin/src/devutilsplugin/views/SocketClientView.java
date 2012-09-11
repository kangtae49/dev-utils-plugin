package devutilsplugin.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import devutilsplugin.utils.ActionNewView;
import devutilsplugin.utils.SocketClient;


public class SocketClientView extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.SocketClientView";
	final int pad_frame = 10;
	final int pad_ctrl = 5;
	
	IoSession currSession = null;
	final SocketClient client = new SocketClient();

	public SocketClientView() {
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

		SashForm sashHor = new SashForm(parent, SWT.HORIZONTAL);
		sashHor.setLayout(new FormLayout());
		
		// Left
		Composite compositeLeft = new Composite(sashHor, SWT.NONE);
		compositeLeft.setLayout(new FormLayout());
		// Right
		Composite compositeRight = new Composite(sashHor, SWT.NONE);
		compositeRight.setLayout(new FormLayout());

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame); 
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		sashHor.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.width = 100; 
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		compositeLeft.setLayoutData(layoutData);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(compositeLeft, pad_ctrl);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		compositeRight.setLayoutData(layoutData);

		final Label lblInfo = new Label(compositeRight, SWT.NONE);
		Label lblRecv = new Label(compositeRight, SWT.NONE);
		Label lblSend = new Label(compositeRight, SWT.NONE);
		
		final Combo cboCharsetOfRecv = new Combo(compositeRight, SWT.BORDER);
		final Combo cboCharsetOfSend = new Combo(compositeRight, SWT.BORDER);
		final Text txtRecv = new Text(compositeRight, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		final Text txtSend = new Text(compositeRight, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		final Button btnSend = new Button(compositeRight, SWT.PUSH);
		final Button btnSaveRecvData = new Button(compositeRight, SWT.PUSH);
		final Button btnClearRecvData = new Button(compositeRight, SWT.PUSH);
		
		Label lblConnect = new Label(compositeLeft, SWT.NONE);
		final Text txtHost = new Text(compositeLeft, SWT.BORDER);
		final Text txtPort = new Text(compositeLeft, SWT.BORDER);
		final Button btnConnect = new Button(compositeLeft, SWT.PUSH);
		Label lblSession = new Label(compositeLeft, SWT.NONE);
		final Table tblSession = new Table(compositeLeft, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		final Text txtSessionID = new Text(compositeLeft, SWT.BORDER | SWT.READ_ONLY);
		final Button btnCloseSession = new Button(compositeLeft, SWT.PUSH);
		final Button chkSSL = new Button(compositeLeft, SWT.CHECK);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(0, 0);
		chkSSL.setLayoutData(layoutData);
		chkSSL.setText("Use SSL");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(chkSSL, pad_ctrl);
		lblConnect.setLayoutData(layoutData);
		lblConnect.setText("Connect");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(lblConnect, pad_ctrl);
		layoutData.width = 100;
		txtHost.setLayoutData(layoutData);
		txtHost.setText("127.0.0.1");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(txtHost, pad_ctrl);
		layoutData.width = 40;
		txtPort.setLayoutData(layoutData);
		txtPort.setText("1234");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(txtPort, pad_ctrl);
		btnConnect.setLayoutData(layoutData);
		btnConnect.setText("Connect");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnConnect, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		lblSession.setLayoutData(layoutData);
		lblSession.setText("Session List");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnConnect, pad_ctrl);
		layoutData.left = new FormAttachment(lblSession, pad_ctrl);
		txtSessionID.setLayoutData(layoutData);
		txtSessionID.setText("");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnConnect, pad_ctrl);
		layoutData.left = new FormAttachment(txtSessionID, pad_ctrl);
		btnCloseSession.setLayoutData(layoutData);
		btnCloseSession.setText("Close Session");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnCloseSession, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, -pad_ctrl);
		layoutData.bottom = new FormAttachment(100, 0);
		tblSession.setLayoutData(layoutData);
		tblSession.setLinesVisible(true);
		tblSession.setHeaderVisible(true);
		TableColumn colSession = null;
		colSession = new TableColumn(tblSession, SWT.LEFT);
		colSession.setText("Session ID");
		colSession.setWidth(30);
		colSession = new TableColumn(tblSession, SWT.LEFT);
		colSession.setText("Remote IP");
		colSession.setWidth(80);
		colSession = new TableColumn(tblSession, SWT.LEFT);
		colSession.setText("Remote Port");
		colSession.setWidth(50);
		colSession = new TableColumn(tblSession, SWT.LEFT);
		colSession.setText("Local Port");
		colSession.setWidth(50);
		
		// right
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, 0);
		lblInfo.setLayoutData(layoutData);
		lblInfo.setText("Select Session List!!!");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(lblInfo, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		lblRecv.setLayoutData(layoutData);
		lblRecv.setText("Received Data ");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(lblInfo, pad_ctrl);
		layoutData.left = new FormAttachment(lblRecv, pad_ctrl);
		cboCharsetOfRecv.setLayoutData(layoutData);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(lblInfo, pad_ctrl);
		layoutData.left = new FormAttachment(cboCharsetOfRecv, pad_ctrl);
		btnSaveRecvData.setLayoutData(layoutData);
		btnSaveRecvData.setText("Save Data");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(lblInfo, pad_ctrl);
		layoutData.left = new FormAttachment(btnSaveRecvData, pad_ctrl);
		btnClearRecvData.setLayoutData(layoutData);
		btnClearRecvData.setText("Clear Data");
		
		
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharsetOfRecv, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, 0);
		layoutData.bottom = new FormAttachment(50, 0);
		txtRecv.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtRecv, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		lblSend.setLayoutData(layoutData);
		lblSend.setText("Send Data ");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtRecv, pad_ctrl);
		layoutData.left = new FormAttachment(lblSend, pad_ctrl);
		cboCharsetOfSend.setLayoutData(layoutData);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtRecv, pad_ctrl);
		layoutData.left = new FormAttachment(cboCharsetOfSend, pad_ctrl);
		btnSend.setLayoutData(layoutData);
		btnSend.setText("Send Data");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(cboCharsetOfSend, pad_ctrl);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, 0);
		layoutData.bottom = new FormAttachment(100, 0);
		txtSend.setLayoutData(layoutData);
		txtSend.setBackground(new Color(parent.getDisplay(), 243, 246, 250));
		txtSend.setText("GET / HTTP/1.0\r\n\r\n");

		Map<String, Charset> charsets = Charset.availableCharsets();
	    Iterator<Charset> iterator = charsets.values().iterator();
		while (iterator.hasNext()) {
			Charset cs = (Charset) iterator.next();
			cboCharsetOfRecv.add(cs.displayName());
			cboCharsetOfSend.add(cs.displayName());
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
		cboCharsetOfRecv.setText(Charset.defaultCharset().displayName());
		cboCharsetOfSend.setText(Charset.defaultCharset().displayName());
		
		chkSSL.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean bSSL = chkSSL.getSelection();
				client.setSSL(bSSL);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnClearRecvData.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtRecv.setText("");
				if(currSession != null){
					IoBuffer buf = (IoBuffer)currSession.getAttribute("buffer");
					buf.clear();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnSaveRecvData.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);
				String path = dialog.open();
				if(path == null){
					return;
				}
				if(currSession == null){
					return;
				}
				FileOutputStream os = null;
				try {
					os = new FileOutputStream(path);
					byte [] bytes = getRecvData();
//					IoBuffer buf = (IoBuffer)currSession.getAttribute("buffer");
//					int pos = buf.position();
//					int limit = buf.limit();
//					
//					buf.flip();
//					buf.position(0);
//					int len = buf.remaining();
//					System.out.println("len:" + len);
//					byte [] bytes = new byte[len];
//					buf.get(bytes);
//					
//					buf.position(pos);
//					buf.limit(limit);
					os.write(bytes);

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
		
		btnCloseSession.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtSessionID.setText("");
				if(currSession != null){
					currSession.close(false);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnSend.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(currSession == null){
					return;
				}
				try{
					IoBuffer buff = IoBuffer.allocate(1024);
					String sendData = txtSend.getText();
					String charset = cboCharsetOfSend.getText();
					buff.put(sendData.getBytes(charset));
					buff.flip();
					currSession.write(buff);
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnConnect.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String host = txtHost.getText();
				String strPort = txtPort.getText();
				
				try {
					int port = Integer.parseInt(strPort);
					client.connect(host, port);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		tblSession.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = tblSession.getSelectionIndex();
				IoSession session = (IoSession)tblSession.getItem(idx).getData("session");
				currSession = session;
				lblInfo.setText("Session ID:" + session.getId());
				txtSessionID.setText("" + session.getId());
				byte [] bytes = getRecvData();
				String charset = cboCharsetOfRecv.getText();
				String recvData = "";
				try {
					recvData = new String(bytes, charset);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				txtRecv.setText(recvData);
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
			    	String[] files = (String[]) event.data;
					//for (int i = 0; i < files.length; i++) {
					for (int i = 0; i < 1; i++) {
						File fd = new File(files[i]);
						FileInputStream is = null;
						try {
							is = new FileInputStream(fd);
							byte byteData[] = new byte[(int) fd.length()]; 
							is.read(byteData);
							
							IoBuffer buff = IoBuffer.allocate(byteData.length);
							buff.put(byteData);
							buff.flip();
							
							currSession.write(buff);
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
		DropTarget dropTarget = new DropTarget(txtSend, DND.DROP_COPY
				| DND.DROP_DEFAULT);
		Transfer[] transfers = new Transfer[] { FileTransfer.getInstance() };

		dropTarget.setTransfer(transfers);
		dropTarget.addDropListener(dtl);

		

		client.setHandler(new IoHandler() {
			
			@Override
			public void sessionOpened(IoSession session) throws Exception {
				
			}
			
			@Override
			public void sessionIdle(IoSession session, IdleStatus idle) throws Exception {
				
			}
			
			@Override
			public void sessionCreated(final IoSession session) throws Exception {
				long id = session.getId();
				
				System.out.println("[id:" + id + "]sessionCreated");
				session.setAttribute("buffer", IoBuffer.allocate(1024));
				parent.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						InetSocketAddress remoteAddr = (InetSocketAddress)session.getRemoteAddress();
						InetSocketAddress localAddr = (InetSocketAddress)session.getLocalAddress();
						String sessionID = "" + session.getId();
						String remoteIP = remoteAddr.getAddress().getHostAddress();
						String remotePort = "" + remoteAddr.getPort();
						String localPort = "" + localAddr.getPort();
						
						TableItem item = new TableItem(tblSession, SWT.NONE);
						item.setText(0, sessionID);
						item.setText(1, remoteIP);
						item.setText(2, remotePort);
						item.setText(3, localPort);
						item.setData("session", session);
					}
				});
			}
			
			@Override
			public void sessionClosed(final IoSession session) throws Exception {
				long id = session.getId();
				System.out.println("[id:" + id + "]sessionClosed");
				
				
				parent.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						TableItem [] items = tblSession.getItems();
						for(int i=0; i<items.length; i++){
							TableItem item = items[i];
							IoSession sessionOfTable = (IoSession)item.getData("session");
							if(session.getId() == sessionOfTable.getId()){
								tblSession.remove(i);
								break;
							}
						}

					}
				});
				
				
				if(currSession.getId() == session.getId()){
					
//					IoBuffer buf = (IoBuffer)session.getAttribute("buffer");
//					String charset = cboCharsetOfRecv.getText();
//					buf.flip();
//					int len = buf.remaining();
//					System.out.println("len:" + len);
//					byte [] bytes = new byte[len];
//					buf.get(bytes);
//					final String msg = new String(bytes, charset);
//					System.out.println(msg);
					
	//				for(int i=0; i<bytes.length; i++){
	//					String a = String.format("%02X", bytes[i]);
	//					System.out.println(a);
	//				}
					
					//String msg = buf.getString(Charset.defaultCharset().newDecoder());
					//System.out.println(msg);
					final byte [] bytes = getRecvData();

					// UI Control
					parent.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
//							txtRecv.setText(msg);

							String charset = cboCharsetOfRecv.getText();
							String recvStr = "";
							try {
								recvStr = new String(bytes, charset);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							txtRecv.setText(recvStr);

							
							
						}
					});
				}
			}
			
			@Override
			public void messageSent(IoSession session, Object message) throws Exception {
				long id = session.getId();
				System.out.println("[id:" + id + "]messageSent");
			}
			
			@Override
			public void messageReceived(IoSession session, Object message) throws Exception {
				long id = session.getId();
				System.out.println("[id:" + id + "]messageReceived");
				IoBuffer msg = (IoBuffer)message;
				IoBuffer buf = (IoBuffer)session.getAttribute("buffer");
				if (buf.remaining() < msg.limit()) {
					int bufCapacity = buf.capacity() + (msg.limit() - buf.remaining());
					buf.capacity(bufCapacity);
					buf.limit(bufCapacity);
				}
				buf.put((IoBuffer)message);
				
				if(currSession.getId() == session.getId()){
					
					final byte [] bytes = getRecvData();
					
//					int pos = buf.position();
//					int limit = buf.limit();
//					
//					buf.flip();
//					buf.position(0);
//					int len = buf.remaining();
//					System.out.println("len:" + len);
//					byte [] bytes = new byte[len];
//					buf.get(bytes);
//					final String recvStr = new String(bytes);
//					buf.position(pos);
//					buf.limit(limit);
					
	//				for(int i=0; i<bytes.length; i++){
	//					String a = String.format("%02X", bytes[i]);
	//					System.out.println(a);
	//				}
					
					//String msg = buf.getString(Charset.defaultCharset().newDecoder());
					//System.out.println(msg);
					// UI Control
					parent.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							String charset = cboCharsetOfRecv.getText();
							String recvStr = "";
							try {
								recvStr = new String(bytes, charset);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							txtRecv.setText(recvStr);
						}
					});
				}
				
				
				
			}
			
			@Override
			public void exceptionCaught(IoSession session, Throwable t)
					throws Exception {
				
			}
		});
	}

	@Override
	public void setFocus() {

	}
	
	@Override
	public void dispose() {
		client.dispose();
		super.dispose();
	}
	
	private byte [] getRecvData(){
		if(currSession == null){
			return null;
		}
			
		IoBuffer buf = (IoBuffer)currSession.getAttribute("buffer");
		
		int pos = buf.position();
		int limit = buf.limit();
		
		buf.flip();
		buf.position(0);
		int len = buf.remaining();
		byte [] bytes = new byte[len];
		buf.get(bytes);
		
		buf.position(pos);
		buf.limit(limit);		
		return bytes;
	}

}
