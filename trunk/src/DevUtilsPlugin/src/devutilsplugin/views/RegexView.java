package devutilsplugin.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import devutilsplugin.utils.ActionNewView;

public class RegexView extends ViewPart {
	public static final String ID = "DevUtilsPlugin.views.RegexView";
	final int pad_frame = 10;
	final int pad_ctrl = 5;

	public RegexView() {
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
		
		final StyledText txtSource = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		final Text txtResult = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		final Label lblPattern = new Label(parent, SWT.NONE);
		final Label lblReplace = new Label(parent, SWT.NONE);
		final Text txtPattern = new Text(parent, SWT.BORDER);
		final Text txtReplace = new Text(parent, SWT.BORDER);
		final Button chkCaseInsensitive = new Button(parent, SWT.CHECK);
		final Button chkMultiline = new Button(parent, SWT.CHECK);
		final Button chkDotall = new Button(parent, SWT.CHECK);
		final Button chkUnicodeCase = new Button(parent, SWT.CHECK);
		final Button chkCanonEq = new Button(parent, SWT.CHECK);
		final Button chkUnixLines = new Button(parent, SWT.CHECK);
		final Button chkLiteral = new Button(parent, SWT.CHECK);
		final Button chkComments = new Button(parent, SWT.CHECK);
		
		Button btnMatch = new Button(parent, SWT.PUSH);
		Button btnReplace = new Button(parent, SWT.PUSH);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		chkCaseInsensitive.setLayoutData(layoutData);
		chkCaseInsensitive.setText("CASE_INSENSITIVE");
		chkCaseInsensitive.setSelection(false);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(chkCaseInsensitive, pad_ctrl);
		chkMultiline.setLayoutData(layoutData);
		chkMultiline.setText("MULTILINE");
		chkMultiline.setSelection(false);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(chkMultiline, pad_ctrl);
		chkDotall.setLayoutData(layoutData);
		chkDotall.setText("DOTALL");
		chkDotall.setSelection(false);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, pad_frame);
		layoutData.left = new FormAttachment(chkDotall, pad_ctrl);
		chkUnicodeCase.setLayoutData(layoutData);
		chkUnicodeCase.setText("UNICODE_CASE");
		chkUnicodeCase.setSelection(false);

		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUnicodeCase, pad_ctrl);
		layoutData.left = new FormAttachment(0, pad_frame);
		chkCanonEq.setLayoutData(layoutData);
		chkCanonEq.setText("CANON_EQ");
		chkCanonEq.setSelection(false);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUnicodeCase, pad_ctrl);
		layoutData.left = new FormAttachment(chkCanonEq, pad_ctrl);
		chkUnixLines.setLayoutData(layoutData);
		chkUnixLines.setText("UNIX_LINES");
		chkUnixLines.setSelection(false);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUnicodeCase, pad_ctrl);
		layoutData.left = new FormAttachment(chkUnixLines, pad_ctrl);
		chkLiteral.setLayoutData(layoutData);
		chkLiteral.setText("LITERAL");
		chkLiteral.setSelection(false);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkUnicodeCase, pad_ctrl);
		layoutData.left = new FormAttachment(chkLiteral, pad_ctrl);
		chkComments.setLayoutData(layoutData);
		chkComments.setText("COMMENTS");
		chkComments.setSelection(false);
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(chkComments, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(50, -pad_ctrl);
		txtSource.setLayoutData(layoutData);
		txtSource.setText("123456");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtSource, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		btnMatch.setLayoutData(layoutData);
		btnMatch.setText("match");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtSource, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		lblPattern.setLayoutData(layoutData);
		lblPattern.setText("Pattern");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(txtSource, pad_frame);
		layoutData.left = new FormAttachment(lblPattern, pad_ctrl);
		layoutData.right = new FormAttachment(btnMatch, -pad_ctrl);
		txtPattern.setLayoutData(layoutData);
		txtPattern.setText("([0-9]{3})([0-9]{3})");
		
		//
		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnMatch, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		btnReplace.setLayoutData(layoutData);
		btnReplace.setText("replace");

		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnMatch, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		lblReplace.setLayoutData(layoutData);
		lblReplace.setText("Replace");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnMatch, pad_frame);
		layoutData.left = new FormAttachment(lblReplace, pad_ctrl);
		layoutData.right = new FormAttachment(btnReplace, -pad_ctrl);
		txtReplace.setLayoutData(layoutData);
		txtReplace.setText("$1-$2");
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(btnReplace, pad_frame);
		layoutData.left = new FormAttachment(0, pad_frame);
		layoutData.right = new FormAttachment(100, -pad_frame);
		layoutData.bottom = new FormAttachment(100, -pad_frame);
		txtResult.setLayoutData(layoutData);
		txtResult.setText("");

		btnMatch.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String regex = txtPattern.getText();
				String source = txtSource.getText();
				
				int flags = 0;
				if(chkCaseInsensitive.getSelection()){
					flags |= Pattern.CASE_INSENSITIVE;
				}
				if(chkMultiline.getSelection()){
					flags |= Pattern.MULTILINE;
				}
				if(chkDotall.getSelection()){
					flags |= Pattern.DOTALL;
				}
				if(chkUnicodeCase.getSelection()){
					flags |= Pattern.UNICODE_CASE;
				}
				if(chkCanonEq.getSelection()){
					flags |= Pattern.CANON_EQ;
				}
				if(chkUnixLines.getSelection()){
					flags |= Pattern.UNIX_LINES;
				}
				if(chkLiteral.getSelection()){
					flags |= Pattern.LITERAL;
				}
				if(chkComments.getSelection()){
					flags |= Pattern.COMMENTS;
				}
				
				Pattern pattern = null;
				if(flags == 0){
					pattern = Pattern.compile(regex);
				}else{
					pattern = Pattern.compile(regex, flags);
				}
				
				Matcher match = pattern.matcher(source);
				
				txtSource.setStyleRange(null);
				
				while(match.find()){
					int groupCount = match.groupCount();
					System.out.println("groupCount" + match.groupCount());
					for(int i=0; i<=groupCount; i++){
						System.out.println("start(" + i + "):" + match.start(i));
						System.out.println("end(" + i + "):" + match.end(i));
						StyleRange style = new StyleRange();
						style.start = match.start(i);
						style.length = match.end(i) - match.start(i);
						System.out.println("style.length:" + style.length);
						if(i==0){
							style.fontStyle = SWT.BOLD;
							style.foreground = new Color(parent.getDisplay(), 255, 0, 0);
						}else{
							style.fontStyle = SWT.BOLD;
							int color = 200/(groupCount+1)*(i+1);
							//if(i%2 == 0){
								style.background = new Color(parent.getDisplay(), 0, color, 0);
							//}else{
							//	style.background = new Color(parent.getDisplay(), 0, 255, 127);
							//}
						}
						txtSource.setStyleRange(style);
					}
				}
		    }
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnReplace.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtResult.setText("");
				String source = txtSource.getText();
				String regex = txtPattern.getText();
				String replace = txtReplace.getText();
				int flags = 0;
				if(chkCaseInsensitive.getSelection()){
					flags |= Pattern.CASE_INSENSITIVE;
				}
				if(chkMultiline.getSelection()){
					flags |= Pattern.MULTILINE;
				}
				if(chkDotall.getSelection()){
					flags |= Pattern.DOTALL;
				}
				if(chkUnicodeCase.getSelection()){
					flags |= Pattern.UNICODE_CASE;
				}
				if(chkCanonEq.getSelection()){
					flags |= Pattern.CANON_EQ;
				}
				if(chkUnixLines.getSelection()){
					flags |= Pattern.UNIX_LINES;
				}
				if(chkLiteral.getSelection()){
					flags |= Pattern.LITERAL;
				}
				if(chkComments.getSelection()){
					flags |= Pattern.COMMENTS;
				}
				
				Pattern pattern = null;
				if(flags == 0){
					pattern = Pattern.compile(regex);
				}else{
					pattern = Pattern.compile(regex, flags);
				}
				String ret = pattern.matcher(source).replaceAll(replace);
				
//				String ret = source.replaceAll(regex, replace);
				txtResult.setText(ret);
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
