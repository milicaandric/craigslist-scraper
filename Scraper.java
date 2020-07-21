import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {

  private static final String baseUrl =
      "https://sfbay.craigslist.org/search/sss?query=iphone%208&sort=rel";

  public static void main(String[] args) {
    WebClient client = new WebClient();
    client.getOptions().setJavaScriptEnabled(false);
    client.getOptions().setCssEnabled(false);
    client.getOptions().setUseInsecureSSL(true);

    try {
      HtmlPage page = client.getPage(baseUrl);
      List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//li[@class='result-row']");

      if (items.isEmpty()) {
        System.out.println("No items found.");
      } else {
        for (HtmlElement htmlItem : items) {
          HtmlAnchor itemAnchor =
              ((HtmlAnchor) htmlItem.getFirstByXPath("p[@class='result-info']/a"));
          HtmlElement spanPrice =
              ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']")); 
                                                                                            
          // case where item does not have price
          String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

          // creates new Item object
          Item item = new Item();
          item.setTitle(itemAnchor.asText());
          item.setUrl(itemAnchor.getHrefAttribute());
          item.setPrice(new BigDecimal(itemPrice.replace("$", "")));
          
          // creates new ObjectMapper object
          ObjectMapper mapper = new ObjectMapper();
          String jsonString = mapper.writeValueAsString(item);
          System.out.println(jsonString);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
