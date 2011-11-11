package com.bsd.android.dict;

import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * The class that does the actual work of getting the definitions.
 * User: mabdullah
 * Date: 11/11/11
 * Time: 10:52 AM
 */
public class WordLookupTask extends AsyncTask<String, Void, String>{
    final TextView textView;
    public WordLookupTask(TextView textView){
        this.textView=textView;
        this.textView.setMovementMethod(new ScrollingMovementMethod());
    }
    @Override
    protected String doInBackground(String... params) {
        String returnVal = "";
        if (params.length < 3){
            throw new IllegalArgumentException("VID, Word & Base URL are all mandatory arguments!");
        }
        String url = buildUrl(params[0], params[1], params[2]);
        HttpURLConnection connection = connectUrl(url);
        if(connection == null){
            returnVal = "Error occurred in connecting to server. Please try again.";
            return returnVal;
        }
        if(HttpURLConnection.HTTP_OK == getResponseCode(connection)){
            returnVal = getContent(connection);
        }else{
            returnVal = "Server returned unrecognized response.";
        }
        return returnVal;
    }

    private String getContent(HttpURLConnection connection){
        try {
            StringBuilder returnVal = new StringBuilder();
            InputStream in = connection.getInputStream();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(in);
            NodeList partsOfSpeech = document.getElementsByTagName("partofspeech");
            for(int x=0;x<partsOfSpeech.getLength();x++){
                Element element = (Element)partsOfSpeech.item(x);
                String speechType = element.getAttribute("pos");
                returnVal.append(speechType).append(":\n\t");
                NodeList definitions = element.getElementsByTagName("def");
                for(int y =0;y<definitions.getLength();y++){
                    Element definition= (Element)definitions.item(y);
                    returnVal.append(definition.getAttribute("defno")).append(". ");
                    returnVal.append(definition.getTextContent()).append("\n\t");
                }
                returnVal.append("\n");
            }
            return returnVal.toString();
        } catch (IOException e) {
            return "Error in communicating with the server. Please try again later.";
        } catch (ParserConfigurationException e) {
            return "Unexpected configuration error.";
        } catch (SAXException e) {
            return "Server returned invalid XML.";
        }
    }

    private int getResponseCode(HttpURLConnection connection) {
        try{
            return connection.getResponseCode();
        }catch(IOException ignore){}
        return -1;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        textView.setText(s);
    }

    private String buildUrl(String baseUrl, String vid, String word){
        StringBuilder bufferedUrl=new StringBuilder();
        bufferedUrl.append(baseUrl); //baseUrl
        try{
            bufferedUrl.append("?vid=").append(URLEncoder.encode(vid, "utf8")); //API Key
            bufferedUrl.append("&q=").append(URLEncoder.encode(word, "utf8")); //The word.
            bufferedUrl.append("&type=define");
            bufferedUrl.append("&site=dictionary");
        } catch (UnsupportedEncodingException ignore) { }
        return bufferedUrl.toString();
    }

    private HttpURLConnection connectUrl(String url){
        try {
            URL targetUrl = new URL(url);
            URLConnection connection = targetUrl.openConnection();
            return (HttpURLConnection) connection;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return null;
    }
}
