package com.doronawa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class asahiKawaii extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(new HttpGet("http://twitter.com/statuses/user_timeline/42816371.rss"));
			BufferedReader stream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			Log.d("hoge", "encode: "+response.getEntity().getContentEncoding());

			/*必要部分まで飛ばす*/
			while(stream.readLine().indexOf("<item>") == -1 && stream.ready()){
				;
			}

			/*最新postを切り出す*/
			StringBuffer strBuf = pickupNewPost(stream);
			String strSrc = strBuf.toString();

			/*必要部分のテキストのみを切り出す*/
			String body = pickUpString(strSrc, "title");		//タイトル部分
			String pubDate= pickUpString(strSrc, "pubDate");	//投稿日時
			String guid = pickUpString(strSrc, "guid");			//呟きの個別URL

			/*実際の表示*/
			TextView tv = (TextView) findViewById(R.id.textView);
			tv.setText(body+"\n"+pubDate+"\n"+guid);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 最新postを切り出す
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private StringBuffer pickupNewPost(BufferedReader stream)
			throws IOException {
		String str = "";
		StringBuffer strBuf = new StringBuffer();
		while(stream.ready() && (str = stream.readLine()).indexOf("</item>") == -1){
			strBuf.append(decode(str));
		}
		return strBuf;
	}

	private String pickUpString(String src, String tag){
		String tagStart = "<"+tag+">";
		int start = src.indexOf(tagStart);
		int end = src.indexOf("</"+tag+">");

		return src.substring(start, end).replace(tagStart, "");
	}

	/**
	 * 文字列に含まれる文字参照を復元
	 * @author http://d.hatena.ne.jp/shime-z/20040623#p1
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
	    Pattern pattern = Pattern.compile("&#(\\d+);|&#([\\da-fA-F]+);");
	    Matcher matcher = pattern.matcher(str);
	    StringBuffer sb = new StringBuffer();
	    Character buf;
	    while(matcher.find()){
	        if(matcher.group(1) != null){
	            buf = new Character(
	                      (char)Integer.parseInt(matcher.group(1)));
	        }else{
	            buf = new Character(
	                      (char)Integer.parseInt(matcher.group(2), 16));
	        }
	        matcher.appendReplacement(sb, buf.toString());
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}
}

