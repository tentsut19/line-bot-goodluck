package com.cabsat.linebot.client;

import com.cabsat.linebot.client.request.CustomerRequest;
import com.cabsat.linebot.client.request.OrderSummaryRequest;
import com.cabsat.linebot.client.request.RegisterLineGroupRequest;
import com.cabsat.linebot.client.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class EcommerceClient {

    @Autowired
    private RestTemplate restTemplate;

    final String HOST_NAME = "http://ecommerce-uat.ap-southeast-1.elasticbeanstalk.com";
//    final String HOST_NAME = "http://localhost:8000";
    final String CREATE_ORDER_ENDPOINT = "/api/v1/order/customer";
    final String CREATE_SUMMARY_ENDPOINT = "/api/v1/order-summary/summary-line";
    final String DELETE_ORDER_ENDPOINT = "/api/v1/order/delete/{orderCode}";
    final String GET_PRODUCT_SETTING_ENDPOINT = "/api/v1/product-setting";
    final String GET_PRODUCT_SETTING_GROUP_ENDPOINT = "/api/v1/product-setting/group/{groupId}";
    final String GET_COMPANY_BY_TAX_ID_ENDPOINT = "/api/v1/company/tax-id/{taxId}";
    final String GET_REGISTER_BY_TAX_ID_ENDPOINT = "/api/v1/register/tax-id/{taxId}";
    final String UPLOAD_ENDPOINT = "/api/v1/upload";

    public ResponseEntity<CustomerResponse> createOrderCustomer(CustomerRequest request) {
        String endpoint = HOST_NAME + CREATE_ORDER_ENDPOINT;
        log.info(
                "Start client for API. {} request {}",
                endpoint,
                request
        );
        HttpEntity<CustomerRequest> httpEntity = new HttpEntity<>(request);
        ResponseEntity<CustomerResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<CustomerResponse>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<CustomerResponse> deleteOrder(String orderCode) {
        String deleteOrder = DELETE_ORDER_ENDPOINT.replace("{orderCode}", orderCode);
        String endpoint = HOST_NAME + deleteOrder;

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("user_id", "");

        log.info(
                "Start client for API. {} orderCode {}",
                endpoint,
                orderCode
        );
        HttpEntity<CustomerRequest> httpEntity = new HttpEntity<>(requestHeader);
        ResponseEntity<CustomerResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.DELETE,
                httpEntity,
                new ParameterizedTypeReference<CustomerResponse>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<List<OrderSummaryResponse>> summary(OrderSummaryRequest request) {
        String endpoint = HOST_NAME + CREATE_SUMMARY_ENDPOINT;
        log.info(
                "Start client for API. {} request {}",
                endpoint,
                request
        );
        HttpEntity<OrderSummaryRequest> httpEntity = new HttpEntity<>(request);
        ResponseEntity<List<OrderSummaryResponse>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<List<OrderSummaryResponse>>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<List<ProductSettingResponse>> getProductSetting() {
        String endpoint = HOST_NAME + GET_PRODUCT_SETTING_ENDPOINT;
        log.info(
                "Start client for API. {}",
                endpoint
        );
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("user_id", "");
        HttpEntity<CustomerRequest> httpEntity = new HttpEntity<>(requestHeader);
        ResponseEntity<List<ProductSettingResponse>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<ProductSettingResponse>>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<List<ProductSettingResponse>> getProductSetting(String groupId) {
        String endpoint = HOST_NAME + GET_PRODUCT_SETTING_GROUP_ENDPOINT.replace("{groupId}", groupId);
        log.info(
                "Start client for API. {}",
                endpoint
        );
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("user_id", "");
        HttpEntity<CustomerRequest> httpEntity = new HttpEntity<>(requestHeader);
        ResponseEntity<List<ProductSettingResponse>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<ProductSettingResponse>>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<FileManagerResponse> uploadImage(byte[] bytes, String originalFilename, String type) {
        String endpoint = HOST_NAME + UPLOAD_ENDPOINT;
        log.info(
                "Start client for update API. {} request {}",
                endpoint,
                originalFilename
        );


        MultiValueMap<String,Object> multipartRequest = new LinkedMultiValueMap<>();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);//Main request's headers

        HttpHeaders requestHeadersAttachment = new HttpHeaders();
        requestHeadersAttachment.setContentType(MediaType.IMAGE_JPEG);// extract mediatype from file extension
        HttpEntity<ByteArrayResource> attachmentPart;
        ByteArrayResource fileAsResource = new ByteArrayResource(bytes){
            @Override
            public String getFilename(){
                return originalFilename;
            }
        };
        attachmentPart = new HttpEntity<>(fileAsResource,requestHeadersAttachment);

        multipartRequest.set("file",attachmentPart);

        HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(multipartRequest,requestHeaders);//final request

        ResponseEntity<FileManagerResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<FileManagerResponse>() {
                }
        );
        log.info("Response update API., http body : {}", response.getBody());
        log.info("End update API., http status : {}", response.getStatusCodeValue());
        return response;
    }

    public ResponseEntity<CompanyResponse> getCompanyByTaxId(String taxId) {
        String endpoint = HOST_NAME + GET_COMPANY_BY_TAX_ID_ENDPOINT.replace("{taxId}", taxId);
        log.info(
                "Start client for API. {}",
                endpoint
        );
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("user_id", "");
        HttpEntity<CustomerRequest> httpEntity = new HttpEntity<>(requestHeader);
        ResponseEntity<CompanyResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<CompanyResponse>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }


    public ResponseEntity<RegisterLineGroupResponse> registerByTaxId(String taxId,RegisterLineGroupRequest request) {
        String registerEndpoint = GET_REGISTER_BY_TAX_ID_ENDPOINT.replace("{taxId}", taxId);
        String endpoint = HOST_NAME + registerEndpoint;
        log.info(
                "Start client for API. {} request {}",
                endpoint,
                request
        );
        HttpEntity<RegisterLineGroupRequest> httpEntity = new HttpEntity<>(request);
        ResponseEntity<RegisterLineGroupResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<RegisterLineGroupResponse>() {
                }
        );
        log.info("Response API., http body : {}", response.getBody());
        log.info("End API., http status : {}", response.getStatusCodeValue());
        return response;
    }
}
