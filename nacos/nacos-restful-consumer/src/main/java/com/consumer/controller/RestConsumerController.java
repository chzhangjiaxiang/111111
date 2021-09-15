package com.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
public class RestConsumerController {
    @Value("${provider.address}")
    private  String providerAddress;

    @GetMapping("/service")
    public String service(){
        //远程调用
        RestTemplate restTemplate = new RestTemplate();
        //调用服务
        String forObject = restTemplate.getForObject("http://" + providerAddress + "/service", String.class);
        return "consumer invoke |" +forObject;
    }

    //指定服务名
    String serviceId="nacos-restful-provider";
    //通过负载均衡发现地址,流程是中服务中心获取到服务列表,通过复杂i均衡算法获取一个地址
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/service1")
    public String service1(){
        //远程调用
        RestTemplate restTemplate = new RestTemplate();
        //发现一个服务地址
        ServiceInstance choose = loadBalancerClient.choose(serviceId);
        //获取一个以http://开头的地址,包括ip和端口
        URI uri = choose.getUri();
        //调用服务
        String forObject = restTemplate.getForObject(uri + "/service", String.class);
        return "consumer invoke |" +forObject;
    }
}
