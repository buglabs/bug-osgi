package com.buglabs.osgi.tester;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import com.buglabs.osgi.shell.ICommand;
import com.buglabs.osgi.shell.pub.AbstractCommand;
import com.buglabs.tableviewer.IContentProvider;
import com.buglabs.tableviewer.ILabelProvider;
import com.buglabs.tableviewer.TableColumn;
import com.buglabs.tableviewer.TableViewer;
import com.buglabs.util.OSGiServiceLoader;

public class ListTestsCommand extends AbstractCommand implements ICommand {

	@Override
	public void execute() throws Exception {
		TableViewer tv = new TableViewer();
		tv.addColumn(new TableColumn("Index", TableColumn.RIGHT));
		tv.addColumn(new TableColumn("Suite Name"));
		tv.addColumn(new TableColumn("Test Count"));

		final List testSuites = OSGiServiceLoader.getServices(context, TestSuite.class.getName(), null);
		tv.setContentProvider(new IContentProvider() {
			Iterator i = testSuites.iterator();

			public Object getNextRow() {
				if (i.hasNext()) {
					return i.next();
				}

				return null;
			}

			public void reset() {
				i = testSuites.iterator();
			}

		});

		tv.setLabelProvider(new ILabelProvider() {
			public String getText(Object model, int column) {
				TestSuite ts = (TestSuite) model;

				switch (column) {
				case 0:
					return "" + (testSuites.indexOf(ts) + 1);
				case 1:
					return ts.getName();
				case 2:
					return "" + ts.countTestCases();
				}

				return null;
			}
		});

		setDefaultFilter(tv, 1);
		tv.render(outWriter);
	}

	@Override
	public String getDescription() {
		return "List all available test suites.";
	}

	@Override
	public String getName() {
		return "tlist";
	}

}
