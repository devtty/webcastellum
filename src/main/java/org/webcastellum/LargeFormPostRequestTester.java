package org.webcastellum;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public final class LargeFormPostRequestTester {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println(Version.tagLine());
            System.err.println("This tool tests a web application by sending a large POST request");
            System.err.println("Please provide the following arguments: url size-of-form-post-in-bytes");
            System.err.println("NOTE: Please use the full qualified real target URL (i.e. index.jsp) of the form post to avoid redirect trouble which might render your POST actually as a GET request");
            System.exit(-1);
        }
        final int size = Integer.parseInt(args[1]);
        final LargeFormPostRequestTester tester = new LargeFormPostRequestTester(args[0]);
        tester.sendLargePostRequest(size);
    }




    /* form post test:
    try {
    // Construct data
    String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
    data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
    // Send data
    URL url = new URL("http://hostname:80/cgi");
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(data);
    wr.flush();
    // Get the response
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
    // Process line...
    }
    wr.close();
    rd.close();
    } catch (Exception e) {
    }
     */


    /* form upload test:
    try {
    // Construct data
    String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
    data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
    // Create a socket to the host
    String hostname = "hostname.com";
    int port = 80;
    InetAddress addr = InetAddress.getByName(hostname);
    Socket socket = new Socket(addr, port);
    // Send header
    String path = "/servlet/SomeServlet";
    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
    wr.write("POST "+path+" HTTP/1.0\r\n");
    wr.write("Content-Length: "+data.length()+"\r\n");
    wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
    wr.write("\r\n");
    // Send data
    wr.write(data);
    wr.flush();
    // Get response
    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
    // Process line...
    }
    wr.close();
    rd.close();
    } catch (Exception e) {
    }
     */

    private final URL url;

    public LargeFormPostRequestTester(final String webAddress) throws MalformedURLException {
        if (webAddress == null) {
            throw new NullPointerException("webAddress must not be null");
        }
        this.url = new URL(webAddress);
    }

    public void sendLargePostRequest(final int size) throws IOException {
        // Construct data
        String encodedData = URLEncoder.encode("test", WebCastellumFilter.DEFAULT_CHARACTER_ENCODING) + "=" + URLEncoder.encode("this is just a mass test", WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
        encodedData += "&" + URLEncoder.encode("payload", WebCastellumFilter.DEFAULT_CHARACTER_ENCODING) + "=" + createTestdata(size);
        // Send data
        final long start = System.currentTimeMillis();
        final long end;
        HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setFollowRedirects(true);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", "" + encodedData.length());
//        System.out.println(encodedData);
        DataOutputStream output = null;
        BufferedReader reader = null;
        try {
            output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(encodedData);
            output.flush();
            // Get the response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
            }
            end = System.currentTimeMillis();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("Duration: "+(end-start)+" ms");
    }
    
    
    // simply some chars with some spaces to simulate a long text of words.
    private static final char[] TEST_DATA = new char[]{'a','b','c','d','e','f',' ','g','h','i','j','k','l','m','n','o',' ','p','q','r','s','t','u','v','w',' ','x','y','z','.','\n'};

    static String createTestdata(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        final StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            result.append( TEST_DATA[i %TEST_DATA.length] );
        }
        return result.toString();
    }
}
