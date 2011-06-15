package cz.quinix.condroid.database;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cz.quinix.condroid.AsyncTaskListener;
import cz.quinix.condroid.CondroidActivity;
import cz.quinix.condroid.ListenedAsyncTask;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.conventions.Convention;
import cz.quinix.condroid.welcome.WelcomeActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

public class DataLoadTask extends ListenedAsyncTask<String, String> {

	private ProgressDialog pd;
	private DataProvider db;
	
	
	public DataLoadTask(AsyncTaskListener listener, ProgressDialog pd2, DataProvider dataProvider) {
		super(listener);
		this.pd = pd2;
		this.db = dataProvider;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(List<?> result) {
		db.insert((List<Annotation>) result);
		super.onPostExecute(null);
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		
		super.onProgressUpdate(values);
		pd.setMessage(values[0]);
	}
	
	@Override
	protected List<?> doInBackground(String... params) {
		List<Annotation> messages = new ArrayList<Annotation>();
		XmlPullParser pull = Xml.newPullParser();
		Annotation annotation = null;
		try {
			try {
				URL url = new URL(params[0]);
				URLConnection conn = url.openConnection();

				pull.setInput(conn.getInputStream(), null);
				
			} catch (Exception ex) {
				throw new XMLProccessException("Stažení seznamu anotací se nezdařilo.", ex);
			}
			int eventType = 0;
			this.publishProgress("Zpracovávám...");
			try {
				eventType = pull.getEventType();
			} catch (XmlPullParserException e) {
				throw new XMLProccessException("Zpracování seznamu anotací se nezdařilo", e);
			}
			try {
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;

					case XmlPullParser.START_TAG:
						String name = pull.getName();
						if (name.equalsIgnoreCase("programme")) {
							annotation = new Annotation();
						} else {
							if (name.equalsIgnoreCase("pid")) {
								annotation.setPid(pull.nextText());
							}
							if (name.equalsIgnoreCase("author")) {
								annotation.setAuthor(pull.nextText());
							}
							if (name.equalsIgnoreCase("title")) {
								annotation.setTitle(pull.nextText());
							}
							if (name.equalsIgnoreCase("length")) {
								annotation.setLength(pull.nextText());
							}
							if (name.equalsIgnoreCase("type")) {
								annotation.setType(pull.nextText());
							}
							if (name.equalsIgnoreCase("program-line")) {
								annotation.setProgramLine(pull.nextText());
							}
							if (name.equalsIgnoreCase("annotation")) {
								annotation.setAnnotation(pull.nextText());
							}
							if (name.equalsIgnoreCase("start-time")) {
								annotation.setStartTime(pull.nextText());
							}
							if (name.equalsIgnoreCase("end-time")) {
								annotation.setEndTime(pull.nextText());
							}
						}
						break;

					case XmlPullParser.END_TAG:
						name = pull.getName();
						if (name.equalsIgnoreCase("programme")
								&& annotation != null) {
							messages.add(annotation);
						}
						break;
					default:
						break;
					}
					eventType = pull.next();
				}
			} catch (Exception e) {
				throw new XMLProccessException("Zpracování zdroje se nezdařilo.", e);
			}
		} catch (XMLProccessException e) {
		//	this.message = e.getMessage();
		}
		return messages;
		
	}
	

}
