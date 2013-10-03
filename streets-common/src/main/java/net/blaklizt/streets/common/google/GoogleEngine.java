package net.blaklizt.streets.common.google;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 5/19/13
 * Time: 2:59 PM
 */
public class GoogleEngine
{
    public GoogleResults search(String searchPhrase)
    {
		GoogleResults results = null;
        try
        {
            String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
            String charset = "UTF-8";

            URL url = new URL(google + URLEncoder.encode(searchPhrase, charset));
            Reader reader = new InputStreamReader(url.openStream(), charset);
            results = new Gson().fromJson(reader, GoogleResults.class);

            // Show title and URL of 1st result.
            System.out.println(results.getResponseData().getResults().get(0).getTitle());
            System.out.println(results.getResponseData().getResults().get(0).getUrl());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return results;
	}
}
