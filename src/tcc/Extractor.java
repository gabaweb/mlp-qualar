package tcc;

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

public class Extractor {

	private HttpClient client;

	Extractor() {

		client = HttpClientBuilder.create().build();

	}

	private void login() throws ClientProtocolException, IOException {

		HttpPost post = new HttpPost("http://qualar.cetesb.sp.gov.br/qualar/autenticador");

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("cetesb_login", "gabriel.barros@hotmail.com"));
		urlParameters.add(new BasicNameValuePair("cetesb_password", "123456"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

	}

	private HttpResponse extract(String station, String parameter, String date1, String date2)
			throws ClientProtocolException, IOException {

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("dataInicialStr", date1));
		urlParameters.add(new BasicNameValuePair("dataFinalStr", date2));
		urlParameters.add(new BasicNameValuePair("estacaoVO.nestcaMonto", station));
		urlParameters.add(new BasicNameValuePair("nparmtsSelecionados", parameter));

		HttpPost post = new HttpPost("http://qualar.cetesb.sp.gov.br/qualar/exportaDadosAvanc.do?method=exportar");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		return response;

	}

	private void saveFile(HttpResponse response, String filePath) throws IOException, FileNotFoundException {
		InputStream is = response.getEntity().getContent();
		FileOutputStream fos = new FileOutputStream(new File(filePath));
		int inByte;
		while ((inByte = is.read()) != -1)
			fos.write(inByte);
		is.close();
		fos.close();
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {

		Extractor extractor = new Extractor();

		extractor.login();

		// Limeira

		extractor.saveFile(extractor.extract("281", "12", "11/09/2014", "11/09/2017"), "281_Limeira_MP10.csv"); //281_Limeira_MP10
		extractor.saveFile(extractor.extract("113", "12", "11/09/2014", "11/09/2017"), "113_Piracicaba_MP10.csv"); //113_Piracicaba_MP10
		extractor.saveFile(extractor.extract("89",  "12", "11/09/2014", "11/09/2017"), "89_Campinas-Centro_MP10.csv"); //89_Campinas-Centro_MP10
		extractor.saveFile(extractor.extract("276", "12", "11/09/2014", "11/09/2017"), "276_Campinas-Taquaral_MP10.csv"); //276_Campinas-Taquaral_MP10
		
		//extractor.saveFile(extractor.extract("281", "23", "01/01/2015", "31/12/2016"), "281_23_01-01-2015_31-12-2016.csv"); // DV
		//extractor.saveFile(extractor.extract("281", "24", "01/01/2015", "31/12/2016"), "281_24_01-01-2015_31-12-2016.csv"); // VV

	}

}
