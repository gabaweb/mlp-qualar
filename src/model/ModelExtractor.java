package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class ModelExtractor {

	private HttpClient client;

	public ModelExtractor() {

		client = HttpClientBuilder.create().build();

	}

	public void login() throws ClientProtocolException, IOException {

		HttpPost post = new HttpPost("https://qualar.cetesb.sp.gov.br/qualar/autenticador");

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("cetesb_login", "gabriel.barros@hotmail.com"));
		urlParameters.add(new BasicNameValuePair("cetesb_password", "123456"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

	}

	public HttpResponse extract(Integer station, String parameter, String date1, String date2) throws ClientProtocolException, IOException {

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("dataInicialStr", date1));
		urlParameters.add(new BasicNameValuePair("dataFinalStr", date2));
		urlParameters.add(new BasicNameValuePair("estacaoVO.nestcaMonto", station.toString()));
		urlParameters.add(new BasicNameValuePair("nparmtsSelecionados", parameter));

		HttpPost post = new HttpPost("https://qualar.cetesb.sp.gov.br/qualar/exportaDadosAvanc.do?method=exportar");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		return response;

	}

	public void saveFile(HttpResponse response, String filePath) throws IOException, FileNotFoundException {
		InputStream is = response.getEntity().getContent();
		FileOutputStream fos = new FileOutputStream(new File(filePath));
		int inByte;
		while ((inByte = is.read()) != -1)
			fos.write(inByte);
		is.close();
		fos.close();
	}

}
