package scaffolding;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.FormContentProvider;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class RestClient {

    public static HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    static {
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not start httpClient", e);
        }
    }

    public static RestClient create(String appRunnerUrl) {
        return new RestClient(httpClient, appRunnerUrl);
    }

    private final HttpClient client;
    private final String appRunnerUrl;

    private RestClient(HttpClient client, String appRunnerUrl) {
        this.client = client;
        this.appRunnerUrl = appRunnerUrl;
    }

    public ContentResponse createApp(String gitUrl) throws Exception {
        return createApp(gitUrl, null);
    }

    public ContentResponse createApp(String gitUrl, String appName) throws Exception {
        Fields fields = new Fields();
        fields.add("gitUrl", gitUrl);
        if (appName != null) {
            fields.add("appName", appName);
        }
        return client.POST(appRunnerUrl + "/api/v1/apps")
            .content(new FormContentProvider(fields)).send();
    }

    public ContentResponse deploy(String app) throws Exception {
        return client.POST(appRunnerUrl + "/api/v1/apps/" + app + "/deploy")
            .header("Accept", "application/json") // to simulate products like the Stash commit hook
            .send();
    }

    public ContentResponse stop(String app) throws Exception {
        return client.newRequest(appRunnerUrl + "/api/v1/apps/" + app + "/stop").method("PUT").send();
    }

    public ContentResponse deleteApp(String appName) throws Exception {
        return client.newRequest(appRunnerUrl + "/api/v1/apps/" + appName).method("DELETE").send();
    }

    public ContentResponse homepage(String appName) throws Exception {
        return client.GET(appRunnerUrl + "/" + appName + "/");
    }

    public ContentResponse get(String url) throws Exception {
        return client.GET(appRunnerUrl + url);
    }

}
