package tcc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

	private HttpResponse extract(String station, String parameter, String date1, String date2) throws ClientProtocolException, IOException {

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

	public static void main(String[] args) throws ClientProtocolException, IOException, SQLException {

		String station = "113";

		Extractor extractor = new Extractor();

		extractor.login();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select date(horario) horario from ENTRADAS_TRATADAS_DIARIO WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");

		String lastdate = rs.getString("horario");
		LocalDate datetime = LocalDate.parse(lastdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		lastdate = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();

		extractor.saveFile(extractor.extract("113", "12", lastdate, dateFormat.format(date)), "113_Piracicaba_MP10.csv"); // 113_Piracicaba_MP10

	}

}
