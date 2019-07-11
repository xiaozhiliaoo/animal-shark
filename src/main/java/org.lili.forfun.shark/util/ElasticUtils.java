package org.lili.forfun.shark.util;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Log4j2
public class ElasticUtils {
    private static RestClient restClient;
    private static RestHighLevelClient restHighLevelClient;
    private static BulkProcessor bulkProcessor;

    public static void init() {
        String hostname = "elasticsearch.lili.net";
        int port = 8001;

        restClient = RestClient.builder(new HttpHost(hostname, port)).build();
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port)));

        // initBulkProcessor();
    }

    public static void destroy() {
        try {
            restClient.close();
            restHighLevelClient.close();
            // bulkProcessor.close();
        } catch (Exception e) {
        }
    }

    public static Long getResponse(List<String> requestIdList, String index) {
        System.out.println("getResponse size : " + requestIdList.size() + ", index : " + index);
        Long avgLatency = 0L;
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(QueryBuilders.termsQuery("request_id", requestIdList));
        // source.aggregation(AggregationBuilders.avg("latency"));

        source.size(requestIdList.size() + 10);
        searchRequest.source(source);

        int count = 0;
        Long totalLatency = 0L;

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Long latency = Long.parseLong((String) searchHit.getSourceAsMap().get("latency"));
                count++;
                totalLatency += latency;
            }
            avgLatency = totalLatency / count;
            System.out.println("avg : " + avgLatency);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return avgLatency;
    }


    private static <T> int bulkProcessor(List<T> requestList) {
        int state = 0;
        if (requestList == null || requestList.size() == 0) {
            return state;
        }

        try {
            requestList.stream().filter(request -> request instanceof DocWriteRequest)
                .forEach(request -> bulkProcessor.add((DocWriteRequest) request));
            state = 1;
        } catch (Exception e) {
            log.warn("bulkProcess failed.", e);
        }

        return state;
    }

    private static  void initBulkProcessor() {
        Settings settings = Settings.builder()
            .put("node.name", "es")
            .build();
        ThreadPool threadPool = new ThreadPool(settings);

        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                log.info("beforeBulk, byteSize : {}, actionSize : {}", request.estimatedSizeInBytes(), request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                log.info("afterBulk1, executionId : {}, request : {}, response : {}", executionId, request, response);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.info("afterBulk2, executionId : {}, request : {}", executionId, request, failure);
            }
        };

        BulkProcessor.Builder builder = new BulkProcessor.Builder(restHighLevelClient::bulkAsync, listener, threadPool);
        builder.setBulkActions(3000);
        builder.setBulkSize(new ByteSizeValue(3L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(5);
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
        builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(10L), 3));

        bulkProcessor = builder.build();
    }
}
