package plugin.parts;

import jakarta.annotation.PostConstruct;
import plugin.http.RestApiClient;
import plugin.http.RestApiClient.Issue;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.statushandlers.StatusManager;

public class SampleDataView {
	
    private Table table;
    private Button fetchDataButton;
    private Composite stackComposite;
    private Label noDataLabel;
    private Label loadingLabel;
    private StackLayout stackLayout;

    @PostConstruct
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        
        stackComposite = new Composite(parent, SWT.NONE);
        stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        stackLayout = new StackLayout();
        stackComposite.setLayout(stackLayout);
        
        table = new Table(stackComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setVisible(false);
        
        noDataLabel = new Label(stackComposite, SWT.CENTER);
        noDataLabel.setText("No data");
        noDataLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        showNoDataLabel();

        loadingLabel = new Label(stackComposite, SWT.CENTER);
        loadingLabel.setText("Loading...");
        loadingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        
        String[] columnNames = { "id", "name", "severity", "updated at" };
        for (String name : columnNames) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(name);
            column.setWidth(150);
        }
        
        fetchDataButton = new Button(parent, SWT.PUSH);
        fetchDataButton.setText("Fetch Data");
        fetchDataButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        fetchDataButton.addListener(SWT.Selection, event -> fetchData());
    }
    
    private void fetchData() {
    	new Thread(() -> {
            try {
            	stackComposite.getDisplay().asyncExec(this::disableFetchDataButton);
            	stackComposite.getDisplay().asyncExec(this::showLoadingLabel);
                List<Issue> issues = RestApiClient.getIssues();
                stackComposite.getDisplay().asyncExec(() -> {
                    table.removeAll();
                    issues.stream().forEach(issue -> {
        	            TableItem item = new TableItem(table, SWT.NONE);
        	            item.setText(new String[] { Integer.toString(issue.id()), issue.name(), issue.severity(), issue.updatedAt() });
        			});
                    showTable();
                });
            } catch (Exception e) {
                Status status = new Status(IStatus.ERROR, "plugin.sampleData", "Failed to fetch issues", e);
                StatusManager.getManager().handle(status,
                        StatusManager.SHOW | StatusManager.LOG);
                stackComposite.getDisplay().asyncExec(this::showNoDataLabel);
            } finally {
            	stackComposite.getDisplay().asyncExec(this::enableFetchDataButton);
            }
        }).start();
    }
    
    private void showTable() {
    	stackLayout.topControl = table;
		stackComposite.layout();
    }
    
    private void showNoDataLabel() {
    	stackLayout.topControl = noDataLabel;
		stackComposite.layout();
    }
	
    private void showLoadingLabel() {
    	stackLayout.topControl = loadingLabel;
		stackComposite.layout();
    }
    
    private void disableFetchDataButton() {
    	fetchDataButton.setEnabled(false);
    }
    
    private void enableFetchDataButton() {
    	fetchDataButton.setEnabled(true);
    }

}
