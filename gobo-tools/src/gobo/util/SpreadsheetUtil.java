package gobo.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.google.apphosting.api.ApiProxy;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.data.spreadsheet.Header;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.RecordEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.TableEntry;
import com.google.gdata.data.spreadsheet.Worksheet;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.data.spreadsheet.Data.InsertionMode;
import com.google.gdata.util.ServiceException;

public class SpreadsheetUtil {

	private String authSubToken;
	private SpreadsheetService ss;
	private DocsService cs;

	public SpreadsheetUtil(String authSubToken) {
		this.authSubToken = authSubToken;
		ss = new SpreadsheetService("dstools");
		ss.setAuthSubToken(this.authSubToken);
		cs = new DocsService("dstools");
		cs.setAuthSubToken(this.authSubToken);
	}

	/**
	 * スプレッドシート(ブック)一覧
	 * 
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<Map<String, String>> getAllSpreadSheets() throws IOException, ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		SpreadsheetQuery spreadsheetQuery =
			new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
		SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
		List<SpreadsheetEntry> entries = spreadsheetFeed.getEntries();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (SpreadsheetEntry spreadSheet : entries) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("key", spreadSheet.getKey());
			row.put("title", spreadSheet.getTitle().getPlainText());
			list.add(row);
		}
		return list;
	}

	/**
	 * ワークシート一覧
	 * 
	 * @param ssKey
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<Map<String, String>> getAllWorkSheets(String ssKey) throws IOException,
			ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		SpreadsheetQuery spreadsheetQuery =
			new SpreadsheetQuery(urlFactory.getWorksheetFeedUrl(ssKey, "private", "values"));
		SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
		List<SpreadsheetEntry> workSheets = spreadsheetFeed.getEntries();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (SpreadsheetEntry workSheet : workSheets) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("wsID", workSheet.getKey());
			row.put("wsTitle", workSheet.getTitle().getPlainText());
			row.put("rowCount", String.valueOf(workSheet.getWorksheets().size()));
			list.add(row);
		}
		return list;
	}

	/**
	 * データ一覧
	 * 
	 * @param ssKey
	 * @param wsID
	 * @param startIndex
	 * @param maxResult
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<Map<String, String>> _getData(String ssKey, String wsID, Integer startIndex,
			Integer maxResult) throws IOException, ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		WorksheetQuery worksheetQuery =
			new WorksheetQuery(urlFactory.getListFeedUrl(ssKey, wsID, "private", "values"));
		worksheetQuery.setStartIndex(startIndex);
		worksheetQuery.setMaxResults(maxResult);
		ListFeed listFeed = ss.query(worksheetQuery, ListFeed.class);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (ListEntry entry : listFeed.getEntries()) {
			// System.out.println(row.getTitle().getPlainText());
			Map<String, String> row = new HashMap<String, String>();
			for (String tag : entry.getCustomElements().getTags()) {
				row.put(tag, entry.getCustomElements().getValue(tag));
			}
			list.add(row);
		}
		return list;
	}

	public String[][] getData(String ssKey, String kind, Integer startIndex, Integer maxRows)
			throws IOException, ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		WorksheetQuery worksheetQuery =
			new WorksheetQuery(urlFactory.getWorksheetFeedUrl(ssKey, "private", "values"));
		worksheetQuery.setTitleQuery(kind);
		WorksheetFeed spreadsheetFeed = ss.query(worksheetQuery, WorksheetFeed.class);
		WorksheetEntry workSheet = spreadsheetFeed.getEntries().get(0);
		final int colCount = workSheet.getColCount();
		final int rowCount = maxRows + 1;
		String[][] data = new String[rowCount][colCount];

		URL cellFeedUrl = workSheet.getCellFeedUrl();
		CellQuery query = new CellQuery(cellFeedUrl);

		// Title
		query.setMinimumRow(1);
		query.setMaximumRow(1);
		CellFeed feed = ss.query(query, CellFeed.class);
		for (CellEntry cell : feed.getEntries()) {
			String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
			int col = Integer.parseInt(shortId.substring(shortId.lastIndexOf('C') + 1));
			// System.out.println(shortId + ":" + cell.getCell().getValue());
			data[0][col - 1] = cell.getCell().getValue();
		}

		// Data (start from line no.2)
		query.setMinimumRow(startIndex);
		final int maxRowCount = workSheet.getRowCount();
		final int nextMax = startIndex + maxRows - 1;
		final int maxRow = (nextMax > maxRowCount) ? maxRowCount : nextMax;
		System.out.println(startIndex + "〜" + maxRow);
		if (startIndex >= maxRow) {
			return null;
		}
		query.setMaximumRow(maxRow);
		feed = ss.query(query, CellFeed.class);
		for (CellEntry cell : feed.getEntries()) {
			String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
			System.out.println(shortId + ":" + cell.getCell().getValue());
			int row = Integer.parseInt(shortId.substring(1, shortId.lastIndexOf('C')));
			int col = Integer.parseInt(shortId.substring(shortId.lastIndexOf('C') + 1));
			data[row - startIndex + 1][col - 1] = cell.getCell().getValue();
		}
		return data;
	}

	/**
	 * worksheetID => worksheetTitleのマップを作成
	 * 
	 * @param ssKey
	 * @param wsIDs
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public Map<String, String> worksheetID2Title(String ssKey, String[] wsIDs) throws IOException,
			ServiceException {

		List<Map<String, String>> list = getAllWorkSheets(ssKey);
		Map<String, String> result = new HashMap<String, String>();
		for (Map<String, String> row : list) {
			final String _wsID = row.get("wsID");
			for (int i = 0; i < wsIDs.length; i++) {
				if (_wsID.equals(wsIDs[i])) {
					result.put(_wsID, row.get("wsTitle"));
					continue;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param kinds
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public String createSpreadsheet(List<String> kinds) throws MalformedURLException, IOException,
			ServiceException {

		// "docs".SpreadsheetEntryを作成
		final String appId = ApiProxy.getCurrentEnvironment().getAppId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String fileName = appId + "_" + sdf.format(new Date());
		System.out.println("created new file:" + fileName);
		DocumentListEntry entry = new com.google.gdata.data.docs.SpreadsheetEntry();
		entry.setTitle(new PlainTextConstruct(fileName));
		cs.insert(new URL("https://docs.google.com/feeds/default/private/full/"), entry);

		// ファイル名から"spreadsheet".SpreadsheetEntryを再取得
		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		SpreadsheetQuery spreadsheetQuery =
			new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
		spreadsheetQuery.setTitleQuery(fileName);
		SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
		SpreadsheetEntry spreadsheetEntry = spreadsheetFeed.getEntries().get(0);
		final String ssKey = spreadsheetEntry.getKey();

		// Get Kind and Property Info
		Map<String, Map<String, Object>> kindInfos = DatastoreUtil.getKindInfos();
		String kind = kinds.get(0);
		Map<String, Object> props = kindInfos.get(kind);

		// Modifying a default worksheet
		URL worksheetFeedUrl = spreadsheetEntry.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = ss.getFeed(worksheetFeedUrl, WorksheetFeed.class);
		WorksheetEntry defaultWorksheet = worksheetFeed.getEntries().get(0);
		defaultWorksheet.setTitle(new PlainTextConstruct(kinds.get(0)));
		defaultWorksheet.setRowCount(2);
		defaultWorksheet.setColCount(props.size() + 1);
		defaultWorksheet.update();

		createTableInWorksheet(ssKey, kind, props);

		// Adding Worksheets
		for (int i = 1; i < kinds.size(); i++) {
			kind = kinds.get(i);
			props = kindInfos.get(kind);

			WorksheetEntry newWorksheet = new WorksheetEntry();
			newWorksheet.setTitle(new PlainTextConstruct(kinds.get(i)));
			newWorksheet.setRowCount(2);
			newWorksheet.setColCount(props.size() + 1);
			ss.insert(worksheetFeedUrl, newWorksheet);

			createTableInWorksheet(ssKey, kind, props);
		}

		return spreadsheetEntry.getKey();
	}

	/**
	 * 
	 * @param ssKey
	 * @param kind
	 * @param columns
	 * @throws IOException
	 * @throws ServiceException
	 */
	void createTableInWorksheet(String ssKey, String kind, Map<String, Object> columns)
			throws IOException, ServiceException {

		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL tableFeedUrl = factory.getTableFeedUrl(ssKey);

		TableEntry tableEntry = new TableEntry();
		tableEntry.setTitle(new PlainTextConstruct(kind));
		tableEntry.setWorksheet(new Worksheet(kind));
		tableEntry.setHeader(new Header(1));

		Data tableData = new Data();
		tableData.setNumberOfRows(0);
		tableData.setStartIndex(2);
		tableData.setInsertionMode(InsertionMode.INSERT);

		tableData.addColumn(new Column("A", Entity.KEY_RESERVED_PROPERTY));
		Object[] keys = columns.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String index = number2columnName(i + 1);
			Object value = keys[i];
			tableData.addColumn(new Column(index, value.toString()));
		}
		tableEntry.setData(tableData);
		ss.insert(tableFeedUrl, tableEntry);
	}

	/**
	 * 
	 * @param ssKey
	 * @param tableId
	 * @param list
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void addTableRow(String ssKey, String kind, String tableId,
			List<Map<String, Object>> list) throws IOException, ServiceException {

		// Adding new rows to table
		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL recordFeedUrl = factory.getRecordFeedUrl(ssKey, tableId);
		for (Map<String, Object> row : list) {
			RecordEntry newEntry = new RecordEntry();
			newEntry.addField(new Field(null, Entity.KEY_RESERVED_PROPERTY, (String) row
				.get(Entity.KEY_RESERVED_PROPERTY)));
			for (String key : row.keySet()) {
				if (row.get(key) != null) {
					newEntry.addField(new Field(null, key, (String) row.get(key)));
				}
			}
			ss.insert(recordFeedUrl, newEntry);
		}
		return;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	String number2columnName(int i) {
		final String[] alpha =
			{
				"A",
				"B",
				"C",
				"D",
				"E",
				"F",
				"G",
				"H",
				"I",
				"J",
				"K",
				"L",
				"M",
				"N",
				"O",
				"P",
				"Q",
				"R",
				"S",
				"T",
				"U",
				"V",
				"W",
				"X",
				"Y",
				"Z" };
		int devided = i / 26;
		int amari = i % 26;
		String name = (devided == 0) ? alpha[amari] : alpha[devided] + alpha[amari];
		return name;
	}
}
