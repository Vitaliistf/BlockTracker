package org.vitaliistf.coingecko;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.vitaliistf.models.CoinInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class CG {
    private static final String URI = "https://api.coingecko.com/api/v3/";
    private static final Cache CACHE = Cache.getInstance();

    public static double getPriceUSD(String symbol) {
        List<CoinInfo> coins = deserializeCoins();
        for(CoinInfo coin : coins) {
            if(coin.getSymbol().equals(symbol.toLowerCase())) {
                return coin.getCurrentPrice();
            }
        }
        return 0;
    }

    private static List<CoinInfo> deserializeCoins() {
        String result = "";
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("vs_currency", "usd"));
            params.add(new BasicNameValuePair("per_page", "250"));
            if (CACHE.getMarketsCache().isPresent()) {
                result = CACHE.getMarketsCache().get();
            } else {
                result = makeAPICall("coins/markets", params);
                CACHE.setMarketsCache(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(result, new TypeToken<List<CoinInfo>>(){}.getType());
    }

    public static List<CoinInfo> getWatchlistInfo(List<String> symbols) {
        List<CoinInfo> coins = deserializeCoins();
        List<CoinInfo> result = new ArrayList<>();
        for(CoinInfo coin : coins) {
            if(symbols.contains(coin.getSymbol())) {
                result.add(coin);
            }
        }

        return result;
    }

    public static Optional<CoinInfo> getCoinInfo(String symbol) {
        List<CoinInfo> coins = deserializeCoins();
        for(CoinInfo coin : coins) {
            if(coin.getSymbol().equalsIgnoreCase(symbol)) {
                return Optional.of(coin);
            }
        }
        return Optional.empty();
    }

    public static String makeAPICall(String uri, List<NameValuePair> parameters) throws IOException, URISyntaxException {
        String response_content = "";
        CloseableHttpClient client = HttpClients.createDefault();

        URIBuilder query = new URIBuilder(URI + uri);
        query.addParameters(parameters);

        HttpGet request = new HttpGet(query.build());

        CloseableHttpResponse response;

        response = client.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
        }

        return response_content;
    }

}
