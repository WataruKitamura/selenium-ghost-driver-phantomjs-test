package jp.ca.ym.sample.web.access;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * ヘッドレスブラウザによるWebアクセステストクラス。
 * <p>
 * 本クラスはサンプル的に作成されてものです。<br>
 * 実行するためにはマシン上にPhnatomJSのモジュールが必要になります。<br>
 * 各々の環境に合ったPhantomJSをダウンロードして{@code /usr/share/phantomjs/bin/phantomjs}
 * に配置してください。<br>
 * Windowsの場合はプライマリドライブ（デフォルトはCドライブ）に{@code C:\}
 * {@code user\share\phantomjs\bin\phantomjs}となるように配置してください。 {@code .exe}
 * の拡張子は無くても構いません。
 * </p>
 * 
 * @see <a href="http://phantomjs.org/download.html">PhantomJSダウンロードページ</a>
 * @author morimichi_yuichi
 */
public class PhantomJSTest {

	private WebDriver driver;
	private String baseUrl;

	enum OsType {
		/** Windows */
		WINDOWS,
		/** Linux */
		LINUX,
		/** その他 */
		OTHER;

		static OsType getOsType() {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				return WINDOWS;
			}
			if (osName.indexOf("Linux") >= 0) {
				return LINUX;
			}
			return OTHER;
		}
	}

	/**
	 * ドライバのセットアップ
	 */
	@Before
	public void setUp() {
		OsType osType = OsType.getOsType();
		if (osType == OsType.OTHER) {
			fail("テスト対象外のOSです。");
		}

		String phantomJsBinaryPath = "/usr/share/phantomjs/bin/phantomjs";

		// === サンプル的に実行できるようにアプリケーションの中にも入れておく
		switch (OsType.getOsType()) {
			case WINDOWS:
				phantomJsBinaryPath = "src/test/resources/phantomjs/windows/phantomjs";
				break;
			case LINUX:
				phantomJsBinaryPath = "src/test/resources/phantomjs/linux/phantomjs";
				break;
			default:
				fail("テスト対象外のOSです。");
		}

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				phantomJsBinaryPath);
		driver = new PhantomJSDriver(caps);

		// htmlの要素が見つからなかったときの待機時間
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		// ブラウザ起動時に開くページ
		baseUrl = "https://www.google.co.jp";
	}

	/**
	 * ドライバを閉じる。
	 */
	@After
	public void tearDown() {
		// 終了
		driver.quit();
	}

	/**
	 * 
	 * YahooをGoogleから検索してしてトップ画面経由でYahooショッピング画面を表示する。
	 * 
	 * @throws IOException キャプチャファイル保存失敗
	 */
	@Test
	public void test() throws IOException {
		// ここに操作内容を記述する
		driver.get(baseUrl + "/");
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("Yahoo");
		element.submit();
		driver.findElement(By.partialLinkText("Yahoo! JAPAN")).click();

		{
			// 検証
			assertEquals("Yahoo! JAPAN", driver.getTitle());

			// キャプチャをとる
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("target/surefire-reports/yahoo-top.png"));
		}

		// 「ショッピング」リンククリック
		driver.findElement(By.cssSelector(".cbysC1")).click();

		{
			// 検証
			assertEquals("Yahoo!ショッピング - Ｔポイントが貯まる！使える！ネット通販",
					driver.getTitle());
			// キャプチャをとる
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("target/surefire-reports/yahoo-shopping.png"));
		}

	}
}