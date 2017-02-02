package org.trimou.basis;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.trimou.basis.BasisConfigurationKey.FS_TEMPLATE_REPO_DIR;
import static org.trimou.basis.BasisConfigurationKey.GLOBAL_JSON_DATA_FILE;
import static org.trimou.basis.Strings.CODE;
import static org.trimou.basis.Strings.RESULT;
import static org.trimou.basis.Strings.SUCCESS;
import static org.trimou.basis.Strings.TEMPLATE_ID;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.spi.context.TestContext;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class RenderTest extends BasisTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return BasisTest.createDefaultClassPath()
                .addSystemProperty(FS_TEMPLATE_REPO_DIR.get(),
                        "src/test/resources/templates")
                .addSystemProperty(GLOBAL_JSON_DATA_FILE.get(),
                        "src/test/resources/global-data.json")
                .add(ShrinkWrap.create(JavaArchive.class)
                        .addClasses(RenderTest.class))
                .build();
    }

    @Rule
    public Timeout globalTimeout = Timeout.millis(5000);

    @RunAsClient
    @Test
    public void testOnetimeHello() {
        Response response = RestAssured.given()
                .header(Strings.HEADER_CONTENT_TYPE, Strings.APP_JSON)
                .body("{\"content\" : \"Hello {{#each data}}{{this}}{{#hasNext}}, {{/hasNext}}{{/each}}!\", \"data\" : [ \"me\", \"Lu\", \"foo\" ]}")
                .post("/render");
        response.then().assertThat().statusCode(200)
                .body(equalTo("Hello me, Lu, foo!"));
    }

    @RunAsClient
    @Test
    public void testOnetimeInvalidInput(TestContext context) {
        RestAssured.given()
                .header(Strings.HEADER_CONTENT_TYPE, Strings.APP_JSON)
                .body("{\"foo\":\"bar\"}").post("/render").then().assertThat()
                .statusCode(400);
    }

    @RunAsClient
    @Test
    public void testHello() {
        Response response = RestAssured.given()
                .header(Strings.HEADER_CONTENT_TYPE, Strings.APP_JSON)
                .body("{\"id\" : \"hello.txt\", \"data\" : [ \"me\", \"Lu\", \"foo\" ], \"contentType\":\"text/plain\"}")
                .post("/render");
        response.then().assertThat().statusCode(200)
                .body(equalTo("Hello me, Lu, foo!"));
    }

    @RunAsClient
    @Test
    public void testHelloMetadata() {
        Response response = RestAssured.given()
                .header(Strings.HEADER_CONTENT_TYPE, Strings.APP_JSON)
                .body("{\"id\" : \"hello.txt\", \"data\" : [ \"me\", \"Lu\", \"foo\" ], \"contentType\":\"text/plain\", \"resultType\":\"metadata\"}")
                .post("/render");
        response.then().assertThat().statusCode(200)
                .body(CODE, equalTo(SUCCESS))
                .body(RESULT, equalTo("Hello me, Lu, foo!"))
                .body(TEMPLATE_ID, equalTo("hello.txt"));
    }

    @RunAsClient
    @Test
    public void testHelloGlobalData() {
        Response response = RestAssured.given()
                .header(Strings.HEADER_CONTENT_TYPE, Strings.APP_JSON)
                .body("{\"id\" : \"hello-global-data.txt\", \"data\" : {\"name\":1}, \"contentType\":\"text/plain\"}")
                .post("/render");
        response.then().assertThat().statusCode(200)
                .body(equalTo("##hello-global-data.txt## Hello 1 and bar!"));
    }

}